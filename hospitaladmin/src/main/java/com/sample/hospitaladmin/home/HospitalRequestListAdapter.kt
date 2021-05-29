package com.sample.hospitaladmin.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.hospitaladmin.databinding.HospitalPendingRequestBinding
import com.sample.hospitaladmin.home.models.Request

class HospitalRequestListAdapter( private val requests:List<Request>): RecyclerView.Adapter<HospitalRequestListAdapter.HospitalRequestViewHolder>() {
    inner class HospitalRequestViewHolder(val binding:HospitalPendingRequestBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalRequestViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
        return HospitalRequestViewHolder(HospitalPendingRequestBinding.inflate(adapterLayout,parent,false))
    }

    override fun onBindViewHolder(holder: HospitalRequestViewHolder, position: Int) {
        val request = requests[position]
        holder.binding.apply {
            ageValuePendingRequests.text = request.age.toString()
            isAccidentValue.text = if (request.isAccident) "Yes" else "No"
            requestId.text = request._id
            requestId.visibility = View.GONE
        }
        holder.binding.root.setOnClickListener {
            val detailsIntent = Intent(it.rootView.context,RequestDetailsActivity::class.java)
            detailsIntent.putExtra("requestId",request._id)
            it.context.startActivity(detailsIntent)

        }
    }

    override fun getItemCount() = requests.size

}