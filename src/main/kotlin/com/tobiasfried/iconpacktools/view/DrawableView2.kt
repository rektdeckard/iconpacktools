package com.tobiasfried.iconpacktools.view

import com.tobiasfried.iconpacktools.app.Styles
import com.tobiasfried.iconpacktools.app.Styles.Companion.bold
import com.tobiasfried.iconpacktools.app.Styles.Companion.fieldLabel
import com.tobiasfried.iconpacktools.app.Styles.Companion.italic
import com.tobiasfried.iconpacktools.controller.DrawableController
import com.tobiasfried.iconpacktools.controller.DrawableOutput
import com.tobiasfried.iconpacktools.utils.PathConverter
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import sun.reflect.generics.tree.Tree
import tornadofx.*
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Callable

class DrawableView2 : View("Drawables2") {

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
    private val validDestination: BooleanBinding = Bindings.createBooleanBinding(Callable {
        !specifyPath.value || File(destinationPath.value.toString().trim()).exists()
    }, specifyPath, destinationPath)

    private var generateProgress = SimpleDoubleProperty(0.0)
    private val statusMessage = SimpleStringProperty()
    private val statusComplete = Bindings.createBooleanBinding(Callable { generateProgress.value.equals(1.0) }, generateProgress)

    private val files = FXCollections.observableArrayList<File>(File("C:\\Users\\Tobias Fried\\Google Drive\\Phosphor\\scratch\\test").walkTopDown().toList().filter { it.isFile })
    private val folders = Bindings.createObjectBinding(Callable {
        files.groupBy { File(it.parent) }
    }, files)
    private val selectedFiles = SimpleObjectProperty<File>()
    private var filesList: TreeView<File> by singleAssign()

    private val makeTreeNodes: (File) -> Node = {
        text(it.name)
    }

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

            filesList = treeview {
                vgrow = Priority.ALWAYS
                root = TreeItem(File("All"))
                isShowRoot = false
                isEditable = true

                cellFormat { text = it.name }

                populate { node ->
                    val item = node.value
                    when {
                        node == root -> folders.value.keys
                        item is File -> folders.value[item]
                        else -> null
                    }
                }

//                bindChildren(files) {  }
                bindSelected(selectedFiles)
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
                        controller.createXML(files, outputType, destinationPath.value)
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
                flattenAndAddFiles(dragBoard.files)
                it.isDropCompleted = true
            } else it.isDropCompleted = false

            it.consume()
        }
    }

    private fun flattenAndAddFiles(newFiles: List<File>) {
        val flattenedFiles = ArrayList<File>()
        newFiles.forEach { file ->
            if (file.isDirectory) flattenedFiles.addAll(file.walkTopDown().toList().filter { it.isFile })
            else flattenedFiles.add(file)
        }

        files.addAll(flattenedFiles.filter { !files.contains(it) && it.extension.toLowerCase() == "png" })
        destinationPath.set(flattenedFiles[0].toPath().parent)
        updateProgress(0.0, null)

        filesList.refresh()
    }

    private fun chooseDestination() {
        val selectedDestination = chooseDirectory("Select Destination", File("/"))
        selectedDestination?.let { destinationPath.set(selectedDestination.toPath()) }
    }
}
