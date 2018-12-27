package com.parfoismeng.slideback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;
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

        TextView textView = findViewById(R.id.textView);
        textView.setText("SecondActivity");

        SlideBack.register(this, new SlideBackCallBack() {
            @Override
            public void onSlideBack() {
                finish();
                Toast.makeText(SecondActivity.this, "SlideBack", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SlideBack.unregister(this);
    }
}
