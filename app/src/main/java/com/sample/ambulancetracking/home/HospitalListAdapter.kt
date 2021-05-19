package com.sample.ambulancetracking.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.ambulancetracking.databinding.LayoutHospitalItemBinding

class HospitalListAdapter : RecyclerView.Adapter<HospitalListAdapter.HospitalViewHolder>() {

    @ExperimentalStdlibApi
    private val hospitals = (1..20).map { Hospital("Title $it", 50+it) }

    inner class HospitalViewHolder(val binding: LayoutHospitalItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
        return HospitalViewHolder(LayoutHospitalItemBinding.inflate(adapterLayout, parent, false))
    }

    @SuppressLint("SetTextI18n")
    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        val hospital = hospitals[position]
        holder.binding.apply {
            textView2.text = hospital.name
            textView3.text = "Distance: ${hospital.distance}"
        }
    }

    @ExperimentalStdlibApi
    override fun getItemCount() = hospitals.size
}