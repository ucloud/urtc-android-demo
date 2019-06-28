package com.urtcdemo.utils;

import android.content.Context;

/**
 * @author ciel
 * @create 2019/6/25
 * @Describe
 */
public class UiHelper {

        /**
         * dip转px
         */
        public static int dipToPx(Context context, float dip) {
            return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
        }

        /**
         * px转dip
         */
        public static int pxToDip(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }

        /**
         * 将sp值转换为px值
         */
        public static int sp2px(Context context, float spValue) {
            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (spValue * fontScale + 0.5f);
        }

        /**
         * 将sp值转换为px值
         */
        public static int px2sp(Context context, float pxValue) {
            final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (pxValue / fontScale + 0.5f);
        }

    /**
     * 获取屏幕分辨率：宽
     */
    public static int getScreenPixWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕分辨率：高
     */
    public static int getScreenPixHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
