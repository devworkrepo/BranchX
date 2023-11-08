package com.branchx.agent.helper.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings


object LocationService {

    fun setupListener(listener: MLocationListener?){
        this.mListener = listener
    }

    private var mLocationListener: LocationListener? = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            mListener?.onLocationChange(location)
            location.let {
                mLocationManager?.removeUpdates(this)
            }
        }


        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
        override fun onProviderEnabled(s: String) {}
        override fun onProviderDisabled(s: String) {}
    }
    private var mLocationManager: LocationManager? = null

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(mLocationManager: LocationManager) {
        this.mLocationManager = mLocationManager
        val isGPSEnabled: Boolean =
                mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled: Boolean =
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var location: Location? = null


        if (isNetworkEnabled) {
            mLocationListener?.let {
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME,
                        LOCATION_UPDATE_MIN_DISTANCE, it
                )
            }
            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        if (isGPSEnabled) {
            mLocationListener?.let {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME,
                        LOCATION_UPDATE_MIN_DISTANCE, it
                )
            }
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        if (location == null) {

        }
    }


    private const val LOCATION_UPDATE_MIN_DISTANCE = 10f
    private const val LOCATION_UPDATE_MIN_TIME = 5000L


    private var mListener : MLocationListener? = null
    interface MLocationListener{
        fun onLocationChange(location: Location)
    }


    fun locationEnabled(context: Context) : Boolean {
        val lm : LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder(context)
                    .setMessage("GPS Enable")
                    .setPositiveButton(
                            "Settings"
                    ) { _, _ ->
                        context.startActivity(
                                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        )
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
        }


        return gps_enabled || network_enabled
    }

    fun isLocationEnabled(context: Context): Boolean{
        val isEnabled = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            lm.isLocationEnabled
        } else {
            val mode = Settings.Secure.getInt(
                context.contentResolver, Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF)
            mode != Settings.Secure.LOCATION_MODE_OFF
        }
        if (!isEnabled) {
            AlertDialog.Builder(context)
                .setMessage("GPS Enable")
                .setPositiveButton("Settings") { _, _ ->
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                .setNegativeButton("Cancel", null)
                .show()
        }
        return isEnabled
    }


}