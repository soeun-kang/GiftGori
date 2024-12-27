package com.example.tabs.ui.gallery

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import com.example.tabs.utils.ManageJson

class GalleryViewModel : ViewModel() {

    private val _names = MutableLiveData<List<String>>()
    val names: LiveData<List<String>> = _names

    private val _personDetails = MutableLiveData<String>()
    val personDetails: LiveData<String> = _personDetails

    fun loadNames(context: Context) {
        try {
            val manageJson = ManageJson(context, "famous_people_data_relevant_gifts.json")
            val contacts = manageJson.dataList
            _names.value = contacts.map { it.name }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadDetails(context: Context, name: String) {
        try {
            val manageJson = ManageJson(context, "famous_people_data_relevant_gifts.json")
            val contact = manageJson.dataList.find { it.name == name }
            contact?.let {
                _personDetails.value = it.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
