package com.example.examplemobileapp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_PERMISSIONS = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    
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
}
