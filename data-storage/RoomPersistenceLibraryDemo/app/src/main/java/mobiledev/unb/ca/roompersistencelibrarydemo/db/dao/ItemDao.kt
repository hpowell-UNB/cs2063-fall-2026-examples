package mobiledev.unb.ca.roompersistencelibrarydemo.db.dao

import androidx.room3.*
import kotlinx.coroutines.flow.Flow
import mobiledev.unb.ca.roompersistencelibrarydemo.db.entities.Item

/**
 * This DAO object validates the SQL at compile-time and associates it with a method
 */
@Dao
interface ItemDao {
    // Observable streams naturally leverage Coroutine Flow
    @Query("SELECT * FROM items ORDER BY id ASC")
    fun getAllItemsFlow(): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertItem(item: Item)

    @Delete
    fun deleteItem(item: Item)
}