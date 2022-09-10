package com.newteo.eplus.base

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


fun Int.toast(duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(EplusApplication.context, this, duration)
    .show()

fun String.toast(duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(EplusApplication.context, this, duration)
    .show()

fun String.snake(view: View, tip: String = "确定", duration: Int = Snackbar.LENGTH_LONG, block: (View) -> Unit) {
    Snackbar.make(view, this, duration).apply {
        setAction(tip, block)
        show()
    }
}

fun file2Json(fileName: String): String? {
    val stringBuilder = StringBuilder()
    try {
        val assetManager: AssetManager = EplusApplication.context.assets

        val isr = InputStreamReader(assetManager.open(fileName))
        val bf = BufferedReader(isr)
        var line: String?
        while (bf.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
        bf.close()
        isr.close()
        return stringBuilder.toString()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

inline fun <reified T>json2List(jsonStr: String?): ArrayList<T> {
    val list = ArrayList<T>()
    val data = JSONArray(jsonStr)
    val gson = Gson()
    for (i in 0 until data.length()) {
        val entity: T = gson.fromJson(data.optJSONObject(i).toString(), T::class.java)
        list.add(entity)
    }
    return list
}

class EplusApplication : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        Echo.d("global context", "$context")

    }
}