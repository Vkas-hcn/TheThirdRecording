package com.orange.bamboo.knot.insect.burning.thethirdrecording.data

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.orange.bamboo.knot.insect.burning.thethirdrecording.net.RetrofitClient
import com.orange.bamboo.knot.insect.burning.thethirdrecording.zore.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class DatabaseChanging {
    val blackUrl = "https://forth.voicemasterrecord.com/jessie/hearken/toddle"

    companion object {
        private val spTTR by lazy {
            App.appDatabase?.getSharedPreferences(
                "TTR",
                Context.MODE_PRIVATE
            )
        }

        var TTRUUID = ""
            set(value) {
                spTTR?.edit()?.run {
                    putString("TTRUUID", value)
                    commit()
                }
                field = value
            }
            get() = spTTR?.getString("TTRUUID", "").toString()

        var TTRBD = ""
            set(value) {
                spTTR?.edit()?.run {
                    putString("TTRBD", value)
                    commit()
                }
                field = value
            }
            get() = spTTR?.getString("TTRBD", "").toString()

        fun String.getBlackList(context: Context) {
            if (TTRBD.isNotEmpty()) {
                return
            }
            GlobalScope.launch(Dispatchers.IO) {
                DatabaseChanging().getMapData(this@getBlackList, DatabaseChanging().cloakMapData(context), onNext = {
                    Log.e("TAG", "The blacklist request is successful：$it")
                    TTRBD = it
                }, onError = {
                    GlobalScope.launch(Dispatchers.IO) {
                        Log.e("TAG", "getBlackList: onError=${it}")
                        delay(10000)
                        DatabaseChanging().blackUrl.getBlackList(context)
                    }
                })
            }
        }
    }

    fun cloakMapData(context: Context): Map<String, Any> {
        return mapOf<String, Any>(
            //bundle_id
            "quad" to ("com.voice.master.record.diary.sound"),
            //os
            "severn" to "pervert",
            //app_version
            "iodine" to context.getAppVersion(),
            //distinct_id
            "bathtub" to (TTRUUID),
            //client_ts
            "gridlock" to (System.currentTimeMillis()),
            //device_model
            "antonym" to Build.MODEL,
            //os_version
            "mcconnel" to Build.VERSION.RELEASE,
            //gaid
            "burnout" to "",
            //android_id
            "met" to context.getAppId(),
        )
    }

    private fun Context.getAppVersion(): String {
        try {
            val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)

            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "Version information not available"
    }

    private fun Context.getAppId(): String {
        return Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun getMapData(
        url: String,
        map: Map<String, Any>,
        onNext: (response: String) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val queryMap = map.mapValues { it.value.toString() }

        val call = RetrofitClient.apiService.getMapData(url, queryMap)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body() != null) {
                    onNext(response.body()!!)
                } else {
                    onError("HTTP error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                onError("Network error: ${t.message}")
            }
        })
    }


}