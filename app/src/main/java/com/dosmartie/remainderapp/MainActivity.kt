package com.dosmartie.remainderapp

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dosmartie.remainderapp.ui.theme.RemainderAppTheme

class MainActivity : ComponentActivity() {
  private val printerViewModel by lazy { PrinterViewModel() }
  private val usbPermissionReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      if (ACTION_USB_PERMISSION == intent.action) {
        synchronized(this) {
          val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
          if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
            device?.apply {
              // Permission granted, open the device
              Log.d("Device List", "permission granted for device $device")
//               printerViewModel.
//              openUsbDevice()
            }
          } else {
            Log.d("Device List", "permission denied for device $device")
            // Inform the user that permission is needed
          }
        }
      }
    }
  }

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val filter = IntentFilter(ACTION_USB_PERMISSION)
    registerReceiver(usbPermissionReceiver, filter, RECEIVER_EXPORTED)
    printerViewModel.initialize(applicationContext)
    setContent {
      RemainderAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

          Column(
            modifier = Modifier
              .padding(innerPadding)
              .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            PrinterScreen(printerViewModel)
          }
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    unregisterReceiver(usbPermissionReceiver)
  }

  companion object {
    private const val ACTION_USB_PERMISSION = "com.dosmartie.remainderapp.USB_PERMISSION"
  }
}

@Composable fun PrinterScreen(viewModel: PrinterViewModel) {
  var textToPrint by remember { mutableStateOf("") }
  val activity = LocalContext.current as Activity

  Column(
    modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    TextField(value = textToPrint,
      onValueChange = { textToPrint = it },
      label = { Text("Text to print") })

    Button(onClick = {
      viewModel.connectAndPrint(textToPrint, activity)
    }) {
      Text("Print Document")
    }
    Text(viewModel.connectionStatus)
  }
}