package com.example.alex.start.extensions

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.view.View
import com.example.alex.start.R

/**
 * Created by Alex on 06.09.2017.
 */


/**
 * Handles the requesting of the permission.  This includes
 * showing a "Snackbar" message of why the permission is needed then
 * sending the request.
 */
fun Activity.checkPermission(permission: String, permRationale: String, view: View, requestCode: Int) {
    val rc = ActivityCompat.checkSelfPermission(this, permission)
    if (rc == PackageManager.PERMISSION_GRANTED) {
        return
    }

    val permissions = arrayOf(permission)
    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
        return
    }

    view.perpetualSnackbar(permRationale, R.string.ok, {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    })
}

fun Activity.checkPermission(permission: String, permRationale: Int, view: View, requestCode: Int) {
    checkPermission(permission, getText(permRationale).toString(), view, requestCode)
}