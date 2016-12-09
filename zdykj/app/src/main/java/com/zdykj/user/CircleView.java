package com.zdykj.user;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * Created by Litao-pc on 2016/6/30.
 */
public class CircleView extends View {

    private final float DEFAULT_LENGTH_1 = 20;

    private final float DEFAULT_LENGTH_2 = 40;

    private final float DEFAULT_LENGTH_3 = 60;

    private final float DEFAULT_LENGTH_4 = 80;

    private final int DEFAULT_HEIGHT_IN_DP = 200;


    private final int DEFAULT_WIGHT_IN_DP = 200;

    private float DEFAULT_SLIDER_RADIUS_PERCENT = 0.4f;

    private static final int DEFAULT_PAINT_STROKE_WIDTH = 5;

    private static final int DEFAULT_SCALE_STROKE_WIDTH = 40;

    private final int mBgColor = Color.parseColor("#227BAE");
    private Paint mPaintText;//文字
    private int mCenterX;
    private int mCenterY;
    private float mOuterRadius;
    private Paint mPaintPath;
    private int layoutHeight;
    private float mInnerRadius;
    private float radius;
    private int smarks;

    public CircleView(Context context) {
        super(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.layout_height});
            try {
                layoutHeight = a.getLayoutDimension(
                        0, ViewGroup.LayoutParams.WRAP_CONTENT);
            } finally {
                a.recycle();
            }
        }

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setTextSize(DEFAULT_PAINT_STROKE_WIDTH);
        mPaintText.setColor(Color.WHITE);


        mPaintPath = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintPath.setStyle(Paint.Style.STROKE);
        mPaintPath.setColor(Color.WHITE);
        mPaintPath.setStrokeWidth(1.8f);

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);

                preComputeDrawingPosition();

                return true;
            }
        });


    }

    private void preComputeDrawingPosition() {
        int w = getWidthWithPadding();
        int h = getHeightWithPadding();

        radius = Math.min(w, h) * DEFAULT_SLIDER_RADIUS_PERCENT;
        mOuterRadius = radius + DEFAULT_SCALE_STROKE_WIDTH;
        mInnerRadius = radius - DEFAULT_SCALE_STROKE_WIDTH;
        mCenterX = w >> 1;
        mCenterY = h >> 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));

    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(mBgColor);
        drawArc(canvas);
        drawAsnText(canvas);

    }

    private void drawArc(Canvas canvas) {
        float startX;
        float startY;
        float stopX;
        float stopY;
        double rad;

        //经过变换后绘制在圆盘周围的角度数字
        for (double angle = 0; angle < 360; angle++) {
            rad = Math.toRadians(angle);
            //主要刻度比次要刻度长
            if (angle % 5 == 0) {
                mInnerRadius = radius - 5;
                mOuterRadius = mInnerRadius + 30;
                if (angle % 45 == 0) {
                    mInnerRadius = radius - 10;
                    mOuterRadius = mInnerRadius + 40;
                }

            } else {
                mInnerRadius = radius;
                mOuterRadius = mInnerRadius + 20;
            }

            startX = (float) (mCenterX + mInnerRadius * Math.cos(rad/Math.PI));
            startY = (float) (mCenterY - mInnerRadius * Math.sin(rad/Math.PI));
            stopX = (float) (mCenterX + mOuterRadius * Math.cos(rad/Math.PI));
            stopY = (float) (mCenterY - mOuterRadius * Math.sin(rad/Math.PI));

            canvas.drawLine(startX, startY, stopX, stopY, mPaintPath);



        }

    }

    private void drawAsnText(Canvas canvas) {
        //绘制文字刻度
        mPaintText.setTextSize(10);
        for (int i = 1; i <= 24; i++) {
            canvas.save();// 保存当前画布
            canvas.rotate(360 / 24 * i, mCenterX, mCenterY);
            canvas.drawText(i * 24 + "", mCenterX, mCenterY- radius -50, mPaintText);
            canvas.restore();//
        }
    }


    public int getHeightWithPadding() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    public int getWidthWithPadding() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }


    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result;
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            final int height;
            if (layoutHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = dpToPx(getContext(), DEFAULT_HEIGHT_IN_DP);
            } else if (layoutHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
                height = getMeasuredHeight();
            } else {
                height = layoutHeight;
            }
            result = height + getPaddingTop() + getPaddingBottom() + DEFAULT_PAINT_STROKE_WIDTH;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result;
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = specSize + getPaddingLeft() + getPaddingRight() + (2 * DEFAULT_PAINT_STROKE_WIDTH) + (int) (2 * radius);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    static int dpToPx(final Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }


    RectF getRectF() {
        RectF oval = new RectF();                     //RectF对象
        oval.left = mCenterX - mOuterRadius;                              //左边
        oval.top = mCenterY - mOuterRadius;                                   //上边
        oval.right = mCenterX + mOuterRadius;                             //右边
        oval.bottom = mCenterY + mOuterRadius;
        return oval;
    }
}
