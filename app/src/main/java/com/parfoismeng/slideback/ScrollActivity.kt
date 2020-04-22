package com.parfoismeng.slideback

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parfoismeng.slidebacklib.registerSlideBack
import com.parfoismeng.slidebacklib.unregisterSlideBack
import kotlinx.android.synthetic.main.activity_scroll.*


class ScrollActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroll)

        registerSlideBack {
            Toast.makeText(this@ScrollActivity, "SlideBack", Toast.LENGTH_SHORT).show()
        }

        val arr = ArrayList<String>()
        for (i in 0 until 20) {
            arr.add("--- item --- $i ---")
        }
        listView.adapter = ArrayAdapter(this@ScrollActivity, android.R.layout.simple_list_item_1, arr)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterSlideBack()
    }
}