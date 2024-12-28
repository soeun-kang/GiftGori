package com.example.tabs.ui.gallery

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tabs.utils.ManageJson
import com.example.tabs.utils.Contact
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class GalleryViewModel : ViewModel() {

    private val _personData = MutableLiveData<List<PersonDetails>>()
    val personData: LiveData<List<PersonDetails>> = _personData

    private val _giftData = MutableLiveData<List<GiftItem>>()
    val giftData: LiveData<List<GiftItem>> = _giftData

    fun loadPersonData(context: Context) {
        try {
            val giftList = loadGiftData(context)
            val manageJson = ManageJson(context, "famous_people_data_relevant_gifts.json")
            val contacts = manageJson.dataList
            _personData.value = contacts.map { parsePersonDetails(it, giftList) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadGifts(context: Context) {
        _giftData.value = loadGiftData(context)
    }

    private fun parsePersonDetails(contact: Contact, giftList: List<GiftItem>): PersonDetails {
        val today = Date()

        // contact.bDay의 연도를 올해로 설정
        val calendar = Calendar.getInstance()
        calendar.time = contact.bDay
        calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
        val thisYearBirthday = calendar.time

        // 다음 생일까지 남은 날짜 계산
        val nextBirthday = if (today.before(thisYearBirthday)) {
            thisYearBirthday
        } else {
            calendar.add(Calendar.YEAR, 1)
            calendar.time
        }

        val remainDays = ((nextBirthday.time - today.time) / (1000 * 60 * 60 * 24)).toInt()

        // 선물 이력 가공
        val historyList = contact.presentHistory.map {
            HistoryItem(
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.date),
                gift = it.gift
            )
        }

        // 추천 선물 리스트를 필터링하여 추가
        val recommendedGifts = giftList.filter { gift ->
            gift.recommendation.any { it.equals(contact.name, ignoreCase = true) }
        }
        Log.d("GalleryViewModel", "recommendedGifts size: ${recommendedGifts.size}")


        return PersonDetails(
            name = contact.name,
            remain = remainDays,
            history = historyList,
            recommendedGifts = recommendedGifts
        )
    }

    fun loadGiftData(context: Context): List<GiftItem> {
        val giftList = mutableListOf<GiftItem>()
        try {
            val jsonString = context.assets.open("gifts_with_recommendations.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val imagePath = jsonObject.getString("image_path")
                val price = jsonObject.getString("price")
                val recommendationArray = jsonObject.getJSONArray("recommendation")
                val recommendation = (0 until recommendationArray.length()).map {
                    recommendationArray.getString(it)
                }
                giftList.add(GiftItem(name, imagePath, price, recommendation))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return giftList
    }


}

data class PersonDetails(
    val name: String,
    val remain: Int,
    val history: List<HistoryItem>,
    val recommendedGifts: List<GiftItem>
)

data class HistoryItem(
    val date: String,
    val gift: String
)
