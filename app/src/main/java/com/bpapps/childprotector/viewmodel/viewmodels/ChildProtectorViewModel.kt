package com.bpapps.childprotector.viewmodel.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.bpapps.childprotector.model.ChildProtectorRepository

private const val TAG = "TAG.ChildProtectorViewModel"

@SuppressLint("LongLogTag")
class ChildProtectorViewModel : ViewModel() {

    private val repository = ChildProtectorRepository.getInstance()
}