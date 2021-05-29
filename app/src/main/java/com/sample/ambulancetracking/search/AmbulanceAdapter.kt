package com.sample.ambulancetracking.journey

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.ambulancetracking.databinding.ActivityListUserRequestsBinding
import com.sample.ambulancetracking.databinding.AmbulanceListItemBinding
import com.sample.ambulancetracking.databinding.UserRequestItemBinding
import com.sample.ambulancetracking.retrofit.Ambulance
import com.sample.ambulancetracking.retrofit.GetNearbyAmbulancesResponse
import com.sample.ambulancetracking.retrofit.GetRequestsByUser

class AmbulanceAdapter(val ambulances:List<Ambulance>):RecyclerView.Adapter<AmbulanceAdapter.ViewHolder>() {
    inner class ViewHolder(val binding:AmbulanceListItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AmbulanceAdapter.ViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
        return ViewHolder(AmbulanceListItemBinding.inflate(adapterLayout,parent,false))

    }

    override fun onBindViewHolder(holder: AmbulanceAdapter.ViewHolder, position: Int) {
        val ambulance = ambulances[position]
        holder.binding.apply {
            hospitalName.text = ambulance.hospitalName
            hospitalMobile.text = ambulance.hospital
        }
        holder.binding.root.setOnClickListener {
            val basicDetailsIntent = Intent(it.context,RequestBasicDetails::class.java)
            basicDetailsIntent.putExtra("hospitalId",ambulance.hospital)
            basicDetailsIntent.putExtra("ambulanceId",ambulance._id)
            it.context.startActivity(basicDetailsIntent)
        }
    }



    override fun getItemCount(): Int = ambulances.size

}