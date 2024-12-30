package com.example.tabs.ui.gallery

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tabs.utils.ManageJson
import com. example.tabs.utils.models.Contact
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
            val manageJson = ManageJson(context)
            val jsonString = manageJson.readFileFromInternalStorage("contacts.json")
            val contacts = manageJson.parseJsonToDataList(jsonString)
            _personData.value = contacts.map { parsePersonDetails(it, giftList) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun loadGifts(context: Context) {
        _giftData.value = loadGiftData(context)
    }

    private fun calculateAge(birthday: Date): Int {
        val today = Calendar.getInstance()
        val birthCalendar = Calendar.getInstance()
        birthCalendar.time = birthday

        var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) age -= 1
        return age
    }

    private fun calculateRemainDays(birthday: Date): Int {
        val today = Calendar.getInstance() // 현재 날짜
        val birthCalendar = Calendar.getInstance()
        birthCalendar.time = birthday

        birthCalendar.set(Calendar.YEAR, today.get(Calendar.YEAR))

        if (today.after(birthCalendar)) {
            birthCalendar.add(Calendar.YEAR, 1)
        }

        val diffInMillis = birthCalendar.timeInMillis - today.timeInMillis
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }


    private fun filterGiftsByPrice(giftList: List<GiftItem>, prices: List<Double>): List<GiftItem> {
        if (prices.isEmpty()) return emptyList()
        val averagePrice = prices.average()
        val minPrice = averagePrice * 0.6
        val maxPrice = averagePrice * 1.5
        Log.d("PriceFilter", "Average Price: $averagePrice, Range: $minPrice ~ $maxPrice")
        return giftList.filter { gift -> gift.price in minPrice.toInt()..maxPrice.toInt() }
    }

    private fun filterGiftsByDemographics(
        giftList: List<GiftItem>,
        gender: String,
        age: Int
    ): List<GiftItem> {
        return giftList.filter { gift ->
            gift.tag.any { it.equals(gender, ignoreCase = true) } && age in gift.ageRange
        }
    }

    private fun parsePersonDetails(contact: Contact, giftList: List<GiftItem>): PersonDetails {
        val age = calculateAge(contact.bDay)
        val prices = contact.presentHistory.mapNotNull { it.price.toDouble() }
        val remainDays = calculateRemainDays(contact.bDay)

        return PersonDetails(
            name = contact.name,
            age = age,
            gender = contact.gender,
            remain = remainDays,
            history = contact.presentHistory.map {
                HistoryItem(
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.date),
                    gift = it.gift,
                    price = it.price
                )
            },
            similarPriceGifts = filterGiftsByPrice(giftList, prices),
            ageGenderGifts = filterGiftsByDemographics(giftList, contact.gender, age),
            recommendedGifts = giftList.filter { gift ->
                gift.recommendation.any { it.equals(contact.name, ignoreCase = true) }
            }
        )
    }

    fun loadGiftData(context: Context): List<GiftItem> {
        val giftList = mutableListOf<GiftItem>()
        try {
            val jsonString = context.assets.open("gifts.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val imagePath = jsonObject.getString("image_path")
                val price = jsonObject.getInt("price")
                val recommendationArray = jsonObject.getJSONArray("recommendation")
                val recommendation = (0 until recommendationArray.length()).map {
                    recommendationArray.getString(it)
                }
                val tagArray = jsonObject.getJSONArray("tag")
                val tag = (0 until tagArray.length()).map {
                    tagArray.getString(it)
                }
                val ageRange = jsonObject.getJSONArray("age_range")
                    .let { it.getInt(0)..it.getInt(1) }
                giftList.add(GiftItem(name, imagePath, price, recommendation, tag, ageRange))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return giftList
    }


}

data class PersonDetails(
    val name: String,
    val age: Int,
    val gender: String,
    val remain: Int,
    val history: List<HistoryItem>,
    val similarPriceGifts: List<GiftItem>,
    val ageGenderGifts: List<GiftItem>,
    val recommendedGifts: List<GiftItem>
)

data class HistoryItem(
    val date: String,
    val gift: String,
    val price: Int
)
