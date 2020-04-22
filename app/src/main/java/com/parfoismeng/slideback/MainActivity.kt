package com.parfoismeng.slideback

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parfoismeng.slidebacklib.registerSlideBack
import com.parfoismeng.slidebacklib.unregisterSlideBack
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerSlideBack {
            Toast.makeText(this, "MainActivity SlideBack", Toast.LENGTH_SHORT).show()
        }

        textView1.setOnClickListener {
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))
        }
        textView2.setOnClickListener {
            startActivity(Intent(this@MainActivity, ScrollActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterSlideBack()
    }
}