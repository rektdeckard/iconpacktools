package com.tobiasfried.iconpacktools.controller

import com.sun.org.apache.xerces.internal.parsers.DOMParser
import com.tobiasfried.iconpacktools.controller.DrawableOutput.*
import org.w3c.dom.Document
import org.xml.sax.InputSource
import tornadofx.*
import java.io.File
import java.io.StringReader
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class DrawableController(val updateProgress: (Double, String?) -> Unit) : Controller() {
    private val dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    private val transformer = TransformerFactory.newInstance().newTransformer().also {
        it.setOutputProperty(OutputKeys.METHOD, "xml")
        it.setOutputProperty(OutputKeys.INDENT, "yes")
        it.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
    }

    //
    fun createXML(files: List<File>, type: DrawableOutput, path: Path) {
        when (type) {
            DRAWABLE -> exportXML(createDrawableDocument(files), type, path)
            ICON_PACK -> exportXML(createIconPackDocument(files), type, path)
            BOTH -> {
                exportXML(createDrawableDocument(files), DRAWABLE, path)
                exportXML(createIconPackDocument(files), ICON_PACK, path)
            }
        }
        updateProgress(1.0, "COMPLETE")
    }

    private fun createDrawableDocument(files: List<File>): Document {
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

    private fun createIconPackDocument(files: List<File>): Document {
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

        with (resources) {
            appendChild(doc.createComment(" Filter Categories "))
            appendChild(doc.createComment(" Make sure the filters names are the same as the other arrays "))
            appendChild(filters)
            appendChild(doc.createComment(" All Drawables "))
            appendChild(all)
            appendChild(doc.createComment(" Drawables to include in Dashboard Preview "))
            appendChild(previews)
        }
        doc.appendChild(resources)

        for (i in 0..files.lastIndex) {
            val item = doc.createElement("item")
            item.appendChild(doc.createTextNode(files[i].nameWithoutExtension))
            all.appendChild(item)
            previews.appendChild(doc.importNode(item, true))

            updateProgress(i / files.size.toDouble(), "$i / ${files.size}")
        }

        return doc
    }

    private fun exportXML(document: Document, type: DrawableOutput, path: Path) {
        val filename = if (type == DRAWABLE) "drawable.xml" else "icon-pack.xml"
        transformer.transform(DOMSource(document), StreamResult(File(path.toString(), filename)))
//        transformer.transform(DOMSource(document), StreamResult(System.out))
    }

    fun parseDrawableXML(file: File) {
        val inputSource = InputSource(StringReader(file.readText()))
        val doc = dBuilder.parse(inputSource)
        val resources = doc.childNodes

        val resourceMap = HashMap<String, ArrayList<String>>()

//        val x = DOMParser()
        println(resources)
        for (i in 0 until resources.length) {
            val child = resources.item(i)
//            println(child.localName)
        }
    }
}

enum class DrawableOutput {
    DRAWABLE, ICON_PACK, BOTH
}