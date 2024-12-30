package com.example.tabs.utils

import android.content.Context
import com.example.tabs.utils.models.Contact
import com.example.tabs.utils.models.PresentHistory
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.text.ParseException

class ManageJson(private val context: Context, fileName: String) {

    // dataList에 데이터 저장
    val jsonString = getJsonDataFromAsset(fileName)
    val dataList = parseJsonToDataList(jsonString)

    fun logging(){
        println("Extracted successfully DataList: $dataList")
    }

    // assets 폴더에서 JSON 파일 읽기 (새로운 import 없이)
    private fun getJsonDataFromAsset(fileName: String): String? {
        val jsonString: String
        try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            jsonString = String(buffer, Charsets.UTF_8)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    // JSON 문자열을 데이터 모델 리스트로 파싱 (새로운 import 없이)
    private fun parseJsonToDataList(jsonString: String?): List<Contact> {
        val dataList = mutableListOf<Contact>()
        if (jsonString == null) {
            return dataList
        }
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val phoneNumber = jsonObject.getString("phoneNumber")
                val bDayString = jsonObject.getString("bDay")
                val bDay = parseDateString(bDayString)
                val gender = jsonObject.getString("gender")
                val recentContact = jsonObject.getInt("recentContact")
                val group = jsonObject.getString("group")
                val presentHistoryArray = jsonObject.getJSONArray("presentHistory")
                val presentHistoryList = mutableListOf<PresentHistory>()
                for (j in 0 until presentHistoryArray.length()) {
                    val presentHistoryObject = presentHistoryArray.getJSONObject(j)
                    val dateString = presentHistoryObject.getString("date")
                    val gift = presentHistoryObject.getString("gift")
                    val date = parseDateString(dateString)
                    val price = presentHistoryObject.getInt("price")
                    val presentHistory = PresentHistory(date, gift, price)
                    presentHistoryList.add(presentHistory)
                }

                val data = Contact(name, phoneNumber, bDay, gender, group, recentContact, presentHistoryList)
                dataList.add(data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dataList
    }
}

fun parseDateString(dateString: String): Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    formatter.isLenient = false // 엄격한 모드 설정
    return try {
        val res = formatter.parse(dateString)
        if (res != null) {
            res
        } else {
            throw ParseException("Invalid date format", 0)
        }
    } catch (e: ParseException) {
        println("Error parsing date string: $dateString")
        e.printStackTrace()
        Date()
    }
}