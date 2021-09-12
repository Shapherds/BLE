package com.example.ble

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ble.databinding.DeviceItemBinding

class DeviceAdapter :
    ListAdapter<DeviceInfo, DeviceAdapter.ViewHolder>(Callback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val layoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val uiBinding = DeviceItemBinding.inflate(layoutInflater)
        return ViewHolder(uiBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.currentDevice = getItem(position)
        holder.bind()
    }

    class ViewHolder(private val uiBinding: DeviceItemBinding) :
        RecyclerView.ViewHolder(uiBinding.root) {

        lateinit var currentDevice: DeviceInfo

        fun bind() {
            uiBinding.nameTextView.text = currentDevice.name
            uiBinding.addressTextView.text = currentDevice.address
        }
    }

}