package com.example.tabs.ui.gallery

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tabs.R

data class GiftItem(
    val name: String,
    val imagePath: String,
    val price: String,
    val recommendation: List<String>
)

class RecommendedGiftsAdapter(
    private val context: Context,
    private val giftList: List<GiftItem>
) : RecyclerView.Adapter<RecommendedGiftsAdapter.GiftViewHolder>() {

    init {
        // 어댑터 생성 시 giftList 로그 출력
        Log.d("RecommendedGiftsAdapter", "giftList size: ${giftList.size}")
        giftList.forEach { gift ->
            Log.d("RecommendedGiftsAdapter", "Gift: ${gift.name}, Price: ${gift.price}")
        }
    }

    class GiftViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val giftImage: ImageView = view.findViewById(R.id.giftImage)
        val giftName: TextView = view.findViewById(R.id.giftName)
        val giftPrice: TextView = view.findViewById(R.id.giftPrice)
        val giftRecommendation: TextView = view.findViewById(R.id.giftRecommendation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gift, parent, false)
        return GiftViewHolder(view)
    }

    override fun onBindViewHolder(holder: GiftViewHolder, position: Int) {
        val gift = giftList[position]

        holder.giftName.text = gift.name
        holder.giftPrice.text = gift.price
        holder.giftRecommendation.text = "추천!"//: ${gift.recommendation.joinToString(", ")}"

        // 이미지 로드
        val resourceId = context.resources.getIdentifier(gift.imagePath, "drawable", context.packageName)
        if (resourceId != 0) {
            Glide.with(context)
                .load(resourceId)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.giftImage)
        } else {
            holder.giftImage.setImageResource(android.R.drawable.ic_menu_report_image)
        }
    }

    override fun getItemCount(): Int = giftList.size
}




