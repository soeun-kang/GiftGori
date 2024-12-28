package com.example.tabs.ui.gallery

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.R

class GalleryAdapter(private var personDetailsList: List<PersonDetails>) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    // ViewHolder 정의
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val birthdayText: TextView = view.findViewById(R.id.birthdayText)
        val birthdayImage: ImageView = view.findViewById(R.id.birthdayImage)
        val historyRecyclerView: RecyclerView = view.findViewById(R.id.historyRecyclerView)
        val recommendedGiftsRecyclerView: RecyclerView = view.findViewById(R.id.recommendedGiftsRecyclerView)
    }

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val giftTextView: TextView = view.findViewById(R.id.giftTextView)
    }

    //class giftRecyclerView1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery_page, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val personDetails = personDetailsList[position]

        // 생일 텍스트 설정
        holder.birthdayText.text = "${personDetails.name}님의 생일이 ${personDetails.remain}일 남았어요!"
        holder.birthdayImage.setImageResource(R.drawable.confetti)

        // 히스토리 섹션 제목 설정
        holder.itemView.findViewById<TextView>(R.id.historyHeader).text =
            "${personDetails.name}님과 주고받은 선물"

        // 히스토리 리스트 설정
        holder.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = object : RecyclerView.Adapter<HistoryViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
                    return HistoryViewHolder(view)
                }

                override fun onBindViewHolder(holder: HistoryViewHolder, historyPosition: Int) {
                    val historyItem = personDetails.history[historyPosition]
                    holder.dateTextView.text = historyItem.date
                    holder.giftTextView.text = historyItem.gift
                }

                override fun getItemCount(): Int = personDetails.history.size
            }
        }

        // 추천 선물 리스트 설정
        holder.recommendedGiftsRecyclerView.apply {
            layoutManager = GridLayoutManager(holder.itemView.context, 2)

            Log.d("GalleryAdapter", "Setting recommendedGiftsRecyclerView for: ${personDetails.name}")
            Log.d("GalleryAdapter", "Recommended Gifts: ${personDetails.recommendedGifts.size}")
            personDetails.recommendedGifts.forEach { gift ->
                Log.d("GalleryAdapter", "Gift: ${gift.name}, Price: ${gift.price}")
            }

            adapter = RecommendedGiftsAdapter(holder.itemView.context, personDetails.recommendedGifts)

        }
    }



    override fun getItemCount(): Int = personDetailsList.size

    fun updateData(newData: List<PersonDetails>) {
        personDetailsList = newData
        notifyDataSetChanged()
    }
}