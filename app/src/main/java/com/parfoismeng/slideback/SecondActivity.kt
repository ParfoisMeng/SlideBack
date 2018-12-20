package com.parfoismeng.slideback

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.parfoismeng.slidebacklib.SlideBack
import kotlinx.android.synthetic.main.activity_main.*

/**
 * author : ParfoisMeng
 * time   : 2018/12/19
 * desc   : ...
 */
class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView.text = "SecondActivity"

        SlideBack.register(this) {
            Toast.makeText(this, "SlideBack", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SlideBack.unregister(this)
    }
}