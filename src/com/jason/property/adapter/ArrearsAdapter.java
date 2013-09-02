package com.jason.property.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jason.property.ChargeFragment.DataChangeCallback;
import com.jason.property.EditPreChargeActivity;
import com.jason.property.R;
import com.jason.property.data.PropertyService;
import com.jason.property.model.ArrearInfo;

public class ArrearsAdapter extends BaseExpandableListAdapter {

    private Context mContext;

    /** group list */
    private ArrayList<String> mGroupArrears = new ArrayList<String>();

    /** child list */
    private ArrayList<ArrayList<ArrearInfo>> mChildArrears = new ArrayList<ArrayList<ArrearInfo>>();

    private DataChangeCallback mDataChangeCallback;

    public ArrearsAdapter(Context context) {
        mContext = context;
        mGroupArrears.add(context.getString(R.string.charge_expand_list_item_group_text));
        mGroupArrears.add(context.getString(R.string.other_expand_list_item_group_text));
        mGroupArrears.add(context.getString(R.string.pre_expand_list_item_group_text));

        mChildArrears.add(PropertyService.getInstance().Arrears);
        mChildArrears.add(PropertyService.getInstance().TempArrears);
        mChildArrears.add(PropertyService.getInstance().PreArrears);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildArrears.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_arrears_layout, null);
        }
        DecimalFormat df = new DecimalFormat("#.00");
        TextView txtFeeDetails = (TextView) convertView.findViewById(R.id.txt_fee_details);
        Button btnDelete = (Button) convertView.findViewById(R.id.btn_delete);
        Button btnEdit = (Button) convertView.findViewById(R.id.btn_edit);

        if (groupPosition == 0 || groupPosition == 1) {
            btnEdit.setVisibility(View.GONE);
        } else {
            btnEdit.setVisibility(View.VISIBLE);
        }
        ArrearInfo areaInfo = mChildArrears.get(groupPosition).get(childPosition);
        if (areaInfo.getFeeType() == 1 || areaInfo.getFeeType() == 2 || areaInfo.getFeeType() == 3) {
            txtFeeDetails.setText(childPosition
                    + "."
                    + mContext.getString(R.string.txt_arrears_format_text, areaInfo.getName(),
                            areaInfo.getStartDegree(), areaInfo.getEndDegree(),
                            areaInfo.getEndDegree() - areaInfo.getStartDegree(),
                            areaInfo.getPrice(), df.format(areaInfo.getAmount())));
        } else if (areaInfo.getFeeType() == 4 || areaInfo.getFeeType() == 5
                || areaInfo.getFeeType() == 6) {
            // 默认数量为1
            txtFeeDetails.setText(childPosition
                    + "."
                    + mContext.getString(R.string.txt_arrears_format_text, areaInfo.getName(),
                            areaInfo.getPayStartDate(), areaInfo.getPayEndDate(),
                            areaInfo.getCount(), areaInfo.getPrice(),
                            df.format(areaInfo.getAmount())));
        } else {
            txtFeeDetails.setText(childPosition
                    + "."
                    + mContext.getString(R.string.txt_other_arrears_format_text,
                            areaInfo.getName(), areaInfo.getPrice(),
                            df.format(areaInfo.getAmount())));
        }
        btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mChildArrears.get(groupPosition).remove(childPosition);
                notifyDataSetChanged();
                // update the total price
                if (mDataChangeCallback != null) {
                    mDataChangeCallback.onDataChange();
                }
            }
        });
        btnEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent((Activity) mContext, EditPreChargeActivity.class);
                intent.putExtra(EditPreChargeActivity.EXTRA_KEY_PRE_FEE_INDEX, childPosition);
                // need use fragment.startActivityForResult
                if (mDataChangeCallback != null) {
                    mDataChangeCallback.editArrear(intent);
                }
            }
        });
        return convertView;
    }

    public void setDataChangeCallback(DataChangeCallback callback) {
        mDataChangeCallback = callback;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildArrears.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupArrears.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mGroupArrears.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_group_layout, null);
        TextView txtFeeDetails = (TextView) convertView.findViewById(R.id.txt_fee_details);
        double totalPrice = 0;
        for (ArrearInfo area : mChildArrears.get(groupPosition)) {
            totalPrice += area.getAmount();
        }
        DecimalFormat df = new DecimalFormat("#.00");
        txtFeeDetails.setText(mGroupArrears.get(groupPosition) + ":" + df.format(totalPrice));
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
