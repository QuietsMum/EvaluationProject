package com.example.evaluationproject.ui.notifications

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.evaluationproject.R

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Окно уведомлений по состоянию активных и готовых оценок!"
    }
    val text: LiveData<String> = _text
}