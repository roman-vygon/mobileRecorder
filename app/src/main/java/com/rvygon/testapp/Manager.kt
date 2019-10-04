package com.rvygon.testapp

import android.os.Environment
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


class Manager {
    val recordingArrayList : MutableList<Recording> = arrayListOf()
    val filteredArrayList : MutableList<Recording> = arrayListOf()
    var filterQuery: String = ""
    lateinit var adapter: AudioAdapter
    fun readFromFile(path:String) : String {

        val file = File(path)
        var text: String

        try {
            val br = BufferedReader(FileReader(file))
            text = br.readLine()
            br.close()
        } catch (e: IOException) {
            text = "ERROR ERROR"
            print(e.stackTrace)
        }
        return text
    }
    fun fetchRecordings() : Boolean {

        val root = Environment.getExternalStorageDirectory()
        val path = root.absolutePath + "/Phoebus/Audios"
        Log.d("Files", "Path: $path")
        val directory = File(path)
        val files = directory.listFiles()

        if (files != null) {
            Log.d("Files", "Size: " + files.size)
            for (i in files.indices) {

                Log.d("Files", "FileName:" + files[i].name)
                val fileName = files[i].name
                if (".mp3" in fileName) {
                    val recordingUri =
                        root.absolutePath + "/Phoebus/Audios/" + fileName
                    val dateUri = root.absolutePath + "/Phoebus/Audios/" + fileName.substring(0,fileName.length-4)+".txt"

                    val dateTxt = readFromFile(dateUri)
                    val recording = Recording(recordingUri, fileName, false, dateTxt)
                    recordingArrayList.add(recording)
                }

            }
            filteredArrayList.addAll(recordingArrayList)
            return true //audiofiles.visibility = View.VISIBLE
        } else {
            return false //audiofiles.visibility = View.GONE
        }

    }
    fun filterRecordings() {

        filteredArrayList.clear()
        if (filterQuery.isEmpty())
            {
                filteredArrayList.addAll(recordingArrayList)
            }
        else
            {
                for (recording in recordingArrayList)
                {
                    if (recording.fileName.contains(filterQuery.toLowerCase()))
                    {
                        filteredArrayList.add(recording)
                    }
                }
            }
        adapter.notifyDataSetChanged()
    }
    fun checkEmpty() : Boolean
    {
        return (adapter.itemCount != 0)
    }
    fun addItem(uri: String, fileName: String, date: String) {
        recordingArrayList.add(Recording(uri,fileName,false, date))
        filterRecordings()
        adapter.notifyDataSetChanged()
    }
    fun removeItem(viewHolder: RecyclerView.ViewHolder) : String  {
        val name = filteredArrayList[viewHolder.adapterPosition].fileName
        File(filteredArrayList[viewHolder.adapterPosition].uri).delete()
        recordingArrayList.clear()
        filteredArrayList.clear()
        fetchRecordings()
        filterRecordings()
        adapter.notifyDataSetChanged()
        return name
    }
}