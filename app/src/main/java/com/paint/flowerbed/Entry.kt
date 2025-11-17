package com.paint.flowerbed

import android.provider.BaseColumns
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Entry : BaseColumns {
    const val TABLE_NAME = "flowers"
    const val COLUMN_NAME_DATE = "date"
    const val COLUMN_NAME_DOODLE = "doodle"
    const val COLUMN_NAME_DIARY = "diary"
}

data class EntryData(
    val day: String,
    val drawing: ByteArray,
    var diary: String = ""
) {
    fun isToday(): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
        val today = sdf.format(Date())
        return day == today
    }

    fun getDate(): String {
        return day
    }

    fun getDoodle(): ByteArray {
        return drawing
    }

    fun hasText(): Boolean {
        return diary != ""
    }

    fun getText(): String {
        return diary
    }

    fun setText(text: String) {
        diary = text
    }

    fun save(){

    }
}