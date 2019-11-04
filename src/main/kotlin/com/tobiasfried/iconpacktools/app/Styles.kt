package com.tobiasfried.iconpacktools.app

import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val fieldLabel by cssclass()
        val italic by cssclass()
        val bold by cssclass()
        val dropArea by cssclass()
        val statusSuccess by cssclass()
    }

    init {
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        fieldset and fieldLabel { fontSize = 12.px }

        italic { fontStyle = FontPosture.ITALIC }

        bold { fontWeight = FontWeight.BOLD }

        dropArea {
            borderWidth += box(1.px)
            backgroundColor += Color.WHITE
            borderColor += box(Color.LIGHTGRAY)
        }

        statusSuccess {
            textFill = Color.WHITE
        }
    }
}