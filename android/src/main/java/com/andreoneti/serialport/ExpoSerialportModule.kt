package com.andreoneti.serialport

import android.app.PendingIntent

import android.content.Intent
import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.BroadcastReceiver

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.hardware.usb.UsbRequest
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDeviceConnection

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap

import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.functions.Queues
import expo.modules.kotlin.exception.Exceptions
import expo.modules.kotlin.exception.CodedException
import expo.modules.kotlin.modules.ModuleDefinition

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

class ExpoSerialportModule : Module() {
    
  // Function to convert hex string to byte array
  private fun hexStringToByteArray(hexString: String): ByteArray {
      val len = hexString.length
      val data = ByteArray(len / 2)
      var i = 0
      while (i < len) {
          data[i / 2] = ((Character.digit(hexString[i], 16) shl 4)
                  + Character.digit(hexString[i + 1], 16)).toByte()
          i += 2
      }
      return data
  }

  override fun definition() = ModuleDefinition {
    Name("ExpoSerialport")

    Function("listDevices") {
      return@Function listDevices()
    }
    
    // --- START OF NEW CODE --- 
    AsyncFunction("write") { deviceId: Int, promise: Promise ->
      println("Debug: write function started")
      
      // Hardcode hexData
      val hexData: String = "02D03" + calculateLRC("D03")

      val usbDevice: UsbDevice? = findDevice(deviceId)
    
      if (usbDevice == null) {
        println("Debug: usbDevice not found")
        val error: CodedException = CodedException(DEVICE_NOT_FOUND)
        promise.reject(error)
      } else {
        println("Debug: usbDevice found")
        val usbManager: UsbManager = getUsbManager()
        val hasPermission: Boolean = usbManager.hasPermission(usbDevice)
    
        if (!hasPermission) {
            println("Debug: No permission")
            val error: CodedException = CodedException(PERMISSION_REQUIRED)
            promise.reject(error)
        } else {
            println("Debug: Permission granted")
            try {
                val connection: UsbDeviceConnection? = usbManager.openDevice(usbDevice)
                println("Debug: Connection opened")
                val usbInterface: UsbInterface? = usbDevice.getInterface(0)
                println("Debug: Interface acquired")
                val endpoint = usbInterface?.getEndpoint(1) // endpoint 1 usually used for writing
    
                connection?.claimInterface(usbInterface, true)
                println("Debug: Interface claimed")
    
                val bytes = hexStringToByteArray(hexData) // Convert hex string to byte array
                println("Debug: Data converted to bytes")
                val result = connection?.bulkTransfer(endpoint, bytes, bytes.size, 1000) // send data to device
                println("Debug: Bulk transfer executed")

                connection?.releaseInterface(usbInterface)
                println("Debug: Interface released")
                connection?.close()
                println("Debug: Connection closed")

                println("Debug: About to resolve promise with result: $result")
                promise.resolve(result)
            } catch (e: Exception) {
                println("Debug: Exception occurred: ${e.message}") 
                val error: CodedException = CodedException("unknown_error")
                promise.reject(error)
            }
        }
      }
    }
    // AsyncFunction("write") { deviceId: Int, hexData: String, promise: Promise ->
    //   println("Debug: write function started")
    //   val usbDevice: UsbDevice? = findDevice(deviceId)
    
    //   if (usbDevice == null) {
    //     println("Debug: usbDevice not found")
    //     val error: CodedException = CodedException(DEVICE_NOT_FOUND)
    //     promise.reject(error)
    //   } else {
    //     println("Debug: usbDevice found")
    //     val usbManager: UsbManager = getUsbManager()
    //     val hasPermission: Boolean = usbManager.hasPermission(usbDevice)
    
    //     if (!hasPermission) {
    //         println("Debug: No permission")
    //         val error: CodedException = CodedException(PERMISSION_REQUIRED)
    //         promise.reject(error)
    //     } else {
    //         println("Debug: Permission granted")
    //         try {
    //             val connection: UsbDeviceConnection? = usbManager.openDevice(usbDevice)
    //             println("Debug: Connection opened")
    //             val usbInterface: UsbInterface? = usbDevice.getInterface(0)
    //             println("Debug: Interface acquired")
    //             val endpoint = usbInterface?.getEndpoint(1) // endpoint 1 usually used for writing
    
    //             connection?.claimInterface(usbInterface, true)
    //             println("Debug: Interface claimed")
    
    //             val bytes = hexStringToByteArray(hexData) // Convert hex string to byte array
    //             println("Debug: Data converted to bytes")
    //             val result = connection?.bulkTransfer(endpoint, bytes, bytes.size, 1000) // send data to device
    //             println("Debug: Bulk transfer executed")

    //             connection?.releaseInterface(usbInterface)
    //             println("Debug: Interface released")
    //             connection?.close()
    //             println("Debug: Connection closed")

    //             println("Debug: About to resolve promise with result: $result")
    //             promise.resolve(result)
    //         } catch (e: Exception) {
    //             println("Debug: Exception occurred: ${e.message}") 
    //             val error: CodedException = CodedException("unknown_error")
    //             promise.reject(error)
    //         }
    //     }
    //   }
    // }
    
    // --- END OF NEW CODE ---

    AsyncFunction("getSerialNumberAsync") { deviceId: Int, promise: Promise ->
      val usbManager: UsbManager = getUsbManager()
      val usbDeviceList: List<UsbDevice>? = usbManager.deviceList.values.toList()

      val usbDevice: UsbDevice? = usbDeviceList?.find { it.deviceId == deviceId }

      if (usbDevice == null) {
        val error: CodedException = CodedException(DEVICE_NOT_FOUND)
        promise.reject(error)
      } else if(!usbManager.hasPermission(usbDevice)) {
        val error: CodedException = CodedException(PERMISSION_REQUIRED)
        promise.reject(error)
      } else {
        promise.resolve(usbDevice.getSerialNumber())
      }
    }

    AsyncFunction("hasPermissionAsync") { deviceId: Int, promise: Promise ->
      val usbDevice: UsbDevice? = findDevice(deviceId)

      if (usbDevice == null) {
        val error: CodedException = CodedException(DEVICE_NOT_FOUND)
        promise.reject(error)
      } else {
        val usbManager: UsbManager = getUsbManager()
        val hasPermission: Boolean = usbManager.hasPermission(usbDevice)

        promise.resolve(hasPermission)
      }
    }

    AsyncFunction("requestPermissionAsync") { deviceId: Int, promise: Promise ->
      val usbDevice: UsbDevice? = findDevice(deviceId)

      if (usbDevice == null) {
        val error: CodedException = CodedException(DEVICE_NOT_FOUND)
        promise.reject(error)
      } else {
        requestPermission(usbDevice, promise)
      }
    }
  }

  private val DEVICE_NOT_FOUND: String = "device_not_found"
  private val PERMISSION_DENIED: String = "permission_denied"
  private val PERMISSION_REQUIRED: String = "permission_required"
  // private val PERMISSION_REQUIRED: String = "permission_required"

  private val context
  get() = requireNotNull(appContext.reactContext)

  private fun getPreferences(): SharedPreferences {
    return context.getSharedPreferences(context.packageName + ".settings", Context.MODE_PRIVATE)
  }

  private fun calculateLRC(input: String): String {
    var lrc = 0
    for (ch in input) {
        lrc = lrc.xor(ch.toInt())
    }
    return lrc.toString(16)
  } 

  private fun getUsbManager(): UsbManager {
    return context.getSystemService(Context.USB_SERVICE) as UsbManager
  }

  private fun listDevices(): WritableArray {
    println("Debug: Entro a list devices")
    val usbManager: UsbManager = getUsbManager()
    val usbDeviceList: List<UsbDevice>? = usbManager.deviceList.values.toList()

    val usbDevicesArray: WritableArray = WritableNativeArray()

    if (usbDeviceList != null) {
      for (usbDevice in usbDeviceList) {
        val usbDeviceMap: WritableMap = WritableNativeMap()

        usbDeviceMap.putInt("deviceId", usbDevice.getDeviceId())
        usbDeviceMap.putInt("vendorId", usbDevice.getVendorId())
        usbDeviceMap.putInt("productId", usbDevice.getProductId())
        usbDeviceMap.putInt("deviceClass", usbDevice.getDeviceClass())
        usbDeviceMap.putString("deviceName", usbDevice.getDeviceName())
        usbDeviceMap.putString("productName", usbDevice.getProductName())
        usbDeviceMap.putInt("deviceProtocol", usbDevice.getDeviceProtocol())
        usbDeviceMap.putInt("interfaceCount", usbDevice.getInterfaceCount())
        usbDeviceMap.putString("manufacturerName", usbDevice.getManufacturerName())

        usbDevicesArray.pushMap(usbDeviceMap)
      }
    }

    return usbDevicesArray
  }

  private fun findDevice(deviceId:Int): UsbDevice? {
    val usbManager: UsbManager = getUsbManager()
    val usbDeviceList: List<UsbDevice>? = usbManager.deviceList.values.toList()

    val usbDevice: UsbDevice? = usbDeviceList?.find { it.deviceId == deviceId }

    return usbDevice
  }

  private fun requestPermission(device: UsbDevice, promise: Promise): Unit {
    val ACTION_USB_PERMISSION: String = context.packageName + ".GRANT_USB"
    val usbManager: UsbManager = getUsbManager()
    val permissionIntent = PendingIntent.getBroadcast(
      context,
      0,
      Intent(ACTION_USB_PERMISSION),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val permissionReceiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_USB_PERMISSION) {
          intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
          val granted: Boolean = usbManager.hasPermission(device)
          if (granted) {
            promise.resolve(null)
          } else {
            val error: CodedException = CodedException(PERMISSION_DENIED)
            promise.reject(error)
          }
          context.unregisterReceiver(this)
        }
      }
    }

    val filter = IntentFilter(ACTION_USB_PERMISSION)
    context.registerReceiver(permissionReceiver, filter)

    usbManager.requestPermission(device, permissionIntent)
  }
}
