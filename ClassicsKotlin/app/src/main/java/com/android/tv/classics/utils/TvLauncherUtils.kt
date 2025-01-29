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

package com.android.tv.classics.utils

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.util.Rational
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.tvprovider.media.tv.PreviewChannel
import androidx.tvprovider.media.tv.PreviewChannelHelper
import androidx.tvprovider.media.tv.PreviewProgram
import androidx.tvprovider.media.tv.TvContractCompat
import com.android.tv.classics.models.TvMediaMetadata
import androidx.tvprovider.media.tv.WatchNextProgram
import com.android.tv.classics.R
import com.android.tv.classics.models.TvMediaCollection

/** Collection of static methods used to handle Android TV Home Screen Launcher operations */
@RequiresApi(26)
class TvLauncherUtils private constructor() {
    companion object {
        private val TAG = TvLauncherUtils::class.java.simpleName

        /** Helper function used to get the URI of something from the resources folder */
        fun resourceUri(resources: Resources, id: Int): Uri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(id))
                .appendPath(resources.getResourceTypeName(id))
                .appendPath(resources.getResourceEntryName(id))
                .build()

        /**
         * Parse an aspect ratio constant into the equivalent rational number. For example,
         * [TvContractCompat.PreviewPrograms.ASPECT_RATIO_16_9] becomes `Rational(16, 9)`. The
         * constant must be one of ASPECT_RATIO_* in [TvContractCompat.PreviewPrograms].
         */
        fun parseAspectRatio(ratioConstant: Int): Rational = when(ratioConstant) {
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_16_9 -> Rational(16, 9)
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_1_1 -> Rational(1, 1)
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_2_3 -> Rational(2, 3)
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_3_2 -> Rational(3, 2)
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_4_3 -> Rational(4, 3)
            TvContractCompat.PreviewPrograms.ASPECT_RATIO_MOVIE_POSTER -> Rational(1000, 1441)
            else -> throw IllegalArgumentException(
                    "Constant must be one of ASPECT_RATIO_* in TvContractCompat.PreviewPrograms")
        }

        /**
         * Retrieve the preview programs associated with the given channel ID or, if ID is null,
         * return all programs associated with any channel.
         */
        fun getPreviewPrograms(context: Context, channelId: Long? = null): List<PreviewProgram> {
            val programs: MutableList<PreviewProgram> = mutableListOf()

            try {
                val cursor = context.contentResolver.query(
                        TvContractCompat.PreviewPrograms.CONTENT_URI,
                        PreviewProgram.PROJECTION,
                        null,
                        null,
                        null)
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val program = PreviewProgram.fromCursor(cursor)
                        if (channelId == null || channelId == program.channelId) {
                            programs.add(program)
                        }
                    } while (cursor.moveToNext())
                }
                cursor?.close()

            } catch (exc: IllegalArgumentException) {
                Log.e(TAG, "Error retrieving preview programs", exc)
            }

            return programs
        }

        /** Retrieve all programs in watch next row that are ours */
        fun getWatchNextPrograms(context: Context): List<WatchNextProgram> {
            val programs: MutableList<WatchNextProgram> = mutableListOf()

            try {
                val cursor = context.contentResolver.query(
                        TvContractCompat.WatchNextPrograms.CONTENT_URI,
                        WatchNextProgram.PROJECTION,
                        null,
                        null,
                        null)
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        programs.add(WatchNextProgram.fromCursor(cursor))
                    } while (cursor.moveToNext())
                }
                cursor?.close()

            } catch (exc: IllegalArgumentException) {
                Log.e(TAG, "Error retrieving Watch Next programs", exc)
            }

            return programs
        }

        /** Remove a program given a [TvMediaMetadata] object */
        @Synchronized fun removeProgram(context: Context, metadata: TvMediaMetadata): Long? {
            Log.d(TAG, "Removing content from watch next: $metadata")

            // First, get all the programs from all of our channels
            val allPrograms = getPreviewPrograms(context)

            // Now find the program with the matching content ID for our metadata
            // Note that we are only getting the first match, this is because in our data models
            // we only allow for a program to be in one "collection" (aka channel) but nothing
            // prevents more than one program added to the content provider sharing content ID by
            // adding the same program to multiple channels
            val foundProgram = allPrograms.find { it.contentId == metadata.id }
            if (foundProgram == null) Log.e(TAG, "No program found with content ID ${metadata.id}")

            // Use the found program's URI to delete it from the content resolver
            return foundProgram?.let {
                PreviewChannelHelper(context).deletePreviewProgram(it.id)
                Log.d(TAG, "Program successfully removed from home screen")
                it.id
            }
        }

        /**
         * Insert or update a channel given a [TvMediaCollection] object. Setting the argument
         * [clearPrograms] to true makes sure that the channel end up with only the items in
         * the [metadatas] argument.
         */
        @Synchronized fun upsertChannel(
                context: Context,
                collection: TvMediaCollection,
                metadatas: List<TvMediaMetadata>,
                clearPrograms: Boolean = false
        ): Long? {

            // Retrieve the app host URL from our string resources
            val host = context.getString(R.string.host_name)

            // Retrieve a list of all previously created channels
            val allChannels: List<PreviewChannel> = try {
                PreviewChannelHelper(context).allChannels
            } catch (exc: IllegalArgumentException) {
                listOf()
            }

            val channelLogoUri =
                    collection.artUri ?: resourceUri(context.resources, R.mipmap.ic_channel_logo)

            // This must match the desired intent filter in the manifest for VIEW intent action
            val appUri = Uri.parse("https://$host/channel/${collection.id}")

            // If we already have a channel with this ID, use it as a base for updated channel
            // This is the only way to set the internal ID field when using [PreviewChannel.Builder]
            // NOTE: This means that any fields not set in the updated channel that were set in the
            //  existing channel will be preserved
            val existingChannel = allChannels.find { it.internalProviderId == collection.id }
            val channelBuilder = if (existingChannel == null) {
                PreviewChannel.Builder()
            } else {
                PreviewChannel.Builder(existingChannel)
            }

            // Build an updated channel object and add our metadata
            val updatedChannel = channelBuilder
                    .setInternalProviderId(collection.id)
                    .setLogo(channelLogoUri)
                    .setAppLinkIntentUri(appUri)
                    .setDisplayName(collection.title)
                    .setDescription(collection.description)
                    .build()

            // Check if a channel with a matching ID already exists
            val channelId = if (existingChannel != null) {

                // Delete all the programs from existing channel if requested
                // NOTE: This is in general a bad practice, since there could be metadata loss such
                //  as playback position, interaction count, etc.
                if (clearPrograms) {
                    // To delete all programs from a channel, we can build a special URI that
                    // references all programs associated with a channel and delete just that one
                    val channelPrograms =
                            TvContractCompat.buildPreviewProgramsUriForChannel(existingChannel.id)
                    context.contentResolver.delete(channelPrograms, null, null)
                }

                // Update channel in the system's content provider
                PreviewChannelHelper(context)
                        .updatePreviewChannel(existingChannel.id, updatedChannel)
                Log.d(TAG, "Updated channel ${existingChannel.id}")

                // Return the existing channel's ID
                existingChannel.id

            } else {

                // Insert channel, return null if URI in content provider is null
                try {
                    val channelId = PreviewChannelHelper(context).publishChannel(updatedChannel)
                    Log.d(TAG, "Published channel $channelId")
                    channelId

                } catch (exc: Throwable) {
                    // Early exit: return null if we couldn't insert the channel
                    Log.e(TAG, "Unable to publish channel", exc)
                    return null
                }
            }

            // If it's the first channel being shown, make it the app's default channel
            if(allChannels.none { it.isBrowsable }) {
                TvContractCompat.requestChannelBrowsable(context, channelId)
            }

            // Retrieve programs already added to this channel, if any
            val existingProgramList = getPreviewPrograms(context, channelId)

            // Create a list of programs from the content URIs, adding as much metadata as
            //  possible for each piece of content. The more metadata added, the better the
            //  content will be presented in the user's home screen.
            metadatas.forEach { metadata ->

                // If we already have a program with this ID, use it as a base for updated program
                // This is the only way to set the internal ID field when using the builder
                // NOTE: This means that any fields not set in the updated program that were set in
                //  the existing program will be preserved
                val existingProgram = existingProgramList.find { it.contentId == metadata.id }
                val programBuilder = if (existingProgram == null) {
                    PreviewProgram.Builder()
                } else {
                    PreviewProgram.Builder(existingProgram)
                }

                // Copy all metadata into our program builder
                val updatedProgram = programBuilder.also { metadata.copyToBuilder(it) }
                        // Set the same channel ID in all programs
                        .setChannelId(channelId)
                        // This must match the desired intent filter in the manifest for VIEW action
                        .setIntentUri(Uri.parse("https://$host/program/${metadata.id}"))
                        // Build the program at once
                        .build()

                // Insert new program into the channel or update if another one with same ID exists
                try {
                    if (existingProgram == null) {
                        PreviewChannelHelper(context).publishPreviewProgram(updatedProgram)
                        Log.d(TAG, "Inserted program into channel: $updatedProgram")
                    } else {
                        PreviewChannelHelper(context)
                                .updatePreviewProgram(existingProgram.id, updatedProgram)
                        Log.d(TAG, "Updated program in channel: $updatedProgram")
                    }
                } catch (exc: IllegalArgumentException) {
                    Log.e(TAG, "Unable to add program: $updatedProgram", exc)
                }
            }

            // Return ID of the created channel
            return channelId
        }


        /** Remove a [TvMediaCollection] object from the channel list */
        @Synchronized fun removeChannel(context: Context, collection: TvMediaCollection): Long? {
            Log.d(TAG, "Removing channel from home screen: $collection")

            // First, get all the channels added to the home screen
            val allChannels = PreviewChannelHelper(context).allChannels

            // Now find the channel with the matching content ID for our collection
            val foundChannel = allChannels.find { it.internalProviderId == collection.id }
            if (foundChannel == null) Log.e(TAG, "No channel with ID ${collection.id}")

            // Use the found channel's ID to delete it from the content resolver
            return foundChannel?.let {
                PreviewChannelHelper(context).deletePreviewChannel(it.id)
                Log.d(TAG, "Channel successfully removed from home screen")

                // Remove all of the channel programs as well
                val channelPrograms =
                        TvContractCompat.buildPreviewProgramsUriForChannel(it.id)
                context.contentResolver.delete(channelPrograms, null, null)

                // Return the ID of the channel removed
                it.id
            }
        }

        /** Insert or update a [TvMediaMetadata] into the watch next row */
        @Synchronized fun upsertWatchNext(context: Context, metadata: TvMediaMetadata): Long? {
            Log.d(TAG, "Adding program to watch next row: $metadata")

            // If we already have a program with this ID, use it as a base for updated program
            val existingProgram = getWatchNextPrograms(context).find { it.contentId == metadata.id }
            val programBuilder = if (existingProgram == null) {
                WatchNextProgram.Builder()
            } else {
                WatchNextProgram.Builder(existingProgram)
            }.also { metadata.copyToBuilder(it) }

            // Required for CONTINUE program type: set last engagement time
            programBuilder.setLastEngagementTimeUtcMillis(System.currentTimeMillis())

            // Set the specific intent for this program in the watch next row
            // Watch next type is inferred from media playback using the following rules:
            // 1. NEXT      - If position is NULL
            // 2. CONTINUE  - If position not NULL AND duration is not NULL
            // 3. UNKNOWN   - If position < 0 OR (position is not NULL AND duration is NULL)
            programBuilder.setWatchNextType(metadata.playbackPositionMillis?.let { position ->
                if (position > 0 && metadata.playbackDurationMillis?.let { it > 0 } == true) {
                    Log.d(TAG, "Inferred watch next type: CONTINUE")
                    TvContractCompat.WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE
                } else {
                    Log.d(TAG, "Inferred watch next type: UNKNOWN")
                    WatchNextProgram.WATCH_NEXT_TYPE_UNKNOWN
                }
            } ?: TvContractCompat.WatchNextPrograms.WATCH_NEXT_TYPE_NEXT)

            // This must match the desired intent filter in the manifest for VIEW intent action
            programBuilder.setIntentUri(Uri.parse(
                    "https://${context.getString(R.string.host_name)}/program/${metadata.id}"))

            // Build the program with all the metadata
            val updatedProgram = programBuilder.build()

            // Check if a program matching this one already exists in the watch next row
            return if (existingProgram != null) {

                // If the program is already in the watch next row, update it
                PreviewChannelHelper(context)
                        .updateWatchNextProgram(updatedProgram, existingProgram.id)
                Log.d(TAG, "Updated program in watch next row: $updatedProgram")
                existingProgram.id
            } else {

                // Otherwise build the program and insert it into the channel
                try {
                    val programId = PreviewChannelHelper(context)
                            .publishWatchNextProgram(updatedProgram)
                    Log.d(TAG, "Added program to watch next row: $updatedProgram")
                    programId
                } catch (exc: IllegalArgumentException) {
                    Log.e(TAG, "Unable to add program to watch next row")
                    null
                }
            }
        }

        /** Remove a [TvMediaMetadata] object from the watch next row */
        @Synchronized fun removeFromWatchNext(context: Context, metadata: TvMediaMetadata): Uri? {
            Log.d(TAG, "Removing content from watch next: $metadata")

            // First, get all the programs in the watch next row
            val allPrograms = getWatchNextPrograms(context)

            // Now find the program with the matching content ID for our metadata
            val foundProgram = allPrograms.find { it.contentId == metadata.id }
            if (foundProgram == null)
                Log.e(TAG, "No program found in Watch Next with content ID ${metadata.id}")

            // Use the found program's URI to delete it from the content resolver
            return foundProgram?.let {
                val programUri = TvContractCompat.buildWatchNextProgramUri(it.id)
                val deleteCount = context.contentResolver.delete(
                        programUri, null, null)

                if (deleteCount == 1) {
                    Log.d(TAG, "Content successfully removed from watch next")
                    programUri

                } else {
                    Log.e(TAG, "Content failed to be removed from watch next " +
                            "(delete count $deleteCount)")
                    null
                }
            }
        }
    }
}