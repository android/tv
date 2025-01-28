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

import android.net.Uri
import androidx.tvprovider.media.tv.TvContractCompat.WatchNextPrograms
import com.android.tv.reference.shared.datamodel.Video
import com.android.tv.reference.shared.datamodel.VideoType
import com.android.tv.reference.watchnext.EngageWatchNextService.Companion.WatchNextVideo
import com.google.android.engage.video.datamodel.MovieEntity
import com.google.android.engage.video.datamodel.TvEpisodeEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EngageEntityConverterTest {
    @Test
    fun shouldConvertVideoToTvEpisodeEntity() {
        val entity = VideoToEngageEntityConverter.convertVideo(
            WatchNextVideo(
                video = episodeVideo,
                watchPosition = 100,
                watchNextType = WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE,
            )
        ) as TvEpisodeEntity

        assertEquals(entity.entityId, VIDEO_ID)
        assertEquals(entity.name, VIDEO_NAME)
        assertEquals(entity.infoPageUri.toString(), URI.toString())
        assertEquals(entity.playBackUri.toString(), VIDEO_URI.toString())
        assertEquals(entity.posterImages.size, 1)
        assertEquals(entity.posterImages[0].imageUri.toString(), THUMBNAIL_URI.toString())
        assertEquals(entity.durationMillis, EPISODE_DURATION)
        assertEquals(entity.episodeDisplayNumber, EPISODE_NUMBER)
        assertEquals(entity.seasonNumber, SEASON_NUMBER)
    }

    @Test
    fun shouldConvertVideoToMovieEntity() {
        val entity = VideoToEngageEntityConverter.convertVideo(
            WatchNextVideo(
                video = movieVideo,
                watchPosition = 100,
                watchNextType = WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE,
            )
        ) as MovieEntity

        assertEquals(entity.entityId, VIDEO_ID)
        assertEquals(entity.name, VIDEO_NAME)
        assertEquals(entity.infoPageUri.toString(), URI.toString())
        assertEquals(entity.playBackUri.toString(), VIDEO_URI.toString())
        assertEquals(entity.posterImages.size, 1)
        assertEquals(entity.posterImages[0].imageUri.toString(), THUMBNAIL_URI.toString())
        assertEquals(entity.durationMillis, MOVIE_DURATION)
    }

    companion object {
        private const val VIDEO_ID = "video-id"
        private const val VIDEO_NAME = "video-name"
        private const val VIDEO_DESCRIPTION = "video-description"
        private val URI = Uri.parse("https://google.com/uri")
        private val VIDEO_URI = Uri.parse("https://google.com/video.mp4")
        private val THUMBNAIL_URI = Uri.parse("https://google.com/thumbnail.mp4")
        private const val VIDEO_CATEGORY = "video-category"

        private const val MOVIE_DURATION = "PT02H35M"

        private const val EPISODE_DURATION = "PT00H45M"
        private const val EPISODE_NUMBER = "02"
        private const val SEASON_NUMBER = "01"

        private val episodeVideo = Video(
            id = VIDEO_ID,
            name = VIDEO_NAME,
            description = VIDEO_DESCRIPTION,
            uri = URI.toString(),
            videoUri = VIDEO_URI.toString(),
            thumbnailUri = THUMBNAIL_URI.toString(),
            backgroundImageUri = THUMBNAIL_URI.toString(),
            category = VIDEO_CATEGORY,
            videoType = VideoType.EPISODE,
            duration = EPISODE_DURATION,
            episodeNumber = EPISODE_NUMBER,
            seasonNumber = SEASON_NUMBER,
        )

        private val movieVideo = Video(
            id = VIDEO_ID,
            name = VIDEO_NAME,
            description = VIDEO_DESCRIPTION,
            uri = URI.toString(),
            videoUri = VIDEO_URI.toString(),
            thumbnailUri = THUMBNAIL_URI.toString(),
            backgroundImageUri = THUMBNAIL_URI.toString(),
            category = VIDEO_CATEGORY,
            videoType = VideoType.MOVIE,
            duration = MOVIE_DURATION,
        )
    }
}
