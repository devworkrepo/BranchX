package com.branchx.agent.helper.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.karumi.dexter.BuildConfig
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

object PermissionUtil {


    inline fun storageCameraPermissions(context: Context, crossinline onPermissionGranted: (Boolean) -> Unit) {
        val permission = if (BuildConfig.VERSION_CODE >= 30) listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        ) else listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        )
        requestPermission(context, permission, onPermissionGranted, "Storage and Camera")
    }

    inline fun bluetoothPermissions(context: Context, crossinline onPermissionGranted: (Boolean) -> Unit) {
        val permission =  listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        requestPermission(context, permission, onPermissionGranted, "Location")
    }




    inline fun locationPermission(context: Context,crossinline onPermissionGranted: (Boolean) -> Unit) {
        val permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        requestPermission(context, permissions, onPermissionGranted, "Location Permission")

    }


    inline fun storagePermission(context: Context, crossinline onPermissionGranted: (Boolean) -> Unit) {
        val permissions = if (BuildConfig.VERSION_CODE >= 30) listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ) else listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
        requestPermission(context,permissions,onPermissionGranted,"Storage")
    }




    inline  fun requestPermission(
        context: Context,
        permissions: List<String>,
        crossinline onPermissionGranted: (Boolean) -> Unit,
        title: String
    ) {
        Dexter.withContext(context)
            .withPermissions(permissions).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    val isAllGranted = report.areAllPermissionsGranted()
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog(context as Activity, title)
                    }
                    onPermissionGranted(isAllGranted)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    fun showSettingsDialog(
        activity: Activity?,
        vararg permissions: String
    ) {
        val sb = StringBuilder()
        for (s in permissions) {
            sb.append(s)
        }

        activity?.let {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Need Permissions")
            builder.setMessage("This app needs $sb permissions to use this feature. You can grant them in app settings.")
            builder.setPositiveButton("GOTO SETTINGS") { dialog: DialogInterface, _: Int ->
                dialog.cancel()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivityForResult(intent, 101)
            }
            builder.setNegativeButton("Cancel") { dialog: DialogInterface, which: Int -> dialog.cancel() }
            builder.show()
        }
    }

}