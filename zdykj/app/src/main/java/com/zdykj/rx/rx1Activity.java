package com.zdykj.rx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zdykj.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class rx1Activity extends AppCompatActivity {

    @InjectView(R.id.btn)
    Button btn;
    @InjectView(R.id.massage)
    TextView massage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Observable<String> addOb = RxBus.get()
                .register("addFeedTag", String.class);

        addOb.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {

                        massage.setText("shoudao");

                    }
                });


    }

    @OnClick({R.id.btn, R.id.massage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                startActivity(new Intent(this, rx2Activity.class));
                break;
            case R.id.massage:
                break;
        }
    }
}
