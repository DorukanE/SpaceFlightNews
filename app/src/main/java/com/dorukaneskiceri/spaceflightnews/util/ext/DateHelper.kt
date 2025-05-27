package com.dorukaneskiceri.spaceflightnews.util.ext

import java.text.SimpleDateFormat
import java.util.Locale

const val BACKEND_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ssXXX"
const val DISPLAY_DATE_TIME = "dd.MM.yyyy - HH:mm"
private val lang = Locale("tr", "TR")

fun changeDateFormat(
    inputDate: String?,
    inputDateFormat: String,
    outputDateFormat: String?
): String {
    return try {
        val inputFormat = SimpleDateFormat(inputDateFormat, lang)
        val outputFormat = SimpleDateFormat(outputDateFormat, lang)
        val formattedInputDate = inputDate?.let { inputFormat.parse(it) }
        return formattedInputDate?.let { outputFormat.format(it) }.orEmpty()
    } catch (e: Exception) {
        ""
    }
}