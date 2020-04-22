package com.parfoismeng.slideback;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.parfoismeng.slidebacklib.SlideBack;
import com.parfoismeng.slidebacklib.callback.SlideCallBack;

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
//                .haveScroll(true) // 是否包含滑动控件 默认false
//                .callBack(new SlideBackCallBack() { // 回调
//                    @Override
//                    public void onSlideBack() {
//                        Toast.makeText(SecondActivity.this, "SlideBack", Toast.LENGTH_SHORT).show();
//                    }
//                })
                .edgeMode(SlideBack.EDGE_BOTH)
                .callBack(new SlideCallBack() {
                    @Override
                    public void onSlide(int edgeFrom) {
                        if (edgeFrom == SlideBack.EDGE_LEFT) {
                            Toast.makeText(SecondActivity.this, "SlideBack + EDGE_LEFT", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SecondActivity.this, "SlideBack + EDGE_RIGHT", Toast.LENGTH_SHORT).show();
                        }
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
