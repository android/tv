/*
 * Copyright 2020 Google LLC
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

package com.android.tv.reference.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.tv.reference.R
import com.android.tv.reference.databinding.FragmentSignInBinding

/**
 * Fragment that allows the user to sign in using an email and password.
 */
class SignInFragment : Fragment() {
    private val viewModel: UserInfoViewModel by activityViewModels {
        UserInfoViewModelFactory(
            requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignInBinding.inflate(inflater, container, false)
        viewModel.userInfo.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().popBackStack()
            }
        })
        viewModel.signInError.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { errorCode ->
                val error = when (errorCode) {
                    UserInfoViewModel.SIGN_IN_ERROR_INVALID_PASSWORD -> getString(R.string.invalid_credentials)
                    else -> getString(R.string.unknown_error)
                }
                binding.signInError.text = getString(R.string.sign_in_error, error)
            }
        })
        binding.signInButton.setOnClickListener {
            val username = binding.usernameEdit.text.toString()
            val password = binding.passwordEdit.text.toString()
            viewModel.signInWithPassword(username, password)
        }
        return binding.root
    }
}
