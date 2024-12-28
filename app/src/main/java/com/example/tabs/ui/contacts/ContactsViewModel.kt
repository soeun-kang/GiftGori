package com.example.tabs.ui.contacts

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.tabs.utils.ManageJson
import com.example.tabs.utils.models.Contact


class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    val manageJson = ManageJson(context, "contacts.json")

    private val _contactList = MutableLiveData<List<Contact>>()
    val contactList: MutableLiveData<List<Contact>>
        get() = _contactList

    init {
        loadContacts()
    }

    fun loadContacts() {
        _contactList.value = manageJson.dataList
    }

}