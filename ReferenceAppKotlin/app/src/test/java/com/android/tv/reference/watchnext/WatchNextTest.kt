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

import android.app.Application
import android.content.ContentProvider
import android.content.ContentProviderOperation
import android.content.ContentProviderResult
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.media.tv.TvContract
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.tvprovider.media.tv.TvContractCompat
import androidx.tvprovider.media.tv.WatchNextProgram
import com.android.tv.reference.R
import com.android.tv.reference.repository.FakeVideoRepository
import com.android.tv.reference.repository.FakeVideoRepository.Companion.TEST_VIDEO_ID
import com.android.tv.reference.repository.FakeVideoRepository.Companion.TEST_VIDEO_NAME
import com.android.tv.reference.repository.FakeVideoRepository.Companion.TEST_VIDEO_PLAYBACK_CREDIT_SCENE_POSITION_MILLIS
import com.android.tv.reference.repository.FakeVideoRepository.Companion.TEST_VIDEO_PLAYBACK_POSITION_MILLIS
import com.android.tv.reference.repository.FakeVideoRepository.Companion.episodes
import com.android.tv.reference.repository.FakeVideoRepository.Companion.video_movie
import com.android.tv.reference.watchnext.WatchNextHelper.PLAY_STATE_PAUSED
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import java.time.Duration

/**
 * Test cases to test adding / updating / removing content
 * from Watch Next channel in Home Screen.
 */
@RunWith(AndroidJUnit4::class)
class WatchNextTest {

    private lateinit var fakeInMemoryTvProvider: FakeInMemoryTvProvider

    companion object {
        private const val DELETE_FAILED = -1
    }

    @Before
    fun setUp() {
        // Register fakeInMemoryTvProvider to use as a mock for TvContentProvider
        // for all robolectric tests.
        fakeInMemoryTvProvider = Robolectric.setupContentProvider(
            FakeInMemoryTvProvider::class.java,
            TvContractCompat.AUTHORITY
        )
    }

    /**
     * Test adding unfinished movie to Watch Next.
     */
    @Test
    fun insertMovieToWatchNext() {

        // Actual : Call the method.
        WatchNextHelper.handleWatchNextForMovie(
            video_movie, TEST_VIDEO_PLAYBACK_POSITION_MILLIS, PLAY_STATE_PAUSED,
            ApplicationProvider.getApplicationContext()
        )

        // Expected : verify if program was added to Watch Next.
        val watchList =
            WatchNextHelper.getWatchNextPrograms(ApplicationProvider.getApplicationContext())
        val program =
            watchList.firstOrNull { it.internalProviderId == TEST_VIDEO_ID }

        // Verify the correct metadata was set.
        assertThat(program).isNotNull()
        assertThat(program?.internalProviderId).isEqualTo(TEST_VIDEO_ID)
        assertThat(program?.title).isEqualTo(TEST_VIDEO_NAME)
        assertThat(program?.lastPlaybackPositionMillis).isEqualTo(
            TEST_VIDEO_PLAYBACK_POSITION_MILLIS)
    }

    /**
     * Test adding a finished movie to Watch Next.
     */
    @Test
    fun insertFinishedMovieToWatchNext() {

        // Actual : Call the method.
        WatchNextHelper.handleWatchNextForMovie(
            video_movie, TEST_VIDEO_PLAYBACK_CREDIT_SCENE_POSITION_MILLIS, PLAY_STATE_PAUSED,
            ApplicationProvider.getApplicationContext()
        )

        // Expected : verify if program was added to Watch Next.
        val watchList =
            WatchNextHelper.getWatchNextPrograms(ApplicationProvider.getApplicationContext())

        assertThat(watchList).hasSize(0)
    }

    /**
     * Test adding unfinished episode to Watch Next.
     */
    @Test
    fun insertUnfinishedEpisodesToWatchNext() {

        val unfinishedEpisode = episodes[0]
        // Actual : Call the method.
        WatchNextHelper.handleWatchNextForEpisode(
            unfinishedEpisode, TEST_VIDEO_PLAYBACK_POSITION_MILLIS, PLAY_STATE_PAUSED,
            FakeVideoRepository(ApplicationProvider.getApplicationContext() as Application),
            ApplicationProvider.getApplicationContext())

        // Expected : verify if program was added to Watch Next.
        val watchList =
            WatchNextHelper.getWatchNextPrograms(ApplicationProvider.getApplicationContext())

        assertThat(watchList).hasSize(1)
        val program = watchList[0]

        // Verify the correct metadata was set.
        assertThat(program).isNotNull()
        assertThat(program.internalProviderId).isEqualTo(unfinishedEpisode.id)
        assertThat(program.title).isEqualTo(unfinishedEpisode.category)
        assertThat(program.episodeNumber).isEqualTo(unfinishedEpisode.episodeNumber)
        assertThat(program.seasonNumber).isEqualTo(unfinishedEpisode.seasonNumber)
        assertThat(program.episodeTitle).isEqualTo(unfinishedEpisode.name)
        assertThat(program.seasonTitle).isEqualTo(
            (ApplicationProvider.getApplicationContext() as Context).getString(
                R.string.season, unfinishedEpisode.category, unfinishedEpisode.seasonNumber))
        assertThat(program.lastPlaybackPositionMillis).isEqualTo(
            TEST_VIDEO_PLAYBACK_POSITION_MILLIS)
    }

    /**
     * Test adding multiple episodes to Watch Next.
     */
    @Test
    fun insertMultipleEpisodeToWatchNext() {

        val firstWatchedEpisode = episodes[0]
        val secondWatchedEpisode = episodes[1]
        // Actual : Call the method.
        WatchNextHelper.handleWatchNextForEpisode(
            firstWatchedEpisode, TEST_VIDEO_PLAYBACK_POSITION_MILLIS, PLAY_STATE_PAUSED,
            FakeVideoRepository(ApplicationProvider.getApplicationContext() as Application),
            ApplicationProvider.getApplicationContext())

        WatchNextHelper.handleWatchNextForEpisode(
            secondWatchedEpisode, TEST_VIDEO_PLAYBACK_POSITION_MILLIS, PLAY_STATE_PAUSED,
            FakeVideoRepository(ApplicationProvider.getApplicationContext() as Application),
            ApplicationProvider.getApplicationContext())

        // Expected : verify if program was added to Watch Next.
        val watchList =
            WatchNextHelper.getWatchNextPrograms(ApplicationProvider.getApplicationContext())

        assertThat(watchList).hasSize(1)

        val program = watchList[0]

        // Verify the correct metadata was set.
        assertThat(program).isNotNull()
        assertThat(program.internalProviderId).isEqualTo(secondWatchedEpisode.id)
        assertThat(program.title).isEqualTo(secondWatchedEpisode.category)
        assertThat(program.episodeNumber).isEqualTo(secondWatchedEpisode.episodeNumber)
        assertThat(program.seasonNumber).isEqualTo(secondWatchedEpisode.seasonNumber)
        assertThat(program.episodeTitle).isEqualTo(secondWatchedEpisode.name)
        assertThat(program.seasonTitle).isEqualTo(
            (ApplicationProvider.getApplicationContext() as Context).getString(
                R.string.season, secondWatchedEpisode.category, secondWatchedEpisode.seasonNumber))
        assertThat(program.lastPlaybackPositionMillis).isEqualTo(
            TEST_VIDEO_PLAYBACK_POSITION_MILLIS)
    }

    /**
     * Test adding multiple episodes to Watch Next.
     */
    @Test
    fun insertMultipleEpisodesInReverseOrderToWatchNext() {

        val firstWatchedEpisode = episodes[1]
        val secondWatchedEpisode = episodes[0]
        // Actual : Call the method.
        WatchNextHelper.handleWatchNextForEpisode(
            firstWatchedEpisode, TEST_VIDEO_PLAYBACK_POSITION_MILLIS, PLAY_STATE_PAUSED,
            FakeVideoRepository(ApplicationProvider.getApplicationContext() as Application),
            ApplicationProvider.getApplicationContext())

        WatchNextHelper.handleWatchNextForEpisode(
            secondWatchedEpisode, TEST_VIDEO_PLAYBACK_POSITION_MILLIS, PLAY_STATE_PAUSED,
            FakeVideoRepository(ApplicationProvider.getApplicationContext() as Application),
            ApplicationProvider.getApplicationContext())

        // Expected : verify if program was added to Watch Next.
        val watchList =
            WatchNextHelper.getWatchNextPrograms(ApplicationProvider.getApplicationContext())

        assertThat(watchList).hasSize(1)

        val program = watchList[0]

        // Verify the correct metadata was set.
        assertThat(program).isNotNull()
        assertThat(program.internalProviderId).isEqualTo(secondWatchedEpisode.id)
        assertThat(program.title).isEqualTo(secondWatchedEpisode.category)
        assertThat(program.episodeNumber).isEqualTo(secondWatchedEpisode.episodeNumber)
        assertThat(program.seasonNumber).isEqualTo(secondWatchedEpisode.seasonNumber)
        assertThat(program.episodeTitle).isEqualTo(secondWatchedEpisode.name)
        assertThat(program.seasonTitle).isEqualTo(
            (ApplicationProvider.getApplicationContext() as Context).getString(
                R.string.season, secondWatchedEpisode.category, secondWatchedEpisode.seasonNumber))
        assertThat(program.lastPlaybackPositionMillis).isEqualTo(
            TEST_VIDEO_PLAYBACK_POSITION_MILLIS)
    }

    /**
     * Test adding next episode to Watch Next.
     */
    @Test
    fun insertNextEpisodeToWatchNext() {

        val currentEpisode = episodes[0]
        val nextEpisode = episodes[1]

        // Actual : Call the method.
        WatchNextHelper.handleWatchNextForEpisode(
            currentEpisode, TEST_VIDEO_PLAYBACK_CREDIT_SCENE_POSITION_MILLIS, PLAY_STATE_PAUSED,
            FakeVideoRepository(ApplicationProvider.getApplicationContext() as Application),
            ApplicationProvider.getApplicationContext())

        // Expected : verify if program was added to Watch Next.
        val watchList =
            WatchNextHelper.getWatchNextPrograms(ApplicationProvider.getApplicationContext())

        assertThat(watchList).hasSize(1)

        val program = watchList[0]

        // Verify the correct metadata was set.
        assertThat(program).isNotNull()
        assertThat(program.internalProviderId).isEqualTo(nextEpisode.id)
        assertThat(program.title).isEqualTo(nextEpisode.category)
        assertThat(program.episodeNumber).isEqualTo(nextEpisode.episodeNumber)
        assertThat(program.seasonNumber).isEqualTo(nextEpisode.seasonNumber)
        assertThat(program.episodeTitle).isEqualTo(nextEpisode.name)
        assertThat(program.seasonTitle).isEqualTo(
            (ApplicationProvider.getApplicationContext() as Context).getString(
                R.string.season, nextEpisode.category, nextEpisode.seasonNumber))
        assertThat(program.lastPlaybackPositionMillis).isEqualTo(0)
    }

    /**
     * Test adding episode in next season to Watch Next
     */
    @Test
    fun insertEpisodeFromNextSeasonToWatchNext() {

        val currentEpisode = episodes[1]
        val nextEpisode = episodes[2]

        // Actual : Call the method.
        WatchNextHelper.handleWatchNextForEpisode(
            currentEpisode, TEST_VIDEO_PLAYBACK_CREDIT_SCENE_POSITION_MILLIS, PLAY_STATE_PAUSED,
            FakeVideoRepository(ApplicationProvider.getApplicationContext() as Application),
            ApplicationProvider.getApplicationContext())

        // Expected : verify if program was added to Watch Next.
        val watchList =
            WatchNextHelper.getWatchNextPrograms(ApplicationProvider.getApplicationContext())

        assertThat(watchList).hasSize(1)

        val program = watchList[0]

        // Verify the correct metadata was set.
        assertThat(program).isNotNull()
        assertThat(program.internalProviderId).isEqualTo(nextEpisode.id)
        assertThat(program.title).isEqualTo(nextEpisode.category)
        assertThat(program.episodeNumber).isEqualTo(nextEpisode.episodeNumber)
        assertThat(program.seasonNumber).isEqualTo(nextEpisode.seasonNumber)
        assertThat(program.episodeTitle).isEqualTo(nextEpisode.name)
        assertThat(program.seasonTitle).isEqualTo(
            (ApplicationProvider.getApplicationContext() as Context).getString(
                R.string.season, nextEpisode.category, nextEpisode.seasonNumber))
        assertThat(program.lastPlaybackPositionMillis).isEqualTo(0)
    }

    /**
     * Remove finished video from Watch Next channel.
     */
    @Test
    fun removeVideoFromWatchNext() {

        WatchNextHelper.insertOrUpdateVideoToWatchNext(
            video_movie,
            TEST_VIDEO_PLAYBACK_POSITION_MILLIS,
            TvContract.WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE,
            ApplicationProvider.getApplicationContext()
        )

        // Actual : Call the method for delete.
        val deletedId = WatchNextHelper.removeVideoFromWatchNext(
            ApplicationProvider.getApplicationContext(), video_movie
        )

        // Expected : (deletedId is returned if delete was successful,
        // null is returned if delete fails in actual code.
        assertThat(deletedId).isNotNull()
        // -1 is returned if fake content provider fails to delete.
        assertThat(deletedId).isNotEqualTo(DELETE_FAILED)
    }

    /**
     * Update video metadata in Watch Next channel.
     * Example: Update playback time.
     */
    @Test
    fun updateVideoInWatchNext() {

        // Actual : Call the method.
        // Add the entry to Watch Next row.
        WatchNextHelper.insertOrUpdateVideoToWatchNext(
            video_movie, TEST_VIDEO_PLAYBACK_POSITION_MILLIS,
            TvContract.WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE,
            ApplicationProvider.getApplicationContext()
        )

        // Update the video data. eg: update playback position by 2 minutes.
        // This value will be picked while inserting the video back.
        val updatedVideoPlaybackPosition =
            TEST_VIDEO_PLAYBACK_POSITION_MILLIS + Duration.ofMinutes(2).toMillis().toInt()

        // Call the updated method.
        WatchNextHelper.insertOrUpdateVideoToWatchNext(
            video_movie, updatedVideoPlaybackPosition,
            TvContract.WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE,
            ApplicationProvider.getApplicationContext()
        )

        // Expected : verify if program was updated in Watch Next.
        val watchList =
            WatchNextHelper.getWatchNextPrograms(ApplicationProvider.getApplicationContext())
        val program =
            watchList.firstOrNull { it.internalProviderId == TEST_VIDEO_ID }

        // Verify the correct updated metadata was set
        assertThat(program).isNotNull()
        assertThat(program?.internalProviderId).isEqualTo(TEST_VIDEO_ID)
        assertThat(program?.title).isEqualTo(TEST_VIDEO_NAME)
        assertThat(program?.lastPlaybackPositionMillis).isEqualTo(updatedVideoPlaybackPosition)
    }

    /**
     * This acts like a fake provider mirroring the TvContentProvider (since we set the same
     * authority).
     *
     * For Roboelectric tests, by default fake provider will be used wherever the code refers to
     * TvContentProvider. This creates a new instance every time and clears out when done,
     * hence acts like in-memory.
     */
    class FakeInMemoryTvProvider : ContentProvider() {

        private val columns = arrayOf(
            TvContractCompat.WatchNextPrograms._ID,
            TvContractCompat
                .WatchNextPrograms.COLUMN_TITLE,
            TvContractCompat.WatchNextPrograms.COLUMN_INTERNAL_PROVIDER_ID,
            TvContractCompat.WatchNextPrograms.COLUMN_LAST_PLAYBACK_POSITION_MILLIS,
            TvContractCompat.WatchNextPrograms.COLUMN_SEASON_DISPLAY_NUMBER,
            TvContractCompat.WatchNextPrograms.COLUMN_SEASON_TITLE,
            TvContractCompat.WatchNextPrograms.COLUMN_EPISODE_DISPLAY_NUMBER,
            TvContractCompat.WatchNextPrograms.COLUMN_EPISODE_TITLE
        )

        // For testing purpose, mimic a mutable list rather than an actual db.
        private val valuesInMemory = mutableListOf<WatchNextProgram>()

        // Persist rowId to auto increment id for every insert.
        private var rowId = 0L

        override fun insert(uri: Uri, values: ContentValues?): Uri? {
            // Insert a new row.
            rowId++

            val programBuilder =
                values?.getAsString(TvContractCompat.WatchNextPrograms.COLUMN_TITLE)?.let {
                    WatchNextProgram.Builder()
                        .setId(rowId)
                        .setTitle(it)
                        .setInternalProviderId(
                            values.getAsString(
                                TvContractCompat.WatchNextPrograms
                                    .COLUMN_INTERNAL_PROVIDER_ID
                            )
                        )
                        .setLastPlaybackPositionMillis(
                            values!!.getAsInteger(
                                TvContractCompat.WatchNextPrograms.COLUMN_LAST_PLAYBACK_POSITION_MILLIS
                            )
                        )
                }


            values.getAsInteger(
                TvContractCompat.WatchNextPrograms.COLUMN_SEASON_DISPLAY_NUMBER
            )?.let { programBuilder.setSeasonNumber(it) }

            values.getAsString(
                TvContractCompat.WatchNextPrograms.COLUMN_SEASON_TITLE
            )?.let { programBuilder.setSeasonTitle(it) }

            values.getAsInteger(
                TvContractCompat.WatchNextPrograms.COLUMN_EPISODE_DISPLAY_NUMBER
            )?.let { programBuilder.setEpisodeNumber(it) }

            values.getAsString(
                TvContractCompat.WatchNextPrograms.COLUMN_EPISODE_TITLE
            )?.let { programBuilder.setEpisodeTitle(it) }

            val program = programBuilder.build()

            this.valuesInMemory.add(program)
            return TvContractCompat.buildWatchNextProgramUri(rowId)
        }

        override fun query(
            uri: Uri,
            projection: Array<out String>?,
            selection: String?,
            selectionArgs: Array<out String>?,
            sortOrder: String?
        ): Cursor {
            val cursor = MatrixCursor(columns)
            valuesInMemory.forEach {
                cursor.addRow(
                    arrayOf(
                        it.id,
                        it.title,
                        it.internalProviderId,
                        it.lastPlaybackPositionMillis,
                        it.seasonNumber,
                        it.seasonTitle,
                        it.episodeNumber,
                        it.episodeTitle

                    )
                )
            }
            return cursor
        }

        override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
            val id = uri.lastPathSegment?.toLong() ?: -1L
            val removed = this.valuesInMemory.removeIf { it.id == id }
            return if (removed)
                id.toInt()
            else
                DELETE_FAILED
        }

        override fun onCreate(): Boolean {
            // This is an in-memory fake so create is trivial.
            return true
        }

        override fun update(
            uri: Uri,
            values: ContentValues?,
            selection: String?,
            selectionArgs: Array<out String>?
        ): Int {
            val id = uri.lastPathSegment?.toLong() ?: return 0

            val program = values?.getAsString(
                TvContractCompat
                    .WatchNextPrograms.COLUMN_INTERNAL_PROVIDER_ID
            )?.let {
                WatchNextProgram.Builder()
                    .setId(id)
                    .setTitle(values.getAsString(TvContractCompat.WatchNextPrograms.COLUMN_TITLE))
                    .setInternalProviderId(
                        it
                    )
                    .setLastPlaybackPositionMillis(
                        values.getAsInteger(
                            TvContractCompat.WatchNextPrograms.COLUMN_LAST_PLAYBACK_POSITION_MILLIS
                        )

                    )
                    .build()
            }

            // Since kotlin collections don't directly provide a replace function, for testing
            // purpose, remove existing and add new entry to avoid complexity.
            this.valuesInMemory.removeIf { it.id == id }
            if (program != null) {
                this.valuesInMemory.add(program)
            }
            return 1
        }

        override fun applyBatch(authority: String, operations: ArrayList<ContentProviderOperation>):
            Array<out ContentProviderResult> {
            var results = ArrayList<ContentProviderResult>()

            operations.forEach { operation ->
                if (operation.isDelete) {
                    val id = operation.uri.lastPathSegment?.toLong() ?: -1L
                    val removed = this.valuesInMemory.removeIf { it.id == id }
                    if (removed) {
                        var result = ContentProviderResult(1)
                        results.add(result)
                    } else {
                        results.add(ContentProviderResult(0))
                    }
                }
            }

            return results.toArray() as Array<out ContentProviderResult>
        }

        override fun getType(uri: Uri): String {
            // This is an in-memory fake so getType is trivial.
            return ""
        }


    }
}
