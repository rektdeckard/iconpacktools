package com.tobiasfried.iconpacktools.model

import java.lang.StringBuilder

data class BaseFilterDocument(val version: Int = 1) {
    var deviceDefaults = arrayListOf<AppComponent>()
    var appComponents = arrayListOf<AppComponent>()

    override fun toString(): String {
        val str = StringBuilder()
        deviceDefaults.forEach{
            with (str) {
                append(it.packageName)
                append(" # ")
                append(it.drawable)
                append("\n")
            }
        }
        appComponents.forEach {
            with (str) {
                append(it.packageName)
                append("/")
                append(it.activityName)
                append(" # ")
                append(it.drawable)
                append("\n")
            }
        }
        return str.toString()
    }
}

data class AppComponent(val packageName: String, val activityName: String = "", val drawable: String)