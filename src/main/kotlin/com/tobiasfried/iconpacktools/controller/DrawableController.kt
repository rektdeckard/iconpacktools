package com.tobiasfried.iconpacktools.controller

import com.tobiasfried.iconpacktools.controller.DrawableOutput.*
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableMap
import javafx.stage.FileChooser
import org.w3c.dom.Document
import org.xml.sax.InputSource
import tornadofx.*
import java.io.File
import java.io.StringReader
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.Callable
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class DrawableController : Controller() {
    val generateDrawable = SimpleBooleanProperty(true)
    val generateIconPack = SimpleBooleanProperty(false)
    val useCategories = SimpleBooleanProperty(false)
    val overwriteExisting = SimpleBooleanProperty(false)
    val specifyPath = SimpleBooleanProperty(false)
    val destinationPath = SimpleObjectProperty<Path>(Paths.get(""))
    val validDestination: BooleanBinding = Bindings.createBooleanBinding(Callable {
        !specifyPath.value || File(destinationPath.value.toString().trim()).exists()
    }, specifyPath, destinationPath)

    val generateProgress = SimpleDoubleProperty(0.0)
    val statusMessage = SimpleStringProperty()
    val statusComplete: BooleanBinding = Bindings.createBooleanBinding(Callable { generateProgress.value.equals(1.0) }, generateProgress)

    val files = FXCollections.observableArrayList<File>()!!
    val folders: ObjectBinding<ObservableMap<File, List<File>>> = Bindings.createObjectBinding(Callable {
        files.groupBy { File(it.parent) }.asObservable()
    }, files)
    val selectedFiles = SimpleObjectProperty<File>()

    private val dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    private val transformer = TransformerFactory.newInstance().newTransformer().also {
        it.setOutputProperty(OutputKeys.METHOD, "xml")
        it.setOutputProperty(OutputKeys.INDENT, "yes")
        it.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
    }

    fun selectFiles() {
        val newFiles = arrayListOf<File>()
        if (!useCategories.value) {
            newFiles.addAll(chooseFile("Select Icons", arrayOf(FileChooser.ExtensionFilter("PNG", "*.png")), FileChooserMode.Multi))
        } else chooseDirectory("Select Icon Directories", null)?.let { newFiles.add(it) }
        if (newFiles.isNotEmpty() && !specifyPath.value) {
            destinationPath.set(newFiles[0].toPath().parent)
        }
        flattenAndAddFiles(newFiles)
        updateProgress()
    }

    fun flattenAndAddFiles(newFiles: List<File>) {
        if (newFiles.isNotEmpty()) {
            val flat = ArrayList<File>()
            newFiles.forEach { file ->
                if (file.isDirectory) flat.addAll(file.walkTopDown().toList().filter { it.isFile })
                else flat.add(file)
            }

            files.addAll(flat.filter { !files.contains(it) && it.extension.toLowerCase() == "png" })
            destinationPath.set(flat[0].toPath().parent)
        }
        updateProgress()
    }

    fun removeSelected() {
        files.removeAll(selectedFiles.value)
        updateProgress()
    }

    fun clearFiles() {
        files.clear()
        updateProgress()
    }

    fun chooseDestination() {
        val selectedDestination = chooseDirectory("Select Destination", File("/"))
        selectedDestination?.let { destinationPath.set(selectedDestination.toPath()) }
    }

    fun useCurrentDirectory() {
        specifyPath.set(false)
        if (files.isNotEmpty()) {
            destinationPath.set(files[files.lastIndex].toPath().parent)
        }
    }

    fun generate() {
        updateProgress()
        val outType: DrawableOutput =
                if (generateDrawable.value && generateIconPack.value) BOTH
                else if (generateDrawable.value) DRAWABLE
                else ICON_PACK
        if (useCategories.value) {
            createCategorizedXML(outType)
        } else createXML(outType)
    }

    private fun updateProgress(progress: Double = 0.0, message: String? = null) {
        generateProgress.set(progress)
        statusMessage.set(message)
    }

    private fun createXML(type: DrawableOutput) {
        when (type) {
            DRAWABLE -> exportXML(createDrawableDocument(), type)
            ICON_PACK -> exportXML(createIconPackDocument(), type)
            BOTH -> {
                exportXML(createDrawableDocument(), DRAWABLE)
                exportXML(createIconPackDocument(), ICON_PACK)
            }
        }
    }

    private fun createCategorizedXML(type: DrawableOutput) {
        when (type) {
            DRAWABLE -> exportXML(createCategorizedDrawableDocument(), type)
            ICON_PACK -> exportXML(createCategorizedIconPackDocument(), type)
            BOTH -> {
                exportXML(createCategorizedDrawableDocument(), DRAWABLE)
                exportXML(createCategorizedIconPackDocument(), ICON_PACK)
            }
        }
    }

    private fun createDrawableDocument(): Document {
        val doc = dBuilder.newDocument()
        val resources = doc.createElement("resources")
        val category = doc.createElement("category").also { it.setAttribute("title", "All") }
        resources.appendChild(category)

        for (i in 0..files.lastIndex) {
            val item = doc.createElement("item")
            item.setAttribute("drawable", files[i].nameWithoutExtension)
            resources.appendChild(item)

            updateProgress(i / files.size.toDouble(), "$i / ${files.size}")
        }

        doc.appendChild(resources)
        return doc
    }

    private fun createCategorizedDrawableDocument(): Document {
        val count = folders.value.values.flatten().size

        val doc = dBuilder.newDocument()
        val resources = doc.createElement("resources")

        folders.value.entries.forEach { entry ->
            val category = doc.createElement("category").also { it.setAttribute("title", entry.key.name) }
            resources.appendChild(category)
            entry.value.forEach {
                val item = doc.createElement("item")
                item.setAttribute("drawable", it.nameWithoutExtension)
                resources.appendChild(item)
            }
        }

        val all = doc.createElement("category").also { it.setAttribute("title", "All") }
        resources.appendChild(all)
        val flat = folders.value.values.flatten().distinctBy { it.name }.sortedBy { it.name }
        for (i in 0..flat.lastIndex) {
            val item = doc.createElement("item")
            item.setAttribute("drawable", flat[i].nameWithoutExtension)
            resources.appendChild(item)
            updateProgress(i / count.toDouble(), "$i / $count")
        }

        doc.appendChild(resources)
        return doc
    }

    private fun createIconPackDocument(): Document {
        val sorted = files.sortedBy { it.name }

        val doc = dBuilder.newDocument()
        val resources = doc.createElement("resources").also {
            it.setAttribute("xmlns:tools", "http://schemas.android.com/tools")
            it.setAttribute("tools:ignore", "MissingTranslation")
        }
        val all = doc.createElement("string-array").also { it.setAttribute("name", "all") }
        val previews = doc.createElement("string-array").also { it.setAttribute("name", "icons_preview") }
        val filters = doc.createElement("string-array").also {
            it.setAttribute("name", "icon_filters")
            val item = doc.createElement("item")
            item.appendChild(doc.createTextNode("all"))
            it.appendChild(item)
        }

        with(resources) {
            appendChild(doc.createComment(" Filter Categories "))
            appendChild(doc.createComment(" Make sure the filters names are the same as the other arrays "))
            appendChild(filters)
            appendChild(doc.createComment(" All Drawables "))
            appendChild(all)
            appendChild(doc.createComment(" Drawables to include in Dashboard Preview "))
            appendChild(previews)
        }
        doc.appendChild(resources)

        for (i in 0..sorted.lastIndex) {
            val item = doc.createElement("item")
            item.appendChild(doc.createTextNode(sorted[i].nameWithoutExtension))
            all.appendChild(item)
            previews.appendChild(doc.importNode(item, true))

            updateProgress(i / sorted.size.toDouble(), "$i / ${sorted.size}")
        }

        return doc
    }

    private fun createCategorizedIconPackDocument(): Document {
        val count = folders.value.values.flatten().size

        val doc = dBuilder.newDocument()
        val resources = doc.createElement("resources").also {
            it.setAttribute("xmlns:tools", "http://schemas.android.com/tools")
            it.setAttribute("tools:ignore", "MissingTranslation")
        }
        val filters = doc.createElement("string-array").also { it.setAttribute("name", "icon_filters") }
        with(resources) {
            appendChild(doc.createComment(" Filter Categories "))
            appendChild(doc.createComment(" Make sure the filters names are the same as the other arrays "))
            appendChild(filters)
        }
        val previews = doc.createElement("string-array").also { it.setAttribute("name", "icons_preview") }
        val all = doc.createElement("string-array").also { it.setAttribute("name", "all") }

        folders.value.entries.forEach { entry ->

            filters.appendChild(doc.createElement("item").also { it.appendChild(doc.createTextNode(entry.key.name)) })
            val category = doc.createElement("string-array").also { it.setAttribute("name", entry.key.name) }
            entry.value.forEach {
                val item = doc.createElement("item")
                item.appendChild(doc.createTextNode(it.nameWithoutExtension))
                category.appendChild(item)
            }

            resources.appendChild(category)
        }


        val flat = folders.value.values.flatten().distinctBy { it.name }.sortedBy { it.name }
        for (i in 0..flat.lastIndex) {
            val item = doc.createElement("item")
            item.appendChild(doc.createTextNode(flat[i].nameWithoutExtension))
            all.appendChild(item)
            previews.appendChild(item.cloneNode(true))

            updateProgress(i / count.toDouble(), "$i / $count")
        }

        with(resources) {
            appendChild(doc.createComment(" All Drawables "))
            appendChild(all)
            appendChild(doc.createComment(" Drawables to include in Dashboard Preview "))
            appendChild(previews)
        }

        doc.appendChild(resources)
        return doc
    }

    private fun exportXML(document: Document, type: DrawableOutput) {
        val filename = if (type == DRAWABLE) "drawable.xml" else "icon-pack.xml"
        if (overwriteExisting.value || !File(destinationPath.value.toString(), filename).exists()) {
            transformer.transform(DOMSource(document), StreamResult(File(destinationPath.value.toString(), filename)))
            updateProgress(1.0, "COMPLETE")
        } else updateProgress(0.0, "FILE ALREADY EXISTS")
    }

    fun parseDrawableXML(file: File) {
        val inputSource = InputSource(StringReader(file.readText()))
        val doc = dBuilder.parse(inputSource)
        val resources = doc.childNodes

        val resourceMap = HashMap<String, ArrayList<String>>()

        println(resources)
        for (i in 0 until resources.length) {
            val child = resources.item(i)
        }
    }
}

enum class DrawableOutput {
    DRAWABLE, ICON_PACK, BOTH
}
