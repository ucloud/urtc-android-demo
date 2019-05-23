package com.urtcdemo.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.urtcdemo.R;

public class VideoProfilePopupWindow extends PopupWindow implements AdapterView.OnItemClickListener {

    private Context mContext;
    private ListView mDataSource;
    private ArrayAdapter<String> mAdapter;
    private OnSpinnerItemClickListener mOnSpinnerItemClickListener;

    public interface OnSpinnerItemClickListener {
        void onItemClick(int pos);
    }

    public VideoProfilePopupWindow(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public void setOnSpinnerItemClickListener(OnSpinnerItemClickListener listener) {
        mOnSpinnerItemClickListener = listener;
    }

    public void setAdapter(ArrayAdapter<String> adapter) {
        mAdapter = adapter;
        mDataSource.setAdapter(mAdapter);
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.videoprofile_popup_window, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.editTextBackground)));

        mDataSource = (ListView) view.findViewById(R.id.config_list_view);
        mDataSource.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnSpinnerItemClickListener != null) {
            mOnSpinnerItemClickListener.onItemClick(position);
        }
    }
}
