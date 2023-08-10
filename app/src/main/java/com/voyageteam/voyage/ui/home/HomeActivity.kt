package com.voyageteam.voyage.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.activity.OnBackPressedCallback
import com.crocodic.core.extension.openActivity
import com.voyageteam.voyage.R
import com.voyageteam.voyage.base.BaseActivity
import com.voyageteam.voyage.databinding.ActivityHomeBinding
import com.voyageteam.voyage.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Show dialog or handle back press here
                val builder = AlertDialog.Builder(ContextThemeWrapper(this@HomeActivity, R.style.CustomAlertDialog))
                builder.setMessage("Are you sure you want to exit?")

                // Create and customize the "No" button
                builder.setNegativeButton("No", null)

                // Create and customize the "Yes" button
                builder.setPositiveButton("Yes") { _, _ ->
                    finishAffinity()
                }

                val alertDialog = builder.create()

                alertDialog.show()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

        getUser()

        binding.homeProfilePicture.setOnClickListener {
            openActivity<ProfileActivity> {  }
        }

    }

    private fun getUser() {
        viewModel.getProfile()
    }

}