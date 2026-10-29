package mobiledev.unb.ca.roompersistencelibrarydemo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mobiledev.unb.ca.roompersistencelibrarydemo.repositories.ItemRepository

/**
 * by viewModels() requires a factory to be able to instantiate any required ViewModel attributes.
 * In this case we need the ItemRepository.
 */
class ItemViewModelFactory(private val repository: ItemRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            return ItemViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}