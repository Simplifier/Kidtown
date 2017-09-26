package com.example.alex.start

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_video.*
import org.jetbrains.anko.toast


/**
 * Created by Alex on 29.08.2017.
 */
class VideoActivity : AppCompatActivity() {
    private lateinit var barCode: String

    companion object {
        const val BAR_CODE = "barCode"
        const val VIDEO_TITLE = "videoTitle"
        const val VIDEO_URL = "videoUrl"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        barCode = intent.getStringExtra(BAR_CODE)
        title = intent.getStringExtra(VIDEO_TITLE)

        val vidUri = Uri.parse(intent.getStringExtra(VIDEO_URL))
        vidView.setVideoURI(vidUri)

        val vidControl = MediaController(this)
        vidControl.setAnchorView(vidView)
        vidView.setMediaController(vidControl)

        vidView.start()
    }

    private fun giveBarCodeBack() {
        val data = Intent()
        data.putExtra(BAR_CODE, barCode)

        setResult(RESULT_OK, data)
    }

    public override fun onPause() {
        super.onPause()
        vidView.suspend()
    }

    public override fun onStop() {
        vidView.stopPlayback()
        giveBarCodeBack()
        super.onStop()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val isBackIconClicked = item.itemId == android.R.id.home
        if (isBackIconClicked) {
            giveBarCodeBack()
            finish()
            // false is returned by default and onCreate() called
            // instead of onActivityResult() (f#@$!!!)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}