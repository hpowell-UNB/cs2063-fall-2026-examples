package mobiledev.unb.ca.sqlitedemo.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobiledev.unb.ca.sqlitedemo.db.entities.Item

class SQLiteManager(context: Context) {
    private val dbHelper: DatabaseHelper = DatabaseHelper(context)

    suspend fun listAllRecords(): List<Item> {
        return withContext(Dispatchers.IO) {
            val cursor = openReadOnlyDatabase().query(
                DatabaseHelper.TABLE_NAME,
                DatabaseHelper.COLUMNS,
                null,
                null,
                null,
                null,
                null
            )

            // .use {} automatically runs cursor.close() when this block exits or fails
            cursor.use { c ->
                val list = mutableListOf<Item>()

                // Resolve column indices once before entering the loop for better performance
                val idIndex = c.getColumnIndexOrThrow(DatabaseHelper.ID_COLUMN)
                val itemIndex = c.getColumnIndexOrThrow(DatabaseHelper.NAME_COLUMN)
                val numberIndex = c.getColumnIndexOrThrow(DatabaseHelper.NUMBER_COLUMN)

                while (c.moveToNext()) {
                    list.add(
                        Item(
                            id = c.getLong(idIndex),
                            name = c.getString(itemIndex),
                            number = c.getInt(numberIndex)
                        )
                    )
                }
                list // Returns a safe, completely detached list to the caller
            }
        }
    }

    suspend fun insertRecord(item: String, num: String) {
        withContext(Dispatchers.IO) {
            val contentValue = ContentValues()
            contentValue.put(DatabaseHelper.NAME_COLUMN, item)
            contentValue.put(DatabaseHelper.NUMBER_COLUMN, num)
            openWriteDatabase().insert(DatabaseHelper.TABLE_NAME, null, contentValue)
        }
    }

    suspend fun deleteRecord(id: Int) {
        withContext(Dispatchers.IO) {
            openWriteDatabase().delete(
                DatabaseHelper.TABLE_NAME,
                DatabaseHelper.ID_COLUMN + "=?", arrayOf(id.toString())
            )
        }
    }

    fun close() {
        dbHelper.close()
    }

    private fun openReadOnlyDatabase(): SQLiteDatabase {
        return dbHelper.readableDatabase
    }

    private fun openWriteDatabase(): SQLiteDatabase {
        return dbHelper.writableDatabase
    }
}