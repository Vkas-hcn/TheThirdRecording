package com.orange.bamboo.knot.insect.burning.thethirdrecording.show.four

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.orange.bamboo.knot.insect.burning.thethirdrecording.R
import com.orange.bamboo.knot.insect.burning.thethirdrecording.data.RecordingInfo
import com.orange.bamboo.knot.insect.burning.thethirdrecording.data.utils.AudioRecorderPlayer
import com.orange.bamboo.knot.insect.burning.thethirdrecording.data.utils.UtilsRecord
import com.orange.bamboo.knot.insect.burning.thethirdrecording.databinding.ActivityHistoryBinding
import com.orange.bamboo.knot.insect.burning.thethirdrecording.databinding.ActivityRecordBinding
import com.orange.bamboo.knot.insect.burning.thethirdrecording.show.five.PlayActivity
import com.orange.bamboo.knot.insect.burning.thethirdrecording.zore.BaseActivity

class HistoryActivity : BaseActivity<ActivityHistoryBinding, HistoryViewModel>() {
    override val layoutId: Int = R.layout.activity_history
    override val viewModelClass: Class<HistoryViewModel> = HistoryViewModel::class.java
    private lateinit var audioRecorderPlayer: AudioRecorderPlayer
    private lateinit var adapter: HistoryAdapter
    private lateinit var listRecordingInfo: List<RecordingInfo>
    private var clickPos: Int = 0
    override fun initViewComponents() {
        audioRecorderPlayer = AudioRecorderPlayer(this)
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.llEdit.setOnClickListener {}

        binding.tvSave.setOnClickListener {
            binding.edName.text.apply {
                if (this.toString().isBlank()) {
                    Toast.makeText(
                        this@HistoryActivity,
                        "Please enter a file name",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                val name = listRecordingInfo[clickPos].name
                val result = audioRecorderPlayer.renameRecording(name, this.trim().toString())
                if (result.contains("Failed") || result.contains("exists")) {
                    Toast.makeText(this@HistoryActivity, "File renamed fail!", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        this@HistoryActivity,
                        "File renamed successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                listRecordingInfo = audioRecorderPlayer.getAllRecordings()
                adapter.setData(listRecordingInfo)
                cloneDialog()
            }
        }
        binding.tvCancel.setOnClickListener {
            cloneDialog()
        }
    }

    override fun initializeData() {
        initAdapter()
    }

    private fun initAdapter() {
        listRecordingInfo = audioRecorderPlayer.getAllRecordings()
        binding.haveList = listRecordingInfo.isNotEmpty()
        adapter = HistoryAdapter(listRecordingInfo)
        binding.rvRecord.layoutManager = LinearLayoutManager(this)
        binding.rvRecord.adapter = adapter
        adapter.setOnItemClickListener(object : HistoryAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val params: Pair<String, Any>
                params = Pair("recordingPos", position)
                navigateTo<PlayActivity>(params)
            }

            override fun onEditClick(position: Int) {
                openDialog()
                clickPos = position
            }

            override fun onDeleteClick(position: Int) {
                showDeleteDialog {
                    audioRecorderPlayer.deleteRecording(listRecordingInfo[position].name)
                    listRecordingInfo = audioRecorderPlayer.getAllRecordings()
                    binding.haveList = listRecordingInfo.isNotEmpty()
                    adapter.setData(listRecordingInfo)
                }
            }
        })
    }

    fun openDialog() {
        binding.llEdit.visibility = View.VISIBLE
        UtilsRecord.showKeyboard(this, binding.edName, true)
    }

    private fun cloneDialog() {
        binding.llEdit.visibility = View.GONE
        UtilsRecord.showKeyboard(this, binding.edName, false)
    }

    private fun showDeleteDialog(deleteFun: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Tip")
            .setMessage("Deletion is irreversible. Are you sure you want to proceed with the deletion?")
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Confirm") { dialog, _ ->
                deleteFun()
                dialog.dismiss()
            }
            .create()
            .show()
    }
}