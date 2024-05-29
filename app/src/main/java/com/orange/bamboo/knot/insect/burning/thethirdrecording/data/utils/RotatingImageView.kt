package com.orange.bamboo.knot.insect.burning.thethirdrecording.data.utils

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator

import android.widget.ImageView
import com.orange.bamboo.knot.insect.burning.thethirdrecording.R

private var ImageView.animator: ObjectAnimator?
    get() = getTag(R.id.img_record) as? ObjectAnimator
    set(value) = setTag(R.id.img_record, value)

fun ImageView.startInfiniteRotation(duration: Long = 2000L) {
    if (animator == null) {
        animator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f).apply {
            this.duration = duration
            interpolator = LinearInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }
    }
    animator?.start()
}

fun ImageView.stopRotation() {
    animator?.cancel()
}

