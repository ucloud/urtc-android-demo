package com.urtcdemo.view;

import android.view.View;

public abstract class CustomerClickListener implements View.OnClickListener {
    private long mLastClickTime;
    private long timeInterval = 1000;

    public CustomerClickListener() {

    }

    public CustomerClickListener(long interval) {
        this.timeInterval = interval;
    }

    @Override
    public void onClick(View v) {
        long nowTime = System.currentTimeMillis();
        if (nowTime - mLastClickTime > timeInterval) {
            // 单次点击事件
            onSingleClick();
            mLastClickTime = nowTime;
        } else {
            // 快速点击事件
            onFastClick();
        }
    }

    protected abstract void onSingleClick();
    protected abstract void onFastClick();
}
