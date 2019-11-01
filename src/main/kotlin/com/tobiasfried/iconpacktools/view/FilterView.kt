package com.tobiasfried.iconpacktools.view

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import tornadofx.*
import java.io.File
import java.nio.file.Path
import java.util.concurrent.Callable

class FilterView : View("Filters") {

    private val specifyPath = SimpleBooleanProperty(false)
    private val destinationPath = SimpleObjectProperty<Path>(PathConverter().fromString("C:\\"))
    private val validDestination: BooleanBinding = Bindings.createBooleanBinding(Callable {
        File(destinationPath.value.toString().trim()).exists()
    }, destinationPath)

    private var filterFile = SimpleObjectProperty<File>()

    override val root = borderpane {
        center = vbox(spacing = 8.0) {
            useMaxHeight = true
            borderpaneConstraints {
                marginLeftRight(8.0)
            }
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
                vgrow = Priority.ALWAYS
                vbox(spacing = 16) {
                    hbox(spacing = 32) {
                        fieldset("Input File") {
                            style {
                                fontSize = 12.px
                            }
                            field {
                                region { fitToParentWidth(); paddingAll = 8.0; style { baseColor = c("#444444") } }
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
                                        radiobutton("Current directory") {
                                            isSelected = true
                                            action {
                                                specifyPath.set(false)
                                                if (filterFile.value.exists()) {
                                                    destinationPath.set(filterFile.value.toPath().parent)
                                                }
                                            }
                                        }
                                        radiobutton("Specify directory") {
                                            action { specifyPath.set(true) }
                                        }.setOnMouseClicked {
                                            chooseDestination()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    fieldset("Target Directory") {
                        style {
                            fontSize = 12.px
                        }
                        field {
                            hgrow = Priority.ALWAYS
                            textfield {
                                bind(destinationPath, false, PathConverter())
                                enableWhen(specifyPath)
                            }
                        }
                        label("Enter a valid directory") {
                            visibleWhen(!validDestination)
                        }
                    }
                }
            }
            buttonbar {
                button("Generate") {
                }
            }
        }

        bottom = progressbar(0.0) {
            borderpaneConstraints { marginTop = 8.0 }
            useMaxWidth = true
        }
    }

    private fun chooseDestination() {
        val selectedDestination = chooseDirectory("Select Destination", File("/"))
        selectedDestination?.let { destinationPath.set(selectedDestination.toPath()) }
    }
}