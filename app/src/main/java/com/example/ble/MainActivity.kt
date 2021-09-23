package com.example.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private val bluetoothLeAdapter = BluetoothAdapter.getDefaultAdapter()
    private var scanning = false
    private val list = mutableListOf<DeviceInfo>()
    private val leDeviceListAdapter = DeviceAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<RecyclerView>(R.id.recyclerView).adapter = leDeviceListAdapter
    }

    override fun onStart() {
        super.onStart()
        Log.e("Logs", bluetoothLeAdapter.isEnabled.toString())
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
        if (!scanning) {
            Handler(Looper.getMainLooper()).postDelayed({
                scanning = false
                bluetoothLeAdapter.bluetoothLeScanner.stopScan(leScanCallback)
                showLog("scan stop")
            }, SCAN_PERIOD)
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
        } else {
            scanning = false
            bluetoothLeAdapter.bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if (result.device.name != null && !checkSame(result.device)) {
                val device = DeviceInfo(result.device.name, result.device.address)
                list.add(device)
                leDeviceListAdapter.submitList(list)
                leDeviceListAdapter.notifyDataSetChanged()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    showLog(
                        "device info : name= ${result.device.name}," +
                                " uui = ${result.device.uuids}," +
                                " type = ${result.device.type} " +
                                " address = ${result.device.address}" +
                                " alias = ${result.device.alias}"
                    )
                }
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