package com.example.ble

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private val bluetoothLeAdapter = BluetoothAdapter.getDefaultAdapter()
    /*
        private val bluetoothLeAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
     */
    private var scanning = false
    private val list = mutableListOf<DeviceInfo>()
    private val leDeviceListAdapter = DeviceAdapter()

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices()
                showLog("connected")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                showLog("disconnected")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                displayGattServices(gatt?.services)
            } else {
                showLog("onServicesDiscovered received: $status")
            }
        }
    }

    private fun displayGattServices(gattServices: List<BluetoothGattService>?) {
        showLog("display null ?")
        if (gattServices == null) return
        showLog("shows")

        // Loops through available GATT Services.
        gattServices.forEach { gattService ->
            val gattCharacteristics = gattService.characteristics
            showLog("service ${gattService.uuid}")

            // Loops through available Characteristics.
            gattCharacteristics.forEach { gattCharacteristic ->
                showLog("characteristics : ${gattCharacteristic.uuid}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<RecyclerView>(R.id.recyclerView).adapter = leDeviceListAdapter
    }

    override fun onStart() {
        super.onStart()
        showLog(bluetoothLeAdapter.isEnabled.toString())
        if (!bluetoothLeAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            checkGeoPermission()
        }
        scanLeDevice()
        checkGeoPermission()
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        if (requestCode == REQUEST_ENABLE_BT) {
            checkGeoPermission()
        }
        super.startActivityForResult(intent, requestCode)
    }

    private fun checkGeoPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                1
            )
        }
    }

    private fun scanLeDevice() {
//        if (!scanning) {
//            Handler(Looper.getMainLooper()).postDelayed({
//                scanning = false
//                bluetoothLeAdapter.bluetoothLeScanner.stopScan(leScanCallback)
//                showLog("scan stop")
//            }, SCAN_PERIOD)
            scanning = true
            bluetoothLeAdapter.bluetoothLeScanner.startScan(
                null,
                ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                    .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                    .setReportDelay(0L)
                    .build(), leScanCallback
            )
            showLog("scan started")
//        } else {
//            scanning = false
//            bluetoothLeAdapter.bluetoothLeScanner.stopScan(leScanCallback)
//        }
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            showLog("scan")
            if (result.device.name != null && !checkSame(result.device)) {
                val device = DeviceInfo(result.device.name, result.device.address)
                list.add(device)
                leDeviceListAdapter.submitList(list)
                leDeviceListAdapter.notifyDataSetChanged()
                showLog(
                    "device info : name= ${result.device.name}," +
                            " uui = ${result.device.uuids}," +
                            " type = ${result.device.type} " +
                            " address = ${result.device.address}"
                )
//                if (result.device.address == "53:D7:64:EB:D3:DE") {
//                    showLog("LOG!")
//                    connect(result.device.address)
     //           }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            showLog("fail")
            super.onScanFailed(errorCode)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            showLog("batch")
            super.onBatchScanResults(results)
        }
    }

    fun connect(address: String) {
        bluetoothLeAdapter.let { adapter ->
            try {
                val device = adapter.getRemoteDevice(address)
                device.connectGatt(this@MainActivity, false, bluetoothGattCallback)
            } catch (exception: IllegalArgumentException) {
                Log.w(TAG, "Device not found with provided address.")
            }
            // connect to the GATT server on the device
        }
    }

    private fun checkSame(BLEDevice: BluetoothDevice): Boolean {
        for (device in list) {
            if (device.name == BLEDevice.name && device.address == BLEDevice.address) {
                return true
            }
        }
        return false
    }

    private fun showLog(message: String) {
        Log.e("Logs", message)
    }

    companion object {

        private val SCAN_PERIOD: Long = 10000
        const val REQUEST_ENABLE_BT = 1
    }
}