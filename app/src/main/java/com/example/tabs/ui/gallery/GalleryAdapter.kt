package com.example.tabs.ui.gallery

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabs.GiftDetailsActivity
import com.example.tabs.R
import java.text.DecimalFormat

class GalleryAdapter(private var personDetailsList: List<PersonDetails>) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    // ViewHolder 정의
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val birthdayText: TextView = view.findViewById(R.id.birthdayText)
        val birthdayImage: ImageView = view.findViewById(R.id.birthdayImage)
        val historyRecyclerView: RecyclerView = view.findViewById(R.id.historyRecyclerView)
        val similarPriceGiftsAdapter: RecyclerView = view.findViewById(R.id.similarpriceGiftsView)
        val ageGenderGiftsAdapter: RecyclerView = view.findViewById(R.id.ageGenderGiftsView)
        val groupGiftsAdapter: RecyclerView = view.findViewById(R.id.groupGiftsView)
        val recommendedGiftsRecyclerView: RecyclerView = view.findViewById(R.id.recommendedGiftsRecyclerView)
    }

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val giftTextView: TextView = view.findViewById(R.id.giftTextView)
    }

    //class giftRecyclerView1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gallery_viewpager, parent, false)
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
            isNestedScrollingEnabled = false
            adapter = HistoryAdapter(personDetails.history)
        }

        // 히스토리 비어 있는지 확인
        val emptyHistoryImage = holder.itemView.findViewById<ImageView>(R.id.empty_history_image)
        val emptyHistoryText = holder.itemView.findViewById<TextView>(R.id.empty_history_textView)
        val ageGenderHeaderTextView = holder.itemView.findViewById<TextView>(R.id.ageGenderGiftsHeader)
        val similarPriceGiftsHeader = holder.itemView.findViewById<TextView>(R.id.similerPriceGiftsHeader)
        val similarPriceGiftsView = holder.itemView.findViewById<RecyclerView>(R.id.similarpriceGiftsView)
        val constraintLayout = holder.itemView.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.constraintLayout)

        if (personDetails.history.isEmpty()) {
            // 히스토리가 비어 있을 때
            emptyHistoryImage.visibility = View.VISIBLE
            emptyHistoryText.visibility = View.VISIBLE

            // similarPriceGiftsHeader를 숨기고 ageGenderGiftsHeader를 birthdayLayout 아래로 이동
            similarPriceGiftsHeader.visibility = View.GONE
            similarPriceGiftsView.visibility = View.GONE

            val constraintSet = androidx.constraintlayout.widget.ConstraintSet()
            constraintSet.clone(constraintLayout)
            constraintSet.connect(
                ageGenderHeaderTextView.id,
                androidx.constraintlayout.widget.ConstraintSet.TOP,
                R.id.birthdayLayout,
                androidx.constraintlayout.widget.ConstraintSet.BOTTOM,
                420 // margin
            )
            constraintSet.applyTo(constraintLayout)
        } else {
            // 히스토리가 있을 때
            emptyHistoryImage.visibility = View.GONE
            emptyHistoryText.visibility = View.GONE

            // similarPriceGiftsHeader를 보이고 ageGenderGiftsHeader를 기본 위치로 복구
            similarPriceGiftsHeader.visibility = View.VISIBLE
            similarPriceGiftsView.visibility = View.VISIBLE

            val constraintSet = androidx.constraintlayout.widget.ConstraintSet()
            constraintSet.clone(constraintLayout)
            constraintSet.connect(
                ageGenderHeaderTextView.id,
                androidx.constraintlayout.widget.ConstraintSet.TOP,
                R.id.similarpriceGiftsView,
                androidx.constraintlayout.widget.ConstraintSet.BOTTOM,
                20 // margin
            )
            constraintSet.applyTo(constraintLayout)
        }

        // 연령대 및 성별에 맞는 헤더 텍스트 설정
        val ageRange = (personDetails.age / 10) * 10
        val gender = if (personDetails.gender == "@string/female") "여성" else "남성"
        ageGenderHeaderTextView.text = "${ageRange}대 ${gender}의 취향 저격 선물 리스트 💝"

        // 그룹에 맞는 헤더 텍스트 설정
        val groupHeaderTextView: TextView = holder.itemView.findViewById(R.id.groupGiftsHeader)
        val groupRecommendMap = loadGroupRecommendations(holder.itemView.context)
        val groupHeaderText = groupRecommendMap[personDetails.group]?.firstOrNull() ?: "추천 문구가 없습니다."
        groupHeaderTextView.text = groupHeaderText

        // 선물 리스트 설정
        holder.similarPriceGiftsAdapter.setup(holder.itemView.context, personDetails.similarPriceGifts){ giftItem ->
            navigateToGiftDetails(holder.itemView.context, giftItem)
        }
        holder.ageGenderGiftsAdapter.setup(holder.itemView.context, personDetails.ageGenderGifts){ giftItem ->
            navigateToGiftDetails(holder.itemView.context, giftItem)
        }
        holder.groupGiftsAdapter.setup(holder.itemView.context, personDetails.groupGifts){ giftItem ->
            navigateToGiftDetails(holder.itemView.context, giftItem)
        }
        holder.recommendedGiftsRecyclerView.setup(holder.itemView.context, personDetails.recommendedGifts){ giftItem ->
            navigateToGiftDetails(holder.itemView.context, giftItem)
        }
    }

    // GiftDetailsActivity로 이동하는 method
    private fun navigateToGiftDetails(context: Context, giftItem: GiftItem) {
        val formattedPrice = "${DecimalFormat("#,###").format(giftItem.price)}원"
        val intent = Intent(context, GiftDetailsActivity::class.java).apply {
            putExtra("giftName", giftItem.name)
            putExtra("giftPrice", formattedPrice)
            putExtra("giftImage", giftItem.imagePath)
            putStringArrayListExtra("giftRecommendations", ArrayList(giftItem.recommendation))
        }
        context.startActivity(intent)
    }

    fun RecyclerView.setup(
        context: Context,
        data: List<GiftItem>,
        onItemClick: (GiftItem) -> Unit
    ) {
        this.layoutManager = GridLayoutManager(context, 2)
        this.adapter = RecommendedGiftsAdapter(context, data, onItemClick)
        this.isNestedScrollingEnabled = false
    }

    private fun loadGroupRecommendations(context: Context): Map<String, List<String>> {
        return try {
            val jsonString = context.assets.open("groupRecommend_header.json").bufferedReader().use { it.readText() }
            val jsonObject = org.json.JSONObject(jsonString)
            jsonObject.keys().asSequence().associateWith { key ->
                val array = jsonObject.getJSONArray(key)
                (0 until array.length()).map { index -> array.getString(index) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap() // 오류 발생 시 빈 맵 반환
        }
    }


    override fun getItemCount(): Int = personDetailsList.size

    fun updateData(newData: List<PersonDetails>) {
        personDetailsList = newData
        notifyDataSetChanged()
    }
}
