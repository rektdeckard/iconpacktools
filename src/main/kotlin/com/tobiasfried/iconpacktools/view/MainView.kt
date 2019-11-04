package com.tobiasfried.iconpacktools.view

import javafx.scene.control.TabPane
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import tornadofx.*

class MainView : View("Icon Pack Tools") {

    init {
        setStageIcon(Image("bolt.png"))
    }

    override val root = tabpane {
        prefWidth = 1000.0
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
//        tabMinWidth = 108.0
        hgrow = Priority.ALWAYS
        tab<DrawableView>()
        tab<FilterView>()
        tab<ResourceView>()
        tab("Automation")

        shortcut("Ctrl+Q") {
            close()
        }
    }


}