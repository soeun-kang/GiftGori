package com.example.tabs.ui.contacts.edit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.R
import com.example.tabs.utils.models.Occasion

class OccasionAdapter(private val items: List<Occasion>) :
    RecyclerView.Adapter<OccasionAdapter.DropdownViewHolder>() {

    class DropdownViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // edit text로
        val occasion: TextView = view.findViewById(R.id.occasion)
        val date: TextView = view.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DropdownViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dropdown_occasion, parent, false)
        println("Connected to Adapter")
        return DropdownViewHolder(view)
    }

    override fun onBindViewHolder(holder: DropdownViewHolder, position: Int) {
        val item = items[position]
        holder.occasion.text = item.occasion
        holder.date.text = item.date.toString()
        println("binded adapter")
    }

    override fun getItemCount(): Int = items.size
}