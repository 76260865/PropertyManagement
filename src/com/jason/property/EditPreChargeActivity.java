package com.jason.property;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jason.property.data.PropertyService;
import com.jason.property.model.ArrearInfo;

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
        mTxtStartNo.setText(String.valueOf(mArreaInfo.getStartDegree()));

        mTxtEndNo = (TextView) findViewById(R.id.txt_end_no);
        mTxtEndNo.setText(String.valueOf(mArreaInfo.getEndDegree()));

        // 数量
        mEditAmount = (EditText) findViewById(R.id.edit_amount);
        mEditAmount.addTextChangedListener(mTextWatcher);

        mTxtPrice = (TextView) findViewById(R.id.txt_price);
        mTxtPrice.setText(String.valueOf(mArreaInfo.getPrice()));

        // 金额
        mEditTotalAmount = (EditText) findViewById(R.id.edit_total_amount);
        mEditTotalAmount.setEnabled(false);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO: update the mArreaInfo and setReslut OK to notify the
            // adapter
            String amount = mEditTotalAmount.getText().toString();
            if (!TextUtils.isEmpty(amount)) {
                mArreaInfo.setAmount(Float.valueOf(amount));
                // mArreaInfo.setEndDegree(endDegree);
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
            // TODO: count the total amount
            // mEditTotalAmount.setText("");
        }
    };
}
