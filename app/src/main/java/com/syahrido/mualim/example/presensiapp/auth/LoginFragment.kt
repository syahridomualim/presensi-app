package com.syahrido.mualim.example.presensiapp.auth

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.syahrido.mualim.example.presensiapp.R
import com.syahrido.mualim.example.presensiapp.base.BaseFragment
import com.syahrido.mualim.example.presensiapp.data.network.AuthApi
import com.syahrido.mualim.example.presensiapp.data.network.AuthDataStore
import com.syahrido.mualim.example.presensiapp.data.network.Resource
import com.syahrido.mualim.example.presensiapp.data.repository.AuthRepository
import com.syahrido.mualim.example.presensiapp.databinding.FragmentLoginBinding
import com.syahrido.mualim.example.presensiapp.model.request.LoginRequest
import com.syahrido.mualim.example.presensiapp.util.handleApiError
import com.syahrido.mualim.example.presensiapp.util.visibility
import com.syahrido.mualim.example.presensiapp.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions

class LoginFragment : BaseFragment<AuthViewModel, FragmentLoginBinding, AuthRepository>(),
    EasyPermissions.PermissionCallbacks {

    private val authDataStore = AuthDataStore()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = getFragmentBinding(inflater, container)
        initViews()
        return binding.root
    }

    override fun getViewModel(): Class<AuthViewModel> {
        return AuthViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(layoutInflater, container, false)
    }

    override fun getFragmentRepository(): AuthRepository {
        return AuthRepository(authDataStore.buildApi(AuthApi::class.java), employeePreferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissions()
        goForgetPasswordPage()

        binding.btnLogin.setOnClickListener {
            val email = binding.edtTextEmail.text.toString().trim()
            val password = binding.edtTextPassword.text.toString().trim()

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                binding.textInputEmail.error = "Please input your email"
                binding.textInputPassword.error = "Please input your password"
            } else if (TextUtils.isEmpty(password)) {
                binding.textInputPassword.error = "Please input your password"
            } else if (TextUtils.isEmpty(email)) {
                binding.textInputPassword.error = "Please input your password"
            } else {
                binding.textInputEmail.error = null
                binding.textInputPassword.error = null
                validate(email, password)
            }
            hideKeyboard()
        }

        goHomePage()
    }

    private fun hideKeyboard() {
        val currentFocus = Activity().currentFocus
        if (currentFocus != null) {
            val imm =
                Activity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    private fun validate(email: String, password: String) {
        viewModel.login(LoginRequest(email, password))
        binding.progressBar.visibility(true)
    }

    private fun goHomePage() {
        binding.progressBar.visibility(false)

        viewModel.loginResponse.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    lifecycleScope.launch {
                        viewModel.saveToken(it.value.token!!)
                        viewModel.saveIdEmployee(it.value.idEmployee!!)
                    }
                }
                is Resource.Failure -> {
                    handleApiError(it)
                    binding.progressBar.visibility(false)
                }
                else -> {
                    binding.progressBar.visibility(true)
                }
            }
        }
    }

    private fun goForgetPasswordPage() {
        val textForgetPassword = binding.textForgetPassword
        val spannedString = SpannableString(getString(R.string.forget_password))

        val clickLister = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
            }
        }

        spannedString.setSpan(
            clickLister,
            spannedString.length - 11,
            spannedString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textForgetPassword.text = spannedString
        textForgetPassword.movementMethod = LinkMovementMethod.getInstance()
    }

}