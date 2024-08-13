package com.example.evaluationproject.ui.camerax

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import com.example.evaluationproject.R
import com.example.evaluationproject.Util
import com.example.evaluationproject.databinding.CameraViewBinding
import com.example.evaluationproject.db.DBHelper
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class CameraView : AppCompatActivity()  {
    private lateinit var binding: CameraViewBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    val db = DBHelper(this, null)
    private val cameraProviderResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permissionGranted->
        if(permissionGranted){
            // cut and paste the previous startCamera() call here.
            startCamera()
        }else {
            Snackbar.make(binding.root,"The camera permission is required", Snackbar.LENGTH_INDEFINITE).show()
        }
    }
    private var imageCapture: ImageCapture? = null
    private lateinit var imgCaptureExecutor: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        binding = CameraViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        startCamera()
        imgCaptureExecutor = Executors.newSingleThreadExecutor()
        binding.imgCaptureBtn.setOnClickListener{
            takePhoto()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                animateFlash()
            }
        }
    }

    private fun startCamera(){
        // listening for data from the camera
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder().build()
            // connecting a preview use case to the preview in the xml file.
            val preview = Preview.Builder().build().also{
                it.setSurfaceProvider(binding.preview.surfaceProvider)
            }
            try{
                // clear all the previous use cases first.
                cameraProvider.unbindAll()
                // binding the lifecycle of the camera to the lifecycle of the application.
                cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture)
            } catch (e: Exception) {
                Log.d(TAG, "Use case binding failed")
            }

        }, ContextCompat.getMainExecutor(this))
    }

    companion object {
        val TAG = "CameraView"
    }
    private fun takePhoto(){
        imageCapture?.let{
            val fileName = "JPEG_${System.currentTimeMillis()}"
            val file = File(externalMediaDirs[0], fileName)
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            it.takePicture(
                outputFileOptions,
                imgCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults){
                        db.addName(Util.folderNameUtil, file.toURI().toString(), "image")
                        Util.filesArraylist.add(file.toURI().toString())
                        this@CameraView.finish()
                    }

                    override fun onError(exception: ImageCaptureException) {
                       Log.d(TAG, "Error taking photo:$exception")
                    }

                })
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun animateFlash() {
        binding.root.postDelayed({
            binding.root.foreground = ColorDrawable(Color.WHITE)
            binding.root.postDelayed({
                binding.root.foreground = null
            }, 50)
        }, 100)
    }

    private fun drawTextToBitmap(bitmap: Bitmap, textSize: Int = 34, text: String): Bitmap {
        val bitmapmute = bitmap.copy(bitmap.config, true)
        val canvas = Canvas(bitmapmute)

        // new antialised Paint - empty constructor does also work
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.GREEN

        // text size in pixels
        val scale = resources.displayMetrics.density
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

//    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
//        outputStream().use { out ->
//            bitmap.compress(format, quality, out)
//            out.flush()
//        }
//    }
}