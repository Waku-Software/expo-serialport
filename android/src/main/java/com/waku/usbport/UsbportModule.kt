package com.waku.usbport; // replace your-apps-package-name with your app’s package name
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class UsbportModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    // add to UsbportModule.kt
    override fun getName() = "UsbportModule"

    @ReactMethod fun testLog() {
        Log.d("testLog")
    }
}