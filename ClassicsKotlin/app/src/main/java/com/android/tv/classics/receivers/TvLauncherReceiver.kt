/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tv.classics.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.tvprovider.media.tv.TvContractCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.android.tv.classics.utils.TvLauncherUtils
import com.android.tv.classics.models.TvMediaDatabase
import com.android.tv.classics.models.TvMediaMetadata
import com.android.tv.classics.workers.TvMediaSynchronizer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/*
 * This [BroadcastReceiver] is invoked when the home screen launcher sends an explicit broadcast
 * to our app. This happens when the user interacts with our programs or after our app is installed
 * and the launcher is ready for our default channel to be created.
 *
 * The app install broadcast is only fired when the app is installed from the Play Store. To test
 * this during development, you can run:
 * $ adb shell am broadcast -a android.media.tv.action.INITIALIZE_PROGRAMS
 *      -n com.android.tv.classics/.receivers.TvLauncherReceiver
 */
class TvLauncherReceiver : BroadcastReceiver() {

    /** Execute the broadcast listener in a co-routine */
    override fun onReceive(context: Context, intent: Intent) { GlobalScope.launch {
        Log.d(TAG, "onReceive ${intent.action}")
        val database = TvMediaDatabase.getInstance(context)

        when (intent.action) {
            // Initializes channel
            TvContractCompat.ACTION_INITIALIZE_PROGRAMS -> {
                Log.d(TAG, "Handling INITIALIZE_PROGRAMS broadcast")
                // Synchronizes all program and channel data
                WorkManager.getInstance(context).enqueue(
                        OneTimeWorkRequestBuilder<TvMediaSynchronizer>().build())
            }

            // One of our programs has been added to watch next system channel
            TvContractCompat.ACTION_PREVIEW_PROGRAM_ADDED_TO_WATCH_NEXT -> {
                // Get the ID of the program from the intent extras
                val programId =
                        intent.extras?.getLong(TvContractCompat.EXTRA_PREVIEW_PROGRAM_ID)
                Log.d(TAG, "User added program $programId to watch next")

                // Retrieve the metadata item that matches this ID and update state in our database
                updateMetadata(context, database, programId) { it.apply { watchNext = true }}
            }

            // One of our programs has been removed from the watch next row
            TvContractCompat.ACTION_WATCH_NEXT_PROGRAM_BROWSABLE_DISABLED -> {
                // Get the ID of the program disabled from the intent extras
                val programId =
                        intent.extras?.getLong(TvContractCompat.EXTRA_PREVIEW_PROGRAM_ID)
                Log.d(TAG, "User removed program $programId from watch next")

                // Retrieve the metadata item that matches this ID and update state in our database
                updateMetadata(context, database, programId) { it.apply { watchNext = false }}
            }

            // One of our programs has been removed from a channel in the home screen
            TvContractCompat.ACTION_PREVIEW_PROGRAM_BROWSABLE_DISABLED -> {
                // Get the ID of the program from the intent extras
                val programId =
                        intent.extras?.getLong(TvContractCompat.EXTRA_PREVIEW_PROGRAM_ID)
                Log.d(TAG, "User removed program $programId from channel")

                // Retrieve the metadata item that matches this ID and mark it as hidden
                updateMetadata(context, database, programId) { it.apply { hidden = true }}
            }
        }
    } }

    /** Helper function used to update the media metadata for a given program ID */
    private fun updateMetadata(
            context: Context,
            db: TvMediaDatabase,
            programId: Long?,
            action: (metadata: TvMediaMetadata) -> TvMediaMetadata
    ) = getMediaItemFromProgramId(context, db, programId)?.let { db.metadata().update(action(it)) }

    /**
     * Helper method used to retrieve a metadata item object given its program ID. Restricted API
     * is being used to compared programs based on their ID.
     */
    private fun getMediaItemFromProgramId(
            context: Context, db: TvMediaDatabase, programId: Long?): TvMediaMetadata? {

        // Early exit: program ID is null
        if (programId == null) return null

        // Retrieve the program that matches this ID
        val previewProgramItem =
                TvLauncherUtils.getPreviewPrograms(context).find { it.id == programId }
        // Retrieve the corresponding metadata item and return
        if(previewProgramItem != null)
            return previewProgramItem.let { db.metadata().findById(it.contentId) }
        // Retrieve the corresponding metadata item and return
        val watchNextProgramItem = TvLauncherUtils.getWatchNextPrograms(context).find { it.id == programId }
        return watchNextProgramItem?.let { db.metadata().findById(it.contentId) }
    }

    companion object {
        private val TAG = TvLauncherReceiver::class.java.simpleName
    }
}