package com.syahrido.mualim.example.presensiapp.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.syahrido.mualim.example.presensiapp.R
import com.syahrido.mualim.example.presensiapp.databinding.FragmentAttendenceBinding

class AttendanceFragment : Fragment() {

    private var _binding: FragmentAttendenceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentAttendenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnCheckIn.setOnClickListener {
            findNavController().navigate(R.id.action_nav_attendance_to_checkInFragment)
        }

        binding.btnCheckOut.setOnClickListener {
            findNavController().navigate(R.id.action_nav_attendance_to_checkOutFragment)
        }
    }
}