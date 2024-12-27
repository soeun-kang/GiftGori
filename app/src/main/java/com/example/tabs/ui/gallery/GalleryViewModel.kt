package com.example.tabs.ui.gallery

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tabs.utils.ManageJson
import com.example.tabs.utils.Contact
import java.text.SimpleDateFormat
import java.util.*

class GalleryViewModel : ViewModel() {

    private val _personData = MutableLiveData<List<PersonDetails>>()
    val personData: LiveData<List<PersonDetails>> = _personData

    fun loadPersonData(context: Context) {
        try {
            val manageJson = ManageJson(context, "famous_people_data_relevant_gifts.json")
            val contacts = manageJson.dataList
            _personData.value = contacts.map { parsePersonDetails(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parsePersonDetails(contact: Contact): PersonDetails {
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

        return PersonDetails(
            name = contact.name,
            remain = remainDays,
            history = historyList
        )
    }
}

data class PersonDetails(
    val name: String,
    val remain: Int,
    val history: List<HistoryItem>
)

data class HistoryItem(
    val date: String,
    val gift: String
)
