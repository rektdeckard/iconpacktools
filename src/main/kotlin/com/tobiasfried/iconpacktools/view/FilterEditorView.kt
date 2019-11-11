package com.tobiasfried.iconpacktools.view

import com.tobiasfried.iconpacktools.app.Styles
import com.tobiasfried.iconpacktools.app.Styles.Companion.bold
import com.tobiasfried.iconpacktools.app.Styles.Companion.fieldLabel
import com.tobiasfried.iconpacktools.controller.FilterEditorController
import com.tobiasfried.iconpacktools.controller.FilterMapperController
import com.tobiasfried.iconpacktools.model.AppComponent
import com.tobiasfried.iconpacktools.utils.FileNameConverter
import javafx.geometry.Pos
import javafx.scene.input.MouseButton
import javafx.scene.input.TransferMode
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.paint.Paint
import tornadofx.*

class FilterEditorView : View("Filter Editor") {
    private val controller: FilterEditorController by inject()
    private var dropTarget: Region by singleAssign()

    override val root = borderpane {
        top = vbox(spacing = 8.0) {
            borderpaneConstraints {
                marginLeftRight(8.0)
            }
            label("App Filter Editor") {
                vboxConstraints {
                    marginTop = 8.0
                }
            }.addClass(bold)
            textflow {
                text("This tool allows for straightforward graphical editing of a new or existing ")
                text("appfilter.xml").addClass(Styles.italic)
                text(" file. Select your project's app filter and edit in place, or add one from your icon requests.")
            }
            form {
                fieldset("Input File") {
                    dropTarget = stackpane {
                        alignment = Pos.CENTER
                        prefHeight = 256.0
                        prefWidth = 256.0
                        setOnMouseClicked { if (it.button == MouseButton.PRIMARY) controller.chooseFile() }

                        vbox(spacing = 16) {
                            alignment = Pos.CENTER
                            visibleWhen { controller.filterFile.isNull.and(controller.validFile) }
                            svgicon("M11.0002,27.2502 C11.3797,27.2502 11.6936583,27.5316931 11.7433493,27.8982642 L11.7502,28.0002 L11.7502,33.2502 L17.0002,33.2502 C17.4142,33.2502 17.7502,33.5852 17.7502,34.0002 C17.7502,34.3797 17.4678667,34.6936583 17.1019257,34.7433493 L17.0002,34.7502 L11.0002,34.7502 C10.6207,34.7502 10.3067417,34.4678667 10.2570507,34.1019257 L10.2502,34.0002 L10.2502,28.0002 C10.2502,27.5852 10.5862,27.2502 11.0002,27.2502 Z M37.0002,27.2502 C37.3797,27.2502 37.6936583,27.5316931 37.7433493,27.8982642 L37.7502,28.0002 L37.7502,34.0002 C37.7502,34.3797 37.4678667,34.6936583 37.1019257,34.7433493 L37.0002,34.7502 L31.0002,34.7502 C30.5862,34.7502 30.2502,34.4142 30.2502,34.0002 C30.2502,33.6197833 30.5325333,33.3065889 30.8984743,33.2570316 L31.0002,33.2502 L36.2502,33.2502 L36.2502,28.0002 C36.2502,27.5852 36.5862,27.2502 37.0002,27.2502 Z M17.0002,13.2502 C17.4142,13.2502 17.7502,13.5852 17.7502,14.0002 C17.7502,14.3797 17.4678667,14.6936583 17.1019257,14.7433493 L17.0002,14.7502 L11.7502,14.7502 L11.7502,20.0002 C11.7502,20.4142 11.4142,20.7502 11.0002,20.7502 C10.6207,20.7502 10.3067417,20.4678667 10.2570507,20.1019257 L10.2502,20.0002 L10.2502,14.0002 C10.2502,13.6197833 10.5325333,13.3065889 10.8984743,13.2570316 L11.0002,13.2502 L17.0002,13.2502 Z M37.0002,13.2502 C37.4142,13.2502 37.7502,13.5852 37.7502,14.0002 L37.7502,14.0002 L37.7502,20.0002 C37.7502,20.4142 37.4142,20.7502 37.0002,20.7502 C36.5862,20.7502 36.2502,20.4142 36.2502,20.0002 L36.2502,20.0002 L36.2502,14.7502 L31.0002,14.7502 C30.5862,14.7502 30.2502,14.4142 30.2502,14.0002 C30.2502,13.5852 30.5862,13.2502 31.0002,13.2502 L31.0002,13.2502 Z", 108, Paint.valueOf("#000000")) { scaleX = 1.25 }
                            label("Drag and drop here") { style { fontSize = 16.px } }
                        }

                        vbox(spacing = 16) {
                            alignment = Pos.CENTER
                            visibleWhen { controller.filterFile.isNotNull.and(controller.validFile) }
                            svgicon("M27.0498,9.3 C27.2058,9.3064 27.35988,9.3512 27.488488,9.442592 L27.5798,9.52 L35.5798,17.52 C35.695,17.6344 35.76156,17.78016 35.784088,17.933728 L35.7928,18.05 L35.7998,18.05 L35.7998,36.05 C35.7998,37.5108148 34.656442,38.7082771 33.2171386,38.7949741 L33.0498,38.8 L15.0498,38.8 C13.5889852,38.8 12.3915229,37.656642 12.3048259,36.2173386 L12.2998,36.05 L12.2998,12.05 C12.2998,10.5891852 13.443158,9.39172291 14.8824614,9.30502586 L15.0498,9.3 L27.0498,9.3 Z M26.2998,10.8 L15.0498,10.8 C14.4038625,10.8 13.870425,11.2930664 13.8062685,11.9224182 L13.7998,12.05 L13.7998,36.05 C13.7998,36.6959375 14.2928664,37.229375 14.9222182,37.2935315 L15.0498,37.3 L33.0498,37.3 C33.6957375,37.3 34.229175,36.8069336 34.2933315,36.1775818 L34.2998,36.05 L34.2998,18.8 L27.0498,18.8 C26.6703,18.8 26.3563417,18.5176667 26.3066507,18.1517257 L26.2998,18.05 L26.2998,10.8 Z M30.0498,31.3 C30.4638,31.3 30.7998,31.636 30.7998,32.05 C30.7998,32.464 30.4638,32.8 30.0498,32.8 L30.0498,32.8 L18.0498,32.8 C17.6358,32.8 17.2998,32.464 17.2998,32.05 C17.2998,31.636 17.6358,31.3 18.0498,31.3 L18.0498,31.3 Z M18.0498,27.3 L30.0498,27.3 C30.4638,27.3 30.7998,27.636 30.7998,28.05 C30.7998,28.4295 30.5174667,28.7434583 30.1515257,28.7931493 L30.0498,28.8 L18.0498,28.8 C17.6358,28.8 17.2998,28.464 17.2998,28.05 C17.2998,27.6705 17.5821333,27.3565417 17.9480743,27.3068507 L18.0498,27.3 L30.0498,27.3 Z M30.0498,23.3 C30.4638,23.3 30.7998,23.636 30.7998,24.05 C30.7998,24.4295 30.5174667,24.7434583 30.1515257,24.7931493 L30.0498,24.8 L18.0498,24.8 C17.6358,24.8 17.2998,24.464 17.2998,24.05 C17.2998,23.6705 17.5821333,23.3565417 17.9480743,23.3068507 L18.0498,23.3 L30.0498,23.3 Z M27.7998,11.861 L27.7998,17.3 L33.2388,17.3 L27.7998,11.861 Z", 108, Paint.valueOf("#5B9D07")) { scaleX = 0.8 }
                            label {
                                bind(controller.filterFile, true, FileNameConverter())
                                style { fontSize = 16.px; textFill = c("#5B9D07") }
                            }
                        }

                        vbox(spacing = 16) {
                            alignment = Pos.CENTER
                            visibleWhen { !controller.validFile }
                            svgicon("M24.0001,11.2497 C24.9287667,11.2497 25.7659667,11.6974511 26.2775453,12.4579892 L26.3821,12.6257 L37.9201,32.6287 C38.4161,33.4897 38.4161,34.5187 37.9191,35.3787 C37.4543,36.1813667 36.6480244,36.6835 35.734428,36.7468837 L35.5371,36.7537 L12.4621,36.7537 C11.4691,36.7537 10.5781,36.2387 10.0811,35.3787 C9.61723333,34.5760333 9.58543778,33.6261489 9.98733941,32.8030401 L10.0801,32.6287 L21.6181,12.6257 C22.1151,11.7637 23.0051,11.2497 24.0001,11.2497 Z M24.0001,12.7497 C23.5983222,12.7497 23.2336802,12.9337988 22.9988819,13.2500237 L22.9171,13.3747 L11.3801,33.3787 C11.1541,33.7707 11.1541,34.2367 11.3801,34.6287 C11.5809889,34.9762556 11.9233099,35.1997617 12.3136528,35.245139 L12.4621,35.2537 L35.5371,35.2537 C35.9891,35.2537 36.3941,35.0197 36.6201,34.6287 C36.8209889,34.2802556 36.8433099,33.873342 36.687063,33.5119044 L36.6201,33.3787 L25.0821,13.3747 C24.8571,12.9827 24.4521,12.7497 24.0001,12.7497 Z M24,30.2469 C24.551,30.2469 25,30.6949 25,31.2469 C25,31.7979 24.551,32.2469 24,32.2469 C23.449,32.2469 23,31.7979 23,31.2469 C23,30.6949 23.449,30.2469 24,30.2469 Z M24,19.2469 C24.3795,19.2469 24.6934583,19.5283931 24.7431493,19.8949642 L24.75,19.9969 L24.75,27.9969 C24.75,28.4109 24.414,28.7469 24,28.7469 C23.6205,28.7469 23.3065417,28.4645667 23.2568507,28.0986257 L23.25,27.9969 L23.25,19.9969 C23.25,19.5819 23.586,19.2469 24,19.2469 Z", 108, Paint.valueOf("#DE2727")) { scaleX = 1.1 }
                            label("Parsing failed") {
                                style { fontSize = 16.px; textFill = c("#DE2727") }
                            }
                        }
                    }.addClass(Styles.dropArea)
                }.addClass(fieldLabel)
            }
        }

        center = tableview(controller.filterDocumentModel.appComponents) {
            borderpaneConstraints {
                marginTop = 16.0
                marginLeftRight(8.0)
                vgrow = Priority.ALWAYS
                hgrow = Priority.ALWAYS
            }
            enableCellEditing()
            enableDirtyTracking()
            regainFocusAfterEdit()
            isTableMenuButtonVisible = true
            isFocusTraversable = true

            column("Package", AppComponent::packageName).makeEditable()
            column("Activity", AppComponent::activityName).makeEditable()
            column("Drawable", AppComponent::drawable).makeEditable()
        }

        bottom = vbox(spacing = 8) {
            buttonbar {
                vboxConstraints {
                    marginTop = 8.0
                    marginRight = 8.0
                }
                button("Commit") {
                    enableWhen(controller.filterFile.isNotNull.and(controller.validFile))
                    action { controller.commit() }
                }
            }
            stackpane {
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
                if (controller.acceptedFiles.contains(newFilterFile.name)) {
                    controller.validateFile(newFilterFile)
                }
                it.isDropCompleted = true
            } else it.isDropCompleted = false

            it.consume()
        }
    }
}