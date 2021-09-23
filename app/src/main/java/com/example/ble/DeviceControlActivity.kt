package com.example.ble

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DeviceControlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_control)
//
//        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
//        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
//
//    private var bluetoothService : BluetoothLeService? = null
//
//    // Code to manage Service lifecycle.
//    private val serviceConnection: ServiceConnection = object : ServiceConnection {
//        override fun onServiceConnected(
//            componentName: ComponentName,
//            service: IBinder
//        ) {
//            bluetoothService = (service as BluetoothLeService.LocalBinder).getService()
//            bluetoothService?.let { bluetooth ->
//                if (!bluetooth.initialize()) {
//                    Log.e(TAG, "Unable to initialize Bluetooth")
//                    finish()
//                }
//                // perform device connection
//            }
//        }
//
//        override fun onServiceDisconnected(componentName: ComponentName) {
//            bluetoothService = null
//        }
//    }
}