package com.tobiasfried.iconpacktools.view

import javafx.scene.control.TabPane
import tornadofx.*

class FilterView : View("Filters") {

    override val root = tabpane {
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        tab<FilterEditorView>()
        tab<FilterMapperView>()
    }
}