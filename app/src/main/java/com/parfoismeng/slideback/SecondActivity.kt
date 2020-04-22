package com.parfoismeng.slideback

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parfoismeng.slidebacklib.registerSlideBack
import com.parfoismeng.slidebacklib.unregisterSlideBack

/**
 * author : ParfoisMeng
 * time   : 2018/12/27
 * desc   : ...
 */
class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.textView2).visibility = View.GONE
        val textView = findViewById<TextView>(R.id.textView1)
        textView.text = "SecondActivity"
        textView.setOnClickListener { unregisterSlideBack() }

        registerSlideBack(callBack = {
            Toast.makeText(this, "SecondActivity SlideBack", Toast.LENGTH_SHORT).show();
        }) {
            iconViewHeight = dp2px(120)
            iconViewArrowSize = dp2px(5)
            iconViewMaxLength = dp2px(20)
            sideSlideLength = dp2px(10)
            dragRate = dp2px(3)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterSlideBack()
    }
}