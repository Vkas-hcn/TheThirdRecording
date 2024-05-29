package com.orange.bamboo.knot.insect.burning.thethirdrecording.zore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity<VB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    protected lateinit var binding: VB
    protected lateinit var viewModel: VM

    abstract val layoutId: Int
    abstract val viewModelClass: Class<VM>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDataBinding()
        setupViewModel()
        initViewComponents()
        initializeData()
    }

    private fun setupDataBinding() {
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(viewModelClass)
    }

    abstract fun initViewComponents()
    abstract fun initializeData()

    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun showCustomDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    protected inline fun <reified T : BaseActivity<*, *>> navigateTo() {
        startActivity(Intent(this, T::class.java))
    }

    protected inline fun <reified T : BaseActivity<*, *>> navigateTo(vararg params: Pair<String, Any>) {
        val intent = Intent(this, T::class.java)
        params.forEach { (key, value) ->
            when (value) {
                is Int -> intent.putExtra(key, value)
                is String -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                // Add other types as needed
            }
        }
        startActivity(intent)
    }
}
