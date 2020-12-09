package com.urtcdemo.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.urtcdemo.R;

public abstract class BaseSwitch extends View implements View.OnClickListener {

    protected Context context;

    protected int mWidth = 0;
    protected int mHeight = 0;

    //底部背景
    protected Paint paintTrack = new Paint(); //底部背景
    protected Paint paintThumb = new Paint(); //可滚动部分
    protected Paint paintStroke = new Paint(); //边框
    //对应颜色(打开/关闭)
    protected int trackColorOff;
    protected int trackColorOn;
    protected int thumbColorOff;
    protected int thumbColorOn;
    //边框
    protected int strokeColorOff;
    protected int strokeColorOn;
    protected int strokeWidth;

    //文字
    protected Paint paintText = new Paint();
    protected String textOff = "";
    protected String textOn = "";
    protected float textSizeOff;
    protected float textSizeOn;
    protected int textColorOff = Color.WHITE; //字体颜色
    protected int textColorOn = Color.WHITE;
    protected boolean isShowText; //是否显示文字
    protected boolean isChecked;
    protected OnCheckedListener onCheckedListener;

    //动画
    protected float animatorValue = 0; //动画变化的值
    protected long animatorDuration = 0; //动画时间

    public BaseSwitch(Context context) {
        this(context, null);
    }

    public BaseSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttr(attrs);
        initBase();
    }

    protected abstract void postInit();
    protected abstract float getAnimatorValueOff(); //动画开始的值
    protected abstract float getAnimatorValueOn(); //动画结束的值

    protected void animatorEnd(){} //动画结束--执行的方法
    protected void unChecked(){} //选中--执行的方法
    protected void checked(){}  //未选中--执行的方法

    /**
     * 初始化属性
     * @param attrs
     */
    private void initAttr(AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseSwitch);
        //获取自定义属性的值
        //off
        trackColorOff = typedArray.getColor(R.styleable.BaseSwitch_track_color_off, Color.parseColor("#F8933B"));
        thumbColorOff = typedArray.getColor(R.styleable.BaseSwitch_thumb_color_off, Color.WHITE);
        textColorOff = typedArray.getColor(R.styleable.BaseSwitch_text_color_off, Color.WHITE);
        textSizeOff = typedArray.getDimensionPixelOffset(R.styleable.BaseSwitch_text_size_off, 10);
        textOff = typedArray.getString(R.styleable.BaseSwitch_text_off);
        if(textOff==null){
            textOff = "开";
        }

        //stroke属性
        strokeColorOff =  typedArray.getColor(R.styleable.BaseSwitch_stroke_color_off, Color.GRAY);
        strokeColorOn =  typedArray.getColor(R.styleable.BaseSwitch_stroke_color_on, Color.GRAY);
        strokeWidth =  typedArray.getDimensionPixelOffset(R.styleable.BaseSwitch_stroke_width, dp2px(1));

        //on
        trackColorOn = typedArray.getColor(R.styleable.BaseSwitch_track_color_on, Color.parseColor("#BECBE4"));
        thumbColorOn = typedArray.getColor(R.styleable.BaseSwitch_thumb_color_on, Color.WHITE);
        textColorOn = typedArray.getColor(R.styleable.BaseSwitch_text_color_on, Color.WHITE);
        textSizeOn = typedArray.getDimensionPixelOffset(R.styleable.BaseSwitch_text_size_off, 10);
        textOn = typedArray.getString(R.styleable.BaseSwitch_text_on);
        if(textOn==null){
            textOn = "关";
        }

        //其它属性
        isShowText = typedArray.getBoolean(R.styleable.BaseSwitch_text_show, false);
        isChecked = typedArray.getBoolean(R.styleable.BaseSwitch_checked, true);
        animatorDuration = typedArray.getInteger(R.styleable.BaseSwitch_animator_duration, 300);
    }

    private void initBase() {
        initPaint();
        setOnClickListener(this);
        post(new Runnable() {
            @Override
            public void run() {
                mWidth = getMeasuredWidth();
                mHeight = getMeasuredHeight();
                postInit();
                animatorValue = (isChecked?getAnimatorValueOff():getAnimatorValueOn());
                invalidate();
            }
        });
    }

    /**
     * 初始化画笔
     */
    private void initPaint(){
        paintTrack.setAntiAlias(true);
        paintThumb.setAntiAlias(true);
        paintStroke.setAntiAlias(true);
        paintText.setAntiAlias(true);
        paintStroke.setStrokeWidth(strokeWidth);
        paintStroke.setStyle(Paint.Style.STROKE);
        if(isChecked){
            setPaintOff();
        }else{
            setPaintOn();
        }
    }

    /**
     * 打开
     */
    private void setPaintOff(){
        paintTrack.setColor(trackColorOff);
        paintThumb.setColor(thumbColorOff);
        paintText.setColor(textColorOff);
        paintStroke.setColor(strokeColorOff);
        paintText.setTextSize(textSizeOff);
    }

    /**
     * 关闭
     */
    private void setPaintOn(){
        paintTrack.setColor(trackColorOn);
        paintThumb.setColor(thumbColorOn);
        paintText.setColor(textColorOn);
        paintText.setTextSize(textSizeOn);
        paintStroke.setColor(strokeColorOn);
    }

    public void setOnCheckedListener(OnCheckedListener onCheckedListener) {
        this.onCheckedListener = onCheckedListener;
    }

    public void setShowText(boolean showText) {
        isShowText = showText;
    }

    /**
     * 是否选中
     * @param isChecked
     */
    public void setChecked(boolean isChecked){
        if(this.isChecked == isChecked){ //状态一样， 不处理
            return;
        }
        this.isChecked = isChecked;
        if(isChecked){
//            setPaintOff();
            startAnimator(getAnimatorValueOn(), getAnimatorValueOff());
            checked();
        }else{
//            setPaintOn();
            startAnimator(getAnimatorValueOff(), getAnimatorValueOn());
            unChecked();
        }
        if(onCheckedListener!=null){
            onCheckedListener.onChecked(isChecked);
        }
    }

    protected int getBaseline(Paint paint){
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        // 获取文字的高
        int fontTotalHeight = fontMetrics.bottom - fontMetrics.top;
        // 计算基线到中心点的距离
        int offY = fontTotalHeight / 2 - fontMetrics.bottom;
        // 计算基线位置
        int baseline = (getMeasuredHeight() + fontTotalHeight) / 2 - offY;
        return baseline;
    }

    @Override
    public void onClick(View v) {
        setChecked(!isChecked);
    }

    /**
     * 动画监听
     * @param startValue 开始的值
     * @param endValue 结束的值
     */
    protected void startAnimator(final float startValue, final float endValue){
        ValueAnimator animator = ValueAnimator.ofFloat(startValue, endValue);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatorValue = (float)animation.getAnimatedValue();

                //判断是否超过中间值
                if(Math.abs(startValue-animatorValue)>Math.abs(endValue-animatorValue)){ //超过一半
                    if(isChecked){
                        setPaintOff();
                    }else{
                        setPaintOn();
                    }
                }

                if(animatorValue==endValue){ //动画结束
                    animatorEnd();
                }
                invalidate();
            }
        });
        animator.setDuration(animatorDuration); //时间
        animator.start();
    }

    /**
     * 获取字宽度
     * @param text
     * @return
     */
    protected int getTextWidth(Paint paint, String text){
        if(TextUtils.isEmpty(text)){
            return 0;
        }
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }

    protected float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    protected int dp2px(float sp) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sp, getResources().getDisplayMetrics());
    }

    //监听选中情况
    public interface OnCheckedListener{
        void onChecked(boolean isChecked);
    }



    //对应的set方法 单位:dp sp
    public void setTrackColorOff(int trackColorOff) {
        this.trackColorOff = trackColorOff;
    }

    public void setTrackColorOn(int trackColorOn) {
        this.trackColorOn = trackColorOn;
    }

    public void setThumbColorOff(int thumbColorOff) {
        this.thumbColorOff = thumbColorOff;
    }

    public void setThumbColorOn(int thumbColorOn) {
        this.thumbColorOn = thumbColorOn;
    }

    public void setTextOff(String textOff) {
        this.textOff = textOff;
    }

    public void setTextOn(String textOn) {
        this.textOn = textOn;
    }

    public void setTextSizeOff(float textSizeOff) {
        this.textSizeOff = sp2px(textSizeOff);
    }

    public void setTextSizeOn(float textSizeOn) {
        this.textSizeOn = sp2px(textSizeOn);
    }

    public void setTextColorOff(int textColorOff) {
        this.textColorOff = textColorOff;
    }

    public void setTextColorOn(int textColorOn) {
        this.textColorOn = textColorOn;
    }

    public void setAnimatorDuration(long animatorDuration) {
        this.animatorDuration = animatorDuration;
    }
}
