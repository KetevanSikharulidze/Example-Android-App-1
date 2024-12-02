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

    // Handle the Bluetooth connection (send and receive messages)
    private fun handleConnection(inputStream: InputStream, outputStream: OutputStream) {
        val data = ByteArray(1024)
        var bytesRead: Int

        // Continuously listen for incoming data
        while (true) {
            try {
                bytesRead = inputStream.read(data)
                if (bytesRead != -1) {
                    val message = String(data, 0, bytesRead)
                    // Do something with the received message (e.g., update UI or handle it)
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, "Received message: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                break
            }
        }
    }

    // Stop listening for incoming connections
    fun stopListening() {
        bluetoothServerSocket?.close()
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
}
    
    fun initializeBluetooth() {
        // Placeholder for any additional Bluetooth initialization logic
        Toast.makeText(context, "Bluetooth is initialized!", Toast.LENGTH_SHORT).show()
    }
}
