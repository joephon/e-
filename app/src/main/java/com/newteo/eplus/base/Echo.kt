package com.newteo.eplus.base

import android.util.Log

object Echo {
    private val levelMap = mapOf<String, Int>("Verbose" to 0, "Debug" to 1, "Info" to 2, "Warn" to 3, "Error" to 4)
    private var level = levelMap["Verbose"] // Debug, Info, Warn, Error

    fun v(tag: String, msg: String) = if (level!! <= 0) Log.v(tag, msg) else {}

    fun d(tag: String, msg: String) = if (level!! <= 1) Log.d(tag, msg) else {}

    fun i(tag: String, msg: String) = if (level!! <= 2) Log.i(tag, msg) else {}

    fun w(tag: String, msg: String) = if (level!! <= 3) Log.w(tag, msg) else {}

    fun e(tag: String, msg: String) = if (level!! <= 4) Log.e(tag, msg) else {}
}