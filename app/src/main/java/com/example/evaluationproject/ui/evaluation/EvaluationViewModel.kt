package com.example.evaluationproject.ui.evaluation

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.evaluationproject.R

class EvaluationViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Окно просмотра списка доступных оценок!"
    }
    val text: LiveData<String> = _text
}