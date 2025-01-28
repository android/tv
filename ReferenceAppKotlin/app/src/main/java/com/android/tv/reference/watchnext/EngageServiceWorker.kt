/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tv.reference.watchnext

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.tv.reference.watchnext.Constants.PUBLISH_TYPE
import com.android.tv.reference.watchnext.Constants.PUBLISH_TYPE_CONTINUATION
import com.google.android.engage.service.AppEngageException
import com.google.android.engage.service.AppEngagePublishClient
import com.google.android.engage.service.AppEngagePublishStatusCode
import com.google.android.engage.service.PublishStatusRequest
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.common.annotations.VisibleForTesting
import timber.log.Timber

/**
 * [EngageServiceWorker] is a [Worker] class that is tasked with publishing cluster
 * requests to Engage Service
 */
class EngageServiceWorker(
    context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    @VisibleForTesting
    constructor(
        context: Context,
        workerParams: WorkerParameters,
        client: AppEngagePublishClient,
    ) : this(context, workerParams) {
        this.client = client
    }

    val TAG = "ENGAGE_SERVICE_WORKER"
    private var client = AppEngagePublishClient(context)
    private val clusterRequestFactory = ClusterRequestFactory(context)

    /**
     * [doWork] is the entry point for the [EngageServiceWorker], and differentiates between
     * publishing tasks of each cluster
     */
    override fun doWork(): Result {
        // If too many publishing attempts have failed, do not attempt to publish again.
        if (runAttemptCount > Constants.MAX_PUBLISHING_ATTEMPTS) {
            return Result.failure()
        }

        Timber.i("Checking for Engage Service availability")

        // Check if engage service is available before publishing.
        val isAvailable = Tasks.await(client.isServiceAvailable)

        // If the service is not available, do not attempt to publish and indicate failure.
        if (!isAvailable) {
            Timber.e("Engage service is not available")
            return Result.failure()
        }

        Timber.i("Engage Service is available. Proceeding to publish clusters")

        // The cluster to publish must be passed into the worker through the input data.
        // This value must be one of the predefined values indicated a valid cluster to publish. Instead
        // of using one worker with flags to determine what cluster to publish, you may also choose to
        // your separate workers to publish different clusters; use whichever approach better fits your
        // app architecture.
        return when (inputData.getString(PUBLISH_TYPE)) {
            PUBLISH_TYPE_CONTINUATION -> publishContinuation()
            else -> throw IllegalArgumentException("Bad publish type")
        }
    }

    /**
     * [publishContinuation] publishes continuation clusters and returns the result of the attempt to
     * publish the continuation clusters if the user is signed in. If the user is signed out it
     * instead publishes a request to delete the continuation cluster that had previously been
     * published.
     *
     * @return result Result of publishing a continuation cluster, or continuation cluster deletion
     */
    private fun publishContinuation(): Result {
        val publishTask: Task<Void> = client.publishContinuationCluster(
            clusterRequestFactory.constructContinuationClusterRequest()
        )
        val statusCode: Int = AppEngagePublishStatusCode.PUBLISHED
        return publishAndProvideResult(publishTask, statusCode)
    }

    /**
     * [publishAndProvideResult] is a method that is in charge of publishing a given task
     *
     * @param publishTask A task to publish some cluster or delete some cluster
     * @param publishStatusCode Publish status code to set through Engage.
     * @return publishResult Result of [publishTask]
     */
    private fun publishAndProvideResult(
        publishTask: Task<Void>,
        publishStatusCode: Int
    ): Result {
        setPublishStatusCode(publishStatusCode)

        // Result initialized to success, it is changed to retry or failure if an exception occurs.
        var result: Result = Result.success()
        try {
            // An AppEngageException may occur while publishing, so we may not be able to await the
            // result.
            Tasks.await(publishTask)
        } catch (publishException: AppEngageException) {
            Publisher.logPublishing(publishException)
            // Some errors are recoverable, such as a threading issue, some are unrecoverable
            // such as a cluster not containing all necessary fields. If an error is recoverable, we
            // should attempt to publish again. Setting the  result to retry means WorkManager will
            // attempt to run the worker again, thus attempting to publish again.
            result =
                if (Publisher.isErrorRecoverable(publishException))
                    Result.retry()
                else
                    Result.failure()
        } catch (exception: Exception) {
            exception.printStackTrace()
            result = Result.failure()
        }
        // This result is returned back to doWork.
        return result
    }

    /**
     * [setPublishStatusCode] method is in charge of updating the publish status code, which monitors
     * the health of the integration with EngageSDK
     *
     * @param statusCode PublishStatus code to be set through Engage.
     */
    private fun setPublishStatusCode(statusCode: Int) {
        client
            .updatePublishStatus(PublishStatusRequest.Builder().setStatusCode(statusCode).build())
            .addOnSuccessListener {
                Log.i(TAG, "Successfully updated publish status code to $statusCode")
            }
            .addOnFailureListener { exception ->
                Log.e(
                    TAG,
                    "Failed to update publish status code to $statusCode\n${exception.stackTrace}"
                )
            }
    }
}
