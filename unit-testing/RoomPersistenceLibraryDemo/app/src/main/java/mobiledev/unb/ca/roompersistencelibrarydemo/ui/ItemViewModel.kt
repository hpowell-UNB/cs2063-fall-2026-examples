package mobiledev.unb.ca.roompersistencelibrarydemo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mobiledev.unb.ca.roompersistencelibrarydemo.db.entities.Item
import mobiledev.unb.ca.roompersistencelibrarydemo.repositories.ItemRepository

class ItemViewModel(private val itemRepository: ItemRepository) : ViewModel() {
    // Convert the cold data Flow into a UI-ready hot StateFlow
    val uiState: StateFlow<List<Item>> = itemRepository.allItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insert(name: String, num: Int) {
        // ViewModel handles UI lifecycle scope, Repository handles threading
        viewModelScope.launch {
            itemRepository.insertRecord(name, num)
        }
    }

    fun delete(item: Item) {
        // ViewModel handles UI lifecycle scope, Repository handles threading
        viewModelScope.launch {
            itemRepository.deleteRecord(item)
        }
    }
}
