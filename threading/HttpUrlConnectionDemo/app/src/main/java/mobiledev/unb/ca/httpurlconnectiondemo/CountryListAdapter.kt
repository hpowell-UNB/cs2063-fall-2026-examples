package mobiledev.unb.ca.httpurlconnectiondemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mobiledev.unb.ca.httpurlconnectiondemo.models.CountryItem

class CountryListAdapter(): ListAdapter<CountryItem, CountryListAdapter.CountryViewHolder>(CountryDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = getItem(position)
        holder.bind(country)
    }

    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countryNameTextView: TextView = itemView.findViewById(R.id.countryName)
        val capitalNameTextView: TextView = itemView.findViewById(R.id.capitalCity)
        val continentNameTextView: TextView = itemView.findViewById(R.id.continentName)

        fun bind(countryItem: CountryItem) {
            countryNameTextView.text = countryItem.title
            capitalNameTextView.text = countryItem.capital
            continentNameTextView.text = countryItem.continent
        }
    }

    // DiffUtil handles efficient item updates instead of recreating the whole list
    object CountryDiffCallback : DiffUtil.ItemCallback<CountryItem>() {
        override fun areItemsTheSame(oldItem: CountryItem, newItem: CountryItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: CountryItem, newItem: CountryItem): Boolean {
            return oldItem == newItem
        }
    }
}