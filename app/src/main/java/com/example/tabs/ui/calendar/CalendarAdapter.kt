package com.example.tabs.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.R
import com.example.tabs.utils.models.NameOccasion

class CalendarAdapter(private val nameOccasions: List<NameOccasion>) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    class CalendarViewHolder(view: View)
        : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.eventTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val outText = nameOccasions[position].name + "의 " + nameOccasions[position].occasion + "입니다!"
        holder.nameTextView.text = outText
    }

    override fun getItemCount() = nameOccasions.size
}