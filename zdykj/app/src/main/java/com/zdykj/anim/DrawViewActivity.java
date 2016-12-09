package com.zdykj.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;

import com.zdykj.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DrawViewActivity extends AppCompatActivity {

    @InjectView(R.id.but1)
    Button but1;
    @InjectView(R.id.but2)
    Button but2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_view);
        ButterKnife.inject(this);

    }

    @OnClick({R.id.but1, R.id.but2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.but1:
                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this, but1, "share");
                startActivity(new Intent(this,DarwPath2Activity.class),
                        transitionActivityOptions.toBundle());
                break;
            case R.id.but2:
                int cx = (but1.getLeft() + but1.getRight()) / 2;
                int cy = (but1.getTop() + but1.getBottom()) / 2;
                int finalRadius = but1.getWidth();
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(but1, cx, cy, finalRadius, 0);
                anim.setDuration(5000);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        but1.setVisibility(View.INVISIBLE);
                    }
                });
                anim.start();
                break;
        }
    }
}
