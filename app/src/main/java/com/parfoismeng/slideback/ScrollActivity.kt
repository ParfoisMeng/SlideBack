package com.parfoismeng.slideback

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.parfoismeng.slidebacklib.SlideBack
import kotlinx.android.synthetic.main.activity_scroll.*


class ScrollActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroll)

        SlideBack.register(this, true) {
            finish()
        }

        val arr = ArrayList<String>()
        for (i in 0 until 20) {
            arr.add("--- item --- $i ---")
        }
        listView.adapter = ArrayAdapter<String>(this@ScrollActivity, android.R.layout.simple_list_item_1, arr)
    }

    override fun onDestroy() {
        super.onDestroy()
        SlideBack.unregister(this)
    }
}