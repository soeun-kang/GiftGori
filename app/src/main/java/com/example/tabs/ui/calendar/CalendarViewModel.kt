package com.example.tabs.ui.calendar

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tabs.utils.ManageJson
import com.example.tabs.utils.models.Assigned
import com.example.tabs.utils.models.Contact

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val manageJson = ManageJson(application.applicationContext)
    private val _assignedList = MutableLiveData<List<Assigned>>()
    val assignedList: MutableLiveData<List<Assigned>>
        get() = _assignedList

    init {
        loadAssigned()
    }

    fun loadAssigned() {
        println("assigned load")
        val jsonString = manageJson.readFileFromInternalStorage("occasion.json")
        _assignedList.value = manageJson.parseJsonToAssignedList(jsonString)
    }
}