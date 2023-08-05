package com.voyageteam.voyage.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.crocodic.core.extension.openActivity
import com.voyageteam.voyage.R
import com.voyageteam.voyage.base.BaseActivity
import com.voyageteam.voyage.data.Session
import com.voyageteam.voyage.databinding.ActivityHomeBinding
import com.voyageteam.voyage.databinding.ActivityMainBinding
import com.voyageteam.voyage.ui.login.LoginActivity
import com.voyageteam.voyage.ui.profile.ProfileActivity
import com.voyageteam.voyage.ui.splash.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.gotoprofile.setOnClickListener {
            openActivity<ProfileActivity> {  }
        }

    }
}