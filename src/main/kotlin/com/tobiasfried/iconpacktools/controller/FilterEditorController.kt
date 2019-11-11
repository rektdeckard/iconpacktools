package com.tobiasfried.iconpacktools.controller

import com.tobiasfried.iconpacktools.model.FilterDocument
import com.tobiasfried.iconpacktools.model.FilterDocumentModel
import com.tobiasfried.iconpacktools.utils.XMLMaker
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File
import java.util.concurrent.Callable

class FilterEditorController : Controller() {
    val generateProgress = SimpleDoubleProperty(0.0)
    val statusMessage = SimpleStringProperty()
    val statusComplete: BooleanBinding = Bindings.createBooleanBinding(Callable { generateProgress.value.equals(1.0) }, generateProgress)
    private val updateProgress: (Double, String?) -> Unit = { progress, message ->
        generateProgress.set(progress)
        statusMessage.set(message)
    }

    private val xmlMaker = XMLMaker(updateProgress)

    val filterFile = SimpleObjectProperty<File>()
    val validFile = SimpleBooleanProperty(true)
    val acceptedFiles = listOf(FilterFormat.APPFILTER.filename)

    val filterDocumentModel: ObjectBinding<FilterDocumentModel> = Bindings.createObjectBinding(Callable{
        FilterDocumentModel(xmlMaker.createFilterDocumentFromAppFilter(filterFile.value))
    }, filterFile)

    fun chooseFile() {
        val files = chooseFile("Select App Filter File", arrayOf(FileChooser.ExtensionFilter("XML", "*.xml")), FileChooserMode.Single)
        if (files.isNotEmpty() && acceptedFiles.contains(files[0].name)) {
            validateFile(files[0])
        }
    }

    fun validateFile(file: File) {
        try {
            xmlMaker.validateAppFilter(file)
            filterFile.set(file)
            validFile.set(true)

        } catch (e: Exception) {
//            println(e.message)
            validFile.set(false)
        }
        updateProgress(0.0, null)
    }

    fun commit() {
        // TODO
    }

}