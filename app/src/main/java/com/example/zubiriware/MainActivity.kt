package com.example.zubiriware

import android.content.Intent
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity(), View.OnTouchListener {
    var dX: Float = 0f
    var dY: Float = 0f
    lateinit var pointer: ImageView
    lateinit var toCalc: ImageView
    lateinit var toFly: ImageView
    lateinit var toBlow: ImageView

    lateinit var botoiaCalc: Button
    lateinit var botoiaFly: Button
    lateinit var botoiaTest: Button

    lateinit var puntuFly: TextView
    lateinit var puntuCalc: TextView

    private var musika: MediaPlayer? = null
    private var puntuakFly = 0;
    private var puntuakCalc = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        pointer = findViewById(R.id.point)
        toCalc = findViewById(R.id.toCalc)
        toFly = findViewById(R.id.toFly)
        toBlow = findViewById(R.id.toBlow)
        botoiaCalc = findViewById(R.id.botoia)
         botoiaFly = findViewById(R.id.botoia2)
         botoiaTest = findViewById(R.id.botoiaTest)
        puntuFly = findViewById(R.id.scoreaFly)
        puntuCalc = findViewById(R.id.scoreaCalc)
        val extras = intent.extras
        if (extras != null) {
            puntuakFly = extras.getInt("scoreFly")
            puntuFly.text = "Puntuazioa: "+puntuakFly
            puntuakCalc = extras.getInt("scoreCalc")
            puntuCalc.text = "Puntuazioa: "+puntuakCalc

        }
        botoiaTest.setOnClickListener {
            musika?.stop()
            if (viewsOverlap(pointer,toCalc)){
                runBlocking{
                    launch{toCalc()}
                }
            }else if (viewsOverlap(pointer,toFly)){
                runBlocking{
                    launch{toFly()}
                }
            }else if (viewsOverlap(pointer,toBlow)){
                runBlocking{
                    launch{toBlow()}
                }

            }else{
                botoiaTest.text = "Ez, lehenengo gohiko argazki hau mugitu jolastu nahi dezun jokora eta klikatu nauzu berriro"
            }
        }



        pointer.setOnTouchListener(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        musika = MediaPlayer.create(this, R.raw.mike)
        musika?.start()
        musika?.isLooping = true
    }
    suspend fun toCalc(){
        botoiaTest.text = "To FastCalc!"
        delay(1000)
        val switchActivityIntent: Intent = Intent(this, FastCalc::class.java)
        switchActivityIntent.putExtra("puntuakFly",puntuakFly)
        switchActivityIntent.putExtra("puntuakCalc",puntuakCalc)
        startActivity(switchActivityIntent)
    }
    suspend fun toFly(){
        botoiaTest.text = "To FastFly!"
        delay(1000)
        val switchActivityIntent: Intent = Intent(this, FastFly::class.java)
        switchActivityIntent.putExtra("puntuakCalc",puntuakCalc)
        switchActivityIntent.putExtra("puntuakFly",puntuakFly)
        startActivity(switchActivityIntent)
    }
    suspend fun toBlow(){
        botoiaTest.text = "To FastBlow!"
        delay(1000)
        val switchActivityIntent: Intent = Intent(this, FastBlow::class.java)
        switchActivityIntent.putExtra("puntuakCalc",puntuakCalc)
        switchActivityIntent.putExtra("puntuakFly",puntuakFly)
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
