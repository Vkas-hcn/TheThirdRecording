package com.orange.bamboo.knot.insect.burning.thethirdrecording.data.utils

import android.app.Activity


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.orange.bamboo.knot.insect.burning.thethirdrecording.data.RecordingInfo
import java.io.File
import java.io.IOException

class AudioRecorderPlayer(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var outputFilePath: String = ""
    private var fileNameRecorder: String = ""
    private var isRecording: Boolean = false
    var isPlaying: Boolean = false
    private var recordingDuration: Long = 0
    private var pauseTime: Long = 0
    var onPlaybackProgress: ((Long) -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())

    init {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity, arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 0
            )
        }
    }

    fun startRecording() {
        if (isRecording) return
        fileNameRecorder = "record_${UtilsRecord.getDateNow()}"
        outputFilePath =
            "${context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/${fileNameRecorder}.3gp"
        Log.e("TAG", "startRecording: ${outputFilePath}")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFilePath)
            prepare()
            start()
        }
        isRecording = true
        pauseTime = System.currentTimeMillis()
    }

    fun pauseRecording() {
        if (!isRecording) return
        mediaRecorder?.pause()
        recordingDuration += System.currentTimeMillis() - pauseTime
    }

    fun resumeRecording() {
        if (!isRecording) return
        mediaRecorder?.resume()
        pauseTime = System.currentTimeMillis()
    }

    fun getFileNameRecorder(): String {
        return fileNameRecorder
    }

    fun stopRecording(): String {
        if (!isRecording) return ""
        mediaRecorder?.apply {
            stop()
            reset()
            release()
        }
        recordingDuration = 0
        mediaRecorder = null
        isRecording = false
        return outputFilePath
    }


    fun renameRecording(oldName: String, newName: String): String {
        val musicDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)

        // Ensure oldName contains the file extension
        val oldFileName = if (oldName.endsWith(".3gp")) oldName else "$oldName.3gp"
        val oldFilePath = "$musicDir/$oldFileName"

        // Ensure newName does not contain the file extension to avoid duplication
        val newFileName = if (newName.endsWith(".3gp")) newName else "$newName.3gp"
        val newFilePath = "$musicDir/$newFileName"

        val oldFile = File(oldFilePath)
        val newFile = File(newFilePath)

        return if (oldFile.exists()) {
            if (newFile.exists()) {
                Log.e("renameRecording", "New file name already exists.")
                "New file name already exists."
            } else {
                val success = oldFile.renameTo(newFile)
                if (success) {
                    outputFilePath = newFilePath
                    Log.i("renameRecording", "File renamed successfully to $newFilePath")
                    newFilePath
                } else {
                    Log.e("renameRecording", "Failed to rename file.")
                    "Failed to rename file."
                }
            }
        } else {
            Log.e("renameRecording", "Old file does not exist.")
            "Old file does not exist."
        }
    }

    fun deleteRecording(fileName: String, isShowToast: Boolean = true): Boolean {
        val musicDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val fileToDeleteName = if (fileName.endsWith(".3gp")) fileName else "$fileName.3gp"
        val fileToDeletePath = "$musicDir/$fileToDeleteName"

        val fileToDelete = File(fileToDeletePath)

        return if (fileToDelete.exists()) {
            val success = fileToDelete.delete()
            if (isShowToast) {
                if (success) {
                    Toast.makeText(context, "File deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show()
                }
            }
            success
        } else {
            if (isShowToast) {
                Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show()
            }
            false
        }
    }

    fun startPlaying(fileName: String, speed: Float = 1.0f, onPlaybackComplete: () -> Unit) {
        if (isPlaying) return
        val filePath = "${context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/$fileName"
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            playbackParams = playbackParams.setSpeed(speed)
            start()
            setOnCompletionListener {
                this@AudioRecorderPlayer.isPlaying = false
                handler.removeCallbacks(updatePlaybackProgress)
                onPlaybackComplete()
            }
        }
        isPlaying = true
        handler.post(updatePlaybackProgress)
    }

    fun pausePlaying() {
        if (!isPlaying) return
        mediaPlayer?.pause()
        handler.removeCallbacks(updatePlaybackProgress)
    }

    fun resumePlaying(speed: Float) {
        if (!isPlaying) return
        mediaPlayer?.apply {
            playbackParams = playbackParams.setSpeed(speed)
            start()
        }
        mediaPlayer?.start()
        handler.post(updatePlaybackProgress)
    }

    fun stopPlaying() {
        if (!isPlaying) return
        mediaPlayer?.apply {
            stop()
            reset()
            release()
        }
        mediaPlayer = null
        isPlaying = false
        handler.removeCallbacks(updatePlaybackProgress)
    }

    fun setPlaybackSpeed(speed: Float) {
        mediaPlayer?.apply {
            if (isPlaying) {
                playbackParams = playbackParams.setSpeed(speed)
                start()
            }
        }
    }

    fun setSeekBar(seekBar: SeekBar) {
        mediaPlayer?.let { player ->
            seekBar.max = player.duration
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        player.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    fun getRecordingDuration(name: String): Long {
        return getAllRecordings().firstOrNull { it.name == name }?.duration ?: 0
    }

    fun getElapsedRecordingTime(): Long {
        return if (isRecording) {
            recordingDuration + (System.currentTimeMillis() - pauseTime)
        } else {
            recordingDuration
        }
    }

    fun getAllRecordings(): List<RecordingInfo> {
        val musicDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val recordingFiles =
            musicDir?.listFiles { _, name -> name.endsWith(".3gp") }?.toList() ?: emptyList()

        val recordingInfoList = recordingFiles.map { file ->
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.absolutePath)
            val duration =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
                    ?: 0
            retriever.release()

            RecordingInfo(
                name = file.name,
                duration = duration,
                date = file.lastModified()
            )
        }

        return recordingInfoList.sortedByDescending { it.date }
    }

    private val updatePlaybackProgress = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                val currentPosition = it.currentPosition.toLong()
                onPlaybackProgress?.invoke(currentPosition)
                handler.postDelayed(this, 500)
            }
        }
    }
}


