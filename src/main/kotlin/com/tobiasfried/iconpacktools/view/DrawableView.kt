package com.tobiasfried.iconpacktools.view

import com.tobiasfried.iconpacktools.app.Styles
import com.tobiasfried.iconpacktools.app.Styles.Companion.bold
import com.tobiasfried.iconpacktools.app.Styles.Companion.fieldLabel
import com.tobiasfried.iconpacktools.app.Styles.Companion.italic
import com.tobiasfried.iconpacktools.controller.DrawableController
import com.tobiasfried.iconpacktools.utils.PathConverter
import javafx.geometry.Orientation
import javafx.scene.control.ListView
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import tornadofx.*
import java.io.File

class DrawableView : View("Drawables") {
    private val controller: DrawableController by inject()
    private var filesList: ListView<File> by singleAssign()

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
                    visibleWhen(controller.files.sizeProperty.isNotEqualTo(0))
                    bind(controller.files.sizeProperty)
                }
            }

            filesList = listview(controller.files) {
                vgrow = Priority.ALWAYS
                multiSelect(true)
                bindSelected(controller.selectedFiles)
                cellFormat { graphic = label(it.name) }
            }

            buttonbar {
                button("Add") {
                    action { controller.selectFiles() }
                    shortcut("Ctrl+O")
                }
                button("Remove") {
                    enableWhen(controller.selectedFiles.isNotNull)
                    action { controller.removeSelected() }
                    shortcut("Ctrl+X")
                }
                button("Clear") {
                    enableWhen(controller.files.sizeProperty.isNotEqualTo(0))
                    action { controller.clearFiles() }
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
                text("icon_pack.xml").addClass(italic)
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
                                checkbox("Generate drawable.xml").bind(controller.generateDrawable)
                            }
                            field {
                                checkbox("Generate icon_pack.xml").bind(controller.generateIconPack)
                            }
                            field {
                                checkbox("Use directories as category names").bind(controller.useCategories)
                                tooltip("Group your drawables into directories by category, and the categories will be generated from the directory names. The \"All\" category will be automatically created.")
                            }
                            field {
                                checkbox("Overwrite existing").bind(controller.overwriteExisting)
                                tooltip("Replaces existing files without confirmation.")
                            }
                        }.addClass(fieldLabel)
                        separator(Orientation.VERTICAL)
                        fieldset("Destination") {
                            field {
                                vbox(spacing = 8) {
                                    togglegroup {
                                        radiobutton("Current directory") {
                                            isSelected = true
                                            action { controller.useCurrentDirectory() }
                                        }
                                        radiobutton("Specify directory") {
                                            action { controller.specifyPath.set(true) }
                                        }.setOnMouseClicked {
                                            controller.chooseDestination()
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
                                bind(controller.destinationPath, false, PathConverter())
                                enableWhen(controller.specifyPath)
                            }
                        }
                        label("Enter a valid directory") {
                            visibleWhen(!controller.validDestination)
                        }
                    }.addClass(fieldLabel)
                }
            }
            buttonbar {
                button("Generate") {
                    enableWhen(controller.files.sizeProperty.greaterThan(0)
                            .and(controller.generateDrawable.or(controller.generateIconPack))
                            .and(controller.validDestination))
                    action { controller.generate() }
                }
            }
        }

        bottom = stackpane {
            borderpaneConstraints { marginTop = 8.0 }
            progressbar {
                useMaxWidth = true
                bind(controller.generateProgress)
            }
            label {
                bind(controller.statusMessage)
                toggleClass(Styles.statusSuccess, controller.statusComplete)
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
                controller.flattenAndAddFiles(dragBoard.files)
                it.isDropCompleted = true
            } else it.isDropCompleted = false

            it.consume()
        }
    }

}
