package com.jason.property;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jason.property.data.PropertyService;
import com.jason.property.model.ArrearInfo;
import com.jason.property.model.StandardFee;
import com.jason.property.net.PropertyNetworkApi;
import com.loopj.android.http.JsonHttpResponseHandler;

public class AddOtherFeeActivity extends Activity {
	private static final String TAG = "AddOtherFeeActivity";
	public static String EXTRA_KEY_FEE_STANDARD_ID = "FeeStandardID";
	public static String EXTRA_KEY_FEE_NAME = "FeeName";
	public static String EXTRA_KEY_FEE_START_DATE = "StartDate";
	public static String EXTRA_KEY_FEE_END_DATE = "EndDate";
	public static String EXTRA_KEY_FEE_QUANTITY = "Quantity";
	public static String EXTRA_KEY_FEE_AMOUNT = "Amount";

	private Spinner mSpinner;
	private EditText mEditFeeName;
	private EditText mEditStartNo;
	private EditText mEditEndNo;
	private EditText mEditAmount;
	private EditText mEditPrice;
	private Button mBtnSubmit;

	private ArrayList<StandardFee> mOtherStandardFees = new ArrayList<StandardFee>();

	private int mSelectedFeeStandardId;

	private DatePickerDialog mDatePickerDialogStart;
	private DatePickerDialog mDatePickerDialogEnd;

	private Calendar mCalendar = Calendar.getInstance();

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_other_fee);

		initOtherStandardFee();
		initSpinner();

		initializeResources();

		mDatePickerDialogStart = new DatePickerDialog(this, mDateStartListener,
				mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
				mCalendar.get(Calendar.DAY_OF_MONTH));
		mDatePickerDialogEnd = new DatePickerDialog(this, mDateEndtListener,
				mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
				mCalendar.get(Calendar.DAY_OF_MONTH));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private DatePickerDialog.OnDateSetListener mDateStartListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mCalendar.set(year, monthOfYear, dayOfMonth);
			mEditStartNo.setText(mDateFormat.format(mCalendar.getTime()));
		}
	};

	private DatePickerDialog.OnDateSetListener mDateEndtListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mCalendar.set(year, monthOfYear, dayOfMonth);
			mEditEndNo.setText(mDateFormat.format(mCalendar.getTime()));
		}
	};

	private void initializeResources() {
		mEditFeeName = (EditText) findViewById(R.id.edit_fee_name);
		mEditStartNo = (EditText) findViewById(R.id.edit_start_no);
		mEditEndNo = (EditText) findViewById(R.id.edit_end_no);
		mEditAmount = (EditText) findViewById(R.id.edit_amount);
		mEditPrice = (EditText) findViewById(R.id.edit_price);
		mBtnSubmit = (Button) findViewById(R.id.btn_submit);
		mBtnSubmit.setOnClickListener(mOnClickListener);
		mEditEndNo.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mDatePickerDialogEnd.show();
				return false;
			}
		});
		mEditStartNo.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mDatePickerDialogStart.show();
				return false;
			}
		});
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (TextUtils.isEmpty(mEditFeeName.getText().toString())
					|| TextUtils.isEmpty(mEditStartNo.getText().toString())
					|| TextUtils.isEmpty(mEditEndNo.getText().toString())
					|| TextUtils.isEmpty(mEditAmount.getText().toString())
					|| TextUtils.isEmpty(mEditPrice.getText().toString())) {
				Toast.makeText(getApplicationContext(), "请填入相关信息",
						Toast.LENGTH_SHORT).show();
				return;
			}
			String employeeId = PropertyService.getInstance().getUserInfo()
					.getEmployeeId();
			String areaId = PropertyService.getInstance().getUserInfo()
					.getAreaId();
			int roomId = PropertyService.getInstance().getRoomInfo()
					.getRoomId();
			PropertyNetworkApi.getInstance().AddOtherFee(employeeId, areaId,
					roomId + "", mSelectedFeeStandardId + "",
					mEditFeeName.getText().toString(),
					mEditStartNo.getText().toString(),
					mEditEndNo.getText().toString(),
					mEditAmount.getText().toString(),
					mEditPrice.getText().toString(),
					mAddOtherFeeResponseHandler);
		}
	};

	private JsonHttpResponseHandler mAddOtherFeeResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e(TAG, arg1);
		}

		@Override
		public void onSuccess(JSONObject object) {
			Log.d(TAG, "mAddOtherFeeResponseHandler:" + object.toString());
			try {
				int resultCode = object.getInt("ResultCode");
				String erroMsg = object.getString("ErrorMessage");
				if (resultCode != 1) {
					Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : "
							+ resultCode);
					Toast.makeText(getApplicationContext(), erroMsg,
							Toast.LENGTH_LONG).show();
					return;
				}

				// get temp Arrears info
				PropertyService.getInstance().TempArrears.clear();
				JSONArray tempArrears = object.getJSONArray("Data");
				for (int i = 0; i < tempArrears.length(); i++) {
					JSONObject arrearObj = tempArrears.getJSONObject(i);
					ArrearInfo arrearInfo = ChargeFragment
							.convertJSONObjectToArrear(arrearObj);
					PropertyService.getInstance().TempArrears.add(arrearInfo);
				}

				Intent intent = new Intent();
				setResult(Activity.RESULT_OK, intent);
				finish();

			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	};

	private void initSpinner() {
		mSpinner = (Spinner) findViewById(R.id.spinner_fee_type);
		String[] standardsFee = new String[mOtherStandardFees.size()];
		for (int i = 0; i < mOtherStandardFees.size(); i++) {
			standardsFee[i] = mOtherStandardFees.get(i).getName();
		}
		ArrayAdapter mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, standardsFee);

		// 设置下拉列表风格
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(mAdapter);
		// 监听Item选中事件
		mSpinner.setOnItemSelectedListener(mOnItemSelectedListener);
	}

	private OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			for (StandardFee standardFee : mOtherStandardFees) {
				if (standardFee.getName().equals(
						mOtherStandardFees.get(position).getName())) {
					mSelectedFeeStandardId = standardFee.getFeeStandardID();
					mEditFeeName.setText(standardFee.getName());
					mEditPrice.setText(standardFee.getPrice() + "");
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}

	};

	private void initOtherStandardFee() {
		for (StandardFee standardFee : PropertyService.getInstance().StandardFees) {
			// 其他费
			if (standardFee.getFeeType() == 8) {
				mOtherStandardFees.add(standardFee);
			}
		}
		if (mOtherStandardFees.size() > 0) {
			mSelectedFeeStandardId = mOtherStandardFees.get(0)
					.getFeeStandardID();
		} else {
			Toast.makeText(getApplicationContext(), "小区没有其他费用信息", Toast.LENGTH_LONG).show();
			finish();
		}
	}

}
