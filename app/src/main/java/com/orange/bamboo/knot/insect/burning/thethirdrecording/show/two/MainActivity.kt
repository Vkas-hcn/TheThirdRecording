package com.orange.bamboo.knot.insect.burning.thethirdrecording.show.two

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.orange.bamboo.knot.insect.burning.thethirdrecording.R
import com.orange.bamboo.knot.insect.burning.thethirdrecording.databinding.ActivityMainBinding
import com.orange.bamboo.knot.insect.burning.thethirdrecording.show.four.HistoryActivity
import com.orange.bamboo.knot.insect.burning.thethirdrecording.show.four.HistoryAdapter
import com.orange.bamboo.knot.insect.burning.thethirdrecording.show.six.SixActivity
import com.orange.bamboo.knot.insect.burning.thethirdrecording.show.three.RecordActivity
import com.orange.bamboo.knot.insect.burning.thethirdrecording.zore.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override val layoutId: Int = R.layout.activity_main
    override val viewModelClass: Class<MainViewModel> = MainViewModel::class.java
    private val PERMISSION_REQUEST_CODE = 123

    override fun initViewComponents() {
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }

    override fun initializeData() {
        binding.mainRecord.setOnClickListener {
            checkPermissions{
                navigateTo<RecordActivity>()
            }
        }
        binding.mainHistory.setOnClickListener {
            navigateTo<HistoryActivity>()
        }
        binding.atvSetting.setOnClickListener {
            navigateTo<SixActivity>()
        }
    }


    private fun checkPermissions(nextFun: () -> Unit) {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
        )

        val permissionsNeeded = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        } else {
            nextFun()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions =
                grantResults.indices.filter { grantResults[it] != PackageManager.PERMISSION_GRANTED }
            if (deniedPermissions.isNotEmpty()) {
                showPermissionDeniedDialog()
            }else{
                navigateTo<RecordActivity>()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Recording and storage permissions are required to use this app. Please grant the permissions in your device settings.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Settings") { dialog, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
                dialog.dismiss()
            }
            .create()
            .show()
    }
}