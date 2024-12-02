package com.example.bluetoothapp
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothManager(private val context: Context) {
    
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter
    }

    private val bluetoothLeScanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }

    private var bluetoothGatt: BluetoothGatt? = null
    
    // connect to a BLE device using GATT
    val gatt = device.connectGatt(context, false, gattCallback)

    // Define your GATT callback to handle service discovery and notifications
    val gattCallback = object : BluetoothGattCallback() {
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Discover characteristics and enable notifications
                val service = gatt?.getService(UUID.fromString(SERVICE_UUID))
                val characteristic = service?.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID))
                gatt?.setCharacteristicNotification(characteristic, true)
            }
        }

         override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
            if (characteristic?.uuid == UUID.fromString(CHARACTERISTIC_UUID)) {
                val signal = characteristic.getStringValue(0)  // Assuming the signal is a string
                onSignalReceived(signal)  // Forward the signal to MainActivity
            }
        }
    }
    
    // Add a function to connect to the device
    fun connectToDevice(device: BluetoothDevice, callback: (Boolean) -> Unit) {
        bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    enableNotifications(gatt)
                    callback(true)
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    callback(false)
                }
            }

            // Handle receiving notifications (signals)
            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                super.onCharacteristicChanged(gatt, characteristic)
                val signal = characteristic.getStringValue(0)  // Assuming the signal is a string
                handleReceivedSignal(signal)
            }

            // Other GATT callbacks...
        })
    }

    // Send signal (e.g., "CHANGE_COLOR", "SOUND_ALERT") to the connected device
    fun sendSignal(signal: String) {
        bluetoothGatt?.let { gatt ->
            // Here, we would send the signal to the GATT server
            // You can write the signal to a characteristic, send a notification, etc.

            val characteristic = BluetoothGattCharacteristic(
                UUID.fromString("your-characteristic-uuid"),
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE
            )
            characteristic.setValue(signal)
            
            // Write signal to the device
            gatt.writeCharacteristic(characteristic)
        }
    }

    // Enable notifications for a characteristic to listen for signals
    private fun enableNotifications(gatt: BluetoothGatt) {
        val service = gatt.getService(UUID.fromString("your-service-uuid"))
        val characteristic = service.getCharacteristic(UUID.fromString("your-characteristic-uuid"))

        gatt.setCharacteristicNotification(characteristic, true)

        // If the device supports notifications, we enable them
        val descriptor = characteristic.getDescriptor(UUID.fromString("your-descriptor-uuid"))
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        gatt.writeDescriptor(descriptor)
    }

    // Handle the signal that was received from the device
    private fun handleReceivedSignal(signal: String) {
        when (signal) {
            "CHANGE_COLOR" -> {
                // Change background color to green when "CHANGE_COLOR" signal is received
                changeBackgroundColor()
            }
            "SOUND_ALERT" -> {
                // Trigger sound alert when the signal is "SOUND_ALERT"
                playSoundAlert()
            }
            else -> {
                 // Handle any unexpected or unknown signals
                Toast.makeText(context, "Unknown signal: $signal", Toast.LENGTH_SHORT).show()
            }
        }
    }
        // Send signal as shown earlier...

    private fun changeBackgroundColor() {
        // Change the background color of the activity to green
        val layout = findViewById<LinearLayout>(R.id.layout_main)
        layout.setBackgroundColor(Color.GREEN)
    }

    private fun playSoundAlert() {
        // Play a siren sound or a notification sound
        mediaPlayer = MediaPlayer.create(this, R.raw.siren)
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            it.release()
            mediaPlayer = null
            binding.stopButton.visibility = View.GONE
        }
        binding.stopButton.visibility = View.VISIBLE
    }
    
    private var bluetoothServerSocket: BluetoothServerSocket? = null
    private var bluetoothSocket: BluetoothSocket? = null

    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")  // Standard Bluetooth UUID

    // Check if Bluetooth is supported and enabled
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

    // Start listening for Bluetooth connections (Server side)
    fun startListening() {
        val adapter = BluetoothAdapter.getDefaultAdapter()

        if (adapter == null || !adapter.isEnabled) {
            Toast.makeText(context, "Bluetooth is not enabled.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create BluetoothServerSocket to listen for incoming connections
        bluetoothServerSocket = adapter.listenUsingRfcommWithServiceRecord("BluetoothApp", uuid)

        // Start a new thread to handle incoming connections
        Thread {
            try {
                while (true) {
                    val socket = bluetoothServerSocket?.accept()  // Accept incoming connection (blocking call)
                    socket?.let {
                        // Handle the connection: get input/output streams
                        val inputStream = it.inputStream
                        val outputStream = it.outputStream
                        handleConnection(inputStream, outputStream)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

   private fun handleConnection(inputStream: InputStream, outputStream: OutputStream) {
        val data = ByteArray(1024)
        var bytesRead: Int
    
        // Continuously listen for incoming data (signals)
        while (true) {
            try {
                bytesRead = inputStream.read(data)
                if (bytesRead != -1) {
                    val signal = String(data, 0, bytesRead).trim()
    
                    // Handle the received signal
                    when (signal) {
                        "CHANGE_COLOR" -> changeColor()  // Change screen color to green
                        "SOUND_ALERT" -> playSound()     // Play sound (e.g., siren)
                        else -> Log.d("Bluetooth", "Unknown signal: $signal")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                break
            }
        }
    }

   // Method to change the screen color to green
    private fun changeColor() {
        // Update the UI to change the background color to green
        Handler(Looper.getMainLooper()).post {
            val activity = context as Activity
            activity.window.decorView.setBackgroundColor(android.graphics.Color.GREEN)
        }
    }
    
    // Method to play a siren sound (make sure you have a siren sound file in resources)
    private fun playSound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.siren)  // Example sound file
        mediaPlayer.start()
    }
    // Stop listening for incoming connections
    fun stopListening() {
        bluetoothServerSocket?.close()
    }

    // Send signal to the server (e.g., send "CHANGE_COLOR" or "SOUND_ALERT")
    fun sendSignal(signal: String) {
        val outputStream = bluetoothSocket?.outputStream
        try {
            outputStream?.write(signal.toByteArray())
            Log.d("Bluetooth", "Signal sent: $signal")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    
    // Connect to a Bluetooth server (Client side)
    fun connectToServer(serverAddress: String) {
        val device = bluetoothAdapter?.getRemoteDevice(serverAddress)

        // Attempt to connect to the server's Bluetooth socket
        Thread {
            try {
                bluetoothSocket = device?.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket?.connect()

                // Get the input/output streams
                val inputStream = bluetoothSocket?.inputStream
                val outputStream = bluetoothSocket?.outputStream

                // Send a test message to the server
                val message = "Hello from Client!"
                outputStream?.write(message.toByteArray())

                // Handle receiving data
                inputStream?.let { handleConnection(it, outputStream!!) }

            } catch (e: IOException) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Connection failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    // Disconnect from the server (Client side)
    fun disconnect() {
        bluetoothSocket?.close()
        Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
    }

    // Initialize Bluetooth (ensure it’s ready to use)
    fun initializeBluetooth() {
        if (bluetoothAdapter?.isEnabled == true) {
            Toast.makeText(context, "Bluetooth is initialized!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show()
        }
    }

    // This method should be called when a signal is received from another device.
    fun onSignalReceived(signal: String) {

        // Forward the signal to MainActivity to handle UI updates
        if (signal == "CHANGE_COLOR") {
            (context as? MainActivity)?.handleReceivedSignal("CHANGE_COLOR")
        } else if (signal == "SOUND_ALERT") {
            (context as? MainActivity)?.handleReceivedSignal("SOUND_ALERT")
        }
        
        // Call handleReceivedSignal from MainActivity
        // (context as MainActivity).handleReceivedSignal(signal)
    } // a method to simulate signal reception:

    // When a device is found during scanning
    // override fun onScanResult(callbackType: Int, result: ScanResult?) {
    //     super.onScanResult(callbackType, result)
    //     result?.device?.let { device ->
    //         // Here we simulate a signal when a device is found. 
    //         // This would ideally come from the device you're scanning for.
    //         (context as MainActivity).handleReceivedSignal("CHANGE_COLOR")
    //     }
    // }

    fun initializeBluetooth() {
        // Placeholder for any additional Bluetooth initialization logic
        Toast.makeText(context, "Bluetooth is initialized!", Toast.LENGTH_SHORT).show()
    }
}
