package com.example.audiorecorder

import android.content.ContentValues
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    lateinit var button: Button
    lateinit var button2: Button
    lateinit var button3: Button
    lateinit var mr: MediaRecorder
    var audiouri: Uri? = null
    var file: ParcelFileDescriptor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: 1")
        button = findViewById(R.id.button)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        Log.d(TAG, "onCreate: 2")
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 111
            )
            button.isEnabled = true
        }
        var path = getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString() + "/myrec.3gp"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Audio.Media.TITLE, "fileName")
                put(
                    MediaStore.Audio.Media.DATE_ADDED,
                    (System.currentTimeMillis() / 1000).toInt()
                )
                put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
                put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings/")
            }
            audiouri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
            file = audiouri?.let { contentResolver.openFileDescriptor(it, "rw") }
        }


        mr = MediaRecorder()
        button.isEnabled = false
        button2.isEnabled = false
        button.setOnClickListener {
            mr.setAudioSource(MediaRecorder.AudioSource.MIC)
            /*    mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)*/
            mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mr.setOutputFile(file!!.fileDescriptor)
            } else {
                mr.setOutputFile(path)
            }
//            Log.d(TAG, "onCreate: path : +$path ")
//            mr.setOutputFile(path)
            mr.prepare()
            mr.start()
            button2.isEnabled = true
            button.isEnabled = false
        }
        button2.setOnClickListener {
            mr.stop()
            button.isEnabled = true
            button2.isEnabled = false
        }
        button3.setOnClickListener {
            var mp = MediaPlayer()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.d(TAG, "onCreate: file path :- " + file!!.fileDescriptor)
                mp.setDataSource(file!!.fileDescriptor)
            } else {
                Log.d(TAG, "onCreate: path :- " + path)
                mp.setDataSource(path)
            }

            mp.prepare()
            mp.start()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            button.isEnabled = true
        }
    }
}