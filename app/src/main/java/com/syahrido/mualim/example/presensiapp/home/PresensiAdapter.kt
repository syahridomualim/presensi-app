package com.syahrido.mualim.example.presensiapp.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.syahrido.mualim.example.presensiapp.databinding.ItemAttendanceBinding
import com.syahrido.mualim.example.presensiapp.model.UiPresensi
import java.text.SimpleDateFormat
import java.util.*

class PresensiAdapter : RecyclerView.Adapter<PresensiAdapter.PresensiViewHolder>() {

    private val presensi = mutableListOf<UiPresensi>()

    @SuppressLint("NotifyDataSetChanged")
    fun updatePresensi(presensi: List<UiPresensi>) {
        this.presensi.clear()
        this.presensi.addAll(presensi)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresensiViewHolder {
        return PresensiViewHolder(
            ItemAttendanceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PresensiViewHolder, position: Int) {
        val uiPresensi = presensi[position]
        holder.bind(uiPresensi)
    }

    override fun getItemCount(): Int {
        return presensi.size
    }

    inner class PresensiViewHolder(private val binding: ItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(uiPresensi: UiPresensi) {
            with(binding){
                txtDate.text = convertLongToTime("dd MMMM yyyy", uiPresensi.date)
                checkIn.text = convertLongToTime("HH:mm:ss a", uiPresensi.timeIn)
                checkOut.text =  if (uiPresensi.timeOut == 0L) {
                    " - "
                } else {
                    convertLongToTime("HH:mm:ss a", uiPresensi.timeOut)
                }
                txtLocationIn.text = uiPresensi.locationIn ?: "-"
                txtLocationOut.text = uiPresensi.locationOut ?: "-"
                txtDescription.text = uiPresensi.note
            }

        }

        private fun convertLongToTime(pattern: String, time: Long) : String? {
            return SimpleDateFormat(pattern, Locale.getDefault()).format(time)
        }
    }
}