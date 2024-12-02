package com.example.examplemobileapp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var bluetoothManager: BluetoothManager
    private val REQUEST_ENABLE_BLUETOOTH = 1001
    private val REQUEST_CODE_PERMISSIONS = 1001

    private lateinit var deviceAdapter: DeviceAdapter
    private val devices = mutableListOf<BluetoothDevice>()
    
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        bluetoothManager = BluetoothManager(this)

        // Set up the ListView and Adapter
        deviceAdapter = DeviceAdapter(this, devices)
        binding.deviceListView.adapter = deviceAdapter

        // Set up the button click listener
        binding.startScanButton.setOnClickListener {
            bluetoothManager.startScanning()

            // Stop scanning after 10 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                bluetoothManager.stopScanning()
            }, 10000)  // Stop scanning after 10 seconds
        }
        
        // Check if the device supports Bluetooth
        if (!bluetoothManager.isBluetoothSupported()) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show()
            finish()
        } else if (!bluetoothManager.isBluetoothEnabled()) {
            // Request to enable Bluetooth
            bluetoothManager.enableBluetooth(this, REQUEST_ENABLE_BLUETOOTH)
        } else {
            bluetoothManager.startAdvertising()  // Start advertising
            bluetoothManager.startScanning()  // Start scanning for other BLE devices
            // Initialize Bluetooth
            //bluetoothManager.initializeBluetooth()
        }
        
        // Check permissions at runtime
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasBluetoothPermissions()) {
                requestBluetoothPermissions()
            } else {
                initializeBluetooth()
            }
        } else {
            initializeBluetooth()
        }

        buttonSendSignal.setOnClickListener {
            val signal = "CHANGE_COLOR"  // or "SOUND_ALERT"
            bluetoothManager.sendSignal(signal)  // Send signal to server
        }

        // Initialize Bluetooth (you can call this in onCreate if needed)
        bluetoothManager.initializeBluetooth()
    }

    override fun onStart() {
        super.onStart()

        // Register scan callback
        bluetoothManager.setScanCallback(object : BluetoothManager.ScanCallback {
            override fun onDeviceFound(device: BluetoothDevice) {
                // Add the found device to the list
                devices.add(device)
                deviceAdapter.notifyDataSetChanged() // Update the list view
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode == Activity.RESULT_OK) {
            bluetoothManager.startAdvertising()  // Start advertising after Bluetooth is enabled
            bluetoothManager.startScanning()  // Start scanning after Bluetooth is enabled
        }
    }
    
    // Function to check if Bluetooth permissions are granted
    private fun hasBluetoothPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
               ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED &&
               ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    }
    
    // Function to request Bluetooth permissions
    private fun requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT
            ),
            REQUEST_CODE_PERMISSIONS
        )
    }
    
    // Callback for permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initializeBluetooth()
            } else {
                Toast.makeText(this, "Bluetooth permissions are required!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // Your Bluetooth initialization logic
    private fun initializeBluetooth() {
        // Start Bluetooth scanning or other functionality here
    }

    override fun onStop() {
        super.onStop()
        bluetoothManager.stopScanning() // Stop scanning when the activity stops
    }
}
