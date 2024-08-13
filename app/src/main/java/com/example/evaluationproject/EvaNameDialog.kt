package com.example.evaluationproject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.evaluationproject.ui.camerax.CameraView
import com.example.evaluationproject.ui.preview.ImagePreview

class EvaNameDialog: DialogFragment() {
    private lateinit var  button: Button
    private lateinit var  nameEva: EditText
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner);
        return inflater.inflate(R.layout.dialog_eva_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button = view.findViewById(R.id.btn_next_dialog)
        nameEva = view.findViewById(R.id.et_evaname)
        button.setOnClickListener {
            Util.evaNameUtil = nameEva.text.toString()
            val intent = Intent(activity, ImagePreview::class.java)
            startActivity(intent)
            dialog!!.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

}