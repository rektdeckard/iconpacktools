package com.tobiasfried.iconpacktools.view

import javafx.scene.image.Image
import tornadofx.*

class MainView : View("Icon Pack Tools") {

    init {
        setStageIcon(Image("ipt.png"))
    }

    override val root = tabpane {
        prefWidth = 1000.0
        tab<DrawableView>()
        tab<FilterView>()
        tab("Resource Files")
        tab("Automation")

        shortcut("Ctrl+Q") {
            close()
        }
    }


}