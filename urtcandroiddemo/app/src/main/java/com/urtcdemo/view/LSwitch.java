package com.urtcdemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.urtcdemo.R;

public class LSwitch extends BaseSwitch{

    private int offTextX = 3;
    private int trackRadius; //背景 圆角
    private int trackHeight; //背景 高度

    private int thumbRadius; //滑块 圆角
    private int thumbHeight; //滑块 高度
    private int thumbWidth; //滑块 宽度

    public LSwitch(Context context) {
        this(context, null);
    }

    public LSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
    }

    private void initAttr(AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LSwitch);
        //获取自定义属性的值
        trackRadius = typedArray.getDimensionPixelOffset(R.styleable.LSwitch_track_radius, -1);
        trackHeight = typedArray.getDimensionPixelOffset(R.styleable.LSwitch_track_height, -1);

        thumbRadius = typedArray.getDimensionPixelOffset(R.styleable.LSwitch_thumb_radius, -1);
        thumbHeight = typedArray.getDimensionPixelOffset(R.styleable.LSwitch_thumb_height, -1);
        thumbWidth = typedArray.getDimensionPixelOffset(R.styleable.LSwitch_thumb_width, -1);
    }


    @Override
    protected void postInit() {
        trackRadius = (trackRadius!=-1?trackRadius:mHeight/2);
        trackHeight = (trackHeight!=-1?trackHeight:mHeight);

        thumbRadius = (thumbRadius!=-1?thumbRadius:mHeight/2);
        thumbHeight = (thumbHeight!=-1?thumbHeight:mHeight);
        thumbWidth = (thumbWidth!=-1?thumbWidth:mHeight);
    }


    @Override
    protected float getAnimatorValueOff() {
        return getMeasuredWidth()-thumbWidth;
    }

    @Override
    protected float getAnimatorValueOn() {
        return 0;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //track
        drawTrack(canvas);
        //thumb
        drawThumb(canvas);
        //边框
        drawStroke(canvas);
        //绘制文字
        drawText(canvas);
    }

    /**
     *  track
     * @param canvas
     */
    public void drawTrack(Canvas canvas) {
        int trackTop = (mHeight-trackHeight)/2;
        int trackBottom = trackTop+trackHeight;
        canvas.drawRoundRect(new RectF(strokeWidth, trackTop+strokeWidth, mWidth-strokeWidth, trackBottom-strokeWidth), trackRadius, trackRadius, paintTrack);
    }

    /**
     * Thumb
     * @param canvas
     */
    public void drawThumb(Canvas canvas) {
        int thumbTop = (mHeight-thumbHeight)/2;
        int thumbBottom = thumbTop+thumbHeight;
        canvas.drawRoundRect(new RectF(animatorValue, thumbTop, animatorValue+thumbWidth, thumbBottom), thumbRadius, thumbRadius, paintThumb);
    }

    /**
     * 边框
     * @param canvas
     */
    public void drawStroke(Canvas canvas) {
//        int trackTop = (mHeight-trackHeight)/2;
//        int trackBottom = trackTop+trackHeight;
//        canvas.drawRoundRect(new RectF(0, trackTop, mWidth, trackBottom), trackRadius, trackRadius, paintStroke);
    }

    /**
     * 绘制文字
     * @param canvas
     */
    protected void drawText(Canvas canvas){
        if(isShowText){
            int baseline = getBaseline(paintText);
            if(isChecked){
                canvas.drawText(textOff, (mWidth-thumbWidth)/2-getTextWidth(paintText, textOff)/2+offTextX, baseline, paintText);
            }else{
                canvas.drawText(textOn, mWidth-(mWidth-thumbWidth)/2-getTextWidth(paintText, textOn)/2-offTextX, baseline, paintText);
            }
        }
    }

    //对应的set方法 单位:dp
    public void setOffTextX(float offTextX) {
        this.offTextX = dp2px(offTextX);
    }

    public void setTrackRadius(int trackRadius) {
        this.trackRadius = dp2px(trackRadius);
    }

    public void setTrackHeight(float trackHeight) {
        this.trackHeight = dp2px(trackHeight);
    }

    public void setThumbRadius(float thumbRadius) {
        this.thumbRadius = dp2px(thumbRadius);
    }

    public void setThumbHeight(float thumbHeight) {
        this.thumbHeight = dp2px(thumbHeight);
    }

    public void setThumbWidth(float thumbWidth) {
        this.thumbWidth = dp2px(thumbWidth);
    }
}
