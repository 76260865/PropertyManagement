package com.jason.property.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jason.property.R;
import com.jason.property.model.ArrearInfo;

public class ArrearsAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<ArrearInfo> arrears;

    public ArrearsAdapter(Context context, ArrayList<ArrearInfo> arrears) {
        mContext = context;
        this.arrears = arrears;
    }

    @Override
    public int getCount() {
        return arrears.size();
    }

    @Override
    public Object getItem(int index) {
        return arrears.get(index);
    }

    @Override
    public long getItemId(int index) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_arrears_layout, null);
        }
        return convertView;
    }
}
