package com.example.alex.start.media

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.view.View
import android.widget.MediaController
import android.widget.ToggleButton

/**
 * Created by Alex on 01.09.2017.
 */
class AudioPlayer(context: Context, private val player: MediaPlayer, private val playPauseBtn: ToggleButton, private val anchorView: View) :
        MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {
    private var bufferPercent = 0
    private val controlPanel: ControlPanel = ControlPanel(context)
    private val handler = Handler()

    init {
        player.prepareAsync()
        player.setOnBufferingUpdateListener { _, percent -> bufferPercent = percent }
        player.setOnPreparedListener(this)
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        controlPanel.setMediaPlayer(this)
        controlPanel.setAnchorView(anchorView)

        handler.post {
            controlPanel.isEnabled = true
            start()
        }
    }

    fun stop() {
        controlPanel.realHide()
        playPauseBtn.isChecked = false
        player.stop()
        player.release()
    }

    override fun start() {
        player.start()
        controlPanel.show()
        playPauseBtn.isChecked = true
    }

    override fun pause() {
        player.pause()
        controlPanel.show()
        playPauseBtn.isChecked = false
    }

    override fun canPause() = true
    override fun isPlaying() = player.isPlaying

    override fun canSeekForward() = true
    override fun canSeekBackward() = true
    override fun seekTo(msec: Int) = player.seekTo(msec)

    override fun getDuration() = player.duration
    override fun getCurrentPosition() = player.currentPosition
    override fun getBufferPercentage() = bufferPercent

    override fun getAudioSessionId() = player.audioSessionId
}

private class ControlPanel(context: Context) : MediaController(context) {
    override fun hide() {}
    fun realHide() {
        super.hide()
    }
}