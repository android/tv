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
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.android.tv.reference.watchnext.Constants.PERIODIC_WORKER_NAME_CONTINUATION
import com.android.tv.reference.watchnext.Constants.PUBLISH_TYPE
import com.android.tv.reference.watchnext.Constants.PUBLISH_TYPE_CONTINUATION
import com.android.tv.reference.watchnext.Constants.WORKER_NAME_CONTINUATION
import com.google.android.engage.service.AppEngageErrorCode
import com.google.android.engage.service.AppEngageException
import java.util.concurrent.TimeUnit

object Publisher {
    private const val TAG = "PUBLISHER:"

    /**
     * Sets continuation cluster to the appropriate state by publishing or deleting the clusters.
     * This occurs immediately then once every 24 hours.
     *
     * @param context Application's context.
     */
    fun publishPeriodically(context: Context) {
        periodicallyCallEngageServiceWorker(
            PERIODIC_WORKER_NAME_CONTINUATION,
            PUBLISH_TYPE_CONTINUATION,
            context
        )
    }

    /**
     * Sets continuation cluster and publish status to the appropriate state using WorkManager. More
     * detail on what the appropriate state is described in {@link
     * com.google.samples.quickstart.engagesdksamples.watch.publish.Publisher#publishPeriodically(Context)}.
     *
     * @param context Application's context
     */
    fun publishContinuationClusters(context: Context) {
        queueOneTimeEngageServiceWorker(
            WORKER_NAME_CONTINUATION,
            PUBLISH_TYPE_CONTINUATION,
            context
        )
    }

    private fun periodicallyCallEngageServiceWorker(
        workerName: String,
        publishType: String,
        context: Context
    ) {
        val data = Data.Builder().apply {
            putString(PUBLISH_TYPE, publishType)
        }.build()

        val workRequest =
            PeriodicWorkRequest.Builder(
                EngageServiceWorker::class.java,
                /* repeatInterval=         */ 24,
                /* repeatIntervalTimeUnit= */ TimeUnit.HOURS
            )
                .setInputData(data)
                .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                workerName,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )

    }

    private fun queueOneTimeEngageServiceWorker(
        workerName: String,
        publishType: String,
        context: Context
    ) {
        val data = Data.Builder().apply {
            putString(PUBLISH_TYPE, publishType)
        }.build()

        val workRequest = OneTimeWorkRequest.Builder(EngageServiceWorker::class.java)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(workerName, ExistingWorkPolicy.REPLACE, workRequest)
    }

    fun logPublishing(publishingException: AppEngageException) {
        val logMessage =
            when (publishingException.errorCode) {
                AppEngageErrorCode.SERVICE_NOT_FOUND ->
                    "SERVICE_NOT_FOUND - The service is not available on the given device"

                AppEngageErrorCode.SERVICE_CALL_EXECUTION_FAILURE ->
                    "SERVICE_CALL_EXECUTION_FAILURE - The task execution failed due to threading " +
                            "issues, can be retired"

                AppEngageErrorCode.SERVICE_NOT_AVAILABLE ->
                    "SERVICE_NOT_AVAILABLE - The service is available on the given device, but " +
                            "not available at the time of the call"

                AppEngageErrorCode.SERVICE_CALL_PERMISSION_DENIED ->
                    "SERVICE_CALL_PERMISSION_DENIED - The The caller is not allowed to make " +
                            "the service call"

                AppEngageErrorCode.SERVICE_CALL_INVALID_ARGUMENT ->
                    "SERVICE_CALL_INVALID_ARGUMENT - The request contains invalid data (e.g. " +
                            "more than allowed number of clusters"

                AppEngageErrorCode.SERVICE_CALL_INTERNAL ->
                    "SERVICE_CALL_INTERNAL - There is an error on the service side"

                AppEngageErrorCode.SERVICE_CALL_RESOURCE_EXHAUSTED ->
                    "SERVICE_CALL_RESOURCE_EXHAUSTED - The service call is made too frequently"

                else -> "An unknown error has occurred"
            }
        Log.d(TAG, logMessage)
    }

    fun isErrorRecoverable(publishingException: AppEngageException): Boolean {
        return when (publishingException.errorCode) {
            // Recoverable Error codes
            AppEngageErrorCode.SERVICE_CALL_EXECUTION_FAILURE,
            AppEngageErrorCode.SERVICE_CALL_INTERNAL,
            AppEngageErrorCode.SERVICE_CALL_RESOURCE_EXHAUSTED -> true
            // Non recoverable error codes
            AppEngageErrorCode.SERVICE_NOT_FOUND,
            AppEngageErrorCode.SERVICE_CALL_INVALID_ARGUMENT,
            AppEngageErrorCode.SERVICE_CALL_PERMISSION_DENIED,
            AppEngageErrorCode.SERVICE_NOT_AVAILABLE -> false

            else -> throw IllegalArgumentException(publishingException.localizedMessage)
        }
    }
}
