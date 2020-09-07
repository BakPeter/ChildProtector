package com.bpapps.childprotector.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bpapps.childprotector.R
import com.bpapps.childprotector.model.classes.Location
import com.bpapps.childprotector.viewmodel.viewmodels.ParentViewViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class LocationsOnMapShowerFragment(private val viewModel: ParentViewViewModel) : Fragment(),
    OnMapReadyCallback, ParentViewViewModel.IOnCurrChildUpdated {

    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_locations_on_map_shower, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.mapView)
        mapView?.let { it ->
            it.onCreate(null)
            it.onResume()
            it.getMapAsync(this)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.registerForCurrChildChangedInd(this)
    }

    override fun onStop() {
        viewModel.unRegisterForCurrChildChangedInd()
        super.onStop()
    }

    override fun onMapReady(p0: GoogleMap?) {
        MapsInitializer.initialize(requireContext())
        map = p0!!

        onUpdate(0)

    }

    override fun onUpdate(ind: Int) {
        map.clear()
        val locations: ArrayList<Location> = viewModel.childrenLocations

        locations.forEach { loc ->
            map.addMarker(
                MarkerOptions().position(LatLng(loc.latitude!!, loc.longitude!!))
                    .title(loc.date.toString())
            )
        }

        if (locations.size > 0) {
            map.moveCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(
                        locations[0].latitude!!,
                        locations[0].longitude!!
                    )
                )
            )
        }

//        val sydney = LatLng(-34.0, 151.0)
//        map.addMarker(MarkerOptions().position(sydney).title("marker in sydney"))
//        map.moveCamera(CameraUpdateFactory.newLatLng((sydney)))
    }
}