package com.orange.bamboo.knot.insect.burning.thethirdrecording.show.five

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.orange.bamboo.knot.insect.burning.thethirdrecording.R
import com.orange.bamboo.knot.insect.burning.thethirdrecording.data.RecordingInfo
import com.orange.bamboo.knot.insect.burning.thethirdrecording.data.utils.AudioRecorderPlayer
import com.orange.bamboo.knot.insect.burning.thethirdrecording.data.utils.startInfiniteRotation
import com.orange.bamboo.knot.insect.burning.thethirdrecording.data.utils.stopRotation
import com.orange.bamboo.knot.insect.burning.thethirdrecording.databinding.ActivityPlayBinding
import com.orange.bamboo.knot.insect.burning.thethirdrecording.databinding.ActivityRecordBinding
import com.orange.bamboo.knot.insect.burning.thethirdrecording.zore.BaseActivity

class PlayActivity : BaseActivity<ActivityPlayBinding, PlayViewModel>() {
    override val layoutId: Int = R.layout.activity_play
    override val viewModelClass: Class<PlayViewModel> = PlayViewModel::class.java
    private lateinit var audioRecorderPlayer: AudioRecorderPlayer
    private var recordingInfoPos = 0
    private lateinit var recordBean: RecordingInfo
    private var speedNum = 1.0f
    private var totalRecordingDuration = 0L

    override fun initViewComponents() {
        audioRecorderPlayer = AudioRecorderPlayer(this)
        recordingInfoPos = intent.getIntExtra("recordingPos", 0)
        recordBean = audioRecorderPlayer.getAllRecordings()[recordingInfoPos]
        audioRecorderPlayer = AudioRecorderPlayer(this)
        playRecording()
        binding.playSpeed = "x1.0"
    }
    private fun playRecording(){
        audioRecorderPlayer.startPlaying(recordBean.name, speedNum){
            binding.havePlay = 0
        }
        binding.havePlay = 1
        totalRecordingDuration =
            audioRecorderPlayer.getRecordingDuration(recordBean.name)
    }
    override fun initializeData() {
        audioRecorderPlayer.onPlaybackProgress = { progress ->
            val remainingTime = totalRecordingDuration - progress
            binding.tvTime.text = formatTime(remainingTime)
        }
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.imgPlay.setOnClickListener {
            when (binding?.havePlay!!) {
                0 -> {
                    playRecording()
                }

                1 -> {
                    audioRecorderPlayer.pausePlaying()
                    binding.havePlay = 2
                }

                2 -> {
                    audioRecorderPlayer.resumePlaying(speedNum)
                    binding.havePlay = 1
                    totalRecordingDuration =
                        audioRecorderPlayer.getRecordingDuration(recordBean.name)
                }
            }
        }

        binding.imgReduce.setOnClickListener {
            when (binding?.playSpeed!!) {
                "x0.5" -> {
                    return@setOnClickListener
                }

                "x1.0" -> {
                    binding.playSpeed = "x0.5"
                    speedNum = 0.5f
                }

                "x1.5" -> {
                    binding.playSpeed = "x1.0"
                    speedNum = 1.0f
                }

                "x2.0" -> {
                    binding.playSpeed = "x1.5"
                    speedNum = 1.5f
                }
            }
            audioRecorderPlayer.setPlaybackSpeed(speedNum)
        }
        binding.imgAdd.setOnClickListener {
            when (binding?.playSpeed!!) {
                "x0.5" -> {
                    binding.playSpeed = "x1.0"
                    speedNum = 1.0f
                }

                "x1.0" -> {
                    binding.playSpeed = "x1.5"
                    speedNum = 1.5f
                }

                "x1.5" -> {
                    binding.playSpeed = "x2.0"
                    speedNum = 2.0f
                }

                "x2.0" -> {
                    return@setOnClickListener
                }
            }
            audioRecorderPlayer.setPlaybackSpeed(speedNum)
        }
    }

    private fun formatTime(ms: Long): String {
        val seconds = ms / 1000 % 60
        val minutes = ms / 1000 / 60 % 60
        val hours = ms / 1000 / 60 / 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onStop() {
        super.onStop()
        audioRecorderPlayer.pausePlaying()
        binding.havePlay = 2
    }
}