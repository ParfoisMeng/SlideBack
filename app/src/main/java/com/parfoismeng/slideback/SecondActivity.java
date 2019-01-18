package com.parfoismeng.slideback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.parfoismeng.slidebacklib.SlideBack;
import com.parfoismeng.slidebacklib.callback.SlideBackCallBack;

/**
 * author : ParfoisMeng
 * time   : 2018/12/27
 * desc   : ...
 */
public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SlideBack.register(this, new SlideBackCallBack() {
//            @Override
//            public void onSlideBack() {
//                finish();
//            }
//        });

        findViewById(R.id.textView2).setVisibility(View.GONE);
        TextView textView = findViewById(R.id.textView1);
        textView.setText("SecondActivity");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlideBack.unregister(SecondActivity.this);
            }
        });

        SlideBack.with(this) // 新 构建侧滑管理器 - 用于更丰富的自定义配置
                .haveScroll(true) // 是否包含滑动控件 默认false
                .callBack(new SlideBackCallBack() { // 回调
                    @Override
                    public void onSlideBack() {
                        finish();
                    }
                })
                .viewHeight(120) //
                .arrowSize(5)
                .maxSlideLength(20)
                .sideSlideLength(10)
                .dragRate(3)
                .register();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SlideBack.unregister(this);
    }
}
