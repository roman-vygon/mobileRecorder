package com.rvygon.testapp
import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import android.transition.TransitionManager
import android.media.MediaPlayer

import android.util.Log
import android.widget.*


import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit



class AudioAdapter(
    private val context: Context,
    private val recordingArrayList: MutableList<Recording>
) : RecyclerView.Adapter<AudioAdapter.ViewHolder>() {
    private var mPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var lastIndex = -1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.audio_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.resetHandler()
        setUpData(holder, position)
    }

    override fun getItemCount(): Int {
        return recordingArrayList.size
    }

    private fun setUpData(holder: ViewHolder, position: Int) {

        val recording = recordingArrayList[position]
        holder.textViewName.text = recording.fileName.substringBeforeLast('.',recording.fileName)
        holder.dateText.text = recording.date

        if (recording.isPlaying) {
            holder.imageViewPlay.setImageResource(R.drawable.ic_pause)
            TransitionManager.beginDelayedTransition(holder.itemView as ViewGroup)
            holder.seekBar.visibility = View.VISIBLE
            holder.seekUpdation(holder)
        } else {
            holder.imageViewPlay.setImageResource(R.drawable.ic_play)
            TransitionManager.beginDelayedTransition(holder.itemView as ViewGroup)
            holder.seekBar.visibility = View.GONE
            holder.allTime.text = ""
            holder.playTime.text = ""
            holder.curTime = 0
        }


        holder.manageSeekBar(holder)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var imageViewPlay: ImageView = itemView.findViewById(R.id.playBtn)
        internal var seekBar: SeekBar = itemView.findViewById(R.id.seekBar)
        internal var textViewName: TextView = itemView.findViewById(R.id.audio_item_name)
        internal var allTime: TextView = itemView.findViewById(R.id.timeAll)
        internal var playTime: TextView = itemView.findViewById(R.id.playTime)
        internal var dateText: TextView = itemView.findViewById(R.id.dateText)
        internal var curTime: Long = 0
        private var recordingUri: String? = null
        private var lastProgress = 0
        private val mHandler = Handler()
        private var holder: ViewHolder? = null

        private var runnable: Runnable = Runnable { seekUpdation(holder) }

        init {
            imageViewPlay.setOnClickListener {
                val position = adapterPosition
                val recording = recordingArrayList[position]

                recordingUri = recording.uri

                if (isPlaying) {
                    stopPlaying()
                    if (position == lastIndex) {
                        recording.isPlaying = false
                        notifyItemChanged(position)
                    } else {
                        markAllPaused()
                        recording.isPlaying = true
                        notifyItemChanged(position)
                        startPlaying(recording, position)
                        lastIndex = position
                    }

                } else {
                    startPlaying(recording, position)
                    recording.isPlaying = true
                    seekBar.max = mPlayer!!.duration
                    Log.d("isPlayin", "False")
                    notifyItemChanged(position)
                    lastIndex = position
                }
            }
        }
        fun resetHandler()
        {
            mHandler.removeCallbacksAndMessages(null)
        }
        fun manageSeekBar(holder: ViewHolder) {
            holder.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (mPlayer != null && fromUser) {
                        mPlayer!!.seekTo(progress)
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })
        }

        private fun markAllPaused() {
            for (i in 0 until recordingArrayList.size) {
                recordingArrayList[i].isPlaying = false
            }
            notifyDataSetChanged()
        }

        @SuppressLint("SetTextI18n")
        fun seekUpdation(holder: ViewHolder?) {
            this.holder = holder
            if (mPlayer != null) {
                val mCurrentPosition = mPlayer!!.currentPosition
                holder?.seekBar?.max = mPlayer!!.duration
                holder?.seekBar?.progress = mCurrentPosition

                lastProgress = mCurrentPosition

                if (mPlayer!!.isPlaying) {
                    curTime += 100
                    holder?.allTime?.text = "/ " + String.format(
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(mPlayer!!.duration.toLong()), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(mPlayer!!.duration.toLong()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mPlayer!!.duration.toLong()))
                    )
                    holder?.playTime?.text = String.format(
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(curTime), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(curTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(curTime))
                    )
                }
            }
            mHandler.postDelayed(runnable, 100)
        }

        private fun stopPlaying() {
            try {
                mPlayer!!.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            curTime = 0
            try {
                mHandler.removeCallbacksAndMessages(null)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }

            holder?.playTime?.text = ""

            holder?.allTime?.text = ""
            mPlayer = null
            isPlaying = false
        }

        private fun startPlaying(audio: Recording, position: Int) {            

            Log.e("LOG_TAG", audio.fileName)
            curTime = 0
            mPlayer = MediaPlayer()

            try {
                mPlayer!!.setDataSource(recordingUri)
                mPlayer!!.prepare()
                mPlayer!!.start()
            } catch (e: IOException) {
                Log.e("LOG_TAG", "prepare() whyyyy failed")
            }


            //showing the pause button
            seekBar.max = mPlayer!!.duration
            isPlaying = true

            mPlayer!!.setOnCompletionListener {
                curTime = 0
                audio.isPlaying = false
                notifyDataSetChanged()
            }
        }
    }


}