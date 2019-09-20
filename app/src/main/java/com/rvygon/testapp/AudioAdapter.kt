package com.rvygon.testapp
import android.content.Context
import android.os.Handler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.audio_row.view.*







class AudioAdapter(val recordingArrayList : ArrayList<Recording>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return recordingArrayList.size
    }
    private val mOnClickListener = getItemCount()

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //val view = LayoutInflater.from(context).inflate(R.layout.audio_row, parent, false)
        //view.setOnClickListener(mOnClickListener)
        //return ViewHolder(view)
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.audio_row, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.fileName?.text = recordingArrayList.get(position).fileName
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val fileName = view.audio_item_name
    val viewPlay = view.playBtn
    var seekBar = view.seekBar
    private val recordingUri: String? = null
    private val lastProgress = 0
    private val mHandler = Handler()
    var holder: ViewHolder? = null


}

