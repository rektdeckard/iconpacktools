package com.tobiasfried.iconpacktools.controller

import com.tobiasfried.iconpacktools.controller.FilterFormat.*
import com.tobiasfried.iconpacktools.utils.XMLMaker
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.stage.FileChooser
import org.w3c.dom.Document
import tornadofx.*
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Callable

class FilterMapperController : Controller() {
    val generateAppMap = SimpleBooleanProperty(true)
    val generateThemeResources = SimpleBooleanProperty(true)
    val overwriteExisting = SimpleBooleanProperty(false)

    val specifyPath = SimpleBooleanProperty(false)
    val destinationPath = SimpleObjectProperty(Paths.get(""))
    val validDestination: BooleanBinding = Bindings.createBooleanBinding(Callable {
        !specifyPath.value || File(destinationPath.value.toString().trim()).exists()
    }, specifyPath, destinationPath)

    val generateProgress = SimpleDoubleProperty(0.0)
    val statusMessage = SimpleStringProperty()
    val statusComplete: BooleanBinding = Bindings.createBooleanBinding(Callable { generateProgress.value.equals(1.0) }, generateProgress)
    val updateProgress: (Double, String?) -> Unit = { progress, message ->
        generateProgress.set(progress)
        statusMessage.set(message)
    }

    val filterFile = SimpleObjectProperty<File>()
    val validFile = SimpleBooleanProperty(true)
    val acceptedFiles = listOf(APPFILTER.filename)

    private val outTypes: ObjectBinding<Array<FilterFormat>> = Bindings.createObjectBinding<Array<FilterFormat>>(Callable {
        // TODO: this is ugly. Better way to do this?
        if (generateAppMap.value && generateThemeResources.value) {
            arrayOf(APPMAP, THEME_RESOURCES)
        } else if (generateAppMap.value) {
            arrayOf(APPMAP)
        } else if (generateThemeResources.value) {
            arrayOf(THEME_RESOURCES)
        } else arrayOf()
    }, generateAppMap, generateThemeResources)

    private val xmlMaker = XMLMaker(updateProgress)

    fun chooseFile() {
        val files = chooseFile("Select App Filter File", arrayOf(FileChooser.ExtensionFilter("XML", "*.xml")), FileChooserMode.Single)
        if (files.isNotEmpty() && acceptedFiles.contains(files[0].name)) {
            validateFile(files[0])
        }
    }

    fun useCurrentDirectory() {
        specifyPath.set(false)
        filterFile.value?.let {
            if (it.exists()) destinationPath.set(filterFile.value.toPath().parent)
        }
        updateProgress(0.0, null)
    }

    fun useSpecifiedDirectory() {
        specifyPath.set(true)
        updateProgress(0.0, null)
    }

    fun validateFile(file: File) {
        try {
            xmlMaker.validateAppFilter(file)
            filterFile.set(file)
            destinationPath.set(filterFile.value.toPath().parent)
            validFile.set(true)
        } catch (e: Exception) {
            validFile.set(false)
        }
        updateProgress(0.0, null)
    }

    fun chooseDestination() {
        val selectedDestination = chooseDirectory("Select Destination", File("/"))
        selectedDestination?.let { destinationPath.set(selectedDestination.toPath()) }
    }

    fun generate() {
        updateProgress(0.0, null)
        createXML(*(outTypes.value))
    }

    private fun createXML(vararg outTypes: FilterFormat) {
        val baseDocument = xmlMaker.createFilterDocumentFromAppFilter(filterFile.value)

        for (outType in outTypes) {
            val formattedDocument = when (outType) {
                APPFILTER -> throw NoSuchMethodException()
                APPMAP -> xmlMaker.createAppMapDocument(baseDocument)
                THEME_RESOURCES -> xmlMaker.createThemeResourcesDocument(baseDocument)
            }
            exportXML(formattedDocument, outType)
        }
    }

    private fun exportXML(document: Document, outType: FilterFormat) {
        if (overwriteExisting.value || !File(destinationPath.value.toString(), outType.filename).exists()) {
            xmlMaker.export(document, File(destinationPath.value.toString(), outType.filename))
            updateProgress(1.0, "COMPLETE")
        } else updateProgress(0.0, "FILE ALREADY EXISTS")
    }
}

enum class FilterFormat(val filename: String) {
    APPFILTER("appfilter.xml"),
    APPMAP("appmap.xml"),
    THEME_RESOURCES("theme_resources.xml")
}