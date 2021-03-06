package com.jason.property;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jason.property.data.PropertyService;
import com.jason.property.model.ArrearInfo;
import com.jason.property.model.StandardFee;

public class EditPreChargeActivity extends Activity {
    public static final String EXTRA_KEY_PRE_FEE_INDEX = "extra_key_pre_fee_index";

    private Button mBtnSubmit;

    private int mPreFeeIndex = -1;

    private TextView mTxtFeeName;

    private TextView mTxtStartNo;

    private TextView mTxtEndNo;

    private EditText mEditAmount;

    private TextView mTxtPrice;

    private EditText mEditTotalAmount;

    private ArrearInfo mArreaInfo;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_charge);

        mPreFeeIndex = getIntent().getIntExtra(EXTRA_KEY_PRE_FEE_INDEX, -1);
        if (mPreFeeIndex == -1) {
            finish();
            return;
        }

        mArreaInfo = PropertyService.getInstance().PreArrears.get(mPreFeeIndex);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(mOnClickListener);

        mTxtFeeName = (TextView) findViewById(R.id.txt_fee_name);
        mTxtFeeName.setText(mArreaInfo.getName());

        mTxtStartNo = (TextView) findViewById(R.id.txt_start_no);

        mTxtEndNo = (TextView) findViewById(R.id.txt_end_no);

        // 数量
        mEditAmount = (EditText) findViewById(R.id.edit_amount);
        mEditAmount.addTextChangedListener(mTextWatcher);

        mTxtPrice = (TextView) findViewById(R.id.txt_price);
        mTxtPrice.setText(String.valueOf(mArreaInfo.getPrice()));

        // 金额
        mEditTotalAmount = (EditText) findViewById(R.id.edit_total_amount);
        mEditTotalAmount.setEnabled(false);

        if (mArreaInfo.getCount() > 0)
            mEditAmount.setText(mArreaInfo.getCount() + "");
        if (mArreaInfo.getFeeType() == 1 || mArreaInfo.getFeeType() == 2
                || mArreaInfo.getFeeType() == 3) {
            mTxtStartNo.setText(String.valueOf(mArreaInfo.getStartDegree()));
            mTxtEndNo.setText(String.valueOf(mArreaInfo.getEndDegree()));
        } else if (mArreaInfo.getFeeType() == 4 || mArreaInfo.getFeeType() == 5
                || mArreaInfo.getFeeType() == 6) {
            mTxtStartNo.setText(String.valueOf(mArreaInfo.getPayStartDate()));
            mTxtEndNo.setText(String.valueOf(mArreaInfo.getPayEndDate()));
        }
    }

    private Calendar countEndDate(String amount) throws ParseException {
        Date date = formatter.parse(mArreaInfo.getPayStartDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, Integer.valueOf(amount));
        calendar.add(Calendar.DATE, -1);
        return calendar;
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO: update the mArreaInfo and setReslut OK to notify the
            // adapter
            String amount = mEditAmount.getText().toString();
            String totalAmount = mEditTotalAmount.getText().toString();
            if (!TextUtils.isEmpty(amount)) {
                mArreaInfo.setCount(Integer.valueOf(amount));
                mArreaInfo.setAmount(Double.valueOf(totalAmount));
                try {
                    Calendar calendar = countEndDate(amount);
                    mArreaInfo.setPayEndDate(Integer.valueOf(amount) == 0 ? "" : formatter
                            .format(calendar.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                setResult(RESULT_OK);
                finish();
            }
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable arg0) {
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(mEditAmount.getText())) {
                mTxtEndNo.setText("");
                return;
            }
            int amount = Integer.valueOf(mEditAmount.getText().toString());

            try {
                if (amount == 0) {
                    mTxtEndNo.setText("");
                } else {
                    Calendar calendar = countEndDate(mEditAmount.getText().toString());
                    mTxtEndNo.setText(formatter.format(calendar.getTime()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ArrearInfo arrearInfo = PropertyService.getInstance().PreArrears.get(mPreFeeIndex);
            DecimalFormat df = new DecimalFormat("#.00");
            for (StandardFee fee : PropertyService.getInstance().StandardFees) {
                if (arrearInfo.getFeeStandardID() == fee.getFeeStandardID()) {
                    double total = 0;
                    if (fee.getRelationArea() == 0) {
                        // 不关联面积
                        total = fee.getPrice() * amount;
                    } else if (fee.getRelationArea() == 1) {
                        // 关联建筑面积
                        total = fee.getPrice() * amount
                                * PropertyService.getInstance().getRoomInfo().getBuildArea();
                    } else if (fee.getRelationArea() == 2) {
                        // 关联使用面积
                        total = fee.getPrice() * amount
                                * PropertyService.getInstance().getRoomInfo().getUseArea();
                    }
                    mEditTotalAmount.setText(df.format(total) + "");
                    break;
                }
            }
        }
    };
}
