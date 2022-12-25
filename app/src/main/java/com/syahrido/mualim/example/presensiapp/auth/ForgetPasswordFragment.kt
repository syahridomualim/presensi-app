package com.syahrido.mualim.example.presensiapp.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.syahrido.mualim.example.presensiapp.R
import com.syahrido.mualim.example.presensiapp.databinding.FragmentForgetPasswordBinding

class ForgetPasswordFragment : Fragment() {

    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSendEmail.setOnClickListener {
            Toast.makeText(it.context, "Please check your email!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_forgetPasswordFragment_to_loginFragment)
        }
    }
}