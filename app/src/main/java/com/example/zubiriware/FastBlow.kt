package com.example.zubiriware

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.runBlocking
import pl.droidsonroids.gif.GifImageView
import java.util.Timer
import java.util.TimerTask
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.util.Log
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

    lateinit var berri: Button
    lateinit var scoretext: TextView
    lateinit var mugitu: TextView
    lateinit var hasiergif: GifImageView
    lateinit var limitea: ImageView
    lateinit var atzeratu: Button
    lateinit var muroa1: ImageView
    lateinit var muroa2: ImageView
    lateinit var muroa3: ImageView
    lateinit var muroa4: ImageView
    var score: Int = 1

    var speed: Int = 0
    var ou: Boolean = true
    var rando: Int = 1
    var kontadorea: Int = 0
    private var puntuakCalc = 0
    private var musika: MediaPlayer? = null
    private var ondo: MediaPlayer? = null
    private var gaizki: MediaPlayer? = null
    val timer: Timer = Timer()
    val timer2: Timer = Timer()
    var puntuakFly: Int =0

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fastblow)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                100
            )
        }

        val extras = getIntent().getExtras()
        if (extras != null) {
            puntuakCalc = extras.getInt("puntuakCalc")
            puntuakFly = extras.getInt("puntuakFly")
        }
        main = findViewById(R.id.main)
        titulo = findViewById(R.id.titulua)
        berri = findViewById(R.id.berriro)
        atzeratu = findViewById(R.id.botoiak)
        hasiergif = findViewById(R.id.hasigif)
        scoretext = findViewById(R.id.score)
        limitea = findViewById(R.id.limit)
        muroa1 = findViewById(R.id.muro1)
        muroa2 = findViewById(R.id.muro2)
        muroa3 = findViewById(R.id.muro3)
        muroa4 = findViewById(R.id.muro4)
        mugitu = findViewById(R.id.mugi)
        ondo = MediaPlayer.create(this,R.raw.youwin)
        audioa = AudioRecorder(Handler(Looper.getMainLooper()),ondo)
        var params = muroa1.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin += i

            params.topMargin = -500
        muroa1.layoutParams = params
         params = muroa2.layoutParams as ConstraintLayout.LayoutParams
        params.bottomMargin += i
            params.bottomMargin = -500
        muroa2.layoutParams = params
         params = muroa3.layoutParams as ConstraintLayout.LayoutParams
        params.leftMargin += i
            params.leftMargin = -500
        muroa3.layoutParams = params
         params = muroa4.layoutParams as ConstraintLayout.LayoutParams
        params.rightMargin += i
            params.rightMargin = -500
        muroa4.layoutParams = params
        gaizki = MediaPlayer.create(this,R.raw.hurt)
        musika = MediaPlayer.create(this, R.raw.airwaves)
        //musika?.start()
       // musika?.isLooping = true

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //timer.schedule(task,0,20)
        //timer2.schedule(task2, 0, 20)
        var i: Boolean = false
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

    class AudioRecorder(handler: Handler, ondo: MediaPlayer?) {
        var handlerra: Handler = handler
        private var ondo: MediaPlayer? = ondo

        companion object {
            private const val SAMPLE_RATE = 16000 // 16 kHz sample rate
            private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

            private var audioRecord: AudioRecord? = null
            private var isRecording = false
            private var recordingJob: Job? = null
        }

        // Start recording using Kotlin Coroutines
        @RequiresPermission(Manifest.permission.RECORD_AUDIO)
        fun startRecording(hasiergif: GifImageView, limitea: ImageView) {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                BUFFER_SIZE
            )
            // Start recording in a background coroutine
            recordingJob = CoroutineScope(Dispatchers.IO).launch {
                audioRecord?.startRecording()
                isRecording = true

                val audioBuffer = ShortArray(BUFFER_SIZE / 2)
                while (isRecording) {
                    val read = audioRecord?.read(audioBuffer, 0, audioBuffer.size) ?: 0
                    if (read > 0) {
                        // Process the audio data (e.g., detect blow intensity)
                        analyzeAudio(audioBuffer, hasiergif,limitea)

                    }
                }
            }
        }

        // Stop recording
        fun stopRecording() {
            isRecording = false
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null

            // Cancel the coroutine
            recordingJob?.cancel()
        }

        // Analyze audio data
        private fun analyzeAudio(
            audioBuffer: ShortArray,
            hasiergif: GifImageView,
            limitea: ImageView
        ) {

            var sum = 0.0
            for (sample in audioBuffer) {
                sum += abs(sample.toDouble())
            }

            // Calculate average amplitude (intensity of the sound)
            val avgAmplitude = sum / audioBuffer.size

            // Define a threshold value to detect blowing intensity
            val intensity: Double = avgAmplitude / 10 // Adjust divisor to scale intensity
            Log.d("AudioIntensity", "Intensity: $intensity")

            handlerra.post {
                val params = hasiergif.layoutParams as ConstraintLayout.LayoutParams
                params.bottomMargin = intensity.toInt()
                hasiergif.layoutParams = params

                if (intensity > maximoa) {
                    maximoa = intensity
                    Log.d("BlowDetected", "Blowing detected!")
                    val params2 = limitea.layoutParams as ConstraintLayout.LayoutParams
                    params2.bottomMargin = params.bottomMargin
                    limitea.layoutParams = params2
                    ondo?.start()
                }
            }
        }
    }


    var i: Int = 0
    val task = object : TimerTask() {
        override fun run() {

          //  muroa1.rotation = (0..359).random().toFloat()
            if (viewsOverlap(muroa1,hasiergif)||viewsOverlap(muroa2,hasiergif)||viewsOverlap(muroa3,hasiergif)||viewsOverlap(muroa4,hasiergif)){
                kontadorea++;
                if (kontadorea==2) {
                    musika?.stop()
                    hasiergif.setImageDrawable(getResources().getDrawable(R.drawable.lose, getApplicationContext().getTheme()));
                    muroa1.visibility = View.GONE
                    muroa2.visibility = View.GONE
                    muroa3.visibility = View.GONE
                    muroa4.visibility = View.GONE
                    runOnUiThread {
                        val params = hasiergif.layoutParams as ConstraintLayout.LayoutParams
                            params.topMargin = 800
                        params.leftMargin = 800
                        muroa1.layoutParams = params
                    }
                    hasiergif.setOnTouchListener(null);
                    Thread.sleep(4000)
                    cambio()
                }
          }else{
              kontadorea=0
          }

        }
    }
    val task2 = object : TimerTask() {
        override fun run() {
            if (ou){
                    Thread.sleep(2000)
                    mugitu.visibility = View.INVISIBLE
                    ou=false

            }

            var i: Int = 10+speed
            var go: Boolean = true

            runOnUiThread {
                when(rando){
                    1->{
                            val params = muroa1.layoutParams as ConstraintLayout.LayoutParams
                            params.topMargin += i
                        if (params.topMargin >2200){
                                params.topMargin = -500
                                speed++
                                go=false
                            score +=i
                            scoretext.text = score.toString()
                            rando = (1..4).random()
                        }
                            muroa1.layoutParams = params
                    }
                    2->{

                            val params = muroa2.layoutParams as ConstraintLayout.LayoutParams
                            params.bottomMargin += i
                            if (params.bottomMargin >2200){
                                params.bottomMargin = -500
                                speed++
                                go=false
                                score +=i
                                scoretext.text = score.toString()
                                rando = (1..4).random()
                            }
                            muroa2.layoutParams = params
                        }
                    3->{
                        val params = muroa3.layoutParams as ConstraintLayout.LayoutParams
                        params.leftMargin += i-5
                        if (params.leftMargin >1500){
                            params.leftMargin = -500
                            speed++
                            go=false
                            score +=i
                            scoretext.text = score.toString()
                            rando = (1..4).random()
                        }
                        muroa3.layoutParams = params
                    }
                    4->{
                        val params = muroa4.layoutParams as ConstraintLayout.LayoutParams
                        params.rightMargin += i-5
                        if (params.rightMargin >1500){
                            params.rightMargin = -500
                            speed++
                            go=false
                            score +=i
                            scoretext.text = score.toString()
                            rando = (1..4).random()
                        }
                        muroa4.layoutParams = params
                    }

                }
                /* val params2 = muroa2.layoutParams as ConstraintLayout.LayoutParams
                 params2.topMargin += i-10
                 if (params2.topMargin >3500){
                     params2.topMargin = -200
                     speed++
                 }

                 muroa2.layoutParams = params2*/
            }
            }
        }
    fun cambio(){
        val switchActivityIntent = Intent(this, MainActivity::class.java)
        switchActivityIntent.putExtra("scoreFly",if (score>puntuakFly){score}else{puntuakFly})
        switchActivityIntent.putExtra("scoreCalc",puntuakCalc)

        Thread.sleep(10)
        startActivity(switchActivityIntent)
    }

    fun sortzen(zenbakia1: Int, zenbakia2: Int, zein: Int): Int = runBlocking {
        var a = 0
        when (zein) {
            0 -> a = zenbakia1 + zenbakia2
            1 -> a = zenbakia1 - zenbakia2
            2 -> a = zenbakia1 * zenbakia2
        }
        a

    }

    private fun viewsOverlap(v1: View, v2: View): Boolean {
        val v1_coords = IntArray(2)
        v1.getLocationOnScreen(v1_coords)
        val v1_w = v1.getWidth()
        val v1_h = v1.getHeight()
        val v1_rect = Rect(v1_coords[0], v1_coords[1], v1_coords[0] + v1_w, v1_coords[1] + v1_h)

        val v2_coords = IntArray(2)
        v2.getLocationOnScreen(v2_coords)
        val v2_w = v2.getWidth()
        val v2_h = v2.getHeight()
        val v2_rect = Rect(v2_coords[0], v2_coords[1], v2_coords[0] + v2_w, v2_coords[1] + v2_h)

        return v1_rect.intersect(v2_rect) || v1_rect.contains(v2_rect) || v2_rect.contains(v1_rect)
    }

}