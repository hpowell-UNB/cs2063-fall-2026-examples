package mobiledev.unb.ca.restapiclientdemo.utils

import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mobiledev.unb.ca.restapiclientdemo.MyAdapter
import mobiledev.unb.ca.restapiclientdemo.models.CountryItem

class LoadDataTask {
    private lateinit var recyclerView: RecyclerView

    fun setRecyclerView(recyclerView: RecyclerView): LoadDataTask {
        this.recyclerView = recyclerView
        return this
    }

    fun execute() {
        MainScope().launch(Dispatchers.IO) {
            val jsonUtils = JsonUtils()

            launch(Dispatchers.Main) {
                // Updates to UI components must take place in the main thread
                updateDisplay(jsonUtils.countryItemList)
            }
        }
    }

    private fun updateDisplay(countryItemList: List<CountryItem>) {
        val adapter = MyAdapter(countryItemList)
        recyclerView.adapter = adapter
    }
}
