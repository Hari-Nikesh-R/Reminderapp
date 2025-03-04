package com.dosmartie.remainderapp

import BluetoothPrinterManager
import BluetoothPrinterManager.Companion.BLUETOOTH_PERMISSION_REQUEST_CODE
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PrinterViewModel : ViewModel() {
  private lateinit var usbPrinterManager: UsbPrinterManager
  private lateinit var bluetoothPrinterManager: BluetoothPrinterManager
  var connectionStatus by mutableStateOf("Disconnected")
    private set

  fun initialize(context: Context) {
    usbPrinterManager = UsbPrinterManager(context)
    bluetoothPrinterManager = BluetoothPrinterManager(context)
  }

  @RequiresApi(Build.VERSION_CODES.S) fun connectAndPrint(text: String, context: Activity) {
    viewModelScope.launch {
      val usbDevice = usbPrinterManager.findPrinter()
      if (usbDevice != null && usbPrinterManager.connect(usbDevice)) {

        connectionStatus = "Connected via Device List"
        usbPrinterManager.print(text)
      } //      { // Fallback to Bluetooth
      //        //        if (bluetoothPrinterManager.connect(
      //        //            "00:11:22:33:44:55", activity = context
      //        //          )
      //        //        ) { // Replace with your printer MAC
      //        //          connectionStatus = "Connected via Bluetooth"
      //        //          bluetoothPrinterManager.print(text)
      //        //        } else {
      //        //          connectionStatus = "Connection failed"
      //        //        } // todo:'
      //        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
      //        if (bluetoothAdapter == null) {
      //          connectionStatus = "Bluetooth not supported"
      //          return@launch
      //        }
      //        if (!bluetoothAdapter.isEnabled) {
      //          connectionStatus = "Bluetooth not enabled"
      //          return@launch
      //        }
      //
      //
      //        if (!checkBluetoothPermissions(context.applicationContext)) { // Request permissions if not granted
      //          requestBluetoothPermissions(context)
      //        }
      //
      //        if (ActivityCompat.checkSelfPermission(
      //            context, BLUETOOTH_CONNECT
      //          ) != PackageManager.PERMISSION_GRANTED
      //        ) {
      //          connectionStatus = "Missing Bluetooth connect permission"
      //          return@launch
      //        }
      //
      //        val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
      //
      //        val printerDevice = pairedDevices.firstOrNull { device ->
      //          device.name.contains("printer", ignoreCase = true)
      //        }
      //
      //        Log.i("Paired devices", "Device $pairedDevices")
      //
      //        if (printerDevice != null) {
      //          if (bluetoothPrinterManager.connect(printerDevice.address, activity = context)) {
      //            connectionStatus = "Connected via Bluetooth"
      //            bluetoothPrinterManager.print(text)
      //          } else {
      //            connectionStatus = "Bluetooth connection failed"
      //          }
      //        } else {
      //          connectionStatus = "No paired printer found"
      //        }
      //
      //
      //
      //        connectionStatus = "No paired printer found, starting discovery..."
      //
      //        val discoveryReceiver = object : BroadcastReceiver() {
      //          private val discoveredDevices = mutableListOf<BluetoothDevice>()
      //
      //          override fun onReceive(context: Context, intent: Intent) {
      //            when (intent.action) {
      //              BluetoothDevice.ACTION_FOUND -> { // Check Bluetooth scan permission
      //                if (ActivityCompat.checkSelfPermission(
      //                    context, BLUETOOTH_SCAN
      //                  ) != PackageManager.PERMISSION_GRANTED
      //                ) {
      //                  connectionStatus = "Missing Bluetooth scan permission"
      //                  return
      //                } // Extract device and add to list
      //                val device =
      //                  intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
      //                device?.let { discoveredDevices.add(it) }
      //              }
      //
      //              BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> { // Stop discovery and unregister receiver
      //                context.unregisterReceiver(this)
      //                bluetoothAdapter.cancelDiscovery() // Show device selection UI
      //                showDeviceSelectionDialog(context, discoveredDevices)
      //              }
      //            }
      //          }
      //        }
      //        val filter = IntentFilter().apply {
      //          addAction(BluetoothDevice.ACTION_FOUND)
      //          addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
      //        }
      //
      //        with(bluetoothAdapter) {
      //          if (isDiscovering) cancelDiscovery()
      //          if (ActivityCompat.checkSelfPermission(
      //              context, BLUETOOTH_SCAN
      //            ) == PackageManager.PERMISSION_GRANTED
      //          ) {
      //            context.registerReceiver(discoveryReceiver, filter)
      //            startDiscovery()
      //          } else {
      //            connectionStatus = "Bluetooth scan permission required"
      //          }
      //        }
      //
      //        Log.i("Paired devices", "Device $pairedDevices")
      //      }
    }
  }

  @RequiresApi(Build.VERSION_CODES.S) private fun showDeviceSelectionDialog(
    context: Context, devices: List<BluetoothDevice>
  ) {
    val activity = context as? Activity ?: return

    // Handle empty USB
    if (devices.isEmpty()) {
      AlertDialog.Builder(activity).setTitle("No Devices Found")
        .setMessage("No Bluetooth devices discovered").setPositiveButton("OK", null).show()
      return
    }

    val deviceNames = devices.map {
      it.name ?: it.address ?: "Unknown Device"
    }.toTypedArray()

    AlertDialog.Builder(activity).setTitle("Select Bluetooth Device")
      .setItems(deviceNames) { _, which ->
        val selectedDevice = devices[which]

        if (ActivityCompat.checkSelfPermission(
            context, BLUETOOTH_CONNECT
          ) != PackageManager.PERMISSION_GRANTED
        ) {
          connectionStatus = "Bluetooth connect permission required"
          ActivityCompat.requestPermissions(
            activity, arrayOf(BLUETOOTH_CONNECT), BLUETOOTH_PERMISSION_REQUEST_CODE
          )
          return@setItems
        }

        if (bluetoothPrinterManager.connect(selectedDevice.address, activity)) {
          connectionStatus = "Connected to ${selectedDevice.name}"
          bluetoothPrinterManager.print("Hello from Bluetooth!")
        } else {
          connectionStatus = "Connection failed"
        }
      }.setNegativeButton("Cancel", null).show()
  }

  @RequiresApi(Build.VERSION_CODES.S) private fun requestBluetoothPermissions(activity: Activity) {
    ActivityCompat.requestPermissions(
      activity, arrayOf(
        BLUETOOTH_CONNECT, BLUETOOTH_SCAN
      ), BLUETOOTH_PERMISSION_REQUEST_CODE
    )
  }

  private fun checkBluetoothPermissions(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
      context, BLUETOOTH_CONNECT
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
      context, BLUETOOTH_SCAN
    ) == PackageManager.PERMISSION_GRANTED
  }
}