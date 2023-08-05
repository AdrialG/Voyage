package com.voyageteam.voyage.ui.login

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.transition.Slide
import android.view.ContextThemeWrapper
import android.view.Gravity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.voyageteam.voyage.R
import com.voyageteam.voyage.base.BaseActivity
import com.voyageteam.voyage.data.Session
import com.voyageteam.voyage.databinding.ActivityLoginBinding
import com.voyageteam.voyage.ui.home.HomeActivity
import com.voyageteam.voyage.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    @Inject
    lateinit var session : Session

    private val startRegisterActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val receivedData = result.data?.getStringExtra("777")
                binding.loginEmail.setText(receivedData)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.login_bg)

        // Blur the bitmap with the desired radius (e.g., 25)
        Blurry.with(this)
            .radius(15)
            .from(originalBitmap)
            .into(binding.loginBg) // imageView is the ImageView where you want to display the blurred image

        val callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Show dialog or handle back press here
                val builder = AlertDialog.Builder(ContextThemeWrapper(this@LoginActivity, R.style.CustomAlertDialog))
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

        binding.loginButton.setOnClickListener {
            val emailOrPhone = binding.loginEmail.textOf()
            val password = binding.loginPassword.textOf()
            viewModel.login(emailOrPhone, password)
        }

        binding.loginToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)

            // Set up the slide animation
            val slide = Slide(Gravity.START)
            slide.duration = 200 // Adjust the duration as needed
            window.exitTransition = slide

            // Start the activity with the slide animation
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, binding.loginToRegister, "transitionButton")
            startRegisterActivityForResult.launch(intent, options)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show(getString(R.string.logging_in))
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                openActivity<HomeActivity>()
                                finish()
                            }

                            ApiStatus.ERROR -> {
                                loadingDialog.dismiss()
                                binding.root.snacked("Login Failed")
                            }
                            else -> loadingDialog.setResponse("This is the message of all time")
                        }
                    }
                }
            }
        }

    }

}