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
package com.android.tv.reference

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.android.tv.reference.browse.BrowseFragmentDirections
import com.android.tv.reference.castconnect.CastHelper
import com.android.tv.reference.deeplink.DeepLinkViewModel
import com.android.tv.reference.deeplink.DeepLinkViewModelFactory
import com.android.tv.reference.shared.datamodel.Video
import com.android.tv.reference.shared.util.Result
import com.android.tv.reference.watchnext.Publisher
import timber.log.Timber

/**
 * FragmentActivity that displays the various fragments
 */
class MainActivity : FragmentActivity() {

    private lateinit var navGraph: NavGraph
    private lateinit var navController: NavController
    private lateinit var viewModel: DeepLinkViewModel
    private lateinit var castHelper: CastHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        castHelper = CastHelper(
            { videoToCast ->
                loadPlaybackFragment(videoToCast)
            },
            application
        )
        if (castHelper.validateAndProcessCastIntent(intent)) {
            return
        }

        loadDeepLinkOrStartingPage(intent.data)
    }

    /**
     * Checks if the app was started with a deep link, loading it if it was
     *
     * If not (or the deep link is invalid), it triggers the normal starting process
     */
    private fun loadDeepLinkOrStartingPage(
        uri: Uri?
    ) {
        if (uri == null) {
            loadStartingPage()
            return
        }

        viewModel = ViewModelProvider(this, DeepLinkViewModelFactory(application, uri)).get(
            DeepLinkViewModel::class.java
        )
        viewModel.deepLinkResult.observe(
            this,
            {
                when (val result = it.getContentIfNotHandled()) {
                    is Result.Success -> {
                        val video = result.data
                        Timber.d("Loaded '${video.name}' for deep link '$uri'")

                        loadPlaybackFragment(video)
                    }
                    is Result.Error -> {
                        // Here you might show an error to the user or automatically trigger a search
                        // for content that might match the deep link; since this is just a demo app,
                        // the error is logged and then the app starts normally
                        Timber.w("Failed to load deep link $uri, ignoring", result.exception)
                        loadStartingPage()
                    }
                }
            }
        )
    }

    /**
     * Chooses whether to show the browse screen or the "no Firebase" notice
     */
    private fun loadStartingPage() {

        @Suppress("ConstantConditionIf")
        if (BuildConfig.FIREBASE_ENABLED) {
            Timber.d("Firebase is enabled, loading browse")
            navGraph.setStartDestination(R.id.browseFragment)
        } else {
            Timber.d("Firebase is not enabled; showing notice")
            navGraph.setStartDestination(R.id.noFirebaseFragment)
        }

        // Set the graph to trigger loading the start destination
        navController.graph = navGraph
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        if (intent == null) {
            return
        }

        // Return early if intent is a valid Cast intent and is processed by the Cast SDK.
        if (castHelper.validateAndProcessCastIntent(intent)) {
            return
        }

        // Include logic to process other types of intents here, if any.
    }

    private fun loadPlaybackFragment(video: Video) {
        // Set the default graph and go to playback for the loaded Video
        navController.graph = navGraph
        navController.navigate(
            BrowseFragmentDirections.actionBrowseFragmentToPlaybackFragment(
                video
            )
        )
    }

    override fun onStop() {
        super.onStop()
        Publisher.publishPeriodically(applicationContext)
    }
}
