package com.bpapps.childprotector.viewmodel.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bpapps.childprotector.model.ChildProtectorRepository
import com.bpapps.childprotector.model.classes.AppUsageStatInterval
import com.bpapps.childprotector.model.classes.ChildProtectorException
import com.bpapps.childprotector.model.classes.Location
import com.bpapps.childprotector.model.classes.User

class ParentViewViewModel : ViewModel() {

    override fun onCleared() {
        super.onCleared()
    }

    private val repository: ChildProtectorRepository = ChildProtectorRepository.getInstance()

    var parent: User? = null
    var children: List<User>? = null
    private var locations: ArrayList<Location>? = null
    private var usagetats: ArrayList<AppUsageStatInterval>? = null

    private var dataLoadedCallback: IOnDataLoaded? = null
    private var parentLoaded: Boolean = false
    private var childrenLoaded: Boolean = false
    private var locationsLoaded = false
    private var usageStatsLoaded = false
    var allDataLoaded: Boolean = false
        get() = parentLoaded && childrenLoaded && locationsLoaded && usageStatsLoaded

    private var currChildViewInd: Int = 0
    private var currChildChangedCallback: IOnCurrChildUpdated? = null
    val childStats: ArrayList<AppUsageStatInterval>
        get() {
            val childId = children?.get(currChildViewInd)?._id
            val stats: ArrayList<AppUsageStatInterval> = arrayListOf()

            usagetats?.forEach { us ->
                if (us._userId == childId) {
                    stats.add(us)
                }
            }

            return stats
        }
    val childrenLocations: ArrayList<Location>
        get() {
            val childId = children?.get(currChildViewInd)?._id
            val locations: ArrayList<Location> = arrayListOf()

            this@ParentViewViewModel.locations?.forEach { location ->
                if (location.userId == childId) {
                    locations.add(location)
                }
            }

            return locations
        }


    init {
        repository.loadChildren(object : ChildProtectorRepository.IOnRegisteredChildrenLoaded {
            override fun onLoaded(children: List<User>) {
                this@ParentViewViewModel.children = children
                childrenLoaded = true

                val ids = arrayListOf<String>()
                children.forEach { child ->
                    ids.add(child._id)
                }

                repository.loadLocations(
                    ids,
                    object : ChildProtectorRepository.IOnLocationsLoadedSuccesses {
                        override fun onLoaded(locations: ArrayList<Location>) {
                            this@ParentViewViewModel.locations = locations
                            locationsLoaded = true

                            Log.d(
                                TAG,
                                "total ${this@ParentViewViewModel.locations?.size} locations"
                            )
                            this@ParentViewViewModel.locations?.forEach { loc ->
                                Log.d(TAG, loc.toString())
                            }

                            locationsLoaded = true

                            if (allDataLoaded)
                                dataLoadedCallback?.onLoaded()
                        }
                    },
                    object : ChildProtectorRepository.IOnLocationLoadedFailure {
                        override fun onFailure(error: ChildProtectorException) {
                        }
                    })

                repository.loadUsageStats(
                    ids,
                    object : ChildProtectorRepository.IOnUsageStatsLoadedSuccesses {
                        override fun onLoaded(usageStats: java.util.ArrayList<AppUsageStatInterval>) {
                            this@ParentViewViewModel.usagetats = usageStats
                            usageStatsLoaded = true

                            Log.d(
                                TAG,
                                "total ${this@ParentViewViewModel.usagetats?.size} usage stats"
                            )
                            this@ParentViewViewModel.usagetats?.forEach { item ->
                                Log.d(TAG, item.toString())
                            }

                            if (allDataLoaded)
                                dataLoadedCallback?.onLoaded()
                        }
                    },
                    object : ChildProtectorRepository.IOnUsageStatsLoadedFailure {
                        override fun onFailure(error: ChildProtectorException) {
                        }
                    })

                if (allDataLoaded)
                    dataLoadedCallback?.onLoaded()
            }
        })

        repository.loadRegisteredParent(
            object : ChildProtectorRepository.IOnRegisteredParentLoaded {
                override fun onLoaded(parent: User) {
                    this@ParentViewViewModel.parent = parent
                    parentLoaded = true

                    if (allDataLoaded)
                        dataLoadedCallback?.onLoaded()
                }
            })
    }

    companion object {
        private const val TAG = "TAG.ParentViewViewModel"
    }

    fun getChildrenNames(): ArrayList<String> {
        val names = arrayListOf<String>()
        children?.forEach { child ->
            names.add(child._name)
        }

        return names
    }

    fun updateCurrChildViewedInd(ind: Int) {
        currChildViewInd = ind
        currChildChangedCallback?.onUpdate(ind)
    }

    fun registerForDataLoadedCallback(callback: IOnDataLoaded) {
        dataLoadedCallback = callback
    }

    fun unRegisterDataLoadedCallback() {
        dataLoadedCallback = null
    }

    fun registerForCurrChildChangedInd(callback: IOnCurrChildUpdated) {
        currChildChangedCallback = callback
    }

    fun unRegisterForCurrChildChangedInd() {
        currChildChangedCallback = null
    }

    interface IOnDataLoaded {
        fun onLoaded()
    }

    interface IOnCurrChildUpdated {
        fun onUpdate(ind: Int)
    }
}