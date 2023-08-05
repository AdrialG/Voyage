package com.voyageteam.voyage.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.crocodic.core.extension.openActivity
import com.voyageteam.voyage.R
import com.voyageteam.voyage.base.BaseActivity
import com.voyageteam.voyage.data.Session
import com.voyageteam.voyage.databinding.ActivityMainBinding
import com.voyageteam.voyage.ui.home.HomeActivity
import com.voyageteam.voyage.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {

    @Inject
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Handler Looper Splash
        Handler(Looper.getMainLooper()).postDelayed({
            val isUser = session.getUser()
            if (isUser == null){
                openActivity<LoginActivity>()
                finish()
            }else{
                openActivity<HomeActivity>()
                finish()

            }
        },0)

    }
}