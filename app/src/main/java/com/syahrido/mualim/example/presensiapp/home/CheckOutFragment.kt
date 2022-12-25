package com.syahrido.mualim.example.presensiapp.home

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.syahrido.mualim.example.presensiapp.R
import com.syahrido.mualim.example.presensiapp.base.BaseFragment
import com.syahrido.mualim.example.presensiapp.constants.Constant
import com.syahrido.mualim.example.presensiapp.data.EmployeePreferences
import com.syahrido.mualim.example.presensiapp.data.repository.AttendanceRepository
import com.syahrido.mualim.example.presensiapp.data.network.EmployeeApi
import com.syahrido.mualim.example.presensiapp.data.network.EmployeeDataStore
import com.syahrido.mualim.example.presensiapp.databinding.FragmentCheckOutBinding
import com.syahrido.mualim.example.presensiapp.util.isButtonEnabled
import com.syahrido.mualim.example.presensiapp.util.showDialog
import com.syahrido.mualim.example.presensiapp.viewmodel.AttendanceViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

class CheckOutFragment :
    BaseFragment<AttendanceViewModel, FragmentCheckOutBinding, AttendanceRepository>(),
    OnMapReadyCallback {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var map: GoogleMap
    private lateinit var locationCallback: LocationCallback
    private lateinit var geocoder: Geocoder
    private lateinit var supportMapFragment: SupportMapFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        initViews()
        binding = FragmentCheckOutBinding.inflate(inflater, container, false)

        employeePreferences = EmployeePreferences(requireContext())
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
        supportMapFragment.getMapAsync(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                updateMapUi(locationResult?.lastLocation, geocoder, binding.txtYourLocation)
            }
        }

        return binding.root
    }

    override fun getViewModel(): Class<AttendanceViewModel> {
        return AttendanceViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentCheckOutBinding {
        return binding
    }

    override fun getFragmentRepository(): AttendanceRepository {
        val token = runBlocking { employeePreferences.token.first() }
        val api = EmployeeDataStore().buildApi(EmployeeApi::class.java, token!!)

        return AttendanceRepository(api)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.isMyLocationEnabled = true
        getCurrentLocation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnCheckOut.isButtonEnabled(false)
        binding.txtYourLocation.doAfterTextChanged {
            binding.btnCheckOut.isButtonEnabled(true)
        }

        binding.btnCheckOut.setOnClickListener {
            val idEmployee = runBlocking { employeePreferences.idEmployee.first() }
            viewModel.checkOut(idEmployee!!, textLocation!!)
            Toast.makeText(requireContext(), "You have been check out", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_checkOutFragment_to_nav_attendance_record)
        }
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
}