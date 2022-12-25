package com.syahrido.mualim.example.presensiapp.home

import android.R.layout
import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.syahrido.mualim.example.presensiapp.data.network.EmployeeApi
import com.syahrido.mualim.example.presensiapp.data.network.EmployeeDataStore
import com.syahrido.mualim.example.presensiapp.data.repository.AttendanceRepository
import com.syahrido.mualim.example.presensiapp.databinding.FragmentCheckInBinding
import com.syahrido.mualim.example.presensiapp.util.isButtonEnabled
import com.syahrido.mualim.example.presensiapp.util.showDialog
import com.syahrido.mualim.example.presensiapp.viewmodel.AttendanceViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

class CheckInFragment :
    BaseFragment<AttendanceViewModel, FragmentCheckInBinding, AttendanceRepository>(),
    OnMapReadyCallback {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var map: GoogleMap
    private lateinit var locationCallback: LocationCallback
    private lateinit var geocoder: Geocoder
    private lateinit var supportMapFragment: SupportMapFragment
    private var description: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        initViews()
        binding = FragmentCheckInBinding.inflate(inflater, container, false)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        getCurrentLocation()
        initDescription()

        binding.btnCheckIn.isButtonEnabled(false)

        binding.descriptionValue.doAfterTextChanged {
            binding.btnCheckIn.isButtonEnabled(true)
        }

        binding.btnCheckIn.setOnClickListener {
            val idEmployee = runBlocking { employeePreferences.idEmployee.first() }

            if (description != null) {
                viewModel.checkIn(idEmployee!!, description!!, textLocation!!)
            }

            Toast.makeText(requireContext(), "You have been check in", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_checkInFragment_to_nav_attendance_record)
        }
    }

    override fun getViewModel(): Class<AttendanceViewModel> {
        return AttendanceViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentCheckInBinding {
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

    private fun initDescription() {
        val descriptionValue = binding.descriptionValue
        val listDescription = listOf(*resources.getStringArray(R.array.description))

        val arrayAdapter = ArrayAdapter(
            requireContext(),
            layout.simple_spinner_dropdown_item,
            listDescription
        )

        descriptionValue.setAdapter(arrayAdapter)
        descriptionValue.setOnItemClickListener { adapterView, view, i, l ->
            apply {
                description = adapterView.getItemAtPosition(i).toString()
            }
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