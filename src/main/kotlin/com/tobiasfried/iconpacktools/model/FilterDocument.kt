package com.tobiasfried.iconpacktools.model

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import java.lang.StringBuilder

data class FilterDocument(val version: Int = 1) {
    val deviceDefaults: ObservableList<AppComponent> = FXCollections.observableArrayList<AppComponent>()
    val appComponents: ObservableList<AppComponent> = FXCollections.observableArrayList<AppComponent>()

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

class FilterDocumentModel(document: FilterDocument) : ItemViewModel<FilterDocument>(document) {
    val deviceDefaults = bind(FilterDocument::deviceDefaults)
    val appComponents = bind(FilterDocument::appComponents)
}

class AppComponent(packageName: String? = null, activityName: String? = null, drawable: String? = null) {
    val packageNameProperty = SimpleStringProperty(this, "packageName", packageName)
    var packageName: String by packageNameProperty

    val activityNameProperty = SimpleStringProperty(this, "activityName", activityName)
    var activityName: String by activityNameProperty

    val drawableProperty = SimpleStringProperty(this, "drawable", drawable)
    var drawable: String by drawableProperty
}

class AppComponentModel(component: AppComponent) : ItemViewModel<AppComponent>(component) {
    val packageName = bind(AppComponent::packageNameProperty)
    val activityName = bind(AppComponent::activityNameProperty)
    val drawable = bind(AppComponent::drawableProperty)
}