package com.billion_dollor_company.securutysystem.activities.main.camera

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import com.billion_dollor_company.securutysystem.R
import com.billion_dollor_company.securutysystem.databinding.ActivityRingBellBinding

class RingBellActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRingBellBinding
    private lateinit var vibrate:Vibrator
    private lateinit var player:MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRingBellBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        ring()



    }

    private fun ring() {
        binding.ringBell.setOnClickListener {
            playSound()
        }
    }


    private fun playSound() {
        player.start()
        player.setOnCompletionListener {
//            sendPhoto()
        }
    }

    private fun init(){
        player = MediaPlayer.create(this,R.raw.doorbell)
        vibrate = this.getSystemService(VIBRATOR_SERVICE) as Vibrator
    }
}