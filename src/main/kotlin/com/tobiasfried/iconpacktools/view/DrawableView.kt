package com.tobiasfried.iconpacktools.view

import com.tobiasfried.iconpacktools.controller.DrawableController
import com.tobiasfried.iconpacktools.controller.DrawableOutput
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File
import kotlin.concurrent.thread

class DrawableView : View("Drawables") {

    private val controller = DrawableController()

    private val generateDrawable = SimpleBooleanProperty(true)
    private val generateIconPack = SimpleBooleanProperty(false)
    private val overwriteExisting = SimpleBooleanProperty(false)
    private var generateProgress = SimpleDoubleProperty(0.0)

    private var files = FXCollections.observableArrayList<File>()
    //    private val files = FXCollections.observableArrayList(
//            "abstruct.png", "accubattery.png", "accuweather.png", "acorns.png", "action_launcher.png", "adblock_alt.png",
//            "ad_block_plus.png", "adblock.png", "adguard.png", "adm.png", "adobe_acrobat.png", "adobe_fill_and_sign.png",
//            "adobe_illustrator.png", "adobe_lightroom.png", "adobe_photoshop_fix.png", "adobe_photoshop_mix.png",
//            "adobe_photoshop.png", "adobe_photoshop_sketch.png", "adobe_scan.png", "adw_launcher.png", "afterglow.png",
//            "afterlight.png", "airbnb.png", "airdroid.png", "airtable.png", "ali_express.png", "alipay.png",
//            "alltrails.png", "alto_odyssey.png", "amaze_alt.png", "amaze.png", "audio.png", "aurora_droid.png"
//    )
    private var selectedFiles = SimpleObjectProperty<File>()

    override val root = borderpane {
        left = vbox(spacing = 8.0) {
            borderpaneConstraints { marginLeftRight(8.0) }

            hbox {
                useMaxWidth = true
                vboxConstraints {
                    marginTop = 8.0
                }
                label("Files") {
                    style { fontWeight = FontWeight.BOLD }
                    hgrow = Priority.ALWAYS
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
                hgrow = Priority.NEVER
                button("Add") {
                    action { files.addAll(chooseFile("Select Icons", arrayOf(FileChooser.ExtensionFilter("PNG", "*.png")), FileChooserMode.Multi)) }
                    shortcut("Ctrl+O")
                }
                button("Remove") {
                    enableWhen(selectedFiles.isNotNull)
                    action {
                        files.removeAll(selectedFiles.value)
                        println("Removed Icon")
                    }
                    shortcut("Ctrl+X")
                }
                button("Clear") {
                    enableWhen(files.sizeProperty.isNotEqualTo(0))
                    action {
                        files.clear()
                        println("Cleared All Icons")
                    }
                }
            }

        }

        center = vbox(spacing = 8.0) {
            borderpaneConstraints { marginLeftRight(8.0)}
            label("Icon Resource Generator") {
                style { fontWeight = FontWeight.BOLD }
                vboxConstraints {
                    marginTop = 8.0
                }
            }
            textflow {
                text("This tool will generate a drawable.xml and icon-pack.xml file for the provided .png icons. ")
                text("Use the add button or drag-and-drop your files or directory into the pane on the left, then select your settings below. ")
            }
            textflow {
                text("By default, the .xml resources will be created in the same file as the last assets added to the list. ")
                text("Optionally, you may select the target destination for the resource files.")
            }
            form {
                vgrow = Priority.ALWAYS
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
                button("Generate") {
                    enableWhen(files.sizeProperty.greaterThan(0)
                            .and(generateDrawable.or(generateIconPack)))
                    action {
                        thread {
                            for (i in 1..100) {
                                Platform.runLater { generateProgress.set(i.toDouble() / 100.0) }
                                Thread.sleep(15)
                            }
                            controller.generateDrawableXML(files, DrawableOutput.DRAWABLE)
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
}