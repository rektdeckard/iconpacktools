package com.tobiasfried.iconpacktools.controller

import org.w3c.dom.Document
import tornadofx.*
import java.io.File
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory

class FilterController(val updateProgress: (Double) -> Unit) : Controller() {
    private val dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    private val transformer = TransformerFactory.newInstance().newTransformer().also {
        it.setOutputProperty(OutputKeys.METHOD, "xml")
        it.setOutputProperty(OutputKeys.INDENT, "yes")
        it.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
    }

    fun generateXML(file: File, type: FilterFormat, path: Path) {

    }

    private fun generateAppFilterDocument(file: File): Document {

    }
}

enum class FilterFormat {
    APPFILTER, APPMAP, THEME_RESOURCES
}