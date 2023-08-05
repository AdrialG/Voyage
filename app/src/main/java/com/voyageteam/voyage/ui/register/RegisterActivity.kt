package com.voyageteam.voyage.ui.register

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.voyageteam.voyage.R
import com.voyageteam.voyage.base.BaseActivity
import com.voyageteam.voyage.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.launch

@SuppressLint("InflateParams")
@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.login_bg)

        // Blur the bitmap with the desired radius (e.g., 25)
        Blurry.with(this)
            .radius(15)
            .from(originalBitmap)
            .into(binding.registerBg) // imageView is the ImageView where you want to display the blurred image

        val callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {

                val slide = Slide(Gravity.END)
                slide.duration = 200
                window.exitTransition = slide

                finish()

            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

        binding.registerToLogin.setOnClickListener {

            val slide = Slide(Gravity.END)
            slide.duration = 200
            window.exitTransition = slide

            finish()
        }

        binding.registerButton.setOnClickListener {

            if (listOf(
                    binding.registerName,
                    binding.registerEmail,
                    binding.registerPassword,
                    binding.registerConfirmPassword)
                    .isEmptyRequired(R.string.fill_please)){
                return@setOnClickListener
            }

            val name = binding.registerName.textOf()
            val email = binding.registerEmail.textOf()
            val password = binding.registerPassword.textOf()
            val confirmPassword = binding.registerConfirmPassword.textOf()

            if (name.length < 5) {
                binding.root.snacked("Name can't be less than 5 characters")
                return@setOnClickListener
            }

            val isValid = isValidEmail(email)
            if (!isValid) {
                binding.root.snacked("Please input a valid Email address")
                return@setOnClickListener
            }

            if (password.length < 8) {
                binding.root.snacked("Password can't be less than 8 characters")
                return@setOnClickListener
            }

            viewModel.register(name, email, password, confirmPassword)

        }

        binding.registerToLogin.setOnClickListener {
            finish()
        }

        observe()

    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    private fun sendDataBack() {
        val data = Intent()
        data.putExtra("777", binding.registerEmail.textOf()) // Replace "key" with your desired key and "value" with the data to be sent back
        setResult(RESULT_OK, data)
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                sendDataBack()

                                val slide = Slide(Gravity.END)
                                slide.duration = 500
                                window.exitTransition = slide

                                finish()
                            }
                            ApiStatus.ERROR -> {
                                loadingDialog.dismiss()
                                binding.root.snacked("Something Went Wrong, Our Bad")
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }
        }
    }

}