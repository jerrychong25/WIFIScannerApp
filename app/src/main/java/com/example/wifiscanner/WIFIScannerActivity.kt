package com.example.wifiscanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.BroadcastReceiver
import android.widget.Toast
import android.net.wifi.WifiManager
import android.content.IntentFilter
import android.widget.ArrayAdapter
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView

class WIFIScannerActivity : AppCompatActivity() {

    private var wifiManager: WifiManager? = null
    private var listView: ListView? = null
    private var buttonScan: Button? = null
    private val size = 0
    private var results: List<ScanResult>? = null
    private val arrayList = arrayListOf<String>()
    private var adapter: ArrayAdapter<*>? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonScan = findViewById(R.id.scanBtn)
        listView = findViewById(R.id.wifiList)
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        adapter = ArrayAdapter(this, R.layout.simple_list_item, arrayList)
        listView!!.setAdapter(adapter)

        if (!wifiManager!!.isWifiEnabled) {
            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show()
            wifiManager!!.isWifiEnabled = true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(ACCESS_COARSE_LOCATION), 0)
            //do something if have the permissions
        } else {
            //do something, permission was previously granted; or legacy device
            scanWifi()
        }

        buttonScan!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                scanWifi()
            }
        })
    }

    private fun scanWifi() {
        arrayList.clear()
        registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        wifiManager!!.startScan()
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show()
    }

    var wifiReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            results = wifiManager!!.scanResults
            unregisterReceiver(this)

            for (scanResult in results!!) {
                var wifi_ssid = ""
                wifi_ssid = scanResult.SSID
                Log.d("WIFIScannerActivity", "WIFI SSID: $wifi_ssid")

                var wifi_ssid_first_nine_characters = ""

                if (wifi_ssid.length > 8) {
                    wifi_ssid_first_nine_characters = wifi_ssid.substring(0, 9)
                } else {
                    wifi_ssid_first_nine_characters = wifi_ssid
                }
                Log.d("WIFIScannerActivity", "WIFI SSID 9: $wifi_ssid_first_nine_characters")

                // Display only WIFI that matched "WIFI_NAME"
//                if (wifi_ssid_first_nine_characters == "WIFI_NAME") {
                    Log.d(
                        "WIFIScannerActivity",
                        "scanResult.SSID: " + scanResult.SSID + ", scanResult.capabilities: " + scanResult.capabilities
                    )
                    arrayList.add(scanResult.SSID + " - " + scanResult.capabilities)
                    adapter!!.notifyDataSetChanged()
//                }

                wifi_ssid = ""
                wifi_ssid_first_nine_characters = ""
            }
        }
    }
}
