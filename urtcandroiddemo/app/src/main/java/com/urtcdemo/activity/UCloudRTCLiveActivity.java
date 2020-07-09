package com.urtcdemo.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.urtcdemo.R;
import com.urtcdemo.utils.StatusBarUtils;

/**
 * @author ciel
 * @create 2020/7/2
 * @Describe
 */
public class UCloudRTCLiveActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private ViewGroup mDrawerContent;
    private Button mButtonDrawer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_living);
        mDrawer = findViewById(R.id.drawer_layout);
        mDrawer.setScrimColor(0x00ffffff);
        mDrawerContent = findViewById(R.id.drawer_content);
        mDrawerContent.setPadding(0,StatusBarUtils.getStatusBarHeight(this),0,0);
        mButtonDrawer = findViewById(R.id.btn_drawer);
        mButtonDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDrawer.isDrawerOpen(Gravity.RIGHT)){
                    mDrawer.closeDrawer(Gravity.RIGHT);
                }else{
                    mDrawer.openDrawer(Gravity.RIGHT);
                }
            }
        });
        StatusBarUtils.setColor(this,getResources().getColor(R.color.color_7F04A5EB));

//        StatusBarUtils.setColorForDrawerLayout(this,mDrawerLayout,getResources().getColor(R.color.color_FF007AFF));
    }


}
