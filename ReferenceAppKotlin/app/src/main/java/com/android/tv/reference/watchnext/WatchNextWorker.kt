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

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.tv.reference.repository.VideoRepositoryFactory
import com.android.tv.reference.shared.datamodel.VideoType
import timber.log.Timber

/**
 * Worker triggered by WorkManager to add/update/remove video to Watch Next channel.
 *
 * <code>
 * WorkManager.getInstance(context).enqueue(OneTimeWorkRequest.Builder(WatchNextWorker::class.java).build())
 * </code>
 */
class WatchNextWorker(private val context: Context, params: WorkerParameters) :
    Worker(context, params) {

    /**
     * Worker thread to add/update/remove content from Watch Next channel.
     * Events triggered from player state change events &
     * playback lifecycle events (onPause) are consumed here.
     */
    override fun doWork(): Result {

        // Step 1 : get the video information from the "inputData".
        val videoId = inputData.getString(WatchNextHelper.VIDEO_ID)
        val watchPosition =
            inputData.getLong(WatchNextHelper.CURRENT_POSITION, /* defaultValue= */ 0)
        val state = inputData.getString(WatchNextHelper.PLAYER_STATE)
        Timber.v("Work Manager called watch id $videoId , watchTime $watchPosition")

        // Step 2 : Check for invalid inputData.
        // If videoId is invalid, abort worker and return.
        if (videoId.isNullOrEmpty()) {
            Timber.e("Error.Invalid entry for Watch Next. videoid: $videoId")
            return Result.failure()
        }
        // Check for invalid player state.
        if ((state != WatchNextHelper.PLAY_STATE_PAUSED) and
            (state != WatchNextHelper.PLAY_STATE_ENDED)
        ) {
            Timber.e("Error.Invalid entry for Watch Next. Player state: $state")
            return Result.failure()
        }

        // Step 3: Get video object from videoId to be added to Watch Next.
        val video =
            VideoRepositoryFactory.getVideoRepository(context.applicationContext as Application)
                .getVideoById(videoId)

        Timber.v("Retrieved video from repository with id %s, %s", videoId, video)

        // Step 4 : Handle Watch Next for different types of content.
        when (video?.videoType) {
            VideoType.MOVIE -> {
                Timber.v("Add Movie to Watch Next : id = ${video.id}")
                WatchNextHelper.handleWatchNextForMovie(video, watchPosition.toInt(), state, context)
            }
            VideoType.EPISODE -> {
                Timber.v("Add Episode to Watch Next : id = ${video.id}")
                WatchNextHelper.handleWatchNextForEpisode(
                    video, watchPosition.toInt(), state,
                    VideoRepositoryFactory.getVideoRepository(
                        context.applicationContext as Application), context)
            }
            VideoType.CLIP -> Timber.w(
                "NOT recommended to add Clips / Trailers /Short videos to Watch Next "
            )
            else -> Timber.e("Invalid category for Video Type: ${video?.videoType}")
        }

        Timber.v("WatchNextWorker finished")
        return Result.success()
    }

}
