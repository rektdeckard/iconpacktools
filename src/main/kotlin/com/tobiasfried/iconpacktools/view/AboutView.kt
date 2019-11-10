package com.tobiasfried.iconpacktools.view

import com.tobiasfried.iconpacktools.app.Styles.Companion.heroImage
import javafx.geometry.Pos
import tornadofx.*

class AboutView : View("About") {

    override val root = borderpane {
        top = vbox {
            imageview("ipt.png", true) {
                fitWidth = 800.0
                fitHeight = 400.0
                vboxConstraints {
                    alignment = Pos.CENTER
                }
            }
        }.addClass(heroImage)
    }
}