package com.orange.bamboo.knot.insect.burning.thethirdrecording.show.three

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import com.orange.bamboo.knot.insect.burning.thethirdrecording.R
import com.orange.bamboo.knot.insect.burning.thethirdrecording.data.utils.AudioRecorderPlayer
import com.orange.bamboo.knot.insect.burning.thethirdrecording.databinding.ActivityRecordBinding
import com.orange.bamboo.knot.insect.burning.thethirdrecording.zore.BaseActivity

class RecordActivity : BaseActivity<ActivityRecordBinding, RecordViewModel>() {
    override val layoutId: Int = R.layout.activity_record
    override val viewModelClass: Class<RecordViewModel> = RecordViewModel::class.java
    private lateinit var audioRecorderPlayer: AudioRecorderPlayer
    private val handler = Handler(Looper.getMainLooper())
    override fun initViewComponents() {
        audioRecorderPlayer = AudioRecorderPlayer(this)
        binding.havePlay = 0
        onBackPressedDispatcher.addCallback(this) {
            finishRecord()
        }
    }

    override fun initializeData() {
        binding.imgBack.setOnClickListener {
            finishRecord()
        }
        binding.imgPlay.setOnClickListener {
            when (binding?.havePlay!!) {
                0 -> {
                    Log.e("TAG", "initializeData: 0")
                    audioRecorderPlayer.startRecording()
                    binding.havePlay = 1
                    startTimer()
                }

                1 -> {
                    Log.e("TAG", "initializeData: 1")
                    audioRecorderPlayer.pauseRecording()
                    binding.havePlay = 2
                }

                2 -> {
                    Log.e("TAG", "initializeData: 2")
                    audioRecorderPlayer.resumeRecording()
                    binding.havePlay = 1
                    startTimer()
                }
            }
        }
        binding.imgStop.setOnClickListener {
            audioRecorderPlayer.stopRecording()
            binding.havePlay = 0
            handler.removeCallbacks(updateTimer)
            binding.tvTime.text = formatTime(0)
            Toast.makeText(this, "Recording completed. Go to the list to view.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun finishRecord() {
        if (binding.havePlay == 1) {
            audioRecorderPlayer.pauseRecording()
            binding.havePlay = 2
            showDeleteDialog()
        } else {
            finish()
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Tip")
            .setMessage("If the recording is not saved, return to the home page.")
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Ok") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .create()
            .show()
    }

    private fun startTimer() {
        handler.post(updateTimer)
    }

    private val updateTimer = object : Runnable {
        override fun run() {
            if (binding.havePlay == 1) {
                val elapsed = audioRecorderPlayer.getElapsedRecordingTime()
                val formattedTime = formatTime(elapsed)
                binding.tvTime.text = formattedTime
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun formatTime(ms: Long): String {
        val seconds = ms / 1000 % 60
        val minutes = ms / 1000 / 60 % 60
        val hours = ms / 1000 / 60 / 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding.havePlay != 0) {
            audioRecorderPlayer.deleteRecording(audioRecorderPlayer.getFileNameRecorder(), false)
        }
    }
}