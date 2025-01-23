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

import android.app.Application
import android.content.Context
import android.media.tv.TvContract
import com.android.tv.reference.repository.VideoRepositoryFactory
import com.android.tv.reference.shared.datamodel.Video
import com.android.tv.reference.shared.datamodel.VideoType
import timber.log.Timber

/**
 * A services which synchronizes watch next videos state with your video repository.
 */
class EngageWatchNextService private constructor(private val context: Context) {
    private var watchNextVideos: MutableList<WatchNextVideo> = mutableListOf()

    /**
     * Returns all videos in the watch next queue
     */
    fun getAllWatchNextVideos(): List<WatchNextVideo> {
        return watchNextVideos.toList()
    }

    fun handleVideoPlaybackStateChange(
        videoId: String?,
        currentWatchPosition: Long,
        playerState: String,
    ) {
        if (videoId.isNullOrEmpty()) {
            Timber.e("Error.Invalid entry for Watch Next. videoId: $videoId")
            return
        }

        // Check for invalid player state.
        if ((playerState != WatchNextHelper.PLAY_STATE_PAUSED) and
            (playerState != WatchNextHelper.PLAY_STATE_ENDED)
        ) {
            Timber.e("Error.Invalid entry for Watch Next. Player state: $playerState")
            return
        }

        val video =
            VideoRepositoryFactory.getVideoRepository(context.applicationContext as Application)
                .getVideoById(videoId)

        when (video?.videoType) {
            VideoType.MOVIE -> {
                Timber.v("Add Movie to Watch Next : id = ${video.id}")
                handleWatchNextForMovie(
                    video = video,
                    playerState = playerState,
                    watchPosition = currentWatchPosition.toInt(),
                )
            }

            VideoType.EPISODE -> {
                Timber.v("Add Episode to Watch Next : id = ${video.id}")
                handleWatchNextForTvEpisode(
                    video = video,
                    currentWatchPosition = currentWatchPosition.toInt(),
                    playerState = playerState,
                )
            }

            VideoType.CLIP -> Timber.w(
                "NOT recommended to add Clips / Trailers /Short videos to Watch Next "
            )

            else -> Timber.e("Invalid category for Video Type: ${video?.videoType}")
        }
    }

    private fun handleWatchNextForMovie(video: Video, playerState: String, watchPosition: Int) {
        Timber.v("Adding/remove movie to Watch Next. Video Name: ${video.name}")

        when {
            // If movie has finished, remove from Watch Next channel.
            (playerState == WatchNextHelper.PLAY_STATE_ENDED) or
                video.isAfterEndCreditsPosition(watchPosition.toLong()) -> {
                WatchNextHelper.removeVideoFromWatchNext(context, video)
            }

            // Add or update unfinished movie to Watch Next channel.
            WatchNextHelper.hasVideoStarted(video.duration(), watchPosition) -> {
                insertOrUpdateIntoWatchNext(
                    video,
                    watchPosition,
                    TvContract.WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE
                )
            }

            else -> {
                Timber.w(
                    "Video not started yet. Can't add to WatchNext.watchPosition: %s, duration: %d",
                    watchPosition,
                    video.duration().toMillis()
                )
            }
        }
    }

    private fun handleWatchNextForTvEpisode(
        video: Video,
        currentWatchPosition: Int,
        playerState: String,
    ) {
        Timber.v("Adding/remove episode to Watch Next. Video Name: ${video.name}")

        when {
            // If episode has finished, remove from Watch Next channel.
            (playerState == WatchNextHelper.PLAY_STATE_ENDED) or
                video.isAfterEndCreditsPosition(currentWatchPosition.toLong()) -> {
                removeFromWatchNext(video)

                // Add next episode from TV series.
                VideoRepositoryFactory
                    .getVideoRepository(context.applicationContext as Application)
                    .getNextEpisodeInSeries(video)?.let {
                    insertOrUpdateIntoWatchNext(
                        it,
                        currentWatchPosition,
                        TvContract.WatchNextPrograms.WATCH_NEXT_TYPE_NEXT
                    )
                }
            }

            // Add or update unfinished episode to Watch Next channel.
            WatchNextHelper.hasVideoStarted(video.duration(), currentWatchPosition) -> {
                insertOrUpdateIntoWatchNext(
                    video,
                    currentWatchPosition,
                    TvContract.WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE
                )
            }

            else -> {
                Timber.w(
                    "Video not started yet. Can't add to WatchNext.watchPosition: %s, duration: %d",
                    currentWatchPosition,
                    video.duration().toMillis()
                )
            }
        }
    }

    /**
     * Updates or adds a video entity from the watch next list
     */
    private fun insertOrUpdateIntoWatchNext(
        video: Video,
        watchPosition: Int,
        watchNextType: Int
    ) {
        if (video.videoType != VideoType.MOVIE && video.videoType != VideoType.EPISODE) {
            throw IllegalArgumentException(
                "Watch Next is not supported for Video Type: ${video.videoType}"
            )
        }

        val existingVideoIndex = watchNextVideos.indexOfFirst { it.video.id == video.id }
        val watchNextVideo = WatchNextVideo(
            video = video,
            watchPosition = watchPosition,
            watchNextType = watchNextType,
        )
        if (existingVideoIndex == -1) {
            watchNextVideos.add(watchNextVideo)
        } else {
            watchNextVideos[existingVideoIndex] = watchNextVideo
        }
    }

    /**
     * Removes a video entity from the watch next list
     */
    private fun removeFromWatchNext(video: Video) {
        watchNextVideos = watchNextVideos.filter { it.video.id != video.id }.toMutableList()
    }

    companion object {
        @Volatile
        private var instance: EngageWatchNextService? = null

        fun getInstance(context: Context): EngageWatchNextService {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = EngageWatchNextService(context)
                    }
                }
            }
            return instance!!
        }

        data class WatchNextVideo(
            val video: Video,
            val watchPosition: Int,
            val watchNextType: Int
        )
    }
}
