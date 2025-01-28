/*
 * Copyright 2020 Google LLC
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
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.android.tv.reference.playback.PlaybackStateListener
import com.android.tv.reference.playback.VideoPlaybackState
import timber.log.Timber

/**
 * Notifies the video state and relevant metadata for adding/removing to Watch Next channel. Used
 * for adding un-finished/next content & removing finished content from Watch Next.
 */
class WatchNextPlaybackStateListener(private val context: Context) : PlaybackStateListener {
    override fun onChanged(state: VideoPlaybackState) {
        if (!(state is VideoPlaybackState.Pause || state is VideoPlaybackState.End)) {
            return
        }

        val video = when (state) {
            is VideoPlaybackState.Pause -> state.video
            is VideoPlaybackState.End -> state.video
            else -> throw IllegalStateException("Watch Next only operates in Pause or End states.")
        }
        val (playerState, position) = when (state) {
            is VideoPlaybackState.Pause ->
                WatchNextHelper.PLAY_STATE_PAUSED to state.position
            else -> WatchNextHelper.PLAY_STATE_ENDED to video.duration().toMillis()
        }

        // Set relevant data about playback state and video.
        val watchData = Data.Builder().apply {
            putString(Constants.PUBLISH_TYPE, Constants.PUBLISH_TYPE_CONTINUATION)
        }.build()

        EngageWatchNextService.getInstance(context).handleVideoPlaybackStateChange(
            videoId = video.id,
            currentWatchPosition = position,
            playerState = playerState,
        )

        // Run on a background thread to process playback states and do relevant operations for
        // Watch Next.
        Timber.d("Trigger WorkManager with updated watchData $watchData using Engage SDK")
        WorkManager.getInstance(context.applicationContext)
            .enqueue(
                OneTimeWorkRequest.Builder(EngageServiceWorker::class.java)
                    .setInputData(watchData)
                    .build()
            )

        // Set relevant data about playback state and video.
//        val watchData = Data.Builder().apply {
//            putString(WatchNextHelper.VIDEO_ID, video.id)
//            putLong(WatchNextHelper.CURRENT_POSITION, position)
//            putLong(WatchNextHelper.DURATION, video.duration().toMillis())
//            putString(WatchNextHelper.PLAYER_STATE, playerState)
//        }.build()

        // Run on a background thread to process playback states and do relevant operations for
        // Watch Next.
//        Timber.d("Trigger WorkManager with updated watchData $watchData using TvProvider")
//        WorkManager.getInstance(context.applicationContext).enqueue(
//            OneTimeWorkRequest.Builder(WatchNextWorker::class.java)
//                .setInputData(watchData)
//                .build()
//        )
    }
}
