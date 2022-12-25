package com.syahrido.mualim.example.presensiapp.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.syahrido.mualim.example.presensiapp.R
import com.syahrido.mualim.example.presensiapp.auth.AuthActivity
import com.syahrido.mualim.example.presensiapp.base.BaseFragment
import com.syahrido.mualim.example.presensiapp.data.network.EmployeeApi
import com.syahrido.mualim.example.presensiapp.data.network.EmployeeDataStore
import com.syahrido.mualim.example.presensiapp.data.network.Resource
import com.syahrido.mualim.example.presensiapp.data.repository.EmployeeRepository
import com.syahrido.mualim.example.presensiapp.databinding.FragmentProfileBinding
import com.syahrido.mualim.example.presensiapp.model.response.EmployeeResponse
import com.syahrido.mualim.example.presensiapp.util.handleApiError
import com.syahrido.mualim.example.presensiapp.util.startNewActivity
import com.syahrido.mualim.example.presensiapp.util.visibility
import com.syahrido.mualim.example.presensiapp.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ProfileFragment :
    BaseFragment<ProfileViewModel, FragmentProfileBinding, EmployeeRepository>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = getFragmentBinding(inflater, container)
        initViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idEmployee = runBlocking { employeePreferences.idEmployee.first() }

        if (idEmployee != null) {
            viewModel.getEmployee(idEmployee)
        }

        showProfile()

        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_setting, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.nav_setting -> {
//                        Toast.makeText(requireContext(), "Hello", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_nav_profile_to_settingFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun showProfile() {
        binding.progressbar.visibility(false)

        viewModel.employee.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    isBindingVisible(true)
                    binding.progressbar.visibility(false)
                    Log.d("HomeFragment", it.value.toString())
                    updateUI(it.value)
                }
                is Resource.Loading -> {
                    isBindingVisible(false)
                    binding.progressbar.visibility(true)
                }
                is Resource.Failure -> {
                    isBindingVisible(false)
                    binding.progressbar.visibility(false)
                    handleApiError(it)
                    logOut()
                }
            }
        }
    }

    private fun isBindingVisible(state: Boolean) {
        with(binding) {
            imgProfile.visibility(state)
            imgIdEmployee.visibility(state)
            imgDepartment.visibility(state)
            imgArea.visibility(state)
            imgEmail.visibility(state)
        }
    }

    private fun updateUI(it: EmployeeResponse) {
        with(binding) {
            textName.text = it.name
            textIdEmployee.text = it.idEmployee.toString()
            textDepartment.text = it.department?.name
            textArea.text = it.area?.name
            textEmail.text = it.email
            Glide.with(this@ProfileFragment)
                .load(it.profileImageUrl ?: R.drawable.profile)
                .into(imgProfile)
        }
    }

    override fun getViewModel(): Class<ProfileViewModel> {
        return ProfileViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun getFragmentRepository(): EmployeeRepository {
        val token = runBlocking { employeePreferences.token.first() }
        val api = EmployeeDataStore().buildApi(EmployeeApi::class.java, token!!)

        return EmployeeRepository(api, employeePreferences)
    }

    private fun logOut() {
        Toast.makeText(requireContext(),
            "Your session has been expired, please login again",
            Toast.LENGTH_SHORT).show()
        viewModel.logOut()
        activity?.startNewActivity(AuthActivity::class.java)
    }
}