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

    private var mediaPlayer: MediaPlayer? = null
    
    private lateinit var binding: ActivityMainBinding

    private var selectedDevice: BluetoothDevice? = null // To store the selected device
    
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bluetoothManager = BluetoothManager(this)

        // Set up the ListView and Adapter
        deviceAdapter = DeviceAdapter(this, devices) { device ->
            // This is the callback when a device is selected
            selectedDevice = device
            connectToDevice(device) // Call the function to connect to the selected device
        }
        binding.deviceListView.adapter = deviceAdapter

        // Request Bluetooth permissions if not already granted
        if (!hasBluetoothPermissions()) {
            requestBluetoothPermissions()  // Request permissions
        } else {
            initializeBluetooth()  // Proceed if permissions are already granted
        }

       binding.stopButton.setOnClickListener {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            binding.stopButton.visibility = View.GONE
        }
       
        // Set up the button click listener
        binding.startScanButton.setOnClickListener {
            bluetoothManager.startScanning()

            // Stop scanning after 10 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                bluetoothManager.stopScanning()
            }, 10000)  // Stop scanning after 10 seconds
        }

         // Check if Bluetooth is supported and enabled
        if (!bluetoothManager.isBluetoothSupported()) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show()
            finish()
        } else if (!bluetoothManager.isBluetoothEnabled()) {
            // Request to enable Bluetooth
            bluetoothManager.enableBluetooth(this, REQUEST_ENABLE_BLUETOOTH)
        } else {
            bluetoothManager.startAdvertising()  // Start advertising
            bluetoothManager.startScanning()  // Start scanning for other BLE devices
        }

        // Initialize Bluetooth (you can call this in onCreate if needed)
        bluetoothManager.initializeBluetooth()

        binding.buttonSendSignal.setOnClickListener {
            val signal = "CHANGE_COLOR"  // or "SOUND_ALERT"

            // bluetoothManager.sendSignal(signal)  // Send the signal to the server (or other devices)
            // Toast.makeText(this, "Signal sent: $signal", Toast.LENGTH_SHORT).show()
            
            selectedDevice?.let { device ->
                bluetoothManager.sendSignal(signal)
            }
        }

        
        // Bluetooth initialization and permission check code (same as before)
        checkPermissions()

        // Handle sending signal when button clicked
        binding.buttonSendSignal.setOnClickListener {
            val signal = "CHANGE_COLOR"  // or "SOUND_ALERT"
            bluetoothManager.sendSignal(signal)  // Send signal to server
        }
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
    
    // React to the received signal (either change color or play sound)
    private fun handleReceivedSignal(signal: String) {
        when (signal) {
            "CHANGE_COLOR" -> {
                // Change the background color to green (or any color of your choice)
                binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            }
            "SOUND_ALERT" -> {
                // Play the siren sound
                playSirenSound()
            }
        }
    }
    // Play the siren sound when the signal is received
    private fun playSirenSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.siren) // Assuming you have the siren.mp3 in the "res/raw" folder
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release()  // Release the media player once the sound is finished
        }
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_SCAN)) {
            // Show a dialog explaining why the app needs Bluetooth permissions
            AlertDialog.Builder(this)
                .setTitle("Bluetooth Permission Required")
                .setMessage("This app requires Bluetooth permissions to scan and connect to devices.")
                .setPositiveButton("OK") { _, _ ->
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
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            // Request the permissions
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
                // If permissions are granted, initialize Bluetooth
                initializeBluetooth()
            } else {
                Toast.makeText(this, "Bluetooth permissions are required!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasBluetoothPermissions()) {
                requestBluetoothPermissions()
            } else {
                initializeBluetooth()
            }
        } else {
            initializeBluetooth()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE_PERMISSION)
        }
    }
    
    // Your Bluetooth initialization logic
    private fun initializeBluetooth() {
        // Start Bluetooth scanning or other functionality here
    }

    private fun initializeBluetooth() {
        if (!bluetoothManager.isBluetoothSupported()) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show()
            finish()
        } else if (!bluetoothManager.isBluetoothEnabled()) {
            bluetoothManager.enableBluetooth(this, REQUEST_ENABLE_BLUETOOTH)
        } else {
            bluetoothManager.startAdvertising()  // Start advertising
            bluetoothManager.startScanning()  // Start scanning for other BLE devices
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        // Assuming you're using Bluetooth LE with GATT for connections:
        bluetoothManager.connectToDevice(device) { success ->
            if (success) {
                Toast.makeText(this, "Connected to ${device.name}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to connect to ${device.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        bluetoothManager.stopScanning() // Stop scanning when the activity stops
    }
}
