package com.example.evaluationproject.ui.preview


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.media.MediaRecorder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.evaluationproject.R
import com.example.evaluationproject.Util
import com.example.evaluationproject.db.DBHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ImagePreview : AppCompatActivity() {

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false
    val db = DBHelper(this, null)
    var filesList = ArrayList<EvaPreviewModal>()
    lateinit var gridViewImages: RecyclerView
    lateinit var imageRec: ImageView
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)

        Util.filesArraylist = ArrayList<String>()
        val actionBar = supportActionBar
        actionBar!!.title = Util.evaNameUtil

//        db.deleteDB()

        imageRec = findViewById(R.id.image_recording)
        gridViewImages = findViewById(R.id.grid_preview)
        makeDir()
        gridViewImages.layoutManager = LinearLayoutManager(this)
        filesList.add(EmptyItem())
        gridViewImages.adapter = EvaPreviewAdapter(this, filesList)
        gridViewImages.setHasFixedSize(true)

        val buttonMakeRecord = findViewById<FloatingActionButton>(R.id.floating_action_button_microphone)
        buttonMakeRecord.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (ContextCompat.checkSelfPermission(this@ImagePreview,
                                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@ImagePreview,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            ActivityCompat.requestPermissions(this@ImagePreview, permissions,0)
                        } else startRecording()
                    }
                    MotionEvent.ACTION_UP -> {
                        stopRecording()
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

    }

    private fun startRecording() {
        output = "${externalMediaDirs[0]}" + "/${System.currentTimeMillis()}recording.mp3"
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(output)
        try {
            imageRec.visibility = View.VISIBLE
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording(){
        if(state){
            Handler(Looper.getMainLooper()).postDelayed({
                imageRec.visibility = View.INVISIBLE
                mediaRecorder?.stop()
                mediaRecorder?.release()
                mediaRecorder = null
                state = false
                var fileName = output?.toUri().toString()
                db.addName(Util.folderNameUtil, fileName, "comment")
                filesList.add(AudioItem(fileName, fileName))
                Util.filesArraylist.add(fileName)
                gridViewImages.adapter?.notifyDataSetChanged()
            }, 1000)
        }else{
            Toast.makeText(this, "You are not recording right now!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if(Util.filesArraylist.size!=0){
            var size = Util.filesArraylist.size-1
            var uri = Uri.parse(Util.filesArraylist[size])
            var inputStream =  this.contentResolver.openInputStream(uri)
            var drawable = Drawable.createFromStream(inputStream, uri.toString())
            filesList.add(FilesItem(Util.filesArraylist[size], drawable!!))
            gridViewImages.adapter?.notifyDataSetChanged()
        }
    }

    private fun makeDir(){
        val sdf = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        var myDirName = currentDate + " " + Util.evaNameUtil
        val evol = File(myDirName)
        evol.mkdirs()
        Util.folderNameUtil = myDirName
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.done_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_done ->{
                Toast.makeText(this,"Оценка отправлена",Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}