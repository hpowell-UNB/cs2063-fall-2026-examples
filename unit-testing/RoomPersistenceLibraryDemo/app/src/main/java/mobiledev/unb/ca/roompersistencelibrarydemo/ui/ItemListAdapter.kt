package mobiledev.unb.ca.roompersistencelibrarydemo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mobiledev.unb.ca.roompersistencelibrarydemo.R
import mobiledev.unb.ca.roompersistencelibrarydemo.db.entities.Item

class ItemListAdapter(
    private val onDeleteClicked: (Item) -> Unit
) : ListAdapter<Item, ItemListAdapter.ItemViewHolder>(ItemDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)
        return ItemViewHolder(view, onDeleteClicked)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ItemViewHolder(itemView: View, private val onDeleteClicked: (Item) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.item_text)
        private val numberTextView: TextView = itemView.findViewById(R.id.number_text)
        private var deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        fun bind(item: Item) {
            nameTextView.text = item.name
            numberTextView.text = item.num.toString()
            deleteButton.setOnClickListener { onDeleteClicked(item) }
        }
    }

    // DiffUtil handles efficient item updates instead of recreating the whole list
    object ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
}