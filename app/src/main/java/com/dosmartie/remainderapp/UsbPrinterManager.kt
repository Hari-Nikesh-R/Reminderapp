package com.dosmartie.remainderapp

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.util.Log

class UsbPrinterManager(private val context: Context) {
  private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
  private var usbDevice: UsbDevice? = null
  private var usbInterface: UsbInterface? = null
  private var usbEndpoint: UsbEndpoint? = null
  private var usbDeviceConnection: UsbDeviceConnection? = null


  private fun requestUsbPermission(device: UsbDevice) {
    val permissionIntent = PendingIntent.getBroadcast(
      context, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE
    )
    usbManager.requestPermission(device, permissionIntent)
  }

  fun findPrinter(): UsbDevice? {
    val deviceList = usbManager.deviceList

    for (device in deviceList.values) {
      Log.i("Device List", "$device")
      // Adjust vendor/product IDs according to your printer
      if (device.vendorId ==  1008 && device.productId == 17492) {
        usbDevice = device
        Log.i("Device List", "Found printer: $device")
        usbDevice?.let {
          requestUsbPermission(it)
        }
        return device
      }
    }
    Log.i("Device List", "Returned null")
    return null
  }

  @FutureDevelopment(
    message = "Need to implement this"
  ) fun newInterfaceImplementation() {
    val interfaceCount = usbDevice?.interfaceCount ?: 0
    for (i in 0 until interfaceCount) {
      val intf = usbDevice?.getInterface(i)
      if (intf?.interfaceClass == UsbConstants.USB_CLASS_PRINTER) {
        for (j in 0 until intf.endpointCount) {
          val endpoint = intf.getEndpoint(j)
          if (endpoint.type == UsbConstants.USB_ENDPOINT_XFER_BULK && endpoint.direction == UsbConstants.USB_DIR_OUT) {
            usbEndpoint = endpoint
            usbInterface = intf
            break
          }
        }
        if (usbEndpoint != null) break
      }
    }

    if (usbInterface == null || usbEndpoint == null) { // Handle error: Printer interface or endpoint not found
      Log.e("Device List", "Printer interface or endpoint not found")
    }

    if (usbDeviceConnection?.claimInterface(
        usbInterface, true
      ) == true
    ) { // Optional: Perform printer initialization (e.g., soft reset)
      val requestType =
        UsbConstants.USB_DIR_OUT or UsbConstants.USB_TYPE_CLASS //      val requestType = UsbConstants.USB_DIR_OUT or UsbConstants.USB_TYPE_CLASS or UsbConstants.USB_INTERFACE_SUBCLASS
      val request = 0x02 // SOFT_RESET request
      val value = 0
      val index = usbInterface?.id
      val timeout = 5000

      val controlResult = usbDeviceConnection?.controlTransfer(
        requestType, request, value, index ?: 0, null, 0, 0, timeout
      )

      // Check control transfer result if needed
      if (controlResult != null) {
        when {
          controlResult >= 0 -> { // Success - proceed with bulk transfer
            Log.i("Device List", "Printer initialized successfully")
          }

          else -> {
            Log.e(
              "Device List", "Printer initialization failed (Code: $controlResult)"
            ) // Abort operation, show error to user
            return
          }
        }
      }
    } else {
      Log.i("Devices list", "Failed to claim interface") //      openUsbDevice()
    }
  }

  fun connect(device: UsbDevice): Boolean {
    return try {
      usbInterface = device.getInterface(0)
      usbEndpoint = usbInterface?.getEndpoint(0)
      usbDeviceConnection = usbManager.openDevice(device)
      usbDeviceConnection?.claimInterface(usbInterface, true)
      true
    } catch (e: Exception) {
      false
    }
  }

  fun print(text: String) {
    val escInit = "\u001B@"    // ESC @ (initialize/reset)
    val helloWorld = "Hello World\n\n"
    val posCommand = escInit + helloWorld

    val bytes = posCommand.toByteArray(Charsets.UTF_8) //    val bytes = text.toByteArray()
    Log.i("Devices list", "$text")

    val bytesSent = usbDeviceConnection?.bulkTransfer(
      usbEndpoint, bytes, bytes.size, 5000
    )

    if (bytesSent != null) {
      if (bytesSent >= 0) { // Data sent successfully
      } else { // Handle bulk transfer error
      }
    }
    usbDeviceConnection?.releaseInterface(usbInterface)
  }

  private fun openUsbDevice() {
    usbDevice?.let { device ->
      try {
        val connection = usbManager.openDevice(device)
        if (connection != null) {
          // Device opened successfully, you can now communicate with it
          Log.d("Device List", "Device opened: $device")
          connection.close() // Remember to close the connection when done
        } else {
          Log.e("Device List", "Failed to open device: $device")
        }
      } catch (e: SecurityException) {
        Log.e("Device List", "Security exception: ${e.message}")
      }
    }
  }

  companion object {
    private const val ACTION_USB_PERMISSION = "com.dosmartie.remainderapp.USB_PERMISSION"
  }
}