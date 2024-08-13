package com.example.evaluationproject.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.evaluationproject.EvaNameDialog
import com.example.evaluationproject.R
import com.example.evaluationproject.databinding.FragmentHomeBinding
import com.example.evaluationproject.db.DBHelper
import com.example.evaluationproject.ui.camerax.CameraView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val root: View = binding.root

        val buttonMakePhotos = root.findViewById(R.id.floating_action_button_photo) as FloatingActionButton
        buttonMakePhotos.setOnClickListener {
            evaNameDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun evaNameDialog() {
        EvaNameDialog().show(parentFragmentManager, "EvaNameDialog")
    }
}