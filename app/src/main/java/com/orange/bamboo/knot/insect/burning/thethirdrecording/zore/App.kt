package com.orange.bamboo.knot.insect.burning.thethirdrecording.zore

import android.app.Application
import android.content.Context
import com.orange.bamboo.knot.insect.burning.thethirdrecording.data.DatabaseChanging
import com.orange.bamboo.knot.insect.burning.thethirdrecording.data.DatabaseChanging.Companion.getBlackList
import java.util.UUID

class App : Application() {
    companion object {
        var appDatabase: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        appDatabase = this.applicationContext
        if(DatabaseChanging.TTRUUID.isBlank()){
            DatabaseChanging.TTRUUID = UUID.randomUUID().toString()
        }
        DatabaseChanging().blackUrl.getBlackList(this)
    }
}