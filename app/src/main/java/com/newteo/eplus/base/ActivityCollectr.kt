package com.newteo.eplus.base

import android.app.Activity
import android.util.Log

object ActivityCollector {
    private val activities = ArrayList<Activity>()

    fun addActivity(activity: Activity) {
        activities.add(activity)
        Echo.d("whois", "added activity -> $activity")
    }

    fun removeActivity(activity: Activity) {
        if (!activity.isFinishing) {
            activity.finish()
        }
        activities.remove(activity)
        Echo.d("whois", "removed activity -> $activity")
    }

    fun clearAll() {
        for (i in activities) {
            removeActivity(i)
        }
        Echo.d("whois", "clear all activities -> $activities")
    }
}