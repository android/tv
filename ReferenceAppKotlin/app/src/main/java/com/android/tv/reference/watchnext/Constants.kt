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

/** Constants that are to be used as reference for publishing guidelines */
object Constants {
    const val MAX_PUBLISHING_ATTEMPTS: Int = 5

    const val WORKER_NAME_CONTINUATION: String = "Upload Continuation"

    const val PERIODIC_WORKER_NAME_CONTINUATION: String = "Periodically Upload Continuation"

    const val PUBLISH_TYPE: String = "PUBLISH_TYPE"
    const val PUBLISH_TYPE_CONTINUATION = "PUBLISH_CONTINUATION"
}
