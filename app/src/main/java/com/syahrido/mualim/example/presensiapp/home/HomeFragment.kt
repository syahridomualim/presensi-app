package com.syahrido.mualim.example.presensiapp.home

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.syahrido.mualim.example.presensiapp.R
import com.syahrido.mualim.example.presensiapp.base.BaseFragment
import com.syahrido.mualim.example.presensiapp.constants.Constant
import com.syahrido.mualim.example.presensiapp.data.network.EmployeeApi
import com.syahrido.mualim.example.presensiapp.data.network.EmployeeDataStore
import com.syahrido.mualim.example.presensiapp.data.repository.EmployeeRepository
import com.syahrido.mualim.example.presensiapp.databinding.FragmentHomeBinding
import com.syahrido.mualim.example.presensiapp.util.showDialog
import com.syahrido.mualim.example.presensiapp.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding, EmployeeRepository>(),
    OnMapReadyCallback {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var map: GoogleMap
    private lateinit var locationCallback: LocationCallback
    private lateinit var geocoder: Geocoder
    private lateinit var supportMapFragment: SupportMapFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        initViews()
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        geocoder = Geocoder(requireActivity(), Locale.getDefault())

        locationRequest = LocationRequest()
        with(locationRequest) {
            this.interval = Constant.DEFAULT_UPDATE_INTERVAL
            this.fastestInterval = Constant.FAST_UPDATE_INTERVAL
            this.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        supportMapFragment =
            childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                updateMapUi(locationResult?.lastLocation, geocoder, binding.txtYourLocation)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        supportMapFragment.onViewCreated(view, savedInstanceState)
        getCurrentLocation()

        supportMapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (isLocationEnabled()) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { task ->
//                update UI
                updateMapUi(task, geocoder, binding.txtYourLocation)
            }
        } else {
            requireContext().showDialog()
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentHomeBinding {
        return binding
    }

    override fun getFragmentRepository(): EmployeeRepository {
        val token = runBlocking { employeePreferences.token.first() }
        val api = EmployeeDataStore().buildApi(EmployeeApi::class.java, token!!)

        return EmployeeRepository(api, employeePreferences)
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.isMyLocationEnabled = true
        getCurrentLocation()
    }
}