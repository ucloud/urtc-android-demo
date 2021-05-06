package com.urtcdemo.adpter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.urtcdemo.R;
import com.cmcc.sdkengine.define.CMCCStreamInfo;

import java.util.ArrayList;
import java.util.List;

public class SelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CMCCStreamInfo> mList;

    private SparseBooleanArray mSelectedPositions = new SparseBooleanArray();
    private boolean mIsSelectable = false;


    public SelectAdapter(List<CMCCStreamInfo> list) {
        if (list == null) {
            throw new IllegalArgumentException("model Data must not be null");
        }
        mList = list;
    }

    public void addStreamInfo(CMCCStreamInfo info, boolean needNotify){
        mList.add(info);
        if(needNotify)notifyDataSetChanged();
    }

    //更新adpter的数据和选择状态
    public void updateDataSet(ArrayList<CMCCStreamInfo> list) {
        this.mList = list;
        mSelectedPositions = new SparseBooleanArray();
//            ab.setTitle("已选择" + 0 + "项");
    }


    //获得选中条目的结果
    public ArrayList<CMCCStreamInfo> getSelectedItem() {
        ArrayList<CMCCStreamInfo> selectList = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            if (isItemChecked(i)) {
                selectList.add(mList.get(i));
            }
        }
        return selectList;
    }

    public void removeStreamInfoByUId(String uid){
        CMCCStreamInfo info = findStreamInfoByUId(uid);
        if(info != null){
            mList.remove(info);
            notifyDataSetChanged();
        }
    }

    public CMCCStreamInfo findStreamInfoByUId(String uid){
        CMCCStreamInfo info = null;
        for (int i = 0; i < mList.size(); i++) {
            if(mList.get(i).getUId().equals(uid)){
                info = mList.get(i);
                break;
            }
        }
        return info;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stream_scribe_item, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    //设置给定位置条目的选择状态
    private void setItemChecked(int position, boolean isChecked) {
        mSelectedPositions.put(position, isChecked);
    }

    //根据位置判断条目是否选中
    private boolean isItemChecked(int position) {
        return mSelectedPositions.get(position);
    }

    //根据位置判断条目是否可选
    private boolean isSelectable() {
        return mIsSelectable;
    }

    //设置给定位置条目的可选与否的状态
    private void setSelectable(boolean selectable) {
        mIsSelectable = selectable;
    }

    //绑定界面，设置监听
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {
        //设置条目状态
        CMCCStreamInfo streamInfo = mList.get(i);
        ((ListItemViewHolder) holder).mainTitle.setText(streamInfo.getUId());
        ((ListItemViewHolder) holder).checkBox.setChecked(isItemChecked(i));

        //checkBox的监听
        ((ListItemViewHolder) holder).checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isItemChecked(i)) {
                    setItemChecked(i, false);
                } else {
                    setItemChecked(i, true);
                }
//                    ab.setTitle("已选择" + getSelectedItem().size() + "项");
            }
        });

        //条目view的监听
        ((ListItemViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isItemChecked(i)) {
                    setItemChecked(i, false);
                } else {
                    setItemChecked(i, true);
                }
                notifyItemChanged(i);
//                    ab.setTitle("已选择" + getSelectedItem().size() + "项");
            }
        });


    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder {
        //ViewHolder
        CheckBox checkBox;
        TextView mainTitle;

        ListItemViewHolder(View view) {
            super(view);
            this.mainTitle = (TextView) view.findViewById(R.id.text);
            this.checkBox = (CheckBox) view.findViewById(R.id.select_checkbox);

        }
    }
}
  