package com.zdykj.anim;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Gravity;

import com.zdykj.R;

public class DarwPath2Activity extends AppCompatActivity {

    // 需要在style加上
//    <!-- 允许使用transitions -->
//    <item name="android:windowContentTransitions">true</item>
//    <!--是否覆盖执行，其实可以理解成是否同步执行还是顺序执行-->
//    <item name="android:windowAllowEnterTransitionOverlap">false</item>
//    <item name="android:windowAllowReturnTransitionOverlap">false</item>
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_darw_path2);
        Slide slideTransition = new Slide(Gravity.RIGHT);
        slideTransition.setDuration(600L);
        getWindow().setEnterTransition(slideTransition);

    }
}
