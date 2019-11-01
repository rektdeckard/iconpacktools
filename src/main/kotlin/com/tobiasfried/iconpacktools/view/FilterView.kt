package com.tobiasfried.iconpacktools.view

import com.tobiasfried.iconpacktools.app.Styles.Companion.bold
import com.tobiasfried.iconpacktools.app.Styles.Companion.dropArea
import com.tobiasfried.iconpacktools.app.Styles.Companion.fieldLabel
import com.tobiasfried.iconpacktools.app.Styles.Companion.italic
import com.tobiasfried.iconpacktools.controller.FilterFormat
import com.tobiasfried.iconpacktools.utils.FileNameConverter
import com.tobiasfried.iconpacktools.utils.PathConverter
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
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

    private var dropTarget: Region by singleAssign()
    private var filterFile = SimpleObjectProperty<File>()
    private val acceptedFiles = listOf("appfilter.xml", "appmap.xml", "theme_resources.xml")

    override val root = borderpane {
        center = vbox(spacing = 8.0) {
            useMaxHeight = true
            borderpaneConstraints {
                marginLeftRight(8.0)
            }
            label("App Filter Generator") {
                vboxConstraints {
                    marginTop = 8.0
                }
            }.addClass(bold)
            textflow {
                text("This tool will map an existing app filter file to any of the other filter file formats. ")
                text("Click or use the drop target to add a filter file. Accepted files include:")
            }
            hbox(20) {
                text("appfilter.xml")
                text("appmap.xml")
                text("theme_resources.xml")
            }.addClass(italic)
            textflow {
                text("By default, the ")
                text(".xml").addClass(italic)
                text(" resources will be created in the same file as the last assets added to the list. ")
                text("Optionally, you may select the target destination for the resource files.")
            }
            form {
                vgrow = Priority.ALWAYS
                vbox(spacing = 16) {
                    hbox(spacing = 32) {
                        vbox {
                            vgrow = Priority.ALWAYS
                            fieldset("Input File") {
                                minHeight = 200.0
                                minWidth = 300.0
                                dropTarget = region {
                                    useMaxSize = true
                                    vgrow = Priority.ALWAYS
                                    label {
                                        bind(filterFile, true, FileNameConverter())
                                        alignment = Pos.CENTER
                                    }
                                }.addClass(dropArea)
                            }.addClass(fieldLabel)
                        }
                        separator(Orientation.VERTICAL)
                        fieldset("Output Options") {
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
                        }.addClass(fieldLabel)
                        separator(Orientation.VERTICAL)
                        fieldset("Destination") {
                            field {
                                vbox(spacing = 8) {
                                    togglegroup {
                                        radiobutton("Current directory") {
                                            isSelected = true
                                            action {
                                                specifyPath.set(false)
                                                filterFile.value?.let {
                                                    if (it.exists()) destinationPath.set(filterFile.value.toPath().parent)
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
                        }.addClass(fieldLabel)
                    }
                    fieldset("Target Directory") {
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
                    }.addClass(fieldLabel)
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

    init {
        dropTarget.setOnDragOver {
            if (it.gestureSource != dropTarget && it.dragboard.hasFiles()) {
                it.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
            }
            it.consume()
        }

        dropTarget.setOnDragDropped {
            val dragBoard = it.dragboard
            if (dragBoard.hasFiles()) {
                val newFilterFile = dragBoard.files[0]
                if (acceptedFiles.contains(newFilterFile.name)) {
                    filterFile.set(dragBoard.files[0])
                    destinationPath.set(filterFile.value.toPath().parent)
                }
                it.isDropCompleted = true
            } else it.isDropCompleted = false

            it.consume()
        }
    }

    private fun chooseDestination() {
        val selectedDestination = chooseDirectory("Select Destination", File("/"))
        selectedDestination?.let { destinationPath.set(selectedDestination.toPath()) }
    }

    private fun getFilterFormat(file: File): FilterFormat {
        return when (file.name) {
            "appfilter.xml" -> FilterFormat.APPFILTER
            "appmap.xml" -> FilterFormat.APPMAP
            "theme_resources.xml" -> FilterFormat.THEME_RESOURCES
            else -> FilterFormat.APPFILTER
        }
    }
}