package com.example.evaluationproject.ui.home

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.evaluationproject.R

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Пожалуйста следуйте инструкции и сделайте снимок для начала оценки!"
    }
    val text: LiveData<String> = _text
}