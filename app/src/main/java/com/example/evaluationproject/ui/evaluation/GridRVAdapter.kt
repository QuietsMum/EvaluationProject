package com.example.evaluationproject.ui.evaluation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.evaluationproject.R

internal class GridRVAdapter(
    private val foldersList: List<GridViewModal>,
    private val context: Context
) :
    BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var courseTextV: TextView
    private lateinit var courseImageV: ImageView
    override fun getCount(): Int {
        return foldersList.size
    }
    override fun getItem(position: Int): Any? {
        return null
    }
    override fun getItemId(position: Int): Long {
        return 0
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.gridview_item, null)
        }
        courseImageV = convertView!!.findViewById(R.id.id_folders_image)
        courseTextV = convertView.findViewById(R.id.id_folders_name)
        courseImageV.setImageResource(foldersList.get(position).courseImg)
        courseTextV.setText(foldersList.get(position).courseName)
        return convertView
    }
}