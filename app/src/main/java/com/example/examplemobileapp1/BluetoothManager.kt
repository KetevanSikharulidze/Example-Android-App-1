package com.example.bluetoothapp

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BluetoothManager(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter
    }

    private val bluetoothLeScanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }
    
    private val bluetoothLeAdvertiser: BluetoothLeAdvertiser? by lazy {
        bluetoothAdapter?.bluetoothLeAdvertiser
    }

    fun isBluetoothSupported(): Boolean {
        return bluetoothAdapter != null
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun enableBluetooth(activity: Activity, requestCode: Int) {
        if (!isBluetoothEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, requestCode)
        }
    }

    fun startAdvertising() {
        if (bluetoothLeAdvertiser != null) {
            val advertiseData = AdvertiseData.Builder()
                .addServiceUuid(ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB")) // Example UUID
                .setIncludeDeviceName(true)
                .build()

            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false)
                .build()

            bluetoothLeAdvertiser?.startAdvertising(settings, advertiseData, object : AdvertiseCallback() {
                override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                    super.onStartSuccess(settingsInEffect)
                    Toast.makeText(context, "Advertising started!", Toast.LENGTH_SHORT).show()
                }

                override fun onStartFailure(errorCode: Int) {
                    super.onStartFailure(errorCode)
                    Toast.makeText(context, "Advertising failed with error code: $errorCode", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "BLE Advertising not supported.", Toast.LENGTH_SHORT).show()
        }
    }    

     fun startScanning() {
        if (bluetoothLeScanner != null) {
            val scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    super.onScanResult(callbackType, result)
                    result?.device?.let { device ->
                        // Handle found device, for example, display device name
                        Toast.makeText(context, "Device found: ${device.name}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    super.onScanFailed(errorCode)
                    Toast.makeText(context, "Scan failed with error: $errorCode", Toast.LENGTH_SHORT).show()
                }
            }
            bluetoothLeScanner?.startScan(scanCallback)
            Toast.makeText(context, "Scanning for devices...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "BLE Scanning not supported.", Toast.LENGTH_SHORT).show()
        }
    }

    // Stop scanning for BLE devices
    fun stopScanning() {
        bluetoothLeScanner?.stopScan(object : ScanCallback() {})
        Toast.makeText(context, "Scanning stopped", Toast.LENGTH_SHORT).show()
    }
    
    fun initializeBluetooth() {
        // Placeholder for any additional Bluetooth initialization logic
        Toast.makeText(context, "Bluetooth is initialized!", Toast.LENGTH_SHORT).show()
    }
}
