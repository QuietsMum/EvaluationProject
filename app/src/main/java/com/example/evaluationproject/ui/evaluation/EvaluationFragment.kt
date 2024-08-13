package com.example.evaluationproject.ui.evaluation

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.evaluationproject.R
import com.example.evaluationproject.Util
import com.example.evaluationproject.databinding.FragmentEvaluationBinding
import com.example.evaluationproject.db.DBHelper
import com.example.evaluationproject.ui.camerax.CameraView
import com.example.evaluationproject.ui.evafiles.EvaFiles
import org.w3c.dom.Text

class EvaluationFragment : Fragment() {

    private var _binding: FragmentEvaluationBinding? = null

    private val binding get() = _binding!!

    lateinit var foldersGRV: GridView
    lateinit var foldersList: List<GridViewModal>
    var itemName: List<String> = arrayListOf()
    var itemNameSort: List<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEvaluationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textNoEva: TextView = root.findViewById(R.id.text_noevayet)
        textNoEva.visibility = View.INVISIBLE

        foldersGRV = root.findViewById(R.id.grid_folders)
        foldersList = ArrayList<GridViewModal>()

        val db = activity?.let { DBHelper(it, null) }

        val cursor = db?.getName()
        if((cursor != null) && cursor.moveToFirst()){
            itemName = itemName + cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.NAME_COl))

            while(cursor.moveToNext()){
                itemName = itemName + cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.NAME_COl))
            }
            itemNameSort = itemName.distinct()
            for (i in itemNameSort){
                foldersList = foldersList + GridViewModal(i, R.drawable.folder_new)
            }

            val courseAdapter = activity?.let { GridRVAdapter(foldersList = foldersList, it) }

            foldersGRV.adapter = courseAdapter

            foldersGRV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                Toast.makeText(
                    context, foldersList[position].courseName + " selected",
                    Toast.LENGTH_SHORT
                ).show()
                activity?.let {
                    val intent = Intent(it, EvaFiles::class.java)
                    Util.folderNameUtil = foldersList[position].courseName
                    it.startActivity(intent)
                }
            }
        } else textNoEva.visibility = View.VISIBLE

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}