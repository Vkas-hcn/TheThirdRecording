package com.orange.bamboo.knot.insect.burning.thethirdrecording.show.six

import android.content.Intent
import android.net.Uri
import com.orange.bamboo.knot.insect.burning.thethirdrecording.R
import com.orange.bamboo.knot.insect.burning.thethirdrecording.databinding.ActivitySettingBinding
import com.orange.bamboo.knot.insect.burning.thethirdrecording.zore.BaseActivity

class SixActivity : BaseActivity<ActivitySettingBinding, SixViewModel>() {
    override val layoutId: Int = R.layout.activity_setting
    override val viewModelClass: Class<SixViewModel> = SixViewModel::class.java

    override fun initViewComponents() {
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.conPp.setOnClickListener {
            openWeb("https://www.baidu.com")
        }
    }

    override fun initializeData() {
    }

    private fun openWeb(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

}