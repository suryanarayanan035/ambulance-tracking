package com.sample.ambulancetracking.journey

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.ambulancetracking.databinding.ActivityListUserRequestsBinding
import com.sample.ambulancetracking.databinding.UserRequestItemBinding
import com.sample.ambulancetracking.retrofit.GetRequestsByUser
import com.sample.ambulancetracking.tracking.LocationActivity
import com.sample.ambulancetracking.tracking.UserLocationTracking

class UserRequestAdapter(val requests:List<GetRequestsByUser>):RecyclerView.Adapter<UserRequestAdapter.ViewHolder>() {
    inner class ViewHolder(val binding:UserRequestItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserRequestAdapter.ViewHolder {
       val adapterLayout = LayoutInflater.from(parent.context)
        return ViewHolder(UserRequestItemBinding.inflate(adapterLayout,parent,false))

    }

    override fun onBindViewHolder(holder: UserRequestAdapter.ViewHolder, position: Int) {
       val request = requests[position]
        holder.binding.apply {
            nameValue.text = request.name
            requestStatus.text = request.requestStatus
            journeyStatus.text = request.journeyStatus
            requestId.text=request._id
        }
        holder.binding.root.setOnClickListener {
            val trackingIntent = Intent(it.context,LocationActivity::class.java)
            trackingIntent.putExtra("requestId",request._id)
            it.context.startActivity(trackingIntent)

        }
    }



    override fun getItemCount(): Int = requests.size
}