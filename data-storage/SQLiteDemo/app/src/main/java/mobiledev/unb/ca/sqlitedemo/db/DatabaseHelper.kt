package mobiledev.unb.ca.sqlitedemo.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper internal constructor(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    companion object {
        // Database information
        private const val DATABASE_NAME = "ITEM_TEST.DB"
        private const val DATABASE_VERSION = 1

        // Table columns
        const val ID_COLUMN = "_id"
        const val NAME_COLUMN = "name"
        const val NUMBER_COLUMN = "number"

        @JvmField
        var COLUMNS = arrayOf(ID_COLUMN, NAME_COLUMN, NUMBER_COLUMN)

        // Table creation statement
        const val TABLE_NAME = "item_db"
        private const val TABLE_CREATE = ("CREATE TABLE " + TABLE_NAME + " (" + ID_COLUMN
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COLUMN + " TEXT, " + NUMBER_COLUMN + " INT);")
    }
}