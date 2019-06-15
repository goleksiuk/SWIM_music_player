package com.example.music_player

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer = MediaPlayer.create(this, R.raw.music)
        mediaPlayer?.isLooping = true
        mediaPlayer?.seekTo(0)
        mediaPlayer?.setVolume(0.5f, 0.5f)
        val totalTime = mediaPlayer?.duration

        playPause_BT.setOnClickListener {
            if(!mediaPlayer!!.isPlaying) {
                playPause_BT.setImageResource(R.drawable.stop)
                mediaPlayer?.start()
            } else {
                mediaPlayer?.pause()
                playPause_BT.setImageResource(R.drawable.play)
            }
        }

        position_SB.max = totalTime!!
        position_SB.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    mediaPlayer?.seekTo(progress)
                    position_SB.progress = progress

                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        volume_SB.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val currentVolume = progress/100f
                mediaPlayer?.setVolume(currentVolume, currentVolume)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        val handler = object : Handler() {
            override fun handleMessage(message: Message) {
                var currentPosition = message.what

                position_SB.progress = currentPosition

                elapsed_TV.text = getTime(currentPosition)
                remain_TV.text = getTime(totalTime-currentPosition)
            }

        }

        Thread(Runnable{
            run {
                while (mediaPlayer != null) {
                    try {
                        val message = Message()
                        message.what = mediaPlayer!!.currentPosition
                        handler.sendMessage(message)
                        Thread.sleep(1000)

                    } catch (e: InterruptedException) {}
                }
            }}
        ).start()
    }

    private fun getTime(time: Int) : String {
        val minutes = time/1000/60
        val seconds = time/1000%60

        var elapsedTime = "$minutes:"
        if (seconds < 10) {
            elapsedTime += "0"
        }
        elapsedTime += seconds

        return elapsedTime
    }
}
