package com.orange.bamboo.knot.insect.burning.thethirdrecording.zore

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel(), CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun showLoading() {
        // Implement loading logic here
    }

    fun hideLoading() {
        // Implement hide loading logic here
    }

    fun showError(message: String) {
        // Implement error display logic here
    }
}
