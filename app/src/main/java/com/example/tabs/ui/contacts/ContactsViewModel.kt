package com.example.tabs.ui.contacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.tabs.utils.ManageJson


class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    val manageJson = ManageJson(context, "contacts.json")

}