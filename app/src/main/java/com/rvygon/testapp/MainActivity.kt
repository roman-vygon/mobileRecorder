package com.rvygon.testapp

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
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
import android.R.string.cancel
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {
    val animals: ArrayList<String> = ArrayList()
    val REQUEST_AUDIO_PERMISSION_CODE = 1
    private var mRecorder: MediaRecorder? = null
    private val LOG_TAG = "AudioRecording"
    private var mFileName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addAnimals()
        audiofiles.layoutManager = LinearLayoutManager(this)
        audiofiles.adapter = AudioAdapter(animals, this)
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/AudioRecording.3gp";
        recordButton.setOnClickListener(View.OnClickListener {
            if (CheckPermissions()) {

                pauseButton.setEnabled(true)
                recordButton.setEnabled(false)
                animateLowerPanel()
                mRecorder = MediaRecorder()
                mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                mRecorder?.setOutputFile(mFileName)
                try {
                    mRecorder?.prepare()
                } catch (e: IOException) {
                    Log.e(LOG_TAG, "prepare() failed")
                }

                mRecorder?.start()
                Toast.makeText(applicationContext, "Recording Started", Toast.LENGTH_LONG).show()
            } else {
                RequestPermissions()
            }
        })

        pauseButton.setOnClickListener(View.OnClickListener {
            pauseButton.setEnabled(false)
            recordButton.setEnabled(true)
            var m_Text = ""
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Save as")

// Set up the input
            val input = EditText(this)
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

// Set up the buttons
            builder.setPositiveButton("Save",
                DialogInterface.OnClickListener { dialog, which -> m_Text = input.text.toString() })
            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

            builder.show()

            mRecorder?.stop()
            mRecorder?.release()
            mRecorder = null
            Toast.makeText(applicationContext, "Recording Stopped", Toast.LENGTH_LONG).show()
        })

    }
    fun animateLowerPanel() {
        recordButton.visibility = View.INVISIBLE
        val bottomUp = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.bottom_anim
        )
        val hiddenPanel = findViewById(R.id.pausePanel) as ViewGroup
        hiddenPanel.startAnimation(bottomUp)
        hiddenPanel.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_AUDIO_PERMISSION_CODE -> if (grantResults.size > 0) {
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
    fun CheckPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val result1 = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.RECORD_AUDIO
        )
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun RequestPermissions() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_AUDIO_PERMISSION_CODE
        )
    }
    fun addAnimals() {
        animals.add("dog")
        animals.add("cat")
        animals.add("owl")
        animals.add("cheetah")
        animals.add("raccoon")
        animals.add("bird")
        animals.add("snake")
        animals.add("lizard")
        animals.add("hamster")
        animals.add("bear")
        animals.add("lion")
        animals.add("tiger")
        animals.add("horse")
        animals.add("frog")
        animals.add("fish")
        animals.add("shark")
        animals.add("turtle")
        animals.add("elephant")
        animals.add("cow")
        animals.add("beaver")
        animals.add("bison")
        animals.add("porcupine")
        animals.add("rat")
        animals.add("mouse")
        animals.add("goose")
        animals.add("deer")
        animals.add("fox")
        animals.add("moose")
        animals.add("buffalo")
        animals.add("monkey")
        animals.add("penguin")
        animals.add("parrot")
    }
}
