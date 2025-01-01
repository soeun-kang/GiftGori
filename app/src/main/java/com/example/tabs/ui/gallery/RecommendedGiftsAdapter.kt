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
import java.text.DecimalFormat

data class GiftItem(
    val name: String,
    val imagePath: String,
    val price: Int,
    val recommendation: List<String>,
    val tag: List<String>,
    val ageRange: IntRange
)

class RecommendedGiftsAdapter(
    private val context: Context,
    private val giftList: List<GiftItem>,
    private val onItemClick: (GiftItem) -> Unit
) : RecyclerView.Adapter<RecommendedGiftsAdapter.GiftViewHolder>() {

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
        holder.giftRecommendation.text = "추천!"
        holder.giftPrice.text = "${DecimalFormat("#,###").format(gift.price)}원"
        Log.d("gift data", "${gift.name} ${gift.imagePath}")


        // 이미지 로드
        val resourceId = context.resources.getIdentifier(gift.imagePath, "drawable", context.packageName)
        if (resourceId != 0) {
            holder.giftImage.setImageResource(resourceId)
        } else {
            holder.giftImage.setImageResource(android.R.drawable.ic_menu_report_image)
        }

        holder.itemView.setOnClickListener {
            onItemClick(gift) // 클릭 이벤트 전달
        }
    }

    override fun getItemCount(): Int = giftList.size
}




