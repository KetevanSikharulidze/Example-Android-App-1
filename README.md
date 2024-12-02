Step-by-Step App Functionality
Launch the App:

Upon opening, the app checks if the device supports Bluetooth.
Bluetooth Check:

If Bluetooth is not enabled, the app prompts the user to enable it using BluetoothAdapter.ACTION_REQUEST_ENABLE.
If Bluetooth is already enabled, the app moves forward to start advertising and scanning.
BLE Advertising:

The app begins broadcasting its signal using BLE advertising. Other nearby devices can pick up this signal, but the app is not required to be directly connected to those devices.
Scanning for Other Devices:

Simultaneously, the app starts scanning for nearby devices that are also advertising.
If the app detects any other devices, it will show a toast message with their names (this can be expanded to more advanced behavior, like displaying the list of devices in the UI).
Ongoing Process:

The app continues advertising and scanning, and this process can run indefinitely or until you manually stop scanning.
