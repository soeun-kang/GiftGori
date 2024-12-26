package com.example.tabs.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GalleryViewModel : ViewModel() {

    private val _images = MutableLiveData<List<Int>>().apply {
        value = listOf(
            com.example.tabs.R.drawable.img1

        )
    }
    val images: LiveData<List<Int>> = _images
}
