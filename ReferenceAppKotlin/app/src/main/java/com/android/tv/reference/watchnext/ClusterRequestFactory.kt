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
import com.android.tv.reference.shared.datamodel.VideoType
import com.google.android.engage.common.datamodel.ContinuationCluster
import com.google.android.engage.service.PublishContinuationClusterRequest

/**
 * Class in charge of constructing the publishing requests and sending them to their respective
 * publishers
 */
class ClusterRequestFactory(context: Context) {

    private val engageWatchNextService = EngageWatchNextService.getInstance(context)

    /**
     * [constructContinuationClusterRequest] returns a [PublishContinuationClusterRequest] to be used
     * by the [EngageServiceWorker] to publish Continuations clusters
     *
     * @return PublishContinuationClusterRequest
     */
    fun constructContinuationClusterRequest(): PublishContinuationClusterRequest {
        val continuationList = engageWatchNextService.getAllWatchNextVideos()
        val continuationCluster = ContinuationCluster.Builder()
        for (watchNextVideo in continuationList) {
            val videoType = watchNextVideo.video.videoType
            if (videoType == VideoType.EPISODE || videoType == VideoType.MOVIE) {
                continuationCluster.addEntity(
                    VideoToEngageEntityConverter.convertVideo(
                        watchNextVideo
                    )
                )
            }
        }
        return PublishContinuationClusterRequest.Builder()
            .setContinuationCluster(continuationCluster.build())
            .build()
    }
}
