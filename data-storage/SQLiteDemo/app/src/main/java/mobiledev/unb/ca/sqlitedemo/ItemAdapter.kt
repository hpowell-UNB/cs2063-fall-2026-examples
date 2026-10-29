package mobiledev.unb.ca.sqlitedemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import mobiledev.unb.ca.sqlitedemo.db.entities.Item

class ItemAdapter : BaseAdapter() {
    private val items = mutableListOf<Item>()

    // Updates the adapter's data safely from the background-fetched list
    fun updateData(newItems: List<Item>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): Item = items[position]
    override fun getItemId(position: Int): Long = items[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_layout, parent, false) // Replace with your layout
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val item = getItem(position)
        holder.nameText.text = item.name
        holder.numberText.text = "${item.number}"

        return view
    }

    private class ViewHolder(view: View) {
        val nameText: TextView = view.findViewById(R.id.nameText)
        val numberText: TextView = view.findViewById(R.id.numberText)
    }
}
