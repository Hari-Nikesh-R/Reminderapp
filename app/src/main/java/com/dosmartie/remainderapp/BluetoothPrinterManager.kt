import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.util.UUID

// Please ignore this, this is not completely done.
class BluetoothPrinterManager(private val context: Context) {
  private val bluetoothAdapter: BluetoothAdapter? by lazy {
    BluetoothAdapter.getDefaultAdapter()
  }
  private var bluetoothSocket: BluetoothSocket? = null

  private fun checkBluetoothPermissions(): Boolean {
    return ActivityCompat.checkSelfPermission(
      context, BLUETOOTH_CONNECT
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
      context, BLUETOOTH_SCAN
    ) == PackageManager.PERMISSION_GRANTED
  }

  @RequiresApi(Build.VERSION_CODES.S) fun connect(macAddress: String, activity: Activity): Boolean {
    return try {
      if (!checkBluetoothPermissions()) { // Request permissions if not granted
        requestBluetoothPermissions(activity)
        return false
      }

      val device = bluetoothAdapter?.getRemoteDevice(macAddress)
      val socket = if (ActivityCompat.checkSelfPermission(
          context, BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED
      ) { // Permission not granted; you might want to request it here.
        return false
      } else {
        device?.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
      }
      socket?.connect()
      bluetoothSocket = socket
      return true
    } catch (e: Exception) {
      false
    }
  }

  @RequiresApi(Build.VERSION_CODES.S)
  private fun requestBluetoothPermissions(activity: Activity) {
    ActivityCompat.requestPermissions(
      activity, arrayOf(
        BLUETOOTH_CONNECT,BLUETOOTH_SCAN
      ), BLUETOOTH_PERMISSION_REQUEST_CODE
    )
  }

  fun print(text: String) {
    try {
      bluetoothSocket?.outputStream?.write(text.toByteArray())
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  companion object {
    const val BLUETOOTH_PERMISSION_REQUEST_CODE = 1001
  }
}