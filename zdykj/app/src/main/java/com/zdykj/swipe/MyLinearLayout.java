package com.zdykj.swipe;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.zdykj.R;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Litao-pc on 2016/9/26.
 */


public class MyLinearLayout extends LinearLayout {


    private final String TAG = getClass().getSimpleName();
    // 存放的是每一层的view，从顶到底
    private ArrayList<View> viewList = new ArrayList<>();
    // 手指松开后，保存的view列表
    private ArrayList<View> releasedViewList = new ArrayList<>();
    // 正常状态下 topView的left和top
    private int initLeft, initTop = 0;
    // swipeCardsView的宽度
    private int mWidth = 0;
    // swipeCardsView的高度
    private int mHeight = 0;
    // 每一个子View对应的宽度
    private int mCardWidth = 0;

    private MyLinearAdapter mAdapter;

    //当前卡片显示位置
    private int mShowingIndex;

    // 卡片的数量
    private int mCount;

    // view叠加垂直偏移量的步长
    private int yOffsetStep = 0;

    // view叠加缩放的步长
    private float scaleOffsetStep = 0f;

    //view叠加透明度的步长
    private int alphaOffsetStep = 0;

    private OnCardsListener mCardsListener;

    private OnClickListener mClickListener;

    private Scroller mScroller;

    private int mTouchSlop;

    private int mMaxVelocity;

    private int mMinVelocity;

    private VelocityTracker mVelocityTracker;
    private int mLastY = -1; // save event y
    private int mLastX = -1; // save event x
    private int mInitialMotionY;
    private int mInitialMotionX;

    private boolean mWaitRefresh = false;
    /**
     * 卡片是否在移动的标记，如果在移动中则不执行onLayout中的layout操作
     */
    private boolean mScrolling = false;
    //是否可以滑动
    private boolean mEnableSwipe = true;

    private boolean isTouching;
    private boolean hasTouchTopView;


    private boolean mRetainLastCard = false;
    private int deltaY;
    private int deltaX;
    private static final int MAX_SLIDE_DISTANCE_LINKAGE = 400; // 水平距离+垂直距离

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);


        Observable.create(new Observable.OnSubscribe<View>() {
            @Override
            public void call(Subscriber<? super View> subscriber) {
                subscriber.onCompleted();
            }
        });
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipCardsView);
        yOffsetStep = (int) a.getDimension(R.styleable.SwipCardsView_yOffsetStep, yOffsetStep);
        alphaOffsetStep = a.getInt(R.styleable.SwipCardsView_alphaOffsetStep, alphaOffsetStep);
        scaleOffsetStep = a.getFloat(R.styleable.SwipCardsView_scaleOffsetStep, scaleOffsetStep);

        a.recycle();
        mClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCardsListener != null) {
                    mCardsListener.onItemClick(mShowingIndex);
                }
            }
        };

        //设置滑动参考

        mScroller = new Scroller(getContext());
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

    }

    public MyLinearLayout(Context context) {
        super(context);
    }

    private MotionEvent mLastMoveEvent;
    private boolean mHasSendCancelEvent = false;

    private boolean isIntercepted = true;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int active = ev.getActionMasked();
        addVelocityTracker(ev);

        switch (active) {

            case MotionEvent.ACTION_DOWN:
                //停止动画
                mScroller.abortAnimation();
                //对view重新排序
                resetViewGroup();
                //判断是否点再触摸在view上
                if (isTouchTopView(ev) && canMoveCard() && mEnableSwipe) {
                    isTouching = true;
                }

                hasTouchTopView = false;

                mLastX = (int) ev.getRawX();
                mLastY = (int) ev.getRawY();
                mInitialMotionY = mLastY;
                mInitialMotionX = mLastX;
                break;
            case MotionEvent.ACTION_MOVE:
                Logs("ACTION_MOVE-");
                //                是否摸到了某个view
                if (isTouchTopView(ev) && canMoveCard() && mEnableSwipe) {
                    isTouching = true;
                }
                mLastMoveEvent = ev;
                int currentY = (int) ev.getRawY();
                int currentX = (int) ev.getRawX();
                deltaY = currentY - mLastY;
                deltaX = currentX - mLastX;
                mLastY = currentY;
                mLastX = currentX;




                if (isIntercepted && (hasTouchTopView || isTouchTopView(ev))) {
                    hasTouchTopView = true;
                    moveTopView(deltaX, deltaY);
//                    sendCancelEvent();

                    return true;
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Logs("ACTION_UP-");
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    //移动最上面的view
    private void moveTopView(int deltaX, int deltaY) {
        View topView = viewList.get(0);
        if (topView != null) {
            topView.offsetLeftAndRight(deltaX);
            topView.offsetTopAndBottom(deltaY);

            /**
             * 顶层卡片View位置改变，底层的位置需要调整
             * 改变的距离
             */


            int topViewTop = topView.getTop();
            int topViewLeft = topView.getLeft();
            int distance = Math.abs(topViewTop - initTop) + Math.abs(topViewLeft - initLeft);
            float rate = distance / (float) MAX_SLIDE_DISTANCE_LINKAGE;
            Logs("rate=" + rate);
            for (int i = 1; i < viewList.size(); i++) {
                float rate3 = rate - 0.2f * i;
                if (rate3 > 1) {
                    rate3 = 1;
                } else if (rate3 < 0) {
                    rate3 = 0;
                }
                //调用改变的方法
                ajustLinkageViewItem(topView, rate3, i);
            }
        }
    }
    private void ajustLinkageViewItem(View topView, float rate, int index) {
        int changeIndex = viewList.indexOf(topView);
        int initPosY = yOffsetStep * index;
        float initScale = 1 - scaleOffsetStep * index;
        float initAlpha = 1.0f * (100 - alphaOffsetStep * index) / 100;

        int nextPosY = yOffsetStep * (index - 1);
        float nextScale = 1 - scaleOffsetStep * (index - 1);
        float nextAlpha = 1.0f * (100 - alphaOffsetStep * (index - 1)) / 100;
        int offset = (int) (initPosY + (nextPosY - initPosY) * rate);
        float scale = initScale + (nextScale - initScale) * rate;
        float alpha = initAlpha + (nextAlpha - initAlpha) * rate;

        View ajustView = viewList.get(changeIndex + index);
        ajustView.offsetTopAndBottom(offset - ajustView.getTop() + initTop);
        ajustView.setScaleX(scale);
        ajustView.setScaleY(scale);
        ajustView.setAlpha(alpha);

    }


    //对view重新排序
    private void resetViewGroup() {

        if (releasedViewList.size() == 0) {
            mScrolling = false;
        }
    }


    /**
     * 添加VelocityTracker
     */
    private void addVelocityTracker(MotionEvent ev) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

    }

    /**
     * 释放VelocityTracker
     */
    private void clearVelocityTracker() {

        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //因为每个孩子的宽高都一样，所以直接传入
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        //测量自己的高度
        setMeasuredDimension(resolveMeasureSize(widthMeasureSpec), resolveMeasureSize(heightMeasureSpec));
    }

    private int resolveMeasureSize(int MeasureSpecRule) {
        int result;
        //尺寸
        int size = MeasureSpec.getSize(MeasureSpecRule);
        //规则
        int mode = MeasureSpec.getMode(MeasureSpecRule);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = size;
            if (mode == MeasureSpec.AT_MOST) {

                result = Math.min(size, result);
            }
        }
        return result;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //获取view列表的size
        int size = viewList.size();
        if (size == 0) return;

        for (int i = 0; i < size; i++) {
            View v = viewList.get(i);
            //对孩子进行位置布局
            layoutChild(v, i);
        }
        //获取child 边距
        initLeft = viewList.get(0).getLeft();
        initTop = viewList.get(0).getTop();
        mCardWidth = viewList.get(0).getMeasuredWidth();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void layoutChild(View v, int i) {
        LayoutParams lp = (LayoutParams) v.getLayoutParams();
        int width = v.getMeasuredWidth();
        int height = v.getMeasuredHeight();
        //得到对齐方式，并且判断是否初始值
        int gravity = lp.gravity;
        if (gravity == -1) {
            gravity = Gravity.START | Gravity.TOP;
        }
        //获取他的rtl排序模式 ，从左网友还是从右往左
        int layoutDirection = getLayoutDirection();
        //获取绝对的
        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
//        result & Gravity.START) == Gravity.START
        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
        //计算出child 左边距和上边距
        int childLeft;
        int childTop;
        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                childLeft = (getWidth() + getPaddingLeft() - getPaddingRight() - width) / 2 +
                        lp.leftMargin - lp.rightMargin;
                break;
            case Gravity.END:
                childLeft = getWidth() + getPaddingRight() - width - lp.rightMargin;
                break;
            case Gravity.START:
            default:
                childLeft = getPaddingLeft() + lp.leftMargin;
                break;
        }
        switch (verticalGravity) {
            case Gravity.CENTER_VERTICAL:
                childTop = (getHeight() + getPaddingTop() - getPaddingBottom() - height) / 2 +
                        lp.topMargin - lp.bottomMargin;
                break;
            case Gravity.BOTTOM:
                childTop = getHeight() - getPaddingBottom() - height - lp.bottomMargin;
                break;
            case Gravity.TOP:
            default:
                childTop = getPaddingTop() + lp.topMargin;
                break;
        }

        //设置view位置
        v.layout(childLeft, childTop, childLeft + width, childTop + height);

//        card:alphaOffsetStep="40"
//        card:scaleOffsetStep="0.08"
//        android:elevation="30dp"
//        card:yOffsetStep="20dp"

        int offset = yOffsetStep * i;
        float scale = 1 - scaleOffsetStep * i;
        float alpha = 1.0f * (100 - alphaOffsetStep * i) / 100;
        v.offsetTopAndBottom(offset);
        v.setScaleX(scale);
        v.setScaleY(scale);
        v.setAlpha(alpha);

    }


    public void setAdapter(MyLinearAdapter adapter) {
        if (adapter == null)
            new Exception("Adapter must not is null");

        this.mAdapter = adapter;

        mShowingIndex = 0;

        removeAllViewsInLayout();

        viewList.clear();
        //获取view数量

        mCount = mAdapter.getCount();


        for (int n = 0; n < mCount; n++) {

            View v = LayoutInflater.from(getContext()).inflate(mAdapter.getCardLayoutId(), this, false);

            if (v == null) {
                return;
            }

            bindCardData(n, v);

            viewList.add(v);

            addView(v, 0);

            setOnItemClickListener(v);

        }


    }

    private void setOnItemClickListener(View v) {

        if (null != mCardsListener)
            v.setOnClickListener(mClickListener);
    }


    //绑定数据
    private void bindCardData(int n, View v) {

        if (mAdapter != null) {
            mAdapter.onBindData(n, v);
            v.setTag(n);
        }
        v.setVisibility(View.VISIBLE);
    }


    interface OnCardsListener {

        void onItemClick(int index);

    }


    public void Logs(String msg) {

        Log.e(TAG, msg);
    }


    public void setOnCardsListener(OnCardsListener l) {
        mCardsListener = l;
    }

    private boolean canMoveCard() {
        return true;
    }


    //判断是否触摸到某个view
    private boolean isTouchTopView(MotionEvent ev) {
        View topView = viewList.get(0);

        if (topView.getVisibility() == VISIBLE && topView != null) {
            Rect bounds = new Rect();
            topView.getGlobalVisibleRect(bounds);
            int x = (int) ev.getX();
            int y = (int) ev.getY();

            if (bounds.contains(x, y)) {
                return true;
            } else {
                return false;
            }
        }


        return true;
    }
}

