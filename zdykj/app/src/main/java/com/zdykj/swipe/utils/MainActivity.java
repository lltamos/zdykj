package com.zdykj.swipe.utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.zdykj.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_activity_main);
        SwipeCardsView swipeCardsView = (SwipeCardsView) findViewById(R.id.swipCardsView);
        MeiziAdapter adapter = new MeiziAdapter(this);


        swipeCardsView.retainLastCard(true);
        swipeCardsView.enableSwipe(true);


        swipeCardsView.setCardsSlideListener(new SwipeCardsView.CardsSlideListener() {
            @Override
            public void onShow(int index) {
                Log.d("swipeCardsView", "test showing index = " + index);
            }

            @Override
            public void onCardVanish(int index, SwipeCardsView.SlideType type) {

            }

            @Override
            public void onItemClick(View cardImageView, int index) {
                Toast.makeText(MainActivity.this, "点击了 position=" + index, Toast.LENGTH_SHORT).show();
            }
        });
        swipeCardsView.setAdapter(adapter);

    }
}
