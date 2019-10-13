package com.parfoismeng.slideback

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.parfoismeng.slidebacklib.SlideBack
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SlideBack.register(this) {
            Toast.makeText(this, "SlideBack", Toast.LENGTH_SHORT).show()
        }

        textView1.setOnClickListener {
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))
        }
        textView2.setOnClickListener {
            startActivity(Intent(this@MainActivity, ScrollActivity::class.java))
        }
        textView3.setOnClickListener {
            startActivity(Intent(this@MainActivity, AnnotationActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SlideBack.unregister(this)
    }
}