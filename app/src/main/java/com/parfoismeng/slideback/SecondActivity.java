package com.parfoismeng.slideback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
    private boolean regSlideBack = false;

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
        textView.setText("SecondActivity\nClick Me To Switch");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (regSlideBack) {
                    SlideBack.unregister(SecondActivity.this);
                } else {
                    registerSlideBack();
                }
                regSlideBack = !regSlideBack;
                showToast("SlideBack-" + regSlideBack);
            }
        });

        regSlideBack = true;
        registerSlideBack();
    }

    private void registerSlideBack() {
        SlideBack.with(this)
                .callBack(new SlideBackCallBack() {
                    @Override
                    public void onSlideBack() {
                        showToast("SlideBack-SecondActivity");
                    }
                })
                .register();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SlideBack.unregister(this);
    }

    private Toast toast;

    private void showToast(String msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
