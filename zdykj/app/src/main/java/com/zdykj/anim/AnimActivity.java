package com.zdykj.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.zdykj.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AnimActivity extends AppCompatActivity {

    @InjectView(R.id.textView2)
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim);
        ButterKnife.inject(this);
        textView2.setVisibility(View.VISIBLE);

    }

    @OnClick({R.id.button1, R.id.button6, R.id.button7})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:

                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1f, 0, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(1000);
                scaleAnimation.setRepeatMode(Animation.REVERSE);
                scaleAnimation.setRepeatCount(3);

                scaleAnimation.setInterpolator(new AccelerateInterpolator());
                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        textView2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        textView2.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                textView2.startAnimation(scaleAnimation);


                break;
            case R.id.button6:
                //水平
                ObjectAnimator animator = ObjectAnimator.ofFloat(textView2, "width", 0f, 500f).setDuration(2000);
                animator.setInterpolator(new AccelerateInterpolator());

                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        textView2.setVisibility(View.VISIBLE);
                    }
                });

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {

                        float cVal = (Float) animation.getAnimatedValue();
                        ViewGroup.LayoutParams p = textView2.getLayoutParams();
                        p.width = (int) cVal;
                        p.height = (int) cVal;
                        textView2.setLayoutParams(p);
                        textView2.setAllCaps(true);

                    }
                });
                animator.start();


                break;
            case R.id.button7:
                textView2.setAlpha(0);

                textView2.animate().alpha(1).setDuration(2000).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        textView2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        textView2.setVisibility(View.INVISIBLE);
                    }
                }).setInterpolator(new AccelerateInterpolator()).start();

                break;
        }
    }

    @OnClick(R.id.button)
    public void onClick() {

        AnimatorSet set=new AnimatorSet();
        ObjectAnimator obj =ObjectAnimator.ofFloat(textView2,"Alpha",1,1);
        ObjectAnimator obj1 =ObjectAnimator.ofFloat(textView2,"TranslationY",0,-400);
        set.setDuration(2000).setInterpolator(new AccelerateInterpolator());

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

            }
        });
        set.playTogether(obj,obj1);
        set.start();

    }
}
