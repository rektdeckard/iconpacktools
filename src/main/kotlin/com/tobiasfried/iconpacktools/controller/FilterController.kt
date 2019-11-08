package com.tobiasfried.iconpacktools.controller

import com.tobiasfried.iconpacktools.controller.FilterFormat.*
import com.tobiasfried.iconpacktools.model.AppComponent
import com.tobiasfried.iconpacktools.model.BaseFilterDocument
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

class FilterController(val updateProgress: (Double, String?) -> Unit) : Controller() {
    private val dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    private val transformer = TransformerFactory.newInstance().newTransformer().also {
        it.setOutputProperty(OutputKeys.METHOD, "xml")
        it.setOutputProperty(OutputKeys.INDENT, "yes")
        it.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
    }

    fun createXML(file: File, path: Path, overwrite: Boolean = false, vararg outTypes: FilterFormat) {
        val baseDocument = createBaseDocumentFromAppFilter(file)

        for (outType in outTypes) {
            val formattedDocument = when (outType) {
                APPFILTER -> throw NoSuchMethodException()
                APPMAP -> createAppMapDocument(baseDocument)
                THEME_RESOURCES -> createThemeResourcesDocument(baseDocument)
            }
            exportXML(formattedDocument, path, outType, overwrite)
        }
    }

    fun validateAppFilter(file: File): Boolean {
        val isValid: Boolean
        try {
            val inputSource = InputSource(StringReader(file.readText()))
            val doc = dBuilder.parse(inputSource)
            val firstItem = doc.getElementsByTagName("item").item(0)
            isValid = (firstItem.hasAttributes() && (firstItem.attributes.getNamedItem("component").nodeValue.isNotEmpty()))
        } catch (e: Exception) { throw e }
        return isValid
    }

    private fun createBaseDocumentFromAppFilter(file: File): BaseFilterDocument {
        val baseDocument = BaseFilterDocument()

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
            updateProgress(i / (items.length.toDouble() * 2), "$i / ${items.length}")
        }

        return baseDocument
    }

//    private fun createAppFilterDocument(baseDocument: BaseFilterDocument): Document {}

    private fun createAppMapDocument(baseDocument: BaseFilterDocument): Document {
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

    private fun createThemeResourcesDocument(baseDocument: BaseFilterDocument): Document {
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

    private fun exportXML(document: Document, path: Path, outType: FilterFormat, overwrite: Boolean) {
//        transformer.transform(DOMSource(document), StreamResult(System.out))
        if (overwrite || !File(path.toString(), outType.filename).exists()) {
            transformer.transform(DOMSource(document), StreamResult(File(path.toString(), outType.filename)))
            updateProgress(1.0, "COMPLETE")
        } else updateProgress(0.0, "FILE ALREADY EXISTS")
    }
}

enum class FilterFormat(val filename: String) {
    APPFILTER("appfilter.xml"),
    APPMAP("appmap.xml"),
    THEME_RESOURCES("theme_resources.xml")
}