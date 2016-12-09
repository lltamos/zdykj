package com.zdykj.rx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.zdykj.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class rx2Activity extends AppCompatActivity {

    @InjectView(R.id.btn2)
    Button btn2;
    @InjectView(R.id.activity_main2)
    RelativeLayout activityMain2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rx2);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btn2)
    public void onClick() {
        RxBus.get().post("addFeedTag", "hello RxBus!");
    }
}
