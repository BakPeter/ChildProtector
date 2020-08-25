package com.bpapps.childprotector.viewmodel.viewmodels

import androidx.lifecycle.ViewModel
import com.bpapps.childprotector.model.ChildProtectorRepository
import com.bpapps.childprotector.model.classes.Location

class DataSqlDebugViewModel : ViewModel() {

    private val repository = ChildProtectorRepository.getInstance()
    private var locations: List<Location>

    init {
        locations = repository.getLocations()
    }

    fun getLocations() = locations
}