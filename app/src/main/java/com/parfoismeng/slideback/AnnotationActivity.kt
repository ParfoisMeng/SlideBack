package com.parfoismeng.slideback

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.parfoismeng.slidebacklib.annotation.SlideBackBinder
import kotlinx.android.synthetic.main.activity_annotation.*

@SlideBackBinder(haveScroll = true)
class AnnotationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annotation)

        val arr = ArrayList<String>()
        for (i in 0 until 20) {
            arr.add("--- item --- $i ---")
        }
        listView.adapter = ArrayAdapter<String>(this@AnnotationActivity, android.R.layout.simple_list_item_1, arr)
    }
}
