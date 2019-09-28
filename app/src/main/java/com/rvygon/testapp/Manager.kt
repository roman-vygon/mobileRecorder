package com.rvygon.testapp

import android.os.Environment
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class Manager {
    val recordingArrayList : MutableList<Recording> = arrayListOf()
    val filteredArrayList : MutableList<Recording> = arrayListOf()
    var filterQuery: String = ""
    lateinit var adapter: AudioAdapter
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
                val recordingUri =
                    root.absolutePath + "/Phoebus/Audios/" + fileName

                val recording = Recording(recordingUri, fileName, false)
                recordingArrayList.add(recording)

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
    fun addItem(uri: String, fileName: String) {
        recordingArrayList.add(Recording(uri,fileName,false))
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