package com.zdykj.swipe;

import android.view.View;



public abstract class MyLinearAdapter {
    public abstract int getCount();



    public abstract int getCardLayoutId();

    /**
     * 将卡片和数据绑定在一起
     *
     * @param position 数据在数据集中的位置
     * @param cardview 要绑定数据的卡片
     */
    public abstract void onBindData(int position, View cardview);

    /**
     * 获取可见的cardview的数目，默认是3
     *
     * @return
     */
    public int getVisibleCardCount() {
        return 10;
    }
}
