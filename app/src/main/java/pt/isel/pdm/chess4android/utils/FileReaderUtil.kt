package pt.isel.pdm.chess4android.utils

import java.io.File
import java.io.InputStream


fun fileToStringList(file : File) : List<String>{
    val inputStream : InputStream = file.inputStream()
    val list = mutableListOf<String>()
    inputStream.bufferedReader().forEachLine { list.add(it) }
    return list
}