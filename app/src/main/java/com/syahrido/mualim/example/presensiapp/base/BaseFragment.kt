package com.syahrido.mualim.example.presensiapp.base

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.syahrido.mualim.example.presensiapp.data.EmployeePreferences
import com.syahrido.mualim.example.presensiapp.util.AuthUtility
import com.syahrido.mualim.example.presensiapp.viewmodel.ViewModelFactory
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

abstract class BaseFragment<VM : ViewModel, B : ViewBinding, R : BaseRepository> : Fragment(),
    EasyPermissions.PermissionCallbacks {

    protected lateinit var employeePreferences: EmployeePreferences
    protected lateinit var binding: B
    protected lateinit var viewModel: VM
    protected var textLocation: String? = "Jakarta"

    abstract fun getViewModel(): Class<VM>

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    abstract fun getFragmentRepository(): R

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    protected fun requestPermissions() {
        if (AuthUtility.hasLocationPermission(requireContext())) {
            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permission to use this app",
                0,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permission to use this app",
                0,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    protected fun initViews() {
        employeePreferences = EmployeePreferences(requireContext())
        val factory = ViewModelFactory(getFragmentRepository())
        viewModel = ViewModelProvider(this, factory)[getViewModel()]
    }

    protected fun updateMapUi(location: Location?, geocoder: Geocoder, textView: TextView) {
        try {
            val latitude = location?.latitude
            val longitude = location?.longitude

            val address = geocoder.getFromLocation(latitude!!, longitude!!, 1)
            if (address != null && address.size > 0) {
                val locality = address[0]
                val stringBuilder = StringBuilder()
                for (i in 0..locality.maxAddressLineIndex) {
                    stringBuilder.append(locality.getAddressLine(i)).append(" ")
                }

                textLocation = stringBuilder.toString()
            }
            textView.text = textLocation
        } catch (e: Exception) {
            textView.text = textLocation
        }
    }

    protected fun isLocationEnabled(): Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}