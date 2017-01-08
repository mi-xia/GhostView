package com.example.hpcompaq.ghostview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by hpcompaq on 2016/11/8.
 */

public class GhostView extends View {

    Paint mBodyPaint, mEyesPaint, mShadowPaint;
    // View宽高
    private int mWidth, mHeight;
    // 默认宽高(WRAP_CONTENT)
    private int mDefaultWidth = (int)dip2px(120);
    private int mDefaultHeight = (int)dip2px(180);

    // 头部的半径
    private int mHeadRadius;
    // 圆心(头部)的X坐标
    private float mHeadCenterX;
    // 圆心(头部)的Y坐标
    private float mHeadCenterY = 0;
    // 头部最左侧的坐标
    private int mHeadLeftX;
    // 头部最右侧的坐标
    private int mHeadRightX;
    // 距离View顶部的内边距
    private int mPaddingTop = (int)dip2px(20);

    // 影子所占区域
    private RectF mRectShadow;
    // 小鬼身体和影子之间的距离
    private int paddingShadow;

    private Path mPath = new Path();
    // 小鬼身体胖过头部的宽度
    private int mGhostBodyWSpace;

    // 单个裙褶的宽高
    private int mSkirtWidth, mSkirtHeight;
    // 裙褶的个数
    private int mSkirtCount = 7;
    private float mHeadCenterY_copy;
    private float diff_CenterY;
    private float last_diff_CenterY;
    private float diff_flag;
    //上升下降时身体的参数值
    private float down_right = -1;
    private float down_left = -1;
    private float left_s;
    private float left_e;
    private float right_s;
    private float right_e;

    public GhostView(Context context) {
        super(context);
    }

    public GhostView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GhostView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPaint();
        if (mHeadCenterY == 0){
            mHeadCenterY = mWidth / 3 + mPaddingTop;
            mHeadCenterY_copy = mHeadCenterY;
            drawHead(canvas);
            drawShadow(canvas);
            drawBody(canvas);
            drawEyes(canvas);
            startAnim();
        }else {
            drawHead(canvas);
            drawShadow(canvas);
            drawBody(canvas);
            drawEyes(canvas);
        }

    }

    //绘制身体
    private void drawBody(Canvas canvas) {
        mPath.reset();
        paddingShadow += diff_CenterY;
        mGhostBodyWSpace = mHeadRadius * 2/15;
        mSkirtWidth = (mHeadRadius * 2 - mGhostBodyWSpace * 2) / mSkirtCount;
        mSkirtHeight = mHeight / 16;
        if (down_left == -1 || down_right == -1) {
            down_right = mHeadRightX + mGhostBodyWSpace * 2;
            down_left = mHeadRightX - mGhostBodyWSpace * 2 - (mSkirtWidth * 8);
            left_s = mHeadRightX - mGhostBodyWSpace * 2 - (mSkirtWidth * 8);
            left_e = mHeadLeftX - mGhostBodyWSpace;
            right_s = mHeadRightX + mGhostBodyWSpace * 2;
            right_e = mHeadRightX + mGhostBodyWSpace;
        }

        // 先画右边的身体
        mPath.moveTo(mHeadLeftX, mHeadCenterY);
        mPath.lineTo(mHeadRightX, mHeadCenterY);
        mPath.quadTo(mHeadRightX + mGhostBodyWSpace, mRectShadow.top - paddingShadow,
                down_right, mRectShadow.top - paddingShadow);
        //下方波浪线,从右至左
        for (int i = 1; i <= mSkirtCount; i++) {
            if (i == 7){
                mPath.quadTo(mHeadRightX - mGhostBodyWSpace - mSkirtWidth * i + (mSkirtWidth / 2), mRectShadow.top - paddingShadow - mSkirtHeight,
                        down_left, mRectShadow.top - paddingShadow);
            }else {
                if (i % 2 != 0) {
                    mPath.quadTo(mHeadRightX - mGhostBodyWSpace - mSkirtWidth * i + (mSkirtWidth / 2), mRectShadow.top - paddingShadow - mSkirtHeight,
                            mHeadRightX - mGhostBodyWSpace - (mSkirtWidth * i), mRectShadow.top - paddingShadow);
                } else {
                    mPath.quadTo(mHeadRightX - mGhostBodyWSpace - mSkirtWidth * i + (mSkirtWidth / 2), mRectShadow.top - paddingShadow + mSkirtHeight,
                            mHeadRightX - mGhostBodyWSpace - (mSkirtWidth * i), mRectShadow.top - paddingShadow);
                }
            }
        }
        mPath.quadTo(mHeadLeftX - mGhostBodyWSpace, mRectShadow.top - paddingShadow, mHeadLeftX, mHeadCenterY);
        canvas.drawPath(mPath,mBodyPaint);
    }

    //绘制影子
    private void drawShadow(Canvas canvas) {
        paddingShadow = mHeight / 10;
        mRectShadow = new RectF();
        mRectShadow.top = (mHeight * 8 / 10) + diff_CenterY/5;
        mRectShadow.bottom = (mHeight * 9 / 10) - diff_CenterY/5;
        mRectShadow.left = (mWidth / 4) + diff_CenterY;
        mRectShadow.right = (mWidth * 3 / 4) - diff_CenterY;
        canvas.drawArc(mRectShadow, 0, 360, false, mShadowPaint);
    }

    //绘制眼睛
    private void drawEyes(Canvas canvas) {
        canvas.drawCircle(mHeadCenterX - mHeadRadius / 6 , mHeadCenterY, mHeadRadius / 6, mEyesPaint);
        canvas.drawCircle(mHeadCenterX + mHeadRadius / 2, mHeadCenterY, mHeadRadius / 6, mEyesPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    //绘制头部
    private void drawHead(Canvas canvas) {
        mHeadRadius = mWidth / 3;
        mHeadCenterX = mWidth / 2;
        mHeadLeftX = (int) (mHeadCenterX - mHeadRadius);
        mHeadRightX = (int) (mHeadCenterX + mHeadRadius);
        canvas.drawCircle(mHeadCenterX, mHeadCenterY, mHeadRadius, mBodyPaint);
    }

    private int measureWidth(int widthMeasureSpec) {
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            mWidth = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            mWidth = Math.min(mDefaultWidth, specSize);
        }
        return mWidth;
    }

    private int measureHeight(int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            mHeight = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            mHeight = Math.min(mDefaultHeight, specSize);
        }
        return mHeight;
    }

    private void initPaint(){
        mBodyPaint = new Paint();
        //设置抗锯齿
        mBodyPaint.setAntiAlias(true);
        //设置画笔风格 Fill 实心 、 STROKE 空心、FILL AND STROKE 同时实心和空心
        mBodyPaint.setStyle(Paint.Style.FILL);
        mBodyPaint.setColor(Color.WHITE);

        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setColor(Color.argb(60,0,0,0));

        mEyesPaint = new Paint();
        mEyesPaint.setAntiAlias(true);
        mEyesPaint.setStyle(Paint.Style.FILL);
        mEyesPaint.setColor(Color.BLACK);
    }

    public static float dip2px(int dipValue)
    {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return  (dipValue * scale + 0.5f);
    }

    private void startAnim(){
        //小鬼上下跳动动画
        ValueAnimator animator = ValueAnimator.ofObject(new MoveEvaluator(),mHeadCenterY, mHeadCenterY-mHeadRadius/2,mHeadCenterY);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                last_diff_CenterY = mHeadCenterY;
                mHeadCenterY = (float) animation.getAnimatedValue();
                diff_CenterY = Math.abs(mHeadCenterY - mHeadCenterY_copy);
                diff_flag = mHeadCenterY - last_diff_CenterY;
                invalidate();
            }
        });
        //小鬼左侧衣角动画
        final ValueAnimator animator_left = ValueAnimator.ofObject(new MoveEvaluator(),left_s,left_e,left_s);
        animator_left.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                down_left = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        //小鬼右侧衣角动画
        final ValueAnimator animator_right = ValueAnimator.ofObject(new MoveEvaluator(),right_s,right_e,right_s);
        animator_right.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                down_right = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator_left.setRepeatMode(ObjectAnimator.RESTART);
        animator_left.setRepeatCount(ObjectAnimator.INFINITE);
        animator_right.setRepeatMode(ObjectAnimator.RESTART);
        animator_right.setRepeatCount(ObjectAnimator.INFINITE);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(animator).with(animator_left).with(animator_right);
        animSet.setDuration(5000);
        animSet.start();
    }

    public void offer(String s){
        Log.i("123123132", "offer: " + s);
    }

    public void setLocal(float x, float y){
        mHeadCenterX = x;
        //mHeadCenterY = y;
        ValueAnimator animator = ValueAnimator.ofObject(new MoveEvaluator(),mHeadCenterY,y,mHeadCenterY);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                last_diff_CenterY = mHeadCenterY;
                mHeadCenterY = (float) animation.getAnimatedValue();
                diff_CenterY = Math.abs(mHeadCenterY - mHeadCenterY_copy);
                diff_flag = mHeadCenterY - last_diff_CenterY;
                invalidate();
            }
        });
        animator.setDuration(5000);
        animator.start();
        Log.i("x + y", "onTouchEvent: x = "+mHeadCenterX+" y = "+mHeadCenterY);
    }
}
