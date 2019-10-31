package com.tobiasfried.iconpacktools.view

import javafx.scene.image.Image
import tornadofx.*

class MainView : View("Icon Pack Tools") {

    init {
        setStageIcon(Image("compass_alt.png"))
    }

    private val drawableView: DrawableView by inject()

    override val root = tabpane {
        maxWidth = 1000.0
        tab<DrawableView>()
        tab<FilterView>()
        tab("Resource Files")
        tab("Automation")

        shortcut("Ctrl+Q") {
            close()
        }
    }


}