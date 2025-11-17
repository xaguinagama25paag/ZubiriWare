package com.example.zubiriware

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import pl.droidsonroids.gif.GifImageView
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import kotlin.math.abs

var maximoa: Double = 0.0
class FastBlow : AppCompatActivity() {

    lateinit var main : ConstraintLayout
    lateinit var audioa: AudioRecorder
    lateinit var titulo: TextView

    lateinit var hasiergif: GifImageView
    lateinit var limitea: ImageView
    lateinit var atzeratu: Button
    var score: Int = 1
    private var puntuakCalc = 0
    private var musika: MediaPlayer? = null
    private var ondo: MediaPlayer? = null
    var puntuakFly: Int =0

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fastblow)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                100
            )
        }

        val extras = intent.extras
        if (extras != null) {
            puntuakCalc = extras.getInt("puntuakCalc")
            puntuakFly = extras.getInt("puntuakFly")
        }
        main = findViewById(R.id.main)
        titulo = findViewById(R.id.titulua)
        atzeratu = findViewById(R.id.botoiak)
        hasiergif = findViewById(R.id.hasigif)
        limitea = findViewById(R.id.limit)

        ondo = MediaPlayer.create(this,R.raw.youwin)
        audioa = AudioRecorder(Handler(Looper.getMainLooper()),ondo)

        musika = MediaPlayer.create(this, R.raw.airwaves)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var i = false
atzeratu.setOnClickListener {

    if (!i){
audioa.startRecording(hasiergif,limitea)
        i=true
    }else{
        audioa.stopRecording()
        i=false
    }

}
    }

    class AudioRecorder(handler: Handler, private var ondo: MediaPlayer?) {
        var handlerra: Handler = handler

        companion object {
            private const val SAMPLE_RATE = 16000
            private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

            private var audioRecord: AudioRecord? = null
            private var isRecording = false
            private var recordingJob: Job? = null
        }

        @RequiresPermission(Manifest.permission.RECORD_AUDIO)
        fun startRecording(hasiergif: GifImageView, limitea: ImageView) {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                BUFFER_SIZE
            )
            recordingJob = CoroutineScope(Dispatchers.IO).launch {
                audioRecord?.startRecording()
                isRecording = true

                val audioBuffer = ShortArray(BUFFER_SIZE / 2)
                while (isRecording) {
                    val read = audioRecord?.read(audioBuffer, 0, audioBuffer.size) ?: 0
                    if (read > 0) {
                        analyzeAudio(audioBuffer, hasiergif,limitea)

                    }
                }
            }
        }

        fun stopRecording() {
            isRecording = false
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null

            recordingJob?.cancel()
        }

        private fun analyzeAudio(
            audioBuffer: ShortArray,
            hasiergif: GifImageView,
            limitea: ImageView
        ) {

            var sum = 0.0
            for (sample in audioBuffer) {
                sum += abs(sample.toDouble())
            }

            val avgAmplitude = sum / audioBuffer.size

            val intensity: Double = avgAmplitude / 10
            handlerra.post {
                val params = hasiergif.layoutParams as ConstraintLayout.LayoutParams
                params.bottomMargin = intensity.toInt()
                hasiergif.layoutParams = params

                if (intensity > maximoa) {
                    maximoa = intensity
                    val params2 = limitea.layoutParams as ConstraintLayout.LayoutParams
                    params2.bottomMargin = params.bottomMargin
                    limitea.layoutParams = params2
                    ondo?.start()
                }
            }
        }
    }


    var i: Int = 0

    fun cambio(){
        val switchActivityIntent = Intent(this, MainActivity::class.java)
        switchActivityIntent.putExtra("scoreFly",if (score>puntuakFly){score}else{puntuakFly})
        switchActivityIntent.putExtra("scoreCalc",puntuakCalc)

        Thread.sleep(10)
        startActivity(switchActivityIntent)
    }

}