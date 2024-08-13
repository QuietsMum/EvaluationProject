package com.example.evaluationproject.ui.preview

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.evaluationproject.R
import com.example.evaluationproject.Util
import com.example.evaluationproject.db.DBHelper
import com.example.evaluationproject.ui.camerax.CameraView
import java.io.IOException

const val VIEW_TYPE_FILES = 1
const val VIEW_TYPE_AUDIO = 2
const val VIEW_TYPE_EMPTY = 3

internal class EvaPreviewAdapter (var context: Context, private val data: ArrayList<EvaPreviewModal>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var player: MediaPlayer? = null
    val db = DBHelper(context, null)

    override fun getItemViewType(position: Int): Int {
        if (data[position] is FilesItem) {
            return VIEW_TYPE_FILES
        }
        if (data[position] is AudioItem) {
            return VIEW_TYPE_AUDIO
        }
        return VIEW_TYPE_EMPTY
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == com.example.evaluationproject.ui.evafiles.VIEW_TYPE_FILES) {
            return FilesViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.gridfiles_item, parent, false)
            )
        }
        if (viewType == com.example.evaluationproject.ui.evafiles.VIEW_TYPE_AUDIO) {
            return AudioViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.gridaudio_item, parent, false)
            )
        }
        return EmptyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.gridadd_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        if (holder is FilesViewHolder && item is FilesItem) {
            holder.bind(item)
        }
        if (holder is AudioViewHolder && item is AudioItem) {
            holder.bind(item)
        }
        if (holder is EmptyViewHolder && item is EmptyItem) {
            holder.bind(item)
        }
    }

    internal inner class FilesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var cardView : CardView
        lateinit var imageView : ImageView
        lateinit var textView: TextView
        fun bind(item: FilesItem) {
            imageView = itemView.findViewById(R.id.id_files_image)
            textView = itemView.findViewById(R.id.id_files_name)
            imageView.setImageDrawable(item.image)
            textView.text = item.name
            cardView = itemView.findViewById(R.id.filesItemView)
            cardView.setOnLongClickListener{
                showDialog(item.name, adapterPosition)
                true
            }
        }
    }

    internal inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var cardView : CardView
        lateinit var imageView : ImageView
        lateinit var textView: TextView
        var mStartPlaying = true
        fun bind(item: AudioItem) {
            textView = itemView.findViewById(R.id.id_audio_name)
            textView.text = item.name
            cardView = itemView.findViewById(R.id.audioItemView)
            cardView.setOnClickListener {
                if(mStartPlaying){
                    cardView.setCardBackgroundColor(Color.GREEN)
                } else  cardView.setCardBackgroundColor(Color.WHITE)
                onPlay(mStartPlaying, item.audio)
                mStartPlaying = !mStartPlaying
            }
            cardView.setOnLongClickListener{
                showDialog(item.name, adapterPosition)
                true
            }
        }
    }

    internal inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var cardView : CardView
        fun bind(item: EmptyItem) {
            cardView = itemView.findViewById(R.id.emptyItemView)
            cardView.setOnClickListener {
                val intent = Intent(context, CameraView::class.java)
                context.startActivity(intent)
            }
        }
    }

    private fun showDialog(name: String, position: Int) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_delete_file)
        val yesBtn = dialog.findViewById(R.id.btn_yes) as Button
        val noBtn = dialog.findViewById(R.id.btn_no) as Button
        yesBtn.setOnClickListener {
            deleteDBRaw(name, position)
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun deleteDBRaw(name: String, position: Int){
        db.deleteRaw(name)
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, data.size)
    }

    private fun onPlay(start: Boolean, fileName: String) = if (start) {
        startPlaying(fileName)
    } else {
        stopPlaying()
    }

    private fun startPlaying(fileName:String) {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("prepare", "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }
}


