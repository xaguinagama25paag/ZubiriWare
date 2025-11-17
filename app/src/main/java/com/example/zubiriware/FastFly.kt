package com.example.zubiriware

import android.content.Intent
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import pl.droidsonroids.gif.GifImageView
import java.util.Timer
import java.util.TimerTask

class FastFly : AppCompatActivity(), View.OnTouchListener {

    lateinit var main : ConstraintLayout
    var dX: Float = 0f
    var dY: Float = 0f
    lateinit var titulo: TextView

    lateinit var scoretext: TextView
    lateinit var mugitu: TextView
    lateinit var hasiergif: GifImageView
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
    val timer: Timer = Timer()
    val timer2: Timer = Timer()
    var puntuakFly: Int =0

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fastfly)
        val extras = getIntent().getExtras()
        if (extras != null) {
            puntuakCalc = extras.getInt("puntuakCalc")
            puntuakFly = extras.getInt("puntuakFly")
        }
        main = findViewById(R.id.main)
        titulo = findViewById(R.id.titulua)
        hasiergif = findViewById(R.id.hasigif)
        scoretext = findViewById(R.id.score)
        muroa1 = findViewById(R.id.muro1)
        muroa2 = findViewById(R.id.muro2)
        muroa3 = findViewById(R.id.muro3)
        muroa4 = findViewById(R.id.muro4)
        mugitu = findViewById(R.id.mugi)

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

        ondo = MediaPlayer.create(this,R.raw.youwin)
        musika = MediaPlayer.create(this, R.raw.airwaves)
        hasiergif.setOnTouchListener(this)
        musika?.start()
        musika?.isLooping = true

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        timer.schedule(task,0,20)
        timer2.schedule(task2, 0, 20)

    }

    var i: Int = 0
    val task = object : TimerTask() {
        override fun run() {

            if (viewsOverlap(muroa1,hasiergif)||viewsOverlap(muroa2,hasiergif)||viewsOverlap(muroa3,hasiergif)||viewsOverlap(muroa4,hasiergif)){
                kontadorea++
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
                        hasiergif.layoutParams = params
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
                            ondo?.start()
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
                                ondo?.start()
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
                            ondo?.start()
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
                            ondo?.start()
                            scoretext.text = score.toString()
                            rando = (1..4).random()
                        }
                        muroa4.layoutParams = params
                    }

                }
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

    public override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.getAction()) {
            MotionEvent.ACTION_DOWN -> {
                dX = view.getX() - event.getRawX()
                dY = view.getY() - event.getRawY()
            }

            MotionEvent.ACTION_MOVE -> view.animate()
                .x(event.getRawX() + dX)
                .y(event.getRawY() + dY)
                .setDuration(0)
                .start()

            else -> return false
        }
        return true
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