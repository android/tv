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
import android.content.ContentProviderOperation
import android.content.Context
import android.database.Cursor
import android.media.tv.TvContract.WatchNextPrograms.COLUMN_INTERNAL_PROVIDER_ID
import android.media.tv.TvContract.WatchNextPrograms.TYPE_MOVIE
import android.media.tv.TvContract.WatchNextPrograms.TYPE_TV_EPISODE
import android.media.tv.TvContract.WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE
import android.media.tv.TvContract.WatchNextPrograms.WATCH_NEXT_TYPE_NEXT
import android.net.Uri
import androidx.tvprovider.media.tv.PreviewChannelHelper
import androidx.tvprovider.media.tv.TvContractCompat
import androidx.tvprovider.media.tv.WatchNextProgram
import androidx.tvprovider.media.tv.WatchNextProgram.Builder
import androidx.tvprovider.media.tv.WatchNextProgram.fromCursor
import com.android.tv.reference.R
import com.android.tv.reference.repository.VideoRepository
import com.android.tv.reference.shared.datamodel.Video
import com.android.tv.reference.shared.datamodel.VideoType
import timber.log.Timber
import java.time.Duration
import java.util.concurrent.TimeUnit


/**
 * Helper class that simplifies interactions with the ATV home screen.
 *
 * To view the Watch Next row directly through adb, run the following command (assumes `adb root`):
 * ```
 * adb shell "sqlite3 -header -csv /data/data/com.android.providers.tv/databases/tv.db \"SELECT * FROM watch_next_programs where package_name='com.android.tv.reference';\"";
 * ```
 * For example, this query returns the title and position of the Watch Next entries, which is useful
 * for debugging updates to a particular entry.
 * ```
 * adb shell "sqlite3 -header -csv /data/data/com.android.providers.tv/databases/tv.db \"SELECT title, last_playback_position_millis FROM watch_next_programs where package_name='com.android.tv.reference';\"";
 * ```
 */
object WatchNextHelper {

    // Values to approximate if video has started.
    private const val WATCH_NEXT_STARTED_MIN_PERCENTAGE = 0.03
    private const val WATCH_NEXT_STARTED_MIN_MINUTES = 2L

    // MetaData sent from Playback to process Watch Next operations.
    internal const val VIDEO_ID = "VIDEO_ID"
    internal const val CURRENT_POSITION = "CURRENT_POSITION"
    internal const val DURATION = "DURATION"
    internal const val PLAYER_STATE = "PLAYER_STATE"

    // TODO(mayurkhin@) add sealed class/enum if more states are required
    // as a part of a performance/tech debt optimization
    internal const val PLAY_STATE_PAUSED = "STATE_PAUSED"
    internal const val PLAY_STATE_ENDED = "STATE_ENDED"

    /**
     * Add all relevant metadata which will be displayed on Watch Next Channel.
     */
    private fun setBuilderMetadata(
        builder: Builder,
        video: Video,
        watchNextType: Int,
        watchPosition: Int,
        type: Int,
        duration: Duration,
        context: Context
    ): Builder {
        builder.setType(type)
            .setWatchNextType(watchNextType)
            .setLastPlaybackPositionMillis(watchPosition)
            .setLastEngagementTimeUtcMillis(System.currentTimeMillis())
            .setTitle(video.name)
            .setDurationMillis(duration.toMillis().toInt())
            .setPreviewVideoUri(Uri.parse(video.videoUri))
            .setDescription(video.description)
            .setPosterArtUri(Uri.parse(video.thumbnailUri))
            // Intent uri used to deep link video when user clicks on Watch Next item.
            .setIntentUri(Uri.parse(video.uri))
            /* The internalProviderId attribute must match the internal ID you provide in the
            Media PlaybackStateCompat.Actions feed. This allows Android TV to reconcile
            the asset more effectively and provides a high-confidence feature to users.
            Refer https://developer.android.com/training/tv/discovery/watch-next-programs?hl=tr*/
            .setInternalProviderId(video.id)
            // Use the contentId to recognize same content across different channels.
            .setContentId(video.id)

        if (type == TYPE_TV_EPISODE) {
            builder.setEpisodeNumber(video.episodeNumber.toInt())
                .setSeasonNumber(video.seasonNumber.toInt())
                // User TV series name and season number to generate a fake season name.
                .setSeasonTitle(context.getString(
                    R.string.season, video.category, video.seasonNumber))
                // Use the name of the video as the episode name.
                .setEpisodeTitle(video.name)
                // Use TV series name as the tile, in this sample,
                // we use category as a fake TV series.
                .setTitle(video.category)
        }
        return builder
    }

    /**
     * The user has "started" a video if they've watched more than 3% or 2 minutes,
     * whichever timestamp is earlier.
     * https://developer.android.com/training/tv/discovery/guidelines-app-developers
     */
    internal fun hasVideoStarted(duration: Duration, currentPosition: Int): Boolean {
        val durationInMilliSeconds = duration.toMillis().toInt()
        // Return true if either X minutes or Y % have passed
        // Following formatting spans over multiple lines to accommodate max 100 limit
        val watchNextMinStartedMillis = TimeUnit.MINUTES.toMillis(WATCH_NEXT_STARTED_MIN_MINUTES)
        // Check if either X minutes or Y% has passed
        val hasVideoStarted =
            (currentPosition >= (durationInMilliSeconds * WATCH_NEXT_STARTED_MIN_PERCENTAGE)) or
              (currentPosition >= watchNextMinStartedMillis)
        val hasVideoStartedWithValidPosition =
            ((currentPosition <= durationInMilliSeconds) and hasVideoStarted)
        Timber.v(
            "Has video started: %s, duration: %s, watchPosition: %s",
            hasVideoStartedWithValidPosition,
            duration,
            currentPosition
        )
        return hasVideoStartedWithValidPosition
    }

    /**
     * Retrieve all programs in Watch Next row.
     */
    @SuppressLint("RestrictedApi")
    // Suppress RestrictedApi due to https://issuetracker.google.com/138150076
    internal fun getWatchNextPrograms(context: Context): List<WatchNextProgram> {
        val programs: MutableList<WatchNextProgram> = mutableListOf()
        val cursor = context.contentResolver.query(
            TvContractCompat.WatchNextPrograms.CONTENT_URI,
            WatchNextProgram.PROJECTION,
            /* selection = */ null,
            /* selectionArgs = */ null,
            /* sortOrder= */ null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    programs.add(fromCursor(cursor))
                } while (it.moveToNext())
            }
        }
        return programs
    }

    /**
     * Add unfinished program to Watch Next.
     * Update the playback position if program already exists in Watch Next channel.
     */
    @SuppressLint("RestrictedApi")
    // Suppress RestrictedApi due to https://issuetracker.google.com/138150076
    @Synchronized
    internal fun insertOrUpdateVideoToWatchNext(
        video: Video,
        watchPosition: Int,
        watchNextType: Int,
        context: Context
    ): Long {

        if (video.videoType != VideoType.MOVIE && video.videoType != VideoType.EPISODE) {
            throw IllegalArgumentException(
                "Watch Next is not supported for Video Type: ${video.videoType}")
        }

        var programId = 0L
        // If we already have a program with this ID, use it as a base for updated program.
        // Avoid Duplicates.

        val existingProgram = getWatchNextProgramByVideoId(video.id, context)
        Timber.v(
            "insertOrUpdateToWatchNext, existingProgram = $existingProgram ,videoid = ${video.id}"
        )
        // If program exists,create builder with existing program.
        val programBuilder = existingProgram
            ?.let {
                Builder(it)
            } ?: Builder()

        val videoType = if(video.videoType == VideoType.MOVIE) {
            TYPE_MOVIE
        } else {
            TYPE_TV_EPISODE
        }

        val updatedBuilder = setBuilderMetadata(
            programBuilder,
            video,
            watchNextType,
            watchPosition,
            videoType,
            video.duration(),
            context
        )

        // Build the program with all the metadata
        val updatedProgram = updatedBuilder.build()

        // If the program is already in the Watch Next row, update it
        if (existingProgram != null) {
            programId = existingProgram.id
            PreviewChannelHelper(context).updateWatchNextProgram(updatedProgram, programId)
            Timber.v("Updated program in Watch Next row: ${updatedProgram.title}")
        }
        // Otherwise build the program and insert it into the channel
        else {
            try {
                programId = PreviewChannelHelper(context)
                    .publishWatchNextProgram(updatedProgram)
                Timber.v("Added New program to Watch Next row: ${updatedProgram.title}")
            } catch (exc: IllegalArgumentException) {
                Timber.e(
                    exc, "Unable to add program to Watch Next row. ${exc.localizedMessage}"
                )
                exc.printStackTrace()
            }
        }

        Timber.v("Final added/updated programId = $programId")
        return programId
    }

    /**
     *  Remove a Video object from the Watch Next row,
     *  Typically after user has finished watching the video.
     *  Returns the number of rows deleted or null if delete fails
     */
    @Synchronized
    @SuppressLint("RestrictedApi")
    // Suppress RestrictedApi due to https://issuetracker.google.com/138150076
    fun removeVideoFromWatchNext(context: Context, video: Video): Uri? {
        Timber.v("Trying to Removing content from Watch Next: ${video.name}")

        // Find the program with the matching ID for our metadata.
        val foundProgram = getWatchNextProgramByVideoId(video.id, context)
        if (foundProgram == null) {
            Timber.e(
                "Unable to delete. No program found with videoID ${video.id}"
            )
            return null
        }

        // Use the found program's URI to delete it from the content resolver
        return foundProgram.let {
            val programUri = TvContractCompat.buildWatchNextProgramUri(it.id)
            // delete returns the number of rows deleted.
            val deleteCount = context.contentResolver.delete(
                programUri, null, null
            )

            if (deleteCount == 1) {
                Timber.v("Content successfully removed from Watch Next")
                programUri
            } else {
                Timber.e("Content failed to be removed from Watch Next, delete count $deleteCount")
                null
            }
        }
    }

    @Synchronized
    @SuppressLint("RestrictedApi")
    // Suppress RestrictedApi due to https://issuetracker.google.com/138150076
    fun removeVideosFromWatchNext(context: Context, videos: List<Video>) {
        // Find the program with the matching ID for our metadata.
        val foundPrograms = getWatchNextProgramByVideoIds(videos.map { it.id }, context)
        val operations = foundPrograms.map {
            val programUri = TvContractCompat.buildWatchNextProgramUri(it.id)
            ContentProviderOperation.newDelete(programUri).build()
        } as ArrayList<ContentProviderOperation>

        val results = context.contentResolver.applyBatch(TvContractCompat.AUTHORITY, operations)

        results.forEach { result ->
            if (result.count != 1) {
                Timber.e("Content failed to be removed from Watch Next: ${result.uri}")
            }
        }
    }

    /**
     * Query the Watch Next list and find the program with given videoId.
     * Return null if not found.
     */
    @Synchronized
    private fun getWatchNextProgramByVideoId(id: String, context: Context): WatchNextProgram? {
        return findFirstWatchNextProgram(context) { cursor ->
            (cursor.getString(cursor.getColumnIndex(COLUMN_INTERNAL_PROVIDER_ID)) == id)
        }
    }

    @Synchronized
    @SuppressLint("RestrictedApi")
    // Suppress RestrictedApi due to https://issuetracker.google.com/138150076
    private fun getWatchNextProgramByVideoIds(ids: List<String>, context: Context):
        List<WatchNextProgram> {
        return getWatchNextPrograms(context).filter {
            ids.contains(it.internalProviderId)
        }
    }

    /**
     * Find the Watch Next program for given id.
     * Returns the first instance available.
     */
    @SuppressLint("RestrictedApi")
    // Suppress RestrictedApi due to https://issuetracker.google.com/138150076
    internal fun findFirstWatchNextProgram(context: Context, predicate: (Cursor) -> Boolean):
        WatchNextProgram? {

            val cursor = context.contentResolver.query(
                TvContractCompat.WatchNextPrograms.CONTENT_URI,
                WatchNextProgram.PROJECTION,
                /* selection = */ null,
                /* selectionArgs = */ null,
                /* sortOrder= */ null
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    do {
                        if (predicate(cursor)) {
                            return fromCursor(cursor)
                        }
                    } while (it.moveToNext())
                }
            }
            return null
    }

    /**
     *  Returns a list of videos which is visible on Watch Next row.
     */
    @SuppressLint("RestrictedApi")
    internal fun filterWatchNextVideos(videos: List<Video>, context: Context): List<Video> {
        val watchedPrograms = getWatchNextProgramByVideoIds(videos.map { it.id }, context)
        val watchedVideosIds = watchedPrograms.map { it.internalProviderId }
        return videos.filter { watchedVideosIds.contains(it.id) }
    }

    /**
     * Handle operations for Watch Next channel for video type 'Movie'.
     */
    // TODO(mayurikhin@) : create a @StringDef for the string constants for the different
    //  player states.
    internal fun handleWatchNextForMovie(
        video: Video,
        watchPosition: Int,
        state: String?,
        context: Context
    ) {
        Timber.v("Adding/remove movie to Watch Next. Video Name: ${video.name}")

        when {
            // If movie has finished, remove from Watch Next channel.
            (state == PLAY_STATE_ENDED) or
                video.isAfterEndCreditsPosition(watchPosition.toLong()) -> {
                removeVideoFromWatchNext(context, video)
            }

            // Add or update unfinished movie to Watch Next channel.
            hasVideoStarted(video.duration(), watchPosition) -> {
                insertOrUpdateVideoToWatchNext(
                    video,
                    watchPosition,
                    WATCH_NEXT_TYPE_CONTINUE,
                    context
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

    /**
     * Handle operations for Watch Next channel for video type 'Episode'.
     */
    internal fun handleWatchNextForEpisode(
        video: Video,
        watchPosition: Int,
        state: String?,
        videoRepository: VideoRepository,
        context: Context
    ) {
        Timber.v("Adding/remove episode to Watch Next. Video Name: ${video.name}")

        var newWatchNextVideo: Video? = null
        when {
            // If episode has finished, remove from Watch Next channel.
            (state == PLAY_STATE_ENDED) or
                video.isAfterEndCreditsPosition(watchPosition.toLong()) -> {
                removeVideoFromWatchNext(context, video)

                // Add next episode from TV series.
                videoRepository.getNextEpisodeInSeries(video)?.let {
                        insertOrUpdateVideoToWatchNext(
                            it,
                            0,
                            WATCH_NEXT_TYPE_NEXT,
                            context
                        )
                        newWatchNextVideo = it
                    }
            }

            // Add or update unfinished episode to Watch Next channel.
            hasVideoStarted(video.duration(), watchPosition) -> {
                insertOrUpdateVideoToWatchNext(
                    video,
                    watchPosition,
                    WATCH_NEXT_TYPE_CONTINUE,
                    context
                )
                newWatchNextVideo = video
            }
            else -> {
                Timber.w(
                    "Video not started yet. Can't add to WatchNext.watchPosition: %s, duration: %d",
                    watchPosition,
                    video.duration().toMillis()
                )
            }
        }

        /**
         *  We suggest to keep only 1 episode for each TV show in Watch Next, remove previous
         *  watched episodes and only keep the last watched one.
         *  1. Figures out which episode from this TV Series are visible in the Watch Next row;
         *  2. Sorts the filtered episodes and excludes the last watched episode;
         *  3. Removes all other episode from Watch Next row;
         */
        newWatchNextVideo?.let { videoToKeep ->
            videoRepository.getAllVideosFromSeries(videoToKeep.seriesUri).let { allEpisodes ->
                    filterWatchNextVideos(allEpisodes, context)
                        .let { watchedEpisodes ->
                            removeVideosFromWatchNext(
                                context, watchedEpisodes.filter { it.id != videoToKeep.id })
                        }
                }
        }
    }
}