package com.rvygon.testapp
import android.content.Context
import android.os.Handler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import android.transition.TransitionManager
import android.media.MediaPlayer
import android.os.SystemClock
import android.util.Log
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.SeekBar

import android.widget.TextView
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class AudioAdapter(
    private val context: Context,
    private val recordingArrayList1: ArrayList<Recording>
) : RecyclerView.Adapter<AudioAdapter.ViewHolder>() {
    private var mPlayer: MediaPlayer? = null
    var recordingArrayList = recordingArrayList1
    private var isPlaying = false
    private var last_index = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(context).inflate(R.layout.audio_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        setUpData(holder, position)
    }

    override fun getItemCount(): Int {
        return recordingArrayList.size
    }


    private fun setUpData(holder: ViewHolder, position: Int) {

        val recording = recordingArrayList[position]
        holder.textViewName.text = recording.fileName


        if (recording.isPlaying) {
            //holder.imageViewPlay.setImageResource(R.drawable.ic_pause)
            TransitionManager.beginDelayedTransition(holder.itemView as ViewGroup)
            holder.seekBar.visibility = View.VISIBLE
            holder.seekUpdation(holder)
        } else {
            //holder.imageViewPlay.setImageResource(R.drawable.ic_play)
            TransitionManager.beginDelayedTransition(holder.itemView as ViewGroup)
            holder.seekBar.visibility = View.GONE
        }


        holder.manageSeekBar(holder)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var imageViewPlay: ImageView
        internal var seekBar: SeekBar
        internal var textViewName: TextView
        internal var allTime: TextView
        internal var playTime: TextView
        internal var curTime: Long = 0
        private var recordingUri: String? = null
        private var lastProgress = 0
        private val mHandler = Handler()
        internal var holder: ViewHolder? = null

        internal var runnable: Runnable = Runnable { seekUpdation(holder) }

        init {

            imageViewPlay = itemView.findViewById(R.id.playBtn)
            seekBar = itemView.findViewById(R.id.seekBar)
            playTime = itemView.findViewById(R.id.playTime)
            allTime = itemView.findViewById(R.id.timeAll)
            textViewName = itemView.findViewById(R.id.audio_item_name)
            imageViewPlay.setOnClickListener(View.OnClickListener {
                val position = adapterPosition
                val recording = recordingArrayList[position]

                recordingUri = recording.uri

                if (isPlaying) {
                    stopPlaying()
                    if (true) {
                        recording.isPlaying = false
                        stopPlaying()
                        notifyItemChanged(position)
                    } else {
                        markAllPaused()
                        recording.isPlaying = true
                        notifyItemChanged(position)
                        startPlaying(recording, position)
                        last_index = position
                    }

                } else {
                    startPlaying(recording, position)
                    recording.isPlaying = true
                    seekBar.max = mPlayer!!.duration
                    Log.d("isPlayin", "False")
                    notifyItemChanged(position)
                    last_index = position
                }
            })
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
                recordingArrayList[i] = recordingArrayList[i]
            }
            notifyDataSetChanged()
        }

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
                    );
                    holder?.playTime?.text = String.format(
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(curTime), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(curTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(curTime))
                    );
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
                stopPlaying()

                notifyItemChanged(position)
            }
        }
    }
}