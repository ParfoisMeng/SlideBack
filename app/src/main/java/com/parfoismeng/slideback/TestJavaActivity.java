package com.parfoismeng.slideback;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parfoismeng.slidebacklib.SlideBack;
import com.parfoismeng.slidebacklib.SlideBackKt;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/**
 * author : ParfoisMeng
 * time   : 2020/4/23
 * desc   : ...
 */
public class TestJavaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SlideBackKt.registerSlideBack(TestJavaActivity.this, true, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return null;
            }
        }, new Function1<SlideBack, Unit>() {
            @Override
            public Unit invoke(SlideBack slideBack) {
                return null;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SlideBackKt.unregisterSlideBack(TestJavaActivity.this);
    }
}
