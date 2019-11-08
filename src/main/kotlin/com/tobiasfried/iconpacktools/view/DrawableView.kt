package com.tobiasfried.iconpacktools.view

import com.tobiasfried.iconpacktools.app.Styles
import com.tobiasfried.iconpacktools.app.Styles.Companion.bold
import com.tobiasfried.iconpacktools.app.Styles.Companion.fieldLabel
import com.tobiasfried.iconpacktools.app.Styles.Companion.italic
import com.tobiasfried.iconpacktools.controller.DrawableController
import com.tobiasfried.iconpacktools.controller.DrawableOutput
import com.tobiasfried.iconpacktools.utils.PathConverter
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.control.ListView
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Callable

class DrawableView : View("Drawables") {

    private val updateProgress: (Double, String?) -> Unit = { progress, message ->
        generateProgress.set(progress)
        statusMessage.set(message)
    }
    private val controller = DrawableController(updateProgress)

    private val generateDrawable = SimpleBooleanProperty(true)
    private val generateIconPack = SimpleBooleanProperty(false)
    private val overwriteExisting = SimpleBooleanProperty(false)
    private val specifyPath = SimpleBooleanProperty(false)
    private val destinationPath = SimpleObjectProperty<Path>(Paths.get(""))
    private val validDestination = Bindings.createBooleanBinding(Callable {
        !specifyPath.value || File(destinationPath.value.toString().trim()).exists()
    }, specifyPath, destinationPath)

    private var generateProgress = SimpleDoubleProperty(0.0)
    private val statusMessage = SimpleStringProperty()
    private val statusComplete = Bindings.createBooleanBinding(Callable { generateProgress.value.equals(1.0) }, generateProgress)

    private var filesList: ListView<File> by singleAssign()
    private var files = FXCollections.observableArrayList<File>()
    private var selectedFiles = SimpleObjectProperty<File>()

    override val root = borderpane {
        left = vbox(spacing = 8.0) {
            borderpaneConstraints { marginLeftRight(8.0) }

            hbox {
                vboxConstraints {
                    marginTop = 8.0
                }
                hbox {
                    hgrow = Priority.ALWAYS
                    label("Files").addClass(bold)
                }
                label {
                    visibleWhen(files.sizeProperty.isNotEqualTo(0))
                    bind(files.sizeProperty)
                }
            }

            filesList = listview(files) {
                vgrow = Priority.ALWAYS
                multiSelect(true)
                bindSelected(selectedFiles)
                cellFormat { graphic = label(it.name) }
            }

            buttonbar {
                button("Add") {
                    hgrow = Priority.ALWAYS
                    action {
                        val newFiles = chooseFile("Select Icons", arrayOf(FileChooser.ExtensionFilter("PNG", "*.png")), FileChooserMode.Multi)
                        if (newFiles.isNotEmpty() && !specifyPath.value) {
                            destinationPath.set(newFiles[0].toPath().parent)
                        }
                        files.addAll(newFiles.filter { !files.contains(it) })
                        updateProgress(0.0, null)
                    }
                    shortcut("Ctrl+O")
                }
                button("Remove") {
                    enableWhen(selectedFiles.isNotNull)
                    action {
                        files.removeAll(selectedFiles.value)
                        updateProgress(0.0, null)
                    }
                    shortcut("Ctrl+X")
                }
                button("Clear") {
                    enableWhen(files.sizeProperty.isNotEqualTo(0))
                    action {
                        files.clear()
                        updateProgress(0.0, null)
                    }
                }
            }

        }

        center = vbox(spacing = 16) {
            borderpaneConstraints { marginLeftRight(8.0) }
            label("Icon Resource Generator") {
                vboxConstraints { marginTop = 8.0 }
            }.addClass(bold)
            textflow {
                text("This tool will generate a ")
                text("drawable.xml").addClass(italic)
                text(" and ")
                text("icon-pack.xml").addClass(italic)
                text(" file for the provided ")
                text(".png").addClass(italic)
                text(" icons. ")
                text("Use the add button or drag-and-drop your files or directory into the pane on the left, then select your settings below. ")
            }
//            textflow {
//                text("By default, the ")
//                text(".xml").addClass(italic)
//                text(" resources will be created in the same directory as the last assets added to the list. ")
//                text("Optionally, you may select the target destination for the resource files.")
//            }
            form {
                vgrow = Priority.ALWAYS
                vbox(spacing = 16) {
                    hbox(spacing = 32) {
                        fieldset("Output Options") {
                            field {
                                checkbox("Generate drawable.xml").bind(generateDrawable)
                            }
                            field {
                                checkbox("Generate icon-pack.xml").bind(generateIconPack)
                            }
                            field {
                                checkbox("Overwrite existing").bind(overwriteExisting)
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
                                                if (files.isNotEmpty()) {
                                                    destinationPath.set(files[files.lastIndex].toPath().parent)
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
                    enableWhen(files.sizeProperty.greaterThan(0)
                            .and(generateDrawable.or(generateIconPack))
                            .and(validDestination))
                    action {
                        updateProgress(0.0, null)
                        val outputType: DrawableOutput =
                                if (generateDrawable.value && generateIconPack.value) DrawableOutput.BOTH
                                else if (generateDrawable.value) DrawableOutput.DRAWABLE
                                else DrawableOutput.ICON_PACK
                        controller.createXML(files, destinationPath.value, overwriteExisting.value, outputType)
                    }
                }
            }
        }

        bottom = stackpane {
            borderpaneConstraints { marginTop = 8.0 }
            progressbar {
                useMaxWidth = true
                bind(generateProgress)
            }
            label {
                bind(statusMessage)
                toggleClass(Styles.statusSuccess, statusComplete)
            }
        }
    }

    init {
        filesList.setOnDragOver {
            if (it.gestureSource != filesList && it.dragboard.hasFiles()) {
                it.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
            }
            it.consume()
        }

        filesList.setOnDragDropped {
            val dragBoard = it.dragboard
            if (dragBoard.hasFiles()) {
                val newFiles = dragBoard.files
                files.addAll(newFiles.filter { newFile ->
                    !files.contains(newFile) && newFile.extension.toLowerCase() == "png"
                })
                destinationPath.set(newFiles[0].toPath().parent)
                updateProgress(0.0, null)
                it.isDropCompleted = true
            } else it.isDropCompleted = false

            it.consume()
        }
    }

    private fun chooseDestination() {
        val selectedDestination = chooseDirectory("Select Destination", File("/"))
        selectedDestination?.let { destinationPath.set(selectedDestination.toPath()) }
    }
}
