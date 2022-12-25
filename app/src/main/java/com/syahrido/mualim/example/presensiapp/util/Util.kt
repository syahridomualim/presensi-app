package com.syahrido.mualim.example.presensiapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.syahrido.mualim.example.presensiapp.auth.LoginFragment
import com.syahrido.mualim.example.presensiapp.data.network.Resource
import com.syahrido.mualim.example.presensiapp.home.AttendanceRecordFragment
import com.syahrido.mualim.example.presensiapp.home.CheckInFragment
import com.syahrido.mualim.example.presensiapp.home.CheckOutFragment
import com.syahrido.mualim.example.presensiapp.home.ProfileFragment

fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun View.visibility(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.isButtonEnabled(enable: Boolean) {
    isEnabled = enable
    alpha = if (enable) 1f else 0.95f
}

fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
    action?.let {
        snackbar.setAction("") {
            it()
        }
    }
    snackbar.show()
}

fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: (() -> Unit)? = null,
) {
    when {
        failure.isNetworkError -> {
            requireView().snackbar("Please check your internet connection")
        }
        failure.errorCode == 400 -> {
            if (this is LoginFragment) {
                requireView().snackbar("You've entered incorrect email or password")
            }
        }
        failure.errorCode == 403 -> {
            if (this is ProfileFragment || this is CheckInFragment || this is CheckOutFragment || this is AttendanceRecordFragment) {
                requireView().snackbar("Your session is expired")
            }
        }
    }
}

fun Context.showDialog() {
    AlertDialog.Builder(this)
        .setTitle("Please turn on your location to access this app")
        .setPositiveButton("OK") { _, _ ->
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }.setNegativeButton("Cancel") { _, _ ->
            Toast.makeText(
                this,
                "Turn on your location to access this app",
                Toast.LENGTH_SHORT
            ).show()
        }.show()
}