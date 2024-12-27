package com.example.tabs.ui.gallery

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class GalleryViewModel : ViewModel() {

    private val _names = MutableLiveData<List<String>>()
    val names: LiveData<List<String>> = _names

    private val _personDetails = MutableLiveData<String>()
    val personDetails: LiveData<String> = _personDetails

    fun loadNames(context: Context) {
        val namesList = mutableListOf<String>()
        try {
            val inputStream = context.assets.open("famous_people_data_relevant_gifts.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonArray = JSONArray(reader.readText())
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                namesList.add(obj.getString("name"))
            }
            _names.value = namesList
            Log.d("GalleryViewModel", "Loaded names: $namesList")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadDetails(context: Context, name: String) {
        try {
            val inputStream = context.assets.open("famous_people_data_relevant_gifts.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonArray = JSONArray(reader.readText())
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                if (obj.getString("name") == name) {
                    val details = """
                        Name: ${obj.getString("name")}
                        Phone: ${obj.getString("phoneNumber")}
                        Birthday: ${obj.getString("bDay")}
                        Recent Gifts: ${
                        obj.getJSONArray("presentHistory").let { history ->
                            (0 until history.length()).joinToString { idx ->
                                history.getJSONObject(idx).getString("gift")
                            }
                        }
                    }
                    """.trimIndent()
                    _personDetails.value = details
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private val _images = MutableLiveData<List<Int>>().apply {
        value = listOf(
            com.example.tabs.R.drawable.gallery_img1

        )
    }
    val images: LiveData<List<Int>> = _images
}
