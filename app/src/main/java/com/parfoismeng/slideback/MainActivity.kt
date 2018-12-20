package com.parfoismeng.slideback

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.parfoismeng.slidebacklib.SlideBackUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView.apply {
            text = "MainActivity"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, SecondActivity::class.java))
            }
        }

        SlideBackUtils.register(this) {
            Toast.makeText(this, "SlideBack", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SlideBackUtils.unregister(this)
    }
}