package com.tobiasfried.iconpacktools.utils

import com.tobiasfried.iconpacktools.model.AppComponent
import com.tobiasfried.iconpacktools.model.FilterDocument
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class XMLMaker(val updateProgress: (Double, String?) -> Unit) {
    private val dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    private val transformer = TransformerFactory.newInstance().newTransformer().also {
        it.setOutputProperty(OutputKeys.METHOD, "xml")
        it.setOutputProperty(OutputKeys.INDENT, "yes")
        it.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
    }

    fun validateAppFilter(file: File): Boolean {
        updateProgress(0.0, null)
        val isValid: Boolean
        try {
            val inputSource = InputSource(StringReader(file.readText()))
            val doc = dBuilder.parse(inputSource)
            val firstItem = doc.getElementsByTagName("item").item(0)
            isValid = (firstItem.hasAttributes() && (firstItem.attributes.getNamedItem("component").nodeValue.isNotEmpty()))
        } catch (e: Exception) { throw e }
        return isValid
    }

    fun createFilterDocumentFromAppFilter(file: File): FilterDocument {
        val baseDocument = FilterDocument()

        val inputSource = InputSource(StringReader(file.readText()))
        val doc = dBuilder.parse(inputSource)
        val items = doc.getElementsByTagName("item")

        for (i in 0 until items.length) {
            val item = items.item(i)
            if (!item.hasAttributes()) continue
            val componentName = item.attributes.getNamedItem("component").nodeValue
            val drawable = item.attributes.getNamedItem("drawable").nodeValue

            if(componentName.startsWith(":")) {
                val appComponent = AppComponent(componentName, drawable = drawable)
                baseDocument.deviceDefaults.add(appComponent)
            } else {
                val packageName = componentName.split("/")[0].substringAfter("{", "ERROR")
                val activityName = componentName.split("/")[1].substringBefore("}", "ERROR")
                val appComponent = AppComponent(packageName, activityName, drawable)
                baseDocument.appComponents.add(appComponent)
            }
            updateProgress(i / (items.length.toDouble()), "$i / ${items.length}")
        }

        return baseDocument
    }

    fun createAppMapDocument(baseDocument: FilterDocument): Document {
        val doc = dBuilder.newDocument()
        val appmap = doc.createElement("appmap")
        val version = doc.createElement("version")
        version.appendChild(doc.createTextNode(baseDocument.version.toString()))
        appmap.appendChild(version)

        for (i in 0 until baseDocument.appComponents.size) {
            val it = baseDocument.appComponents[i]
            val item = doc.createElement("item")
            item.setAttribute("class", it.activityName)
            item.setAttribute("name", it.drawable)
            appmap.appendChild(item)

            updateProgress((i + baseDocument.appComponents.size) / (baseDocument.appComponents.size * 2.0),
                    "${i + baseDocument.appComponents.size} / ${baseDocument.appComponents.size * 2}")
        }

        doc.appendChild(appmap)
        return doc
    }

    fun createThemeResourcesDocument(baseDocument: FilterDocument): Document {
        val doc = dBuilder.newDocument()
        val theme = doc.createElement("Theme").also { it.setAttribute("version", baseDocument.version.toString()) }

        with (theme) {
            appendChild(doc.createComment(" SET THESE VALUES ON YOUR OWN "))
            appendChild(doc.createElement("Label").also { it.setAttribute("value", "YOUR APP NAME") })
            appendChild(doc.createElement("Wallpaper").also { it.setAttribute("image", "") })
            appendChild(doc.createElement("LockScreenWallpaper").also { it.setAttribute("image", "") })
            appendChild(doc.createElement("ThemePreview").also { it.setAttribute("image", "") })
            appendChild(doc.createElement("ThemePreviewWork").also { it.setAttribute("image", "") })
            appendChild(doc.createElement("ThemePreviewMenu").also { it.setAttribute("image", "") })
            appendChild(doc.createElement("DockMenuAppIcon").also { it.setAttribute("selector", "") })
        }

        for (i in 0 until baseDocument.appComponents.size) {
            val it = baseDocument.appComponents[i]
            val item = doc.createElement("AppIcon")
            item.setAttribute("name", "${it.packageName}/${it.activityName}")
            item.setAttribute("image", it.drawable)
            theme.appendChild(item)

            updateProgress((i + baseDocument.appComponents.size) / (baseDocument.appComponents.size * 2.0),
                    "${i + baseDocument.appComponents.size} / ${baseDocument.appComponents.size * 2}")
        }

        doc.appendChild(theme)
        return doc
    }

    fun export(document: Document, outFile: File) {
        transformer.transform(DOMSource(document), StreamResult(outFile))
    }
}