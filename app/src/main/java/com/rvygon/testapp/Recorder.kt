package com.rvygon.testapp

import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import java.io.BufferedWriter
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*


class Recorder (){
    private var mRecorder: MediaRecorder? = null
    var mFileName: String = ""
    var mDateFileName: String = ""
    var curDateAndTime: String = ""
    fun writeToFile(text: String, path:String) {
        val dataFile =
            File(path)
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile()
            } catch (e: IOException) {

                e.printStackTrace()
            }

        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            val buf = BufferedWriter(FileWriter(dataFile, false))
            buf.write(text)
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    fun startRecording() {
        mFileName = Environment.getExternalStorageDirectory().absolutePath
        val root = Environment.getExternalStorageDirectory()
        val file = File(root.absolutePath + "/Phoebus/Audios")
        if (!file.exists()) {
            file.mkdirs()
        }
        mFileName = root.absolutePath + "/Phoebus/Audios/temporaryFile.mp3"
        mDateFileName = root.absolutePath + "/Phoebus/Audios/temporaryDateFile.txt"
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")
        curDateAndTime = sdf.format(Date())
        writeToFile(curDateAndTime, mDateFileName)
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
