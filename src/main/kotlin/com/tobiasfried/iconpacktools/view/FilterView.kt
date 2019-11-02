package com.tobiasfried.iconpacktools.view

import com.tobiasfried.iconpacktools.app.Styles.Companion.bold
import com.tobiasfried.iconpacktools.app.Styles.Companion.dropArea
import com.tobiasfried.iconpacktools.app.Styles.Companion.fieldLabel
import com.tobiasfried.iconpacktools.app.Styles.Companion.italic
import com.tobiasfried.iconpacktools.controller.FilterController
import com.tobiasfried.iconpacktools.controller.FilterFormat
import com.tobiasfried.iconpacktools.utils.FileNameConverter
import com.tobiasfried.iconpacktools.utils.PathConverter
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.paint.Paint
import javafx.scene.shape.FillRule
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Callable

class FilterView : View("Filters") {

    private val updateProgress: (Double) -> Unit = { generateProgress.set(it) }
    private val controller = FilterController(updateProgress)

    private val generateAppMap = SimpleBooleanProperty(true)
    private val generateThemeResources = SimpleBooleanProperty(true)
    private val overwriteExisting = SimpleBooleanProperty(false)
    private val specifyPath = SimpleBooleanProperty(false)
    private val destinationPath = SimpleObjectProperty<Path>(Paths.get(""))
    private val validDestination: BooleanBinding = Bindings.createBooleanBinding(Callable {
        File(destinationPath.value.toString().trim()).exists()
    }, destinationPath)

    private val generateProgress = SimpleDoubleProperty(0.0)

    private var dropTarget: Region by singleAssign()
    private var filterFile = SimpleObjectProperty<File>()
    private val acceptedFiles = listOf("appfilter.xml")
    private val outTypes = Bindings.createObjectBinding<Array<FilterFormat>>(Callable {
        if (generateAppMap.value && generateThemeResources.value) {
            arrayOf(FilterFormat.APPMAP, FilterFormat.THEME_RESOURCES)
        } else if (generateAppMap.value) {
            arrayOf(FilterFormat.APPMAP)
        } else if (generateThemeResources.value) {
            arrayOf(FilterFormat.THEME_RESOURCES)
        } else arrayOf()
    }, generateAppMap, generateThemeResources)

    override val root = borderpane {
        center = vbox(spacing = 8.0) {
            borderpaneConstraints {
                marginLeftRight(8.0)
            }
            label("App Filter Mapper") {
                vboxConstraints {
                    marginTop = 8.0
                }
            }.addClass(bold)
            textflow {
                text("This tool will map an existing ")
                text("appfilter.xml").addClass(italic)
                text(" file to either of the other filter file formats: ")
                text("appfilter.xml").addClass(italic)
                text(" or ")
                text("theme_resources.xml.").addClass(italic)
            }
            textflow {
                text("Click or use the drop target to add a filter file.")
                text("By default, the ")
                text(".xml").addClass(italic)
                text(" resources will be created in the same file as the last assets added to the list. ")
                text("Optionally, you may select the target destination for the resource files.")
            }
            form {
                vgrow = Priority.ALWAYS
                vbox(spacing = 16) {
                    hbox(spacing = 32) {
                        hbox {
                            fieldset("Input File") {
                                dropTarget = stackpane {
                                    alignment = Pos.CENTER
                                    prefHeight = 256.0
                                    prefWidth = 256.0
                                    setOnMouseClicked { chooseFile() }

                                    vbox(spacing = 16) {
                                        alignment = Pos.CENTER
                                        visibleWhen { filterFile.isNull }
                                        svgicon("M11.0002,27.2502 C11.3797,27.2502 11.6936583,27.5316931 11.7433493,27.8982642 L11.7502,28.0002 L11.7502,33.2502 L17.0002,33.2502 C17.4142,33.2502 17.7502,33.5852 17.7502,34.0002 C17.7502,34.3797 17.4678667,34.6936583 17.1019257,34.7433493 L17.0002,34.7502 L11.0002,34.7502 C10.6207,34.7502 10.3067417,34.4678667 10.2570507,34.1019257 L10.2502,34.0002 L10.2502,28.0002 C10.2502,27.5852 10.5862,27.2502 11.0002,27.2502 Z M37.0002,27.2502 C37.3797,27.2502 37.6936583,27.5316931 37.7433493,27.8982642 L37.7502,28.0002 L37.7502,34.0002 C37.7502,34.3797 37.4678667,34.6936583 37.1019257,34.7433493 L37.0002,34.7502 L31.0002,34.7502 C30.5862,34.7502 30.2502,34.4142 30.2502,34.0002 C30.2502,33.6197833 30.5325333,33.3065889 30.8984743,33.2570316 L31.0002,33.2502 L36.2502,33.2502 L36.2502,28.0002 C36.2502,27.5852 36.5862,27.2502 37.0002,27.2502 Z M17.0002,13.2502 C17.4142,13.2502 17.7502,13.5852 17.7502,14.0002 C17.7502,14.3797 17.4678667,14.6936583 17.1019257,14.7433493 L17.0002,14.7502 L11.7502,14.7502 L11.7502,20.0002 C11.7502,20.4142 11.4142,20.7502 11.0002,20.7502 C10.6207,20.7502 10.3067417,20.4678667 10.2570507,20.1019257 L10.2502,20.0002 L10.2502,14.0002 C10.2502,13.6197833 10.5325333,13.3065889 10.8984743,13.2570316 L11.0002,13.2502 L17.0002,13.2502 Z M37.0002,13.2502 C37.4142,13.2502 37.7502,13.5852 37.7502,14.0002 L37.7502,14.0002 L37.7502,20.0002 C37.7502,20.4142 37.4142,20.7502 37.0002,20.7502 C36.5862,20.7502 36.2502,20.4142 36.2502,20.0002 L36.2502,20.0002 L36.2502,14.7502 L31.0002,14.7502 C30.5862,14.7502 30.2502,14.4142 30.2502,14.0002 C30.2502,13.5852 30.5862,13.2502 31.0002,13.2502 L31.0002,13.2502 Z", 108, Paint.valueOf("#000000")) { scaleX = 1.25 }
                                        label("Drag and drop here") { style { fontSize = 16.px} }
                                    }

                                    vbox(spacing = 16) {
                                        alignment = Pos.CENTER
                                        visibleWhen { filterFile.isNotNull }
                                        svgicon("M27.0498,9.3 C27.2058,9.3064 27.35988,9.3512 27.488488,9.442592 L27.5798,9.52 L35.5798,17.52 C35.695,17.6344 35.76156,17.78016 35.784088,17.933728 L35.7928,18.05 L35.7998,18.05 L35.7998,36.05 C35.7998,37.5108148 34.656442,38.7082771 33.2171386,38.7949741 L33.0498,38.8 L15.0498,38.8 C13.5889852,38.8 12.3915229,37.656642 12.3048259,36.2173386 L12.2998,36.05 L12.2998,12.05 C12.2998,10.5891852 13.443158,9.39172291 14.8824614,9.30502586 L15.0498,9.3 L27.0498,9.3 Z M26.2998,10.8 L15.0498,10.8 C14.4038625,10.8 13.870425,11.2930664 13.8062685,11.9224182 L13.7998,12.05 L13.7998,36.05 C13.7998,36.6959375 14.2928664,37.229375 14.9222182,37.2935315 L15.0498,37.3 L33.0498,37.3 C33.6957375,37.3 34.229175,36.8069336 34.2933315,36.1775818 L34.2998,36.05 L34.2998,18.8 L27.0498,18.8 C26.6703,18.8 26.3563417,18.5176667 26.3066507,18.1517257 L26.2998,18.05 L26.2998,10.8 Z M30.0498,31.3 C30.4638,31.3 30.7998,31.636 30.7998,32.05 C30.7998,32.464 30.4638,32.8 30.0498,32.8 L30.0498,32.8 L18.0498,32.8 C17.6358,32.8 17.2998,32.464 17.2998,32.05 C17.2998,31.636 17.6358,31.3 18.0498,31.3 L18.0498,31.3 Z M18.0498,27.3 L30.0498,27.3 C30.4638,27.3 30.7998,27.636 30.7998,28.05 C30.7998,28.4295 30.5174667,28.7434583 30.1515257,28.7931493 L30.0498,28.8 L18.0498,28.8 C17.6358,28.8 17.2998,28.464 17.2998,28.05 C17.2998,27.6705 17.5821333,27.3565417 17.9480743,27.3068507 L18.0498,27.3 L30.0498,27.3 Z M30.0498,23.3 C30.4638,23.3 30.7998,23.636 30.7998,24.05 C30.7998,24.4295 30.5174667,24.7434583 30.1515257,24.7931493 L30.0498,24.8 L18.0498,24.8 C17.6358,24.8 17.2998,24.464 17.2998,24.05 C17.2998,23.6705 17.5821333,23.3565417 17.9480743,23.3068507 L18.0498,23.3 L30.0498,23.3 Z M27.7998,11.861 L27.7998,17.3 L33.2388,17.3 L27.7998,11.861 Z", 108, Paint.valueOf("#000000")) { scaleX = 0.8 }
                                        label {
                                            bind(filterFile, true, FileNameConverter())
                                            style { fontSize = 16.px}
                                        }
                                    }
                                }.addClass(dropArea)
                            }.addClass(fieldLabel)
                        }
                        separator(Orientation.VERTICAL)
                        fieldset("Output Options") {
                            field {
                                checkbox("Generate appmap.xml").bind(generateAppMap)
                            }
                            field {
                                checkbox("Generate theme-resources.xml").bind(generateThemeResources)
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
                    enableWhen(filterFile.isNotNull
                            .and(generateAppMap.or(generateThemeResources))
                            .and(validDestination))
                    action {
                        generateProgress.set(0.0)
                        runAsync {
                            controller.createXML(filterFile.value, destinationPath.value, *(outTypes.value))
                        }
                    }
                    shortcut("Enter")
                }
            }
        }

        bottom = progressbar(0.0) {
            useMaxWidth = true
            borderpaneConstraints { marginTop = 8.0 }
            bind(generateProgress)
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

    private fun chooseFile() {
        val files = chooseFile("Select App Filter File", arrayOf(FileChooser.ExtensionFilter("XML", "*.xml")), FileChooserMode.Single)
        if (files.isNotEmpty() && acceptedFiles.contains(files[0].name)) {
            filterFile.set(files[0])
            destinationPath.set(filterFile.value.toPath().parent)
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