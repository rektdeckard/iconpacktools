package com.tobiasfried.iconpacktools.view

import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import tornadofx.*

class FilterView : View("Filters") {
    override val root = borderpane {
        center = vbox(spacing = 8.0) {
            useMaxHeight = true
            borderpaneConstraints { marginLeftRight(16.0) }
            label("App Filter Generator") {
                style { fontWeight = FontWeight.BOLD }
                vboxConstraints {
                    marginTop = 8.0
                }
            }
            textflow {
                text("This tool will map an existing app filter file to any of the other filter filetypes. ")
                text("Click or use the drop target to add a filter file. Accepted files include:")
            }
            hbox(20) {
                style {
                    fontStyle = FontPosture.ITALIC
                }
                text("appfilter.xml")
                text("appmap.xml")
                text("theme_resources.xml")
            }
            textflow {
                text("By default, the .xml resources will be created in the same file as the last assets added to the list. ")
                text("Optionally, you may select the target destination for the resource files.")
            }
            form {
                hbox(spacing = 32) {
                    fieldset("Input File") {
                        style {
                            fontSize = 12.px
                        }
                        field {
                            region { fitToParentWidth(); paddingAll = 8.0; style { baseColor = c("#444444")} }
                        }
                    }
                    separator(Orientation.VERTICAL)
                    fieldset("Output Options") {
                        style {
                            fontSize = 12.px
                        }
                        field {
                            checkbox("Generate appfilter.xml")
                        }
                        field {
                            checkbox("Generate appmap.xml")
                        }
                        field {
                            checkbox("Generate theme-resources.xml")
                        }
                        field {
                            checkbox("Overwrite existing")
                        }
                    }
                    separator(Orientation.VERTICAL)
                    fieldset("Destination") {
                        style {
                            fontSize = 12.px
                        }
                        field {
                            vbox(spacing = 8) {
                                togglegroup {
                                    radiobutton("Current directory") { isSelected = true }
                                    radiobutton("Specify directory") {
                                        //                                                textfield()
                                    }
                                }
                            }
                        }
                        field("Target directory:") {
                            textfield { }
                        }
                    }
                }
            }
            buttonbar {
                style {
                    alignment = Pos.BOTTOM_RIGHT
                }
                button("Generate") {
                }
            }
        }

        bottom = progressbar(0.0) {
            borderpaneConstraints { marginTop = 8.0 }
            useMaxWidth = true
        }
    }
}