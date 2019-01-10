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

        SlideBack.register(this, new SlideBackCallBack() {
            @Override
            public void onSlideBack() {
                finish();
            }
        });

        TextView textView = findViewById(R.id.textView1);
        textView.setText("SecondActivity");
        findViewById(R.id.textView2).setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SlideBack.unregister(this);
    }
}
