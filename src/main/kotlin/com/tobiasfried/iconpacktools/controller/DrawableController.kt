package com.tobiasfried.iconpacktools.controller

import com.tobiasfried.iconpacktools.controller.DrawableOutput.*
import org.w3c.dom.Document
import tornadofx.*
import java.io.File
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class DrawableController(val updateProgress: (Double) -> Unit) : Controller() {
    private val dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    private val transformer = TransformerFactory.newInstance().newTransformer().also {
        it.setOutputProperty(OutputKeys.METHOD, "xml")
        it.setOutputProperty(OutputKeys.INDENT, "yes")
        it.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
    }

    //    fun generateXML(files: List<File>, type: DrawableOutput, path: Path) {
    fun generateXML(files: List<File>, type: DrawableOutput, path: Path) {
        when (type) {
            DRAWABLE -> exportXML(generateDrawableDocument(files), type, path)
            ICON_PACK -> exportXML(generateIconPackDocument(files), type, path)
            BOTH -> {
                exportXML(generateDrawableDocument(files), DRAWABLE, path)
                exportXML(generateIconPackDocument(files), ICON_PACK, path)
            }
        }
        updateProgress(1.0)
    }

    private fun generateDrawableDocument(files: List<File>): Document {
        val doc = dBuilder.newDocument()
        val resources = doc.createElement("resources")
        val category = doc.createElement("category").also { it.setAttribute("title", "All") }
        resources.appendChild(category)

        for (i in 0..files.lastIndex) {
            val item = doc.createElement("item")
            item.setAttribute("drawable", files[i].nameWithoutExtension)
            resources.appendChild(item)

            updateProgress(i / files.size.toDouble())
        }

        doc.appendChild(resources)
        return doc
    }

    private fun generateIconPackDocument(files: List<File>): Document {
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
        resources.appendChild(doc.createComment(" Filter Categories "))
        resources.appendChild(doc.createComment(" Make sure the filters names are the same as the other arrays "))
        resources.appendChild(filters)
        resources.appendChild(doc.createComment(" All Drawables "))
        resources.appendChild(all)
        resources.appendChild(doc.createComment(" Drawables to include in Dashboard Preview "))
        resources.appendChild(previews)
        doc.appendChild(resources)

        for (i in 0..files.lastIndex) {
            val item = doc.createElement("item")
            item.appendChild(doc.createTextNode(files[i].nameWithoutExtension))
            all.appendChild(item)
            previews.appendChild(doc.importNode(item, true))

            updateProgress(i / files.size.toDouble())
        }

        return doc
    }

    private fun exportXML(document: Document, type: DrawableOutput, path: Path) {
        val filename = if (type == DRAWABLE) "drawable.xml" else "icon-pack.xml"
        transformer.transform(DOMSource(document), StreamResult(System.out))
        transformer.transform(DOMSource(document), StreamResult(File(path.toString(), filename)))
    }
}

enum class DrawableOutput {
    DRAWABLE, ICON_PACK, BOTH
}