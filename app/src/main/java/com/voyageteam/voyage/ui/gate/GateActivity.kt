package com.voyageteam.voyage.ui.gate

import android.graphics.BitmapFactory
import android.os.Bundle
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.extension.openActivity
import com.voyageteam.voyage.R
import com.voyageteam.voyage.databinding.ActivityGateBinding
import com.voyageteam.voyage.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry

@AndroidEntryPoint
class GateActivity : NoViewModelActivity<ActivityGateBinding>(R.layout.activity_gate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.gate_image)

        // Blur the bitmap with the desired radius (e.g., 25)
        Blurry.with(this)
            .radius(15)
            .from(originalBitmap)
            .into(binding.gateBackground) // imageView is the ImageView where you want to display the blurred image

        binding.gateJoinButton.setOnClickListener {
            openActivity<LoginActivity> {  }
        }

    }
}