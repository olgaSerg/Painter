package com.example.painter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var drawView: DrawingView
    private lateinit var currPaint: ImageButton

    private val REQUEST_EXTERNAL_STORAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawView = findViewById(R.id.drawing)
        val paintLayout = findViewById<LinearLayout>(R.id.paint_colors)
        currPaint = paintLayout.getChildAt(0) as ImageButton
        currPaint.setImageResource(R.drawable.paint_pressed)

    }

    fun paintClicked(view: View) {
        val dw = drawView

        if (view !== currPaint) {
            val imgView = view as ImageButton
            val color = view.getTag().toString()
            dw.setColor(color)
            imgView.setImageResource(R.drawable.paint_pressed)
            currPaint.setImageResource(R.drawable.paint)
            currPaint = view
        }
    }

    fun createNew(view: View) {
        val dw = drawView
        dw.startNew()
    }

    fun doEraser(view: View) {
        val dw = drawView
        dw.setColor("#ffffff")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var granted = true
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.size > 0) {
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        granted = false
                    }
                }
            } else {
                granted = false
            }
            if (granted) {
                saveImage()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Unable to save image. Insufficient permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun saveImage() {
        val bitmapToSave = drawView.getBitmap()
        val imgSaved = MediaStore.Images.Media.insertImage(
            contentResolver, bitmapToSave,
            UUID.randomUUID().toString() + ".png", "drawing"
        )
        Toast.makeText(applicationContext, "Image saved", Toast.LENGTH_SHORT).show()
    }

    fun onSaveImageClicked(view: View) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            saveImage()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    fun onShareImageClicked(view: View?) {
        val bitmapToSave = drawView.getBitmap()
        var outputFile: File? = null
        try {
            val outputDir = this.cacheDir
            outputFile = File.createTempFile("temp_", ".jpg", outputDir)
            val fileOutputStream = FileOutputStream(outputFile)
            bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val contentUri = FileProvider.getUriForFile(
            this, "com.android.painter",
            outputFile!!
        )
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        startActivity(Intent.createChooser(shareIntent, "Share Image"))
        startActivity(shareIntent)
    }
}