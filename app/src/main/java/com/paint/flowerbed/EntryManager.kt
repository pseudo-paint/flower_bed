package com.paint.flowerbed

import android.content.ContentValues
import android.database.Cursor
import android.database.DatabaseUtils
import android.provider.BaseColumns
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EntryManager
{

    fun addFlowerEntry(dbHelper: FlowersDbHelper, doodle: ByteArray, diary: String) {
        val db = dbHelper.writableDatabase

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
        val currentDate = sdf.format(Date())

        val values = ContentValues().apply {
            put(Entry.COLUMN_NAME_DATE, currentDate)
            put(Entry.COLUMN_NAME_DOODLE, doodle)
            put(Entry.COLUMN_NAME_DIARY, diary)
        }

        val newRowId = db?.insert(Entry.TABLE_NAME, null, values)
    }


    fun findAndProcessEntry(dbHelper: FlowersDbHelper, dateToFind: String) {
        val cursor = getFlowerEntryByDate(dbHelper, dateToFind)

        cursor?.use {
            if (it.moveToFirst()) {
                val diary = it.getString(it.getColumnIndexOrThrow(Entry.COLUMN_NAME_DIARY))
                val doodle = it.getBlob(it.getColumnIndexOrThrow(Entry.COLUMN_NAME_DOODLE))

                println("Found entry for $dateToFind: Diary is '$diary'")
            } else {
                println("No entry found for $dateToFind.")
            }
        }
    }


    fun getFlowerEntryByDate(dbHelper: FlowersDbHelper, date: String): Cursor? {
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            Entry.COLUMN_NAME_DATE,
            Entry.COLUMN_NAME_DOODLE,
            Entry.COLUMN_NAME_DIARY
        )

        val selection = "${Entry.COLUMN_NAME_DATE} = ?"
        val selectionArgs = arrayOf(date)

        val cursor = db.query(
            Entry.TABLE_NAME, // The table to query
            projection,                       // The columns to return
            selection,                        // The columns for the WHERE clause
            selectionArgs,                    // The values for the WHERE clause
            null,                             // don't group the rows
            null,                             // don't filter by row groups
            null                              // The sort order
        )

        return cursor
    }

    fun countAllEntries(dbHelper: FlowersDbHelper, date: String): Long {
        val db = dbHelper.readableDatabase

        val selection = "${BaseColumns._ID} = *"

        val count = DatabaseUtils.queryNumEntries(
            db,
            Entry.TABLE_NAME,
            selection
        )

        db.close()
        return count
    }

    fun countEntriesForDate(dbHelper: FlowersDbHelper, date: String): Long {
        val db = dbHelper.readableDatabase

        val selection = "${Entry.COLUMN_NAME_DATE} = ?"
        val selectionArgs = arrayOf(date)

        val count = DatabaseUtils.queryNumEntries(
            db,
            Entry.TABLE_NAME,
            selection,
            selectionArgs
        )

        db.close()
        return count
    }
}