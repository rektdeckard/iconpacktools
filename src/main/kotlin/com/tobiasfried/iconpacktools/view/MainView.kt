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
        prefHeight = 600.00
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        hgrow = Priority.ALWAYS
        tab<DrawableView>()
        tab<FilterMapperView>()
        tab("Resources") { isDisable = true }
        tab("Automation") { isDisable = true }
    }

}