package com.syahrido.mualim.example.presensiapp.home

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.syahrido.mualim.example.presensiapp.R
import com.syahrido.mualim.example.presensiapp.alarm.AlarmInReceiver
import com.syahrido.mualim.example.presensiapp.alarm.AlarmOutReceiver
import com.syahrido.mualim.example.presensiapp.auth.AuthActivity
import com.syahrido.mualim.example.presensiapp.base.BaseFragment
import com.syahrido.mualim.example.presensiapp.data.EmployeePreferences
import com.syahrido.mualim.example.presensiapp.data.network.EmployeeApi
import com.syahrido.mualim.example.presensiapp.data.network.EmployeeDataStore
import com.syahrido.mualim.example.presensiapp.data.repository.EmployeeRepository
import com.syahrido.mualim.example.presensiapp.databinding.FragmentSettingBinding
import com.syahrido.mualim.example.presensiapp.util.startNewActivity
import com.syahrido.mualim.example.presensiapp.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SettingFragment :
    BaseFragment<ProfileViewModel, FragmentSettingBinding, EmployeeRepository>() {

    private lateinit var timePicker: MaterialTimePicker
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var alarmInReceiver: AlarmInReceiver
    private lateinit var alarmOutReceiver: AlarmOutReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        employeePreferences = EmployeePreferences(requireContext())
        sharedPreferences =
            requireContext().getSharedPreferences("alarm", AppCompatActivity.MODE_PRIVATE)
        alarmInReceiver = AlarmInReceiver()
        alarmOutReceiver = AlarmOutReceiver()

        with(binding) {
            checkInAlarm.txtAlarm.text = getString(R.string.check_in_alarm)
            checkOutAlarm.txtAlarm.text = getString(R.string.check_out_alarm)
            checkInAlarm.txtTime.text = sharedPreferences.getString("time_in", "07:00")
            checkOutAlarm.txtTime.text = sharedPreferences.getString("time_out", "09:00")
        }

        switchBtn()

        binding.checkInAlarm.imageEdit.setOnClickListener {
            showTimePicker("time_in", binding.checkInAlarm.txtTime)
        }

        binding.checkOutAlarm.imageEdit.setOnClickListener {
            showTimePicker("time_out", binding.checkOutAlarm.txtTime)
        }

        binding.buttonLogout.setOnClickListener {
            logOut()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showTimePicker(key: String, textView: TextView) {
        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(6)
            .setMinute(0)
            .setTitleText(R.string.set_alarm_label)
            .build()
        timePicker.show(childFragmentManager, "presensi-app")

        timePicker.addOnPositiveButtonClickListener {
            val editor = sharedPreferences.edit()
            val selectedTime = String.format(
                "%02d", timePicker.hour
            ) + ":" + String.format(
                "%02d", timePicker.minute
            )
            editor.putString(key, selectedTime)
            editor.apply()
            textView.text = selectedTime
        }
    }

    private fun switchBtn() {
        val switchCheckIn = binding.checkInAlarm.switchBtn
        switchCheckIn.isChecked = sharedPreferences.getBoolean("chek-in", true)
        switchCheckIn.setOnClickListener {
            if (switchCheckIn.isChecked) {
                // When switch checked
                sharedPreferences.edit().putBoolean("chek-in", true).apply()
                val time = sharedPreferences.getString("time_in", "07:00")
                if (time != null) {
                    alarmInReceiver.setRepeatingAlarm(requireContext(),
                        getHour(time),
                        getMinute(time))
                }
                switchCheckIn.isChecked = true
            } else {
                // When switch unchecked
                sharedPreferences.edit().putBoolean("chek-in", false).apply()
                alarmInReceiver.setCancelAlarm(requireContext())
                switchCheckIn.isChecked = false
            }
        }

        val switchCheckOut = binding.checkOutAlarm.switchBtn
        switchCheckOut.isChecked = sharedPreferences.getBoolean("chek-out", true)
        switchCheckOut.setOnClickListener {
            if (switchCheckOut.isChecked) {
                // When switch checked
                sharedPreferences.edit().putBoolean("chek-out", true).apply()
                val time = sharedPreferences.getString("time_out", "07:00")
                if (time != null) {
                    alarmOutReceiver.setRepeatingAlarm(requireContext(),
                        getHour(time),
                        getMinute(time))
                }
                switchCheckOut.isChecked = true
            } else {
                // When switch unchecked
                sharedPreferences.edit().putBoolean("chek-out", false).apply()
                alarmOutReceiver.setCancelAlarm(requireContext())
                switchCheckOut.isChecked = false
            }
        }
    }

    private fun getHour(time: String): Int {
        var hourTime = ""
        for (i in 0..time.length - 4) {
            hourTime += time[i]
        }
        return hourTime.toInt()
    }

    private fun getMinute(time: String): Int {
        var minuteTime = ""
        for (i in 3 until time.length) {
            minuteTime += time[i]
        }

        return minuteTime.toInt()
    }

    private fun logOut() {
        viewModel.logOut()
        activity?.startNewActivity(AuthActivity::class.java)
    }

    override fun getViewModel(): Class<ProfileViewModel> {
        return ProfileViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(inflater, container, false)
    }

    override fun getFragmentRepository(): EmployeeRepository {
        val token = runBlocking { employeePreferences.token.first() }
        val api = EmployeeDataStore().buildApi(EmployeeApi::class.java, token!!)

        return EmployeeRepository(api, employeePreferences)
    }
}