package com.tobiasfried.iconpacktools.utils

import com.tobiasfried.iconpacktools.model.AppComponent
import com.tobiasfried.iconpacktools.model.FilterDocument
import javafx.util.StringConverter
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import java.nio.file.Path
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilder

class PathConverter : StringConverter<Path>() {
    override fun toString(path: Path?): String {
        path?.let { return it.toString() }
        return ""
    }

    override fun fromString(string: String?): Path {
        string?.let { return Paths.get(string.trim()) }
        return Paths.get("")
    }
}

class FileNameConverter : StringConverter<File>() {
    override fun toString(file: File?): String {
        file?.let { return file.name }
        return ""
    }

    override fun fromString(string: String?): File {
        string?.let { return File(string) }
        return File("")
    }
}