package com.urtcdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 指定宽高比的布局
 */
public class AspectFillView extends RelativeLayout {
    /**
     * 宽高比
     */
    private float widthHeigthRatio;

    /**
     * 比例参照，0-参照width（默认），1-参照height
     */
    private int ratioReference;

    public AspectFillView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 宽高比
        String value = attrs.getAttributeValue(null, "width_height_ratio");

        if (value != null) {
            try {
                this.widthHeigthRatio = Float.parseFloat(value);
            } catch (NumberFormatException e) {
                widthHeigthRatio = 0;
            }
        }

        // if (this.widthHeigthRatio != 0)
        // {
        // 指定参照
        value = attrs.getAttributeValue(null, "ratio_reference");

        if (value != null) {
            if ("width".equalsIgnoreCase(value)) {
                ratioReference = 0;
            } else if ("height".equalsIgnoreCase(value)) {
                ratioReference = 1;
            } else {
                // 只接受width和height
                ratioReference = 0;

            }
        }
        // }

    }

    // @Override
    // protected void onLayout(boolean changed, int l, int t, int r, int b)
    // {
    // LogUtils.e(changed + "," + l + "," + t + "," + r + "," + b);
    // super.onLayout(changed, l, t, r, b);
    //
    // if (changed)
    // {
    // int width = getWidth();
    // int height = getHeight();
    //
    // if (widthHeigthRatio != 0)
    // {
    //
    // // 判断参照物
    // if (ratioReference == 0 && width > 0)
    // {
    // // width固定
    //
    // height = (int) (width / widthHeigthRatio);
    // ViewGroup.LayoutParams params = getLayoutParams();
    // params.height = height;
    // setLayoutParams(params);
    //
    // }
    // else if (height > 0)
    // {
    // // height固定
    //
    // width = (int) (height * widthHeigthRatio);
    //
    // ViewGroup.LayoutParams params = getLayoutParams();
    // params.width = width;
    // setLayoutParams(params);
    // }
    // else
    // {
    //
    // }
    //
    // }
    //
    // }
    // }

    public float getWidthHeigthRatio() {
        return widthHeigthRatio;
    }

    public void setWidthHeigthRatio(float widthHeigthRatio) {
        this.widthHeigthRatio = widthHeigthRatio;

        requestLayout();
    }

    public void setRatioReference(int ratioReference) {
        this.ratioReference = ratioReference;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getDefaultSize(-1, widthMeasureSpec);
        int height = getDefaultSize(-1, heightMeasureSpec);
        // LogUtils.debug(width + "x" + height);
        if (widthHeigthRatio != 0) {
            int pleft = getPaddingLeft();
            int pright = getPaddingRight();
            int ptop = getPaddingTop();
            int pbottom = getPaddingBottom();

            int imageWidth = 0;
            int imageHeigth = 0;

            // 判断参照物
            if (ratioReference == 0 && width > 0) {
                // width固定

                // 图片实际大小
                imageWidth = width - pleft - pright;

                imageHeigth = (int) (imageWidth / widthHeigthRatio);

                height = imageHeigth + ptop + pbottom;
            } else if (height > 0) {
                // height固定

                imageHeigth = height - ptop - pbottom;

                imageWidth = (int) (imageHeigth * widthHeigthRatio);

                width = imageWidth + pleft + pright;
            } else {

            }
        }
        // LogUtils.debug(width + "x" + height);
        // setMeasuredDimension(width, height);

        int measureWidth = width;
        int measureHeigth = height;

        // for (int i = 0; i < getChildCount(); i++)
        // {
        // View v = getChildAt(i);
        //
        // int widthSpec = 0;
        // int heightSpec = 0;
        // ViewGroup.LayoutParams params = v.getLayoutParams();
        // if (params.width > 0)
        // {
        // widthSpec = MeasureSpec.makeMeasureSpec(params.width,
        // MeasureSpec.EXACTLY);
        // }
        // else if (params.width == ViewGroup.LayoutParams.FILL_PARENT)
        // {
        // widthSpec = MeasureSpec.makeMeasureSpec(measureWidth,
        // MeasureSpec.EXACTLY);
        // }
        // else if (params.width == ViewGroup.LayoutParams.WRAP_CONTENT)
        // {
        // widthSpec = MeasureSpec.makeMeasureSpec(measureWidth,
        // MeasureSpec.AT_MOST);
        // }
        //
        // if (params.height > 0)
        // {
        // heightSpec = MeasureSpec.makeMeasureSpec(params.height,
        // MeasureSpec.EXACTLY);
        // }
        // else if (params.height == -1)
        // {
        // heightSpec = MeasureSpec.makeMeasureSpec(measureHeigth,
        // MeasureSpec.EXACTLY);
        // }
        // else if (params.height == -2)
        // {
        // heightSpec = MeasureSpec.makeMeasureSpec(measureWidth,
        // MeasureSpec.AT_MOST);
        // }
        // v.measure(widthSpec, heightSpec);
        //
        // LogUtils.e(v.getMeasuredWidth() + "x" + v.getMeasuredHeight());
        // }

        int widthSpec = 0;
        int heightSpec = 0;
        widthSpec = MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.EXACTLY);
        heightSpec = MeasureSpec.makeMeasureSpec(measureHeigth, MeasureSpec.EXACTLY);
        super.onMeasure(widthSpec, heightSpec);

    }
}
