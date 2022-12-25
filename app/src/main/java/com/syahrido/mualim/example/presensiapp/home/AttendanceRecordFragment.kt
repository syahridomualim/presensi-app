package com.syahrido.mualim.example.presensiapp.home

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.syahrido.mualim.example.presensiapp.base.BaseFragment
import com.syahrido.mualim.example.presensiapp.data.EmployeePreferences
import com.syahrido.mualim.example.presensiapp.data.database.PresensiDatabase
import com.syahrido.mualim.example.presensiapp.data.network.EmployeeApi
import com.syahrido.mualim.example.presensiapp.data.network.EmployeeDataStore
import com.syahrido.mualim.example.presensiapp.data.network.Resource
import com.syahrido.mualim.example.presensiapp.data.repository.PresensiRepository
import com.syahrido.mualim.example.presensiapp.databinding.FragmentAttendanceRecordBinding
import com.syahrido.mualim.example.presensiapp.mapper.PresensiMapper
import com.syahrido.mualim.example.presensiapp.model.UiPresensi
import com.syahrido.mualim.example.presensiapp.util.handleApiError
import com.syahrido.mualim.example.presensiapp.util.visibility
import com.syahrido.mualim.example.presensiapp.viewmodel.RecordViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AttendanceRecordFragment :
    BaseFragment<RecordViewModel, FragmentAttendanceRecordBinding, PresensiRepository>() {

    private lateinit var presensiAdapter: PresensiAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        initViews()
        binding = FragmentAttendanceRecordBinding.inflate(inflater, container, false)

        employeePreferences = EmployeePreferences(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presensiAdapter = PresensiAdapter()
        binding.recyclerView.adapter = presensiAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getPresensi().observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressbar.visibility(true)
                }
                is Resource.Failure -> {
                    binding.progressbar.visibility(false)
                    handleApiError(resource)
                }
                is Resource.Success -> {
                    binding.progressbar.visibility(false)
                    updateUI(resource.value.sortedByDescending { it.date })
                }
            }
        }
    }

    override fun getViewModel(): Class<RecordViewModel> {
        return RecordViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentAttendanceRecordBinding {
        return binding
    }

    override fun getFragmentRepository(): PresensiRepository {
        val token = runBlocking { employeePreferences.token.first() }
        val api = EmployeeDataStore().buildApi(EmployeeApi::class.java, token!!)
        val presensiDao = Room.databaseBuilder(requireContext(),
            PresensiDatabase::class.java, "presensi-db").fallbackToDestructiveMigration().build()

        return PresensiRepository(api,
            presensiDao.presensiDao(),
            PresensiMapper(),
            employeePreferences)
    }

    private fun updateUI(list: List<UiPresensi>) {
        presensiAdapter.updatePresensi(list)
    }
}