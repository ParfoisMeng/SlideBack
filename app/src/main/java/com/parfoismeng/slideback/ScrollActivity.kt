package com.parfoismeng.slideback

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Toast
import com.parfoismeng.slidebacklib.SlideBack
import kotlinx.android.synthetic.main.activity_scroll.*


class ScrollActivity : AppCompatActivity() {
    private var regSlideBack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroll)

        val arr = ArrayList<String>()
        for (i in 0 until 20) {
            arr.add("--- item --- $i ---")
        }
        listView.adapter = ArrayAdapter<String>(this@ScrollActivity, android.R.layout.simple_list_item_1, arr)

        tvScroll.text = "ScrollActivity with ListView\nClick Me To Switch"
        tvScroll.setOnClickListener {
            if (regSlideBack) {
                SlideBack.unregister(this@ScrollActivity)
            } else {
                registerSlideBack()
            }
            regSlideBack = !regSlideBack
            showToast("SlideBack-$regSlideBack")
        }

        regSlideBack = true
        registerSlideBack()
    }


    private fun registerSlideBack() {
        SlideBack.with(this)
            .haveScroll(true)
            .callBack { showToast("SlideBack-ScrollActivity") }
            .register()
    }

    override fun onDestroy() {
        super.onDestroy()
        SlideBack.unregister(this)
    }

    private var toast: Toast? = null

    private fun showToast(msg: String) {
        if (toast != null) {
            toast!!.cancel()
        }
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast?.show()
    }
}