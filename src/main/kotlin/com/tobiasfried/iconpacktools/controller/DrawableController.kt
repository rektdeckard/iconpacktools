package com.tobiasfried.iconpacktools.controller

import tornadofx.*
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class DrawableController : Controller() {
    private val dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    private val transformer = TransformerFactory.newInstance().newTransformer().also {
        it.setOutputProperty(OutputKeys.METHOD, "xml")
        it.setOutputProperty(OutputKeys.INDENT, "yes")
        it.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
    }

    fun generateDrawableXML(files: List<File>, type: DrawableOutput) {
        val doc = dBuilder.newDocument()
        val resources = doc.createElement("resources")
        val category = doc.createElement("category").also { it.setAttribute("title", "All") }
        resources.appendChild(category)

        files.forEach {
            val item = doc.createElement("item")
            item.setAttribute("drawable", it.nameWithoutExtension)
            resources.appendChild(item)
        }

        doc.appendChild(resources)

        transformer.transform(DOMSource(doc), StreamResult(System.out))
        transformer.transform(DOMSource(doc), StreamResult(File("C:\\Users\\Tobias Fried\\Desktop\\drawable.xml")))
    }
}

enum class DrawableOutput {
    DRAWABLE, ICON_PACK, BOTH
}