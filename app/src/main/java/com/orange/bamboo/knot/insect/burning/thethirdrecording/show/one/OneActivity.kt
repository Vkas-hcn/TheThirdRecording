package com.orange.bamboo.knot.insect.burning.thethirdrecording.show.one

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.view.animation.LinearInterpolator
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.orange.bamboo.knot.insect.burning.thethirdrecording.R
import com.orange.bamboo.knot.insect.burning.thethirdrecording.databinding.ActivityMainBinding
import com.orange.bamboo.knot.insect.burning.thethirdrecording.databinding.ActivityOneBinding
import com.orange.bamboo.knot.insect.burning.thethirdrecording.show.two.MainActivity
import com.orange.bamboo.knot.insect.burning.thethirdrecording.zore.BaseActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OneActivity : BaseActivity<ActivityOneBinding, OneViewModel>() {
    override val layoutId: Int = R.layout.activity_one
    override val viewModelClass: Class<OneViewModel> = OneViewModel::class.java

    override fun initViewComponents() {
        onBackPressedDispatcher.addCallback(this) {
        }
    }

    override fun initializeData() {
        startCountdown()
    }

    private fun startCountdown() {
        lifecycleScope.launch {
            delay(2000)
            navigateTo<MainActivity>()
            finish()
        }
    }
}