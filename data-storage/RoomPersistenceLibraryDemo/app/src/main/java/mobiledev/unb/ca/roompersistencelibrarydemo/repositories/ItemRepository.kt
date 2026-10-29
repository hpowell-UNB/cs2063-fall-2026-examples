package mobiledev.unb.ca.roompersistencelibrarydemo.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import mobiledev.unb.ca.roompersistencelibrarydemo.db.dao.ItemDao
import mobiledev.unb.ca.roompersistencelibrarydemo.db.entities.Item

class ItemRepository(
    private val itemDao: ItemDao,
    // Injecting the dispatcher makes this class flexible and highly testable
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    // Expose the Flow directly to the presentation layer.
    // Room automatically updates this on a background thread when data changes.
    val allItems: Flow<List<Item>> = itemDao.getAllItemsFlow()

    // One-shot suspend operations which enforce execution on the IO thread pool
    suspend fun insertRecord(name: String, num: Int) {
        return withContext(ioDispatcher) {
            val newItem = Item(name=name, num=num)
            itemDao.insertItem(newItem)
        }
    }

    suspend fun deleteRecord(item: Item) {
        return withContext(ioDispatcher) {
            itemDao.deleteItem(item)
        }
    }
}
