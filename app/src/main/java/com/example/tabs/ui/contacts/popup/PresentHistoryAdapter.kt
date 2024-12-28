package com.example.tabs.ui.contacts.popup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.R
import com.example.tabs.utils.models.PresentHistory
import java.text.SimpleDateFormat
import java.util.Locale

class PresentHistoryAdapter(private val presentHistoryList: List<PresentHistory>) :
    RecyclerView.Adapter<PresentHistoryAdapter.PresentHistoryViewHolder>() {

    inner class PresentHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val presentNameTextView: TextView = itemView.findViewById(R.id.presentName)
        val presentDateTextView: TextView = itemView.findViewById(R.id.presentDate)

        fun bind(presentHistory: PresentHistory) {
            presentNameTextView.text = presentHistory.gift
            val historyFormatter = SimpleDateFormat("yy-MM-dd", Locale.getDefault())
            presentDateTextView.text = historyFormatter.format(presentHistory.date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresentHistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_present_history, parent, false) // 아이템 레이아웃 inflate
        return PresentHistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PresentHistoryViewHolder, position: Int) {
        val presentHistory = presentHistoryList[position]
        holder.bind(presentHistory)
    }

    override fun getItemCount(): Int {
        return presentHistoryList.size
    }
}