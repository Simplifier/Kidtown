package com.example.alex.start

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.alex.start.extensions.checkPermission
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
}
