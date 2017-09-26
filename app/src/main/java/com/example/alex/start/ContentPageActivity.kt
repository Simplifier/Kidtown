package com.example.alex.start

/**
 * Created by Alex on 05.09.2017.
 */
import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.View
import android.widget.ImageView
import com.example.alex.start.db.DBHelper
import com.example.alex.start.db.database
import com.example.alex.start.extensions.checkPermission
import com.example.alex.start.extensions.perpetualSnackbar
import com.example.alex.start.media.AudioPlayer
import kotlinx.android.synthetic.main.activity_content_page.*
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.net.URL


class ContentPageActivity : AppCompatActivity() {
    private var videoUrl = ""
    private var audioUrl = ""
    private var audioPlayer: AudioPlayer? = null
    private var barCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_page)


        checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, R.string.access_net_rationale, root, 3)
        checkInternetConnection()
    }

    private fun checkInternetConnection(v: View? = null) {
        if (isOnline()) {
            createActivity()
        } else {
            root.perpetualSnackbar(R.string.check_internet_connection, R.string.ok, this::checkInternetConnection)
        }
    }

    private fun createActivity() {
        database.initDb(this)

        val code = intent.getStringExtra(VideoActivity.BAR_CODE)
        if (code != null) {
            barCode = code
            initContentByID(code)
        }
    }

    private fun initContentByID(id: String?) {
        val row = database.use {
            select(DBHelper.pagesTable)
                    .whereSimple("id = ?", id!!)
                    .parseOpt(object : MapRowParser<Map<String, Any?>> {
                        override fun parseRow(columns: Map<String, Any?>) = columns
                    })
        }

        if (row == null) {
            toast("Этот QR-код не ссылается на наши мастерские")
            finish()
            return
        }

        supportActionBar?.title = row["title"] as String

        videoUrl = row["video_link"] as String
        DownloadImageTask(imageView).execute(row["video_preview"] as String)
        videoTitleView.text = row["video_title"] as String
        videoDescView.text = row["video_desc"] as String

        audioUrl = row["audio_link"] as String
        audioTitleView.text = row["audio_title"] as String
    }

    override fun onStop() {
        super.onStop()
        audioPlayer?.stop()
    }

    fun playVideo(view: View) {
        startActivityForResult<VideoActivity>(5,
                VideoActivity.VIDEO_TITLE to videoTitleView.text,
                VideoActivity.VIDEO_URL to videoUrl,
                VideoActivity.BAR_CODE to barCode!!
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            return
        }

        barCode = data.getStringExtra(VideoActivity.BAR_CODE)
        initContentByID(barCode)
    }

    fun playAudio(view: View) {
        val player = MediaPlayer()
        player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player.setDataSource(audioUrl)
        audioPlayer = AudioPlayer(this, player, play_pause, root)

        findViewById<CardView>(R.id.audio_card).setOnClickListener {
            if (audioPlayer!!.isPlaying) {
                audioPlayer?.pause()
            } else {
                audioPlayer?.start()
            }
        }
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}

private class DownloadImageTask(private val bmImage: ImageView) : AsyncTask<String, Void, Bitmap>() {
    override fun doInBackground(vararg urls: String): Bitmap? {
        val url = urls[0]
        var bitmap: Bitmap? = null
        try {
            val inputStream = URL(url).openStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bitmap
    }

    override fun onPostExecute(result: Bitmap) {
        bmImage.setImageBitmap(result)
    }
}