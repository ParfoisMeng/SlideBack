package com.parfoismeng.slideback

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.parfoismeng.slidebacklib.SlideBackUtils
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

        SlideBackUtils.register(this) {
            Toast.makeText(this, "SlideBack", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SlideBackUtils.unregister(this)
    }
}