package com.example.evaluationproject.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                NAME_COl + " TEXT," +
                FILES_COL + " TEXT," +
                COMMENT_COL + " TEXT" + ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun deleteDB() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.close()
    }

    fun deleteRaw(filename: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "filename=?", arrayOf(filename))
        db.close()
    }

    fun addName(name : String, filename : String, comment : String  ){
        val values = ContentValues()
        values.put(NAME_COl, name)
        values.put(FILES_COL, filename)
        values.put(COMMENT_COL, comment)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }


    fun getName(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
    }

    fun getFiles(folderName : String): Cursor? {
        val db = this.readableDatabase
        val selectQuery = "SELECT $FILES_COL, * FROM $TABLE_NAME WHERE $NAME_COl = ?"
        return db.rawQuery(selectQuery, arrayOf(folderName))
    }

    fun updateRaw(filename: String, filenameDate: String) {
        val db = this.writableDatabase
        val values = ContentValues()
//        values.put(ID_COL, courseName)
//        values.put(NAME_COl, courseDuration)
        values.put(FILES_COL, filenameDate)
//        values.put(COMMENT_COL, courseTracks)
        db.update(TABLE_NAME, values, "filename=?", arrayOf(filename))
        db.close()
    }

    companion object{
        // below is variable for database name
        private val DATABASE_NAME = "EVALUATION_DB"
        // below is the variable for database version
        private val DATABASE_VERSION = 1
        // below is the variable for table name
        val TABLE_NAME = "eva_tables"
        // below is the variable for id column
        val ID_COL = "id"
        // below is the variable for foldername column
        val NAME_COl = "foldername"
        // below is the variable for filename column
        val FILES_COL = "filename"
        // below is the variable for filename column
        val COMMENT_COL = "eva_comment"
    }
}