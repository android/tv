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

import android.media.tv.TvContentRating
import android.net.Uri
import androidx.tvprovider.media.tv.TvContractCompat.WatchNextPrograms
import com.android.tv.reference.shared.datamodel.Video
import com.android.tv.reference.shared.datamodel.VideoType
import com.android.tv.reference.watchnext.EngageWatchNextService.Companion.WatchNextVideo
import com.google.android.engage.common.datamodel.ContentAvailability
import com.google.android.engage.common.datamodel.Image
import com.google.android.engage.video.datamodel.MovieEntity
import com.google.android.engage.video.datamodel.RatingSystem
import com.google.android.engage.video.datamodel.TvEpisodeEntity
import com.google.android.engage.video.datamodel.VideoEntity
import com.google.android.engage.video.datamodel.WatchNextType

object VideoToEngageEntityConverter {

    fun convertVideo(watchNextVideo: WatchNextVideo): VideoEntity {
        val video = watchNextVideo.video
        val watchPosition = watchNextVideo.watchPosition
        val watchNextType = watchNextVideo.watchNextType

        return when (video.videoType) {
            VideoType.MOVIE -> convertMovieEntity(
                video = video,
                watchPosition = watchPosition,
                watchNextType = watchNextType
            )

            VideoType.EPISODE -> convertTvEpisodeEntity(
                video = video,
                watchPosition = watchPosition,
                watchNextType = watchNextType
            )

            else -> {
                throw IllegalArgumentException(
                    "Conversion is not supported for Video Type: ${video.videoType}"
                )
            }
        }
    }

    private fun convertTvEpisodeEntity(
        video: Video,
        watchPosition: Int,
        watchNextType: Int
    ): TvEpisodeEntity {
        return TvEpisodeEntity
            .Builder()
            .setWatchNextType(convertToEngageWatchNextType(watchNextType))
            .setLastPlayBackPositionTimeMillis(watchPosition.toLong())
            .setLastEngagementTimeMillis(System.currentTimeMillis())
            .setName(video.name)
            .setDurationMillis(convertDurationStringToMillis(video.duration))
            .addPosterImage(
                Image
                    .Builder()
                    .setImageWidthInPixel(200)
                    .setImageHeightInPixel(200)
                    .setImageUri(Uri.parse(video.thumbnailUri))
                    .build()
            )
            .setAvailability(ContentAvailability.AVAILABILITY_AVAILABLE)
            .setAirDateEpochMillis(System.currentTimeMillis() - FIVE_YEARS_MILLIS)
            .setPlayBackUri(Uri.parse(video.uri))
            .setEntityId(video.id)
            .setSeasonNumber(video.seasonNumber)
            .setSeasonTitle("${video.category} Season ${video.seasonNumber}")
            .addContentRating(
                RatingSystem
                    .Builder()
                    .setAgencyName("Agency")
                    .setRating(
                        TvContentRating
                            .createRating("com.android.tv", "US_TV", "US_TV_PG")
                            .flattenToString()
                    )
                    .build()
            )
            // TODO: check why this is needed
            .addContentRating("bla")
            .setEpisodeDisplayNumber(video.name)
            .build()
    }

    private fun convertMovieEntity(
        video: Video,
        watchPosition: Int,
        watchNextType: Int
    ): MovieEntity {
        return MovieEntity
            .Builder()
            .setWatchNextType(convertToEngageWatchNextType(watchNextType))
            .setLastPlayBackPositionTimeMillis(watchPosition.toLong())
            .setLastEngagementTimeMillis(System.currentTimeMillis())
            .setName(video.name)
            .setDurationMillis(convertDurationStringToMillis(video.duration))
            .addPosterImage(
                Image
                    .Builder()
                    .setImageWidthInPixel(200)
                    .setImageHeightInPixel(200)
                    .setImageUri(Uri.parse(video.thumbnailUri))
                    .build()
            )
            .setAvailability(ContentAvailability.AVAILABILITY_AVAILABLE)
            .addContentRating(
                RatingSystem
                    .Builder()
                    .setAgencyName("Agency")
                    .setRating("PG-13")
                    .build()
            )
            .setPlayBackUri(Uri.parse(video.uri))
            .setEntityId(video.id)
            .build()
    }

    private fun convertToEngageWatchNextType(watchNextType: Int): Int {
        return when (watchNextType) {
            WatchNextPrograms.WATCH_NEXT_TYPE_NEXT -> WatchNextType.TYPE_NEXT
            WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE -> WatchNextType.TYPE_CONTINUE
            WatchNextPrograms.WATCH_NEXT_TYPE_NEW -> WatchNextType.TYPE_NEW
            else -> WatchNextType.TYPE_UNKNOWN
        }
    }

    // This method is specific to "PT00H25M" format of duration
    private fun convertDurationStringToMillis(duration: String): Long {
        val time = duration.split("PT")[1]
        val hours = time.split("H")[0].toLong()
        val minutes = time.split("H")[1].split("M")[0].toLong()
        return (hours * 60 + minutes) * 60 * 1000
    }

    private const val ONE_DAY_MILLIS = (86400 * 1000).toLong()
    private const val ONE_YEAR_MILLIS = ONE_DAY_MILLIS * 365
    private const val FIVE_YEARS_MILLIS = ONE_YEAR_MILLIS * 5
}
