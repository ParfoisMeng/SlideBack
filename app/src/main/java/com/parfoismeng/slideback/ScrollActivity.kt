package com.parfoismeng.slideback

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Toast
import com.parfoismeng.slidebacklib.SlideBack
import com.parfoismeng.slidebacklib.callback.SlideCallBack
import kotlinx.android.synthetic.main.activity_scroll.*


class ScrollActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroll)

//        SlideBack.register(this, true) {
//            finish()
//        }
        SlideBack.with(this) // 新 构建侧滑管理器 - 用于更丰富的自定义配置
            .edgeMode(SlideBack.EDGE_BOTH)
            .haveScroll(true)
            .callBack(object : SlideCallBack() {
                override fun onSlide(edgeFrom: Int) {
                    if (edgeFrom == SlideBack.EDGE_LEFT) {
                        Toast.makeText(this@ScrollActivity, "SlideBack + EDGE_LEFT", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ScrollActivity, "SlideBack + EDGE_RIGHT", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            .register()

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