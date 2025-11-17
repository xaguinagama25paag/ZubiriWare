package com.example.zubiriware

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.resume
import kotlin.math.max
import pl.droidsonroids.gif.GifImageView

class FastCalc : AppCompatActivity() {

    lateinit var main : ConstraintLayout
    lateinit var titulo: TextView

    lateinit var galde: TextView
    lateinit var erantzun: EditText
    lateinit var puntu: TextView
    lateinit var denbor: TextView
    lateinit var bat: Button
    lateinit var bi: Button
    lateinit var hiru: Button
    lateinit var scoregif: GifImageView
    lateinit var hasiergif: GifImageView
    lateinit var bidak: TextView
    var score: Int = 1
    var denbora: Int = 10
    var dificultad: Int = 2
    var num1: Int = 0
    var num2: Int = 0
    var erantzuna: Int = 0
    var multiplier: Int = 1
    var gameOver: Boolean = false
    var checking: Boolean = false
    var checked: Boolean = false
    var checky: Boolean = false
    var girazioa: Int = 0
    var bizitzak: Int = 3
    private var puntuakFly = 0
    private var puntuakCalc = 0


    private var musika: MediaPlayer? = null
    private var ondo: MediaPlayer? = null
    private var gaizki: MediaPlayer? = null
    val timer: Timer = Timer()
    val timer2: Timer = Timer()
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun klikatua(): Int = suspendCancellableCoroutine { cont ->
        bat.setOnClickListener {
            cont.resume(1)
        }
        bi.setOnClickListener {
            cont.resume(2)
        }
        hiru.setOnClickListener {
            cont.resume(3)
        }
    }
    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fastcalc)
        val extras = getIntent().getExtras()
        if (extras != null) {
            puntuakFly = extras.getInt("puntuakFly")
            puntuakCalc = extras.getInt("puntuakCalc")
        }
        main = findViewById(R.id.main)
        titulo = findViewById(R.id.titulua)
        galde  = findViewById(R.id.galdera)
        erantzun = findViewById(R.id.erantzuna)
        puntu = findViewById(R.id.puntuak)
        denbor = findViewById(R.id.denbo)
        bat = findViewById(R.id.bat)
        bi = findViewById(R.id.bi)
        hiru = findViewById(R.id.hiru)
        hasiergif = findViewById(R.id.hasigif)
        bidak = findViewById(R.id.lives)
        ondo = MediaPlayer.create(this,R.raw.youwin)
        gaizki = MediaPlayer.create(this,R.raw.hurt)
        musika = MediaPlayer.create(this, R.raw.catswing)
        musika?.start()
        musika?.isLooping = true
        timer.schedule(task,0,20)
        bizitzak = 3
            checky = true
            denbor.visibility = View.VISIBLE

            num1 = (0 + dificultad..10 * dificultad).random()
            num2 = (0 + dificultad..10 * dificultad).random()

            val a: Int = (0..2).random()
            erantzuna = sortzen(num1, num2, a)
            when (a) {
                0->galde.text = "$num1+$num2?"
                1->galde.text = "$num1-$num2?"
                2->galde.text = "$num1*$num2?"
            }

            galde.visibility = View.VISIBLE
            erantzun.visibility = View.VISIBLE
            puntu.visibility = View.VISIBLE
            titulo.visibility = View.INVISIBLE
             bidak.visibility = View.VISIBLE
            erantzun.setText("")
            erantzun.requestFocus()
            Thread.sleep(1)

            GlobalScope.launch(Dispatchers.Main) {
                while (!gameOver) {
                    var tempo: Int = denbora

                    while (tempo!=0){
                        tempo--
                        denbor.text = tempo.toString()
                                delay(1000)
                        }
                    if (erantzun.text.toString()==erantzuna.toString()) {
                        ondo?.start()

                        score+=dificultad*multiplier
                        puntu.text = "Puntuazioa: $score"
                        bat.visibility = View.VISIBLE
                            bi.visibility = View.VISIBLE
                            hiru.visibility = View.VISIBLE

                            bat.animate().apply {
                                setDuration(1000)
                                rotation(360F)
                            }.start()
                            bi.animate().apply {
                                setDuration(1000)
                                rotation(360F)
                            }.start()
                            hiru.animate().apply {
                                setDuration(1000)
                                rotation(360F)
                            }.start()

                        val zer : Int = klikatua()

                            bat.visibility = View.GONE
                            bi.visibility = View.GONE
                            hiru.visibility = View.GONE
                            bat.rotation = 0F
                            bi.rotation = 0F
                            hiru.rotation = 0F

                        when(zer) {
                            1 -> {
                                denbora /= 2
                                multiplier *= 2
                            }

                            3 -> {
                                dificultad *= 2
                                multiplier *= 3

                            }
                        }

                            denbora += 3
                        erantzun.requestFocus()
                        dificultad += 2

                        num1 = (0 + dificultad..10 * dificultad).random()
                        num2 = (0 + dificultad..10 * dificultad).random()
                        val a: Int = if (num1*num2<max(num1,num2)*5){
                            (0..2).random()
                        }else{
                            (0..1).random()
                        }

                        erantzuna = sortzen(num1, num2, a)
                        when (a) {
                            0->galde.text = "$num1+$num2?"
                            1->galde.text = "$num1-$num2?"
                            2->galde.text = "$num1*$num2?"
                        }
                    }else {
                        gaizki?.start()
                        if (bizitzak == 0) {

                            gameOver = true

                            puntu.visibility = View.INVISIBLE
                            erantzun.visibility = View.GONE
                            galde.visibility = View.INVISIBLE
                            denbor.text = "AMAIERA \n Puntuazio finala: $score"

                            if (!checking) {
                                timer2.schedule(task2, 0, 20)
                                checking = true
                            }

                            if (score < 10) {
                                scoregif = findViewById(R.id.scoreGifLau)
                            } else if (score < 100) {
                                scoregif = findViewById(R.id.scoreGifHiru)
                            } else if (score < 1000) {
                                scoregif = findViewById(R.id.scoreGifBat)
                            } else {
                                scoregif = findViewById(R.id.scoreGifBi)

                            }

                            checked = false
                            hasiergif.visibility = View.INVISIBLE
                            scoregif.visibility = View.VISIBLE
                            delay(3000)
                            musika?.stop()
                            cambio()
                        }else{

                            bizitzak--
                            when(bizitzak){
                                2->bidak.text = "❤❤"
                                1->bidak.text = "❤"
                                0 ->bidak.text = ""
                            }
                        }
                    }
                }
            }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    var i: Int = 0
    var check: Boolean = false
    val task = object : TimerTask() {
        override fun run() {
                if (!checky) {
                    titulo.rotation = i.toFloat()
                    if (i == 25) {
                        check = false
                    }
                    if (i == -25) {
                        check = true
                    }
                    if (check) {
                        i++
                    } else {
                        i--
                    }
                }else if (girazioa!=0){
                    galde.rotation += girazioa
                }
        }
    }
    val task2 = object : TimerTask() {
        override fun run() {
            if (!checked) {
                denbor.rotation = i.toFloat()
                if (i == 25) {
                    check = false
                }
                if (i == -25) {
                    check = true
                }
                if (check) {
                    i++
                } else {
                    i--
                }
            }
        }
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
    fun cambio(){

            val switchActivityIntent = Intent(this, MainActivity::class.java)
        switchActivityIntent.putExtra("scoreCalc",if (score>puntuakCalc){score}else{puntuakCalc})
        switchActivityIntent.putExtra("scoreFly",puntuakFly);
        startActivity(switchActivityIntent)
    }


}