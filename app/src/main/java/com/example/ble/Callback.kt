package com.example.ble

import android.bluetooth.BluetoothDevice
import androidx.recyclerview.widget.DiffUtil

object Callback : DiffUtil.ItemCallback<DeviceInfo>() {
    override fun areItemsTheSame(oldItem: DeviceInfo, newItem: DeviceInfo): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: DeviceInfo, newItem: DeviceInfo): Boolean {
        return oldItem.address == newItem.address
    }
}