package com.ismt.suitcase.utils

import android.content.Context
import com.ismt.suitcase.constants.AppConstants

class SharedPrefUtils(context: Context) {
    private val sharedPref = context.getSharedPreferences(AppConstants.FILE_SHARED_PREF, Context.MODE_PRIVATE)
    private val editor = sharedPref.edit()

    fun saveString(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPref.getString(key, defaultValue) ?: defaultValue
    }

    fun saveInt(key: String, value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPref.getInt(key, defaultValue)
    }

    fun saveBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPref.getBoolean(key, defaultValue)
    }

    fun removeKey(key: String) {
        editor.remove(key)
        editor.apply()
    }
}