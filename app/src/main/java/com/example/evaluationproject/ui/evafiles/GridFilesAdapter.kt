package com.example.evaluationproject.ui.evafiles

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.evaluationproject.R
import com.example.evaluationproject.db.DBHelper
import com.example.evaluationproject.ui.camerax.CameraView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

const val VIEW_TYPE_FILES = 1
const val VIEW_TYPE_AUDIO = 2

internal class GridFilesAdapter (var context: Context, private val data: ArrayList<GridFilesViewModal>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var player: MediaPlayer? = null
    val db = DBHelper(context, null)

    override fun getItemViewType(position: Int): Int {
        if (data[position] is FilesItem) {
            return VIEW_TYPE_FILES
        }
        return VIEW_TYPE_AUDIO
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_FILES) {
            return FilesViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.gridfiles_item, parent, false)
            )
        }
        return AudioViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.gridaudio_item, parent, false)
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
            cardView.setOnLongClickListener {
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
        }
    }

    private fun showDialog(name: String, position: Int) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_setdate_image)
        val yesBtn = dialog.findViewById(R.id.btn_yes) as Button
        val noBtn = dialog.findViewById(R.id.btn_no) as Button
        yesBtn.setOnClickListener {
            val fileNameDate = "PNG_${System.currentTimeMillis()}"
            val fileDate = File(context.externalMediaDirs[0], fileNameDate)
            val fileOld = name.toUri().path?.let { it1 -> File(it1) }
            val bitmap = BitmapFactory.decodeFile(fileOld?.path)
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.getDefault())
            val currentDate = sdf.format(Date())
            val bitmapdate = drawTextToBitmap(bitmap, 34, currentDate)
            val stream: OutputStream = FileOutputStream(fileDate)
            bitmapdate.compress(Bitmap.CompressFormat.PNG,25,stream)
            stream.flush()
            stream.close()
            db.updateRaw(name, fileDate.toURI().toString())
            val drawable = Drawable.createFromPath(fileDate.path)
            data[position] = drawable?.let { it1 -> FilesItem(fileDate.toURI().toString(), it1) }!!
            notifyItemChanged(position)
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
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

    private fun drawTextToBitmap(bitmap: Bitmap, textSize: Int = 34, text: String): Bitmap {
        val bitmapmute = bitmap.copy(bitmap.config, true)
        val canvas = Canvas(bitmapmute)

        // new antialised Paint - empty constructor does also work
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.GREEN

        // text size in pixels
        val scale = context.resources.displayMetrics.density
        paint.textSize = (textSize * scale).roundToInt().toFloat()

        //custom fonts or a default font
//        val fontFace = ResourcesCompat.getFont(this@CameraView, R.font.roboto)
        paint.typeface = Typeface.DEFAULT


        // draw text to the Canvas center
        val bounds = Rect()
        //draw the text
        paint.getTextBounds(text, 0, text.length, bounds)

        //x and y defines the position of the text, starting in the top left corner
        canvas.drawText(text, 50F, 100F, paint)
        return bitmapmute
    }
}


