package com.zdykj.swipe.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zdykj.R;

import java.util.ArrayList;
import java.util.List;



public class MeiziAdapter extends BaseCardAdapter {
    private List<String> datas;
    private Context context;

    public MeiziAdapter(Context context) {
        datas = new ArrayList<>();
        datas.add("http://i.meizitu.net/2016/09/25b01.jpg");
        datas.add("http://i.meizitu.net/2016/09/25b02.jpg");
        datas.add("http://i.meizitu.net/2016/09/25b03.jpg");
        datas.add("http://i.meizitu.net/2016/09/25b04.jpg");
        datas.add("http://i.meizitu.net/2016/09/25b05.jpg");
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getCardLayoutId() {
        return R.layout.card_item;
    }

    @Override
    public void onBindData(int position, View cardview) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        ImageView imageView = (ImageView) cardview.findViewById(R.id.iv_meizi);
        String url = datas.get(position);
        Picasso.with(context).load(url).config(Bitmap.Config.RGB_565).into(imageView);
    }

    /**
     * 如果可见的卡片数是3，则可以不用实现这个方法
     *
     * @return
     */
    @Override
    public int getVisibleCardCount() {
        return super.getVisibleCardCount();
    }
}