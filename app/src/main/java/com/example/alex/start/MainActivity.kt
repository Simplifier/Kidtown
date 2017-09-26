package com.example.alex.start

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.alex.start.extensions.checkPermission
import com.example.alex.start.extensions.perpetualSnackbar
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_main.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private var mScannerView: ZXingScannerView? = null

    // permission request codes need to be < 256
    private val RC_HANDLE_CAMERA_PERM = 2

    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_main)

        checkPermission(Manifest.permission.CAMERA, R.string.permission_camera_rationale, main_root, RC_HANDLE_CAMERA_PERM)
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)
    }

    public override fun onResume() {
        super.onResume()
        mScannerView?.setResultHandler(this)
        mScannerView?.startCamera()
    }

    public override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera()
    }

    override fun handleResult(rawResult: Result) {
        startActivity<ContentPageActivity>(VideoActivity.BAR_CODE to rawResult.text)
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private fun checkCameraPermission() {
        val rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (rc == PackageManager.PERMISSION_GRANTED) {
            return
        }

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            requestCameraPermission()
            return
        }

        main_root.perpetualSnackbar(R.string.permission_camera_rationale, R.string.ok, this::requestCameraPermission)
    }

    private fun requestCameraPermission(v: View? = null) {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM)
    }
}
