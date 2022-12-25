package com.syahrido.mualim.example.presensiapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.syahrido.mualim.example.presensiapp.auth.AuthActivity
import com.syahrido.mualim.example.presensiapp.data.EmployeePreferences
import com.syahrido.mualim.example.presensiapp.databinding.ActivitySplashScreenBinding
import com.syahrido.mualim.example.presensiapp.home.HomeActivity
import com.syahrido.mualim.example.presensiapp.util.startNewActivity
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goAuthActivity()
        setFullscreen()
    }

    private fun goAuthActivity(): Job {
        return CoroutineScope(Dispatchers.Main).launch {
            delay(3000L)

            // create instance of EmployeePreferences to check token from datastore
            val employeePreferences = EmployeePreferences(this@SplashScreenActivity)
            employeePreferences.token.collect {
                val activity =
                    if (it == null) AuthActivity::class.java else HomeActivity::class.java
                startNewActivity(activity)
            }

            finish()
        }
    }

    private fun setFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    }

    override fun onBackPressed() {
    }
}