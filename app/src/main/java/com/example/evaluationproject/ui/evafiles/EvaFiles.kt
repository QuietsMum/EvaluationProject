package com.example.evaluationproject.ui.evafiles

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.evaluationproject.R
import com.example.evaluationproject.Util
import com.example.evaluationproject.db.DBHelper
import com.example.evaluationproject.ui.camerax.CameraView
import java.io.File

class EvaFiles : AppCompatActivity() {
    lateinit var fileName: String
    lateinit var comment: String
    lateinit var uri : Uri
    lateinit var drawable : Drawable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eva_files)

        val actionBar = supportActionBar
        actionBar!!.title = Util.folderNameUtil

        val gridViewfiles: RecyclerView = findViewById(R.id.grid_files)
        gridViewfiles.layoutManager = LinearLayoutManager(this)

        val db = DBHelper(this, null)
        val cursor = db.getFiles(Util.folderNameUtil)
        cursor!!.moveToFirst()
        val filesList = arrayListOf<GridFilesViewModal>()
        fileName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.FILES_COL))
        comment = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COMMENT_COL))
        if(comment.equals("image")){
            uri = Uri.parse(fileName)
            var inputStream =  this.contentResolver.openInputStream(uri)
            drawable = Drawable.createFromStream(inputStream, uri.toString())!!
            filesList.add(FilesItem(fileName, drawable))
        } else filesList.add(AudioItem(fileName, fileName))
        while(cursor.moveToNext()){
            fileName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.FILES_COL))
            comment = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COMMENT_COL))
            if(comment.equals("image")){
                uri = Uri.parse(fileName)
                var inputStream =  this.contentResolver.openInputStream(uri)
                drawable = Drawable.createFromStream(inputStream, uri.toString())!!
                filesList.add(FilesItem(fileName, drawable))
            } else filesList.add(AudioItem(fileName, fileName))
        }
        gridViewfiles.adapter = GridFilesAdapter(this, filesList)
        gridViewfiles.setHasFixedSize(true)

//        gridViewfiles.adapter.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//            Toast.makeText(
//                applicationContext, filesList[position].name + " selected",
//                Toast.LENGTH_SHORT
//            ).show()
//        }

    }
}