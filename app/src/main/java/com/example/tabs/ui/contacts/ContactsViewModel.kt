package com.example.tabs.ui.contacts

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.tabs.utils.ManageJson
import com.example.tabs.utils.models.Assigned
import com.example.tabs.utils.models.Contact


class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    private val manageJson = ManageJson(application.applicationContext)

    private val _contactList = MutableLiveData<List<Contact>>()
    val contactList: MutableLiveData<List<Contact>>
        get() = _contactList

    private val _assignedList = MutableLiveData<List<Assigned>>()
    val assignedList: MutableLiveData<List<Assigned>>
        get() = _assignedList

    init {
        loadContacts()
        loadAssigned()
    }

    fun loadContacts() {
        val jsonString = manageJson.readFileFromInternalStorage("contacts.json")
        println("loading finished")
        _contactList.value = manageJson.parseJsonToDataList(jsonString)
    }

    fun loadAssigned() {
        val jsonString = manageJson.readFileFromInternalStorage("occasion.json")
        _assignedList.value = manageJson.parseJsonToAssignedList(jsonString)
    }

    fun updateContactList(newContactList: List<Contact>) {
        _contactList.value = newContactList
        val jsonString = manageJson.parseDataListToJson(_contactList.value!!)
        manageJson.writeFileToInternalStorage("contacts.json", jsonString)
        println("writing finished")
    }

}