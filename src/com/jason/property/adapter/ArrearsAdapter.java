package com.jason.property.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jason.property.R;
import com.jason.property.model.ArrearInfo;

public class ArrearsAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<ArrearInfo> arrears = new ArrayList<ArrearInfo>();

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
        TextView txtFeeDetails = (TextView) convertView.findViewById(R.id.txt_fee_details);
        Button btnDelete = (Button) convertView.findViewById(R.id.btn_delete);
        ArrearInfo areaInfo = arrears.get(position);
        txtFeeDetails.setText(mContext.getString(R.string.txt_arrears_format_text,
                areaInfo.getName(), areaInfo.getStartDegree(), areaInfo.getEndDegree(),
                areaInfo.getAmount(), areaInfo.getPrice(), areaInfo.getAmount()));
        return convertView;
    }
}
