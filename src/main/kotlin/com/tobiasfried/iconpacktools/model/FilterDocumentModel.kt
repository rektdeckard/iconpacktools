package com.tobiasfried.iconpacktools.model

import java.lang.StringBuilder

data class FilterDocumentModel(val version: Int = 1) {
    var deviceDefaults = arrayListOf<AppComponent>()
    var appComponents = arrayListOf<AppComponent>()

    override fun toString(): String {
        val str = StringBuilder()
        deviceDefaults.forEach{
            str.append(it.packageName)
            str.append(" # ")
            str.append(it.drawable)
            str.append("\n")
        }
        appComponents.forEach {
            str.append(it.packageName)
            str.append("/")
            str.append(it.activityName)
            str.append(" # ")
            str.append(it.drawable)
            str.append("\n")
        }
        return str.toString()
    }
}

data class AppComponent(val packageName: String, val activityName: String = "", val drawable: String)