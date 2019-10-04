package com.rvygon.testapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.provider.MediaStore
import android.text.InputType
import android.view.Menu
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

import java.io.File
import android.widget.Chronometer
import android.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private var mChronometer: Chronometer? = null
    private lateinit var deleteIcon: Drawable
    val recordingArrayList : MutableList<Recording> = arrayListOf()
    val REQUEST_AUDIO_PERMISSION_CODE = 1
    private var mRecorder: MediaRecorder? = null
    private val LOG_TAG = "AudioRecording"
    private val swipeBg: ColorDrawable = ColorDrawable(Color.parseColor("#EF2727"))
    private val recordingObj = Recorder()
    private val managerObj = Manager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        managerObj.fetchRecordings()
        initViews()
        audiofiles.requestFocus()

        deleteIcon = ContextCompat.getDrawable(this, R.drawable.delete_icon)!!
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(query: String): Boolean {
                managerObj.filterQuery = query
                managerObj.filterRecordings()
//                if (query.isEmpty())
//                {
//                    audiofiles.adapter.filter("", true)
//                }
//                else
//                {
//                    val newQuery = query.toLowerCase()
//                    audiofiles.adapter.filter(newQuery,false)
//                }
//                if (newText.isEmpty()) {
//                    audioAdapter?.recordingArrayList?.addAll(recordingArrayList)
//                }
//                else
//                {
//                    val text = newText.toLowerCase()
//                    var tempArrayList : ArrayList<Recording>? = ArrayList<Recording>(0)
//                    recordingArrayList.filterTo(tempArrayList!!) {
//                        if (it.fileName.isEmpty())
//                        {
//                            false
//                        }
//                        else
//                        {
//                            it.fileName.contains(text)
//                        }
//                    }
//                    audioAdapter?.recordingArrayList = tempArrayList
//                }
//
//                audioAdapter?.notifyDataSetChanged()
                return false

            }

            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                return false
            }

        })

        recordButton.setOnClickListener {
            prepareRecording()
        }

        pauseButton.setOnClickListener {
            animateLowerPanel(false)
            pauseButton.isEnabled = false
            recordButton.isEnabled = true
            recordingObj.stopRecording()
            makeDialog()
            mChronometer?.stop()
            mChronometer?.base = SystemClock.elapsedRealtime()
        }
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //recordingArrayList.removeAt(viewHolder.adapterPosition)
                val fileName = managerObj.removeItem(viewHolder)
                if (!managerObj.checkEmpty())
                {
                    emptyText.visibility = View.VISIBLE
                    audiofiles.visibility = View.INVISIBLE
                }

                Toast.makeText(applicationContext,"$fileName deleted", Toast.LENGTH_LONG).show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2

                swipeBg.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                deleteIcon.setBounds(itemView.right - iconMargin-deleteIcon.intrinsicWidth, itemView.top + iconMargin, itemView.right - iconMargin, itemView.bottom - iconMargin)
                swipeBg.draw(c)
                deleteIcon.draw(c)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(audiofiles)
    }

    private fun renameFileTxt (from: String, to: String) : String
    {
        if (to.isEmpty())
        {
            File(from).delete()
        }
        else
        {
            val root = Environment.getExternalStorageDirectory()
            val fFrom = File(from)
            val fTo = File(root.absolutePath + "/Phoebus/Audios/" + to +".txt")
            if (fFrom.exists())
                fFrom.renameTo(fTo)
            return root.absolutePath + "/Phoebus/Audios/" + to +".txt"
        }
        return ""
    }
    private fun renameFile (from: String, to: String) : String
    {
        if (to.isEmpty())
        {
            File(from).delete()
        }
        else
        {
            val root = Environment.getExternalStorageDirectory()
            val fFrom = File(from)
            val fTo = File(root.absolutePath + "/Phoebus/Audios/" + to +".mp3")
            if (fFrom.exists())
                fFrom.renameTo(fTo)
            return root.absolutePath + "/Phoebus/Audios/" + to +".mp3"
        }
        return ""
    }

    private fun makeDialog() {
        var mText = ""

        val builder = android.app.AlertDialog.Builder(this)//AlertDialog.Builder(this)
        builder.setTitle("Save as")

        // Set up the input
        val input = EditText(this)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT

        builder.setView(input)
        builder.setCancelable(false)

        // Set up the buttons

        Toast.makeText(applicationContext, "Recording Stopped", Toast.LENGTH_SHORT).show()
        builder.setPositiveButton("Save") { dialog, _ ->
            mText = input.text.toString()
            if (mText.isEmpty()) {
                Toast.makeText(applicationContext, "Please enter a string", Toast.LENGTH_LONG)
                    .show()
            } else {
                renameFile(recordingObj.mFileName, mText)
                renameFileTxt(recordingObj.mDateFileName, mText)
                dialog.dismiss()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
            renameFileTxt(recordingObj.mDateFileName, mText)
        }

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            var wantToCloseDialog = false
            var fullName = ""
            mText = input.text.toString()

            if (mText.isEmpty()) {
                Toast.makeText(applicationContext, "Please enter a string", Toast.LENGTH_LONG)
                    .show()
            } else {
                fullName = renameFile(recordingObj.mFileName, mText)
                renameFileTxt(recordingObj.mDateFileName, mText)
                dialog.dismiss()
                wantToCloseDialog = true
            }
            if (wantToCloseDialog) {
                dialog.dismiss()
                managerObj.addItem(fullName, mText, recordingObj.curDateAndTime)
                if (managerObj.checkEmpty())
                {
                    emptyText.visibility = View.INVISIBLE
                    audiofiles.visibility = View.VISIBLE
                }
                recordingArrayList.add(Recording(fullName, mText, false, recordingObj.curDateAndTime))
            }
        }
    }
    private fun settingsClick() {

        mainViewLayout
    }
     private fun initViews() {
         audiofiles.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
         audiofiles.setHasFixedSize(true)
         audiofiles.adapter = AudioAdapter(this, managerObj.filteredArrayList)
         managerObj.adapter = audiofiles.adapter as AudioAdapter
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bottom_bar_buttons, menu)
        return true
    }
    private fun prepareRecording()
    {
        mChronometer = recordTime
        mChronometer?.base = SystemClock.elapsedRealtime()
        if (checkPermissions()) {
            animateLowerPanel(true)
            pauseButton.isEnabled = true
            recordButton.isEnabled = false

            mChronometer?.start()

            recordingObj.startRecording()
            Toast.makeText(applicationContext, "Recording Started", Toast.LENGTH_SHORT).show()
        }
        else
        {
            requestPermissions()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun animateLowerPanel(up:Boolean) {

        if (up) {
            recordButton.visibility = View.INVISIBLE
            lowerPanel?.visibility = View.INVISIBLE
            pauseButton.visibility = View.VISIBLE
            recordTime.visibility = View.VISIBLE
            val bottomUp = AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.bottom_anim
            )
            val hiddenPanel = findViewById<ViewGroup>(R.id.pausePanel)
            hiddenPanel?.startAnimation(bottomUp)
            hiddenPanel?.visibility = View.VISIBLE
        }
        else
        {
            recordButton.visibility = View.VISIBLE
            lowerPanel?.visibility = View.VISIBLE
            pauseButton.visibility = View.INVISIBLE
            recordTime.visibility = View.INVISIBLE
            val bottomUp = AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.up_anim
            )
            val hiddenPanel = findViewById<ViewGroup>(R.id.pausePanel)
            hiddenPanel?.startAnimation(bottomUp)
            hiddenPanel?.visibility = View.INVISIBLE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_AUDIO_PERMISSION_CODE -> if (grantResults.isNotEmpty()) {
                val permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (permissionToRecord && permissionToStore) {
                    Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
    private fun checkPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val result1 = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.RECORD_AUDIO
        )
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_AUDIO_PERMISSION_CODE
        )
    }

}
