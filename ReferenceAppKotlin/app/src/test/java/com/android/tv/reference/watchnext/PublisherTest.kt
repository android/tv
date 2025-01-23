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
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.android.tv.reference.watchnext.Constants.PERIODIC_WORKER_NAME_CONTINUATION
import com.android.tv.reference.watchnext.Constants.WORKER_NAME_CONTINUATION
import com.android.tv.reference.watchnext.Publisher.publishContinuationClusters
import com.android.tv.reference.watchnext.Publisher.publishPeriodically
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PublisherTest {

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val config =
            Configuration.Builder()
                .setExecutor(SynchronousExecutor())
                .setTaskExecutor(SynchronousExecutor())
                .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        workManager = WorkManager.getInstance(context)
    }

    @Test
    fun publishContinuationStartsWorkTest() {
        publishContinuationClusters(context)
        assertSetStateWorkIsQueuedHelper(WORKER_NAME_CONTINUATION)
    }

    @Test
    fun publishPeriodicWorkersTest() {
        publishPeriodically(context)
        assertSetStateWorkIsQueuedHelper(PERIODIC_WORKER_NAME_CONTINUATION)
    }

    private fun assertSetStateWorkIsQueuedHelper(workName: String) {
        val workInfo = workManager.getWorkInfosForUniqueWork(workName).get()
        // This should always be true since publishing work is unique and non-chainable
        assertTrue(workInfo.size == 0 || workInfo.size == 1)

        // Work info will only be present if the work was triggered.
        val hasStarted = workInfo.size == 1
        assertTrue(hasStarted)
    }

    private companion object {
        lateinit var workManager: WorkManager
        lateinit var context: Context
    }
}
