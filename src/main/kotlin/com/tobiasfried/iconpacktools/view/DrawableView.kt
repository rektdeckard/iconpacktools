package com.tobiasfried.iconpacktools.view

import com.tobiasfried.iconpacktools.controller.DrawableController
import com.tobiasfried.iconpacktools.controller.DrawableOutput
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.control.TextFormatter
import javafx.scene.layout.Priority
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import javafx.util.StringConverter
import tornadofx.*
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Callable

class DrawableView : View("Drawables") {


    private val updateProgress: (Double) -> Unit = { generateProgress.set(it) }
    private val controller = DrawableController(updateProgress)

    private val generateDrawable = SimpleBooleanProperty(true)
    private val generateIconPack = SimpleBooleanProperty(false)
    private val overwriteExisting = SimpleBooleanProperty(false)
    private val specifyPath = SimpleBooleanProperty(false)
    private val destinationPath = SimpleObjectProperty<Path>(PathConverter().fromString("C:\\"))
    private val validDestination: BooleanBinding = Bindings.createBooleanBinding(Callable {
        File(destinationPath.value.toString().trim()).exists()
    }, destinationPath)

    private var generateProgress = SimpleDoubleProperty(0.0)

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
                    label("Files") {
                        style { fontWeight = FontWeight.BOLD }
                    }
                }
                label {
                    visibleWhen(files.sizeProperty.isNotEqualTo(0))
                    bind(files.sizeProperty)
                }
            }

            listview(files) {
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
                        updateProgress(0.0)
                    }
                    shortcut("Ctrl+O")
                }
                button("Remove") {
                    enableWhen(selectedFiles.isNotNull)
                    action {
                        files.removeAll(selectedFiles.value)
                        updateProgress(0.0)
                    }
                    shortcut("Ctrl+X")
                }
                button("Clear") {
                    enableWhen(files.sizeProperty.isNotEqualTo(0))
                    action {
                        files.clear()
                        updateProgress(0.0)
                    }
                }
            }

        }

        center = vbox(spacing = 16) {
            borderpaneConstraints { marginLeftRight(8.0) }
            label("Icon Resource Generator") {
                style { fontWeight = FontWeight.BOLD }
                vboxConstraints {
                    marginTop = 8.0
                }
            }
            textflow {
                text("This tool will generate a ")
                text("drawable.xml") { style { fontStyle = FontPosture.ITALIC } }
                text(" and ")
                text("icon-pack.xml") { style { fontStyle = FontPosture.ITALIC } }
                text(" file for the provided ")
                text(".png") { style { fontStyle = FontPosture.ITALIC } }
                text(" icons. ")
                text("Use the add button or drag-and-drop your files or directory into the pane on the left, then select your settings below. ")
            }
            textflow {
                text("By default, the ")
                text(".xml") { style { fontStyle = FontPosture.ITALIC } }
                text(" resources will be created in the same directory as the last assets added to the list. ")
                text("Optionally, you may select the target destination for the resource files.")
            }
            form {
                vgrow = Priority.ALWAYS
                vbox(spacing = 16) {
                    hbox(spacing = 32) {
                        fieldset("Output Options") {
                            style {
                                fontSize = 12.px
                            }
                            field {
                                checkbox("Generate drawable.xml").bind(generateDrawable)
                            }
                            field {
                                checkbox("Generate icon-pack.xml").bind(generateIconPack)
                            }
                            field {
                                checkbox("Overwrite existing").bind(overwriteExisting)
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
                    enableWhen(files.sizeProperty.greaterThan(0)
                            .and(generateDrawable.or(generateIconPack))
                            .and(validDestination))
                    action {
                        runAsync {
                            //                            for (i in 1..100) {
//                                Platform.runLater { generateProgress.set(i.toDouble() / 100.0) }
//                                Thread.sleep(10)
//                            }
                            val outputType: DrawableOutput =
                                    if (generateDrawable.value && generateIconPack.value) DrawableOutput.BOTH
                                    else if (generateDrawable.value) DrawableOutput.DRAWABLE
                                    else DrawableOutput.ICON_PACK
                            controller.generateXML(files, outputType, destinationPath.value)
                        }
                    }
                }
            }
        }

        bottom = progressbar {
            borderpaneConstraints { marginTop = 8.0 }
            useMaxWidth = true
            bind(generateProgress)
        }
    }

    private fun chooseDestination() {
        val selectedDestination = chooseDirectory("Select Destination", File("/"))
        selectedDestination?.let { destinationPath.set(selectedDestination.toPath()) }
    }
}

class PathConverter : StringConverter<Path>() {
    override fun toString(path: Path?): String {
        path?.let { return it.toString() }
        return ""
    }

    override fun fromString(string: String?): Path {
        string?.let {
            return Paths.get(string.trim())
        }
        return Paths.get("C:\\")
    }
}