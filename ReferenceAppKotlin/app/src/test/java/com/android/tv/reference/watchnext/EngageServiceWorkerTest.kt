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
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.android.tv.reference.watchnext.Constants.MAX_PUBLISHING_ATTEMPTS
import com.android.tv.reference.watchnext.Constants.PUBLISH_TYPE
import com.android.tv.reference.watchnext.Constants.PUBLISH_TYPE_CONTINUATION
import com.google.android.engage.service.AppEngageErrorCode
import com.google.android.engage.service.AppEngageException
import com.google.android.engage.service.AppEngagePublishClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.verify

class EngageServiceWorkerTest {

    @Before
    fun setUp() {
        mockedContext = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun publishContinuationFailsWhenServiceUnavailableTest() {
        val mockedAvailability = Tasks.forResult(false)
        Mockito.`when`(mockedClient.isServiceAvailable()).thenReturn(mockedAvailability)

        val mockedWorker =
            createEngageServiceWorker(mockedContext, PUBLISH_TYPE_CONTINUATION, runAttempts = 0)
        runBlocking {
            val resultFail = mockedWorker.doWork()
            assertEquals(Result.failure(), resultFail)
            verify(mockedClient, Mockito.never()).publishContinuationCluster(any())
            verify(mockedClient, Mockito.never()).deleteContinuationCluster()
            verify(mockedClient, Mockito.never()).updatePublishStatus(any())
        }
    }

    @Test
    fun publishContinuationFailsOnUnrecoverableExceptions() {
        val unrecoverableErrorCodes =
            listOf(
                AppEngageErrorCode.SERVICE_NOT_FOUND,
                AppEngageErrorCode.SERVICE_NOT_AVAILABLE,
                AppEngageErrorCode.SERVICE_CALL_INVALID_ARGUMENT,
                AppEngageErrorCode.SERVICE_CALL_PERMISSION_DENIED,
            )
        for (errorCode in unrecoverableErrorCodes) {
            verifyPublishContinuationWithErrorReturnsResultHelper(errorCode, Result.failure())
        }
    }

    @Test
    fun publishContinuationRetryOnRecoverableExceptions() {
        val unrecoverableErrorCodes =
            listOf(
                AppEngageErrorCode.SERVICE_CALL_RESOURCE_EXHAUSTED,
                AppEngageErrorCode.SERVICE_CALL_EXECUTION_FAILURE,
                AppEngageErrorCode.SERVICE_CALL_INTERNAL
            )
        for (errorCode in unrecoverableErrorCodes) {
            verifyPublishContinuationWithErrorReturnsResultHelper(errorCode, Result.retry())
        }
    }

    @Test
    fun attemptToPublishContinuationAtMaxAttemptsTest() {
        val mockedAvailability = Tasks.forResult(true)
        Mockito.`when`(mockedClient.isServiceAvailable()).thenReturn(mockedAvailability)

        val resultingTask: Task<Void> = Tasks.forResult(null)

        Mockito.`when`(mockedClient.publishContinuationCluster(any())).thenReturn(resultingTask)
        Mockito.`when`(mockedClient.updatePublishStatus(any())).thenReturn(resultingTask)
        // At least one movie is in progress

        val worker =
            createEngageServiceWorker(
                mockedContext,
                PUBLISH_TYPE_CONTINUATION,
                MAX_PUBLISHING_ATTEMPTS
            )

        runBlocking {
            worker.doWork()
            verify { mockedClient.publishContinuationCluster(any()) }
        }
    }

    @Test
    fun doNotAttemptToPublishOrDeleteContinuationPastMaxAttemptsTest() {
        val mockedAvailability = Tasks.forResult(true)
        Mockito.`when`(mockedClient.isServiceAvailable()).thenReturn(mockedAvailability)

        val worker =
            createEngageServiceWorker(
                mockedContext,
                PUBLISH_TYPE_CONTINUATION,
                MAX_PUBLISHING_ATTEMPTS + 1
            )

        runBlocking {
            worker.doWork()
            verify(mockedClient, Mockito.never()).publishContinuationCluster(any())
            verify(mockedClient, Mockito.never()).deleteContinuationCluster()
        }
    }

    private fun verifyPublishContinuationWithErrorReturnsResultHelper(
        errorCode: Int,
        expectedResult: Result
    ) {
        val mockedAvailability = Tasks.forResult(true)
        Mockito.`when`(mockedClient.isServiceAvailable).thenReturn(mockedAvailability)

        val resultException = AppEngageException(errorCode)
        val resultingTask: Task<Void> = Tasks.forException(resultException)

        Mockito.`when`(mockedClient.publishContinuationCluster(any())).thenReturn(resultingTask)
        Mockito.`when`(mockedClient.updatePublishStatus(any())).thenReturn(Tasks.forResult(null))
        // At least one movie is in progress

        val worker =
            createEngageServiceWorker(mockedContext, PUBLISH_TYPE_CONTINUATION, runAttempts = 0)

        runBlocking {
            val actualResult = worker.doWork()
            assertEquals(expectedResult, actualResult)
        }
    }

    private fun createEngageServiceWorker(
        context: Context,
        publishClusterType: String,
        runAttempts: Int
    ): EngageServiceWorker {
        val workerData = workDataOf(PUBLISH_TYPE to publishClusterType)
        return TestListenableWorkerBuilder<EngageServiceWorker>(
            context = context,
            inputData = workerData,
            runAttemptCount = runAttempts
        )
            .setWorkerFactory(EngageServiceWorkerFactory())
            .build()
    }

    private class EngageServiceWorkerFactory() : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            return EngageServiceWorker(appContext, workerParameters, mockedClient)
        }
    }

    companion object {
        private lateinit var mockedContext: Context
        private val mockedClient: AppEngagePublishClient =
            Mockito.mock(AppEngagePublishClient::class.java)
    }
}
