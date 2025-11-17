package com.paint.flowerbed

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.paint.flowerbed.FlowersContract.SQL_CREATE_ENTRIES
import com.paint.flowerbed.FlowersContract.SQL_DELETE_ENTRIES

object FlowersContract {

    const val SQL_CREATE_ENTRIES = ("CREATE TABLE ${Entry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${Entry.COLUMN_NAME_DATE} TEXT," +
            "${Entry.COLUMN_NAME_DOODLE} BLOB," +
            "${Entry.COLUMN_NAME_DIARY} TEXT)")

    const val SQL_DELETE_ENTRIES = ("DROP TABLE IF EXISTS ${Entry.TABLE_NAME}")
}

class FlowersDbHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FeedReader.db"
    }
}