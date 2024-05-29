package com.orange.bamboo.knot.insect.burning.thethirdrecording.data.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.text.SimpleDateFormat
import java.util.Date


object UtilsRecord {

    fun showKeyboard(context: Context, view: View, isOpen: Boolean) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (isOpen) {
            view.requestFocus()
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        } else {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateNow(): String {
        val now1 = Date()
        val format = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
        return format.format(now1)
    }

}