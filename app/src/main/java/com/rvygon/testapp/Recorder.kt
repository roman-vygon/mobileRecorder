package com.rvygon.testapp

import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException

class Recorder (){
    private var mRecorder: MediaRecorder? = null
    var mFileName: String = ""
    fun startRecording() {
        mFileName = Environment.getExternalStorageDirectory().absolutePath
        val root = Environment.getExternalStorageDirectory()
        val file = File(root.absolutePath + "/Phoebus/Audios")
        if (!file.exists()) {
            file.mkdirs()
        }
        mFileName = root.absolutePath + "/Phoebus/Audios/temporaryFile.mp3"
        Log.d("filename", mFileName)
        mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mRecorder?.setOutputFile(mFileName)
        try {
            mRecorder?.prepare()
        } catch (e: IOException) {
            Log.e("LOG_TAG", "prepare() failed")
        }
        mRecorder?.start()
    }

    fun stopRecording() {
        mRecorder?.stop()
        mRecorder?.release()
        mRecorder = null
        }
    }
