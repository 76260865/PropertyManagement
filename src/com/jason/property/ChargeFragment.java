package com.jason.property;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothprinter.BlueToothService;
import com.jason.property.adapter.ArrearsAdapter;
import com.jason.property.data.PropertyService;
import com.jason.property.model.Area;
import com.jason.property.model.ArrearInfo;
import com.jason.property.model.Equipment;
import com.jason.property.model.RoomInfo;
import com.jason.property.model.StandardFee;
import com.jason.property.model.UserInfo;
import com.jason.property.net.PropertyNetworkApi;
import com.loopj.android.http.JsonHttpResponseHandler;

public class ChargeFragment extends Fragment {
	private static final String TAG = "ChargeActivity";

	public static final String EXTRA_KEY_PARED_ADDR = "extra_key_pared_addr";

	private static final int REQUEST_CODE_CONFIRM_PRINT = 1000;

	public static final int REQUEST_CODE_START_BLUETUTH = 1001;

	private static final int REQUEST_CODE_START_ADD_OTHER_FEE = 1002;

	private Button mBtnQuery;

	private TextView mBtnCal;

	private EditText mEditRoomNo;

	private EditText mEditActualAmount;

	private TextView mTxtRoomInfo;

	private Spinner mSpinChangeArea;

	private ExpandableListView mExpandableListView;

	private ArrearsAdapter mArrearsAdapter;

	private static DateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");

	private TextView mBtnCharge, mBtnReprint;

	private TextView mTxtTotalPrice;

	private TextView mTxtInfo;

	private TextView mTxtAccountAmount;

	private SharedPreferences mPrefs;

	public interface ChargeFragmentCallback {
		void onDataChange();

		void editArrear(Intent intent);

		void addOtherFee();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_charge, null);

		mBtnQuery = (Button) view.findViewById(R.id.btn_query);
		mBtnCal = (TextView) view.findViewById(R.id.btn_cal);
		mBtnCal.setOnClickListener(mOnBtnCalClickListener);
		mBtnQuery.setOnClickListener(mOnBtnQueryClickListener);
		mEditRoomNo = (EditText) view.findViewById(R.id.edit_room_no);
		mEditActualAmount = (EditText) view
				.findViewById(R.id.edit_actual_amount);
		mTxtRoomInfo = (TextView) view.findViewById(R.id.txt_room_info);
		mExpandableListView = (ExpandableListView) view
				.findViewById(R.id.expand_list);
		mExpandableListView.setCacheColorHint(0);
		mSpinChangeArea = (Spinner) view.findViewById(R.id.spin_change_area);
		// mSpinChangeArea.setOnItemSelectedListener(mOnSpinChangeAreaItemSelectListener);
		mBtnCharge = (TextView) view.findViewById(R.id.btn_charge);
		mBtnReprint = (TextView) view.findViewById(R.id.btn_reprint);
		mBtnCharge.setOnClickListener(mOnBtnChargeClickListener);
		mBtnReprint.setOnClickListener(mOnBtnReprintClickListener);
		mTxtTotalPrice = (TextView) view.findViewById(R.id.txt_total_price);
		mTxtInfo = (TextView) view.findViewById(R.id.txt_info);
		mTxtAccountAmount = (TextView) view
				.findViewById(R.id.txt_account_amount);

		// bind the data to spinner
		// setAreaAdapter();

		getActivity().setTitle("当前小区 : ");
		return view;
	}

	private OnClickListener mOnBtnCalClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (TextUtils.isEmpty(mEditRoomNo.getText().toString())) {
				Toast.makeText(getActivity(), "请输入房间号", 1).show();
				return;
			}
			String employeeId = PropertyService.getInstance().getUserInfo()
					.getEmployeeId();
			String areaId = PropertyService.getInstance().getUserInfo()
					.getAreaId();
			String roomCode = mEditRoomNo.getText().toString();
			int roomId = PropertyService.getInstance().getRoomInfo()
					.getRoomId();
			float actualAmount = 0;
			try {
				actualAmount = Float.valueOf(mEditActualAmount.getText()
						.toString());
			} catch (NumberFormatException e) {
				Log.e(TAG, e.getMessage());
				Toast.makeText(getActivity(), "请输入正确的金额", Toast.LENGTH_LONG)
						.show();
				return;
			}
			PropertyNetworkApi.getInstance().calFee(employeeId, areaId,
					roomId + "", actualAmount + "", mCalFeeResponseHandler);
		}
	};

	private void setAreaAdapter() {
		List<String> list = new ArrayList<String>();
		int index = 0;

		if (PropertyService.getInstance().getUserInfo() == null) {
			Toast.makeText(getActivity(), "请重新登录!", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
			getActivity().finish();
			return;
		}

		for (int i = 0; i < PropertyService.getInstance().getUserInfo()
				.getAreas().size(); i++) {
			Area area = PropertyService.getInstance().getUserInfo().getAreas()
					.get(i);
			list.add(area.getAreaName());
			if (area.getAreaId().equals(
					PropertyService.getInstance().getUserInfo().getAreaId())) {
				index = i;
			}
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinChangeArea.setAdapter(dataAdapter);
		mSpinChangeArea.setSelection(index);
	}

	private OnClickListener mOnBtnQueryClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			String employeeId = PropertyService.getInstance().getUserInfo()
					.getEmployeeId();
			String areaId = PropertyService.getInstance().getUserInfo()
					.getAreaId();
			String roomCode = mEditRoomNo.getText().toString();
			mBtnQuery.setText(R.string.btn_query_loading_text);
			PropertyNetworkApi.getInstance().getRoomInfo(employeeId, areaId,
					roomCode, mRoomInfoJsonHandler);
			mTxtAccountAmount.setText("");
			mTxtInfo.setText("");
		}
	};

	private JsonHttpResponseHandler mRoomInfoJsonHandler = new JsonHttpResponseHandler() {
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e(TAG, arg1);
			Toast.makeText(getActivity(), arg1, Toast.LENGTH_SHORT).show();
			mBtnQuery.setText(R.string.btn_query_text);
		}

		@Override
		public void onSuccess(JSONObject object) {
			mBtnQuery.setText(R.string.btn_query_text);
			Log.d(TAG, "get room info :" + object.toString());
			try {
				int resultCode = object.getInt("ResultCode");
				String erroMsg = object.getString("ErrorMessage");
				if (resultCode != 1) {
					Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : "
							+ resultCode);
					Toast.makeText(getActivity(), erroMsg, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				JSONObject dataObj = object.getJSONObject("Data");
				RoomInfo roomInfo = new RoomInfo();
				roomInfo.setRoomId(dataObj.getInt("RoomID"));
				roomInfo.setRoomCode(dataObj.getString("RoomCode"));
				roomInfo.setBuildArea(dataObj.getDouble("BuildArea"));
				roomInfo.setUseArea(dataObj.getDouble("UseArea"));
				try {
					roomInfo.setReceiveDate(mFormatter.format(mFormatter
							.parse(dataObj.getString("ReceiveDate"))));
				} catch (ParseException e) {
					Log.e(TAG, e.getMessage());
				}
				roomInfo.setOwnerName(dataObj.getString("OwnerName"));
				roomInfo.setAccountAmount(dataObj.getLong("AccountAmount"));
				JSONArray equipments = dataObj.getJSONArray("Equipments");
				for (int i = 0; i < equipments.length(); i++) {
					JSONObject obj = equipments.getJSONObject(i);
					Equipment equipment = new Equipment();
					equipment.setEquipmentId(obj.getInt("EquipmentID"));
					equipment.setEquipmentId(obj.getInt("RoomID"));
					equipment.setEquipmentId(obj.getInt("EquipmentType"));
					roomInfo.getEquipments().add(equipment);
				}
				PropertyService.getInstance().setRoomInfo(roomInfo);
				mTxtRoomInfo.setText(Html.fromHtml(getResources().getString(
						R.string.txt_room_info_format_text,
						"<b>" + roomInfo.getRoomCode() + "</b>",
						"<b>" + roomInfo.getOwnerName() + "</b><br />",
						roomInfo.getReceiveDate(),
						"<b>" + roomInfo.getBuildArea())
						+ "</b>"));
				getArrearsInfo();
				mTxtAccountAmount.setText("账户余额 :"
						+ PropertyService.getInstance().getRoomInfo()
								.getAccountAmount());
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
				Toast.makeText(getActivity(), e.getMessage(),
						Toast.LENGTH_SHORT).show();
				return;
			}
		}
	};

	private void getArrearsInfo() {
		String employeeId = PropertyService.getInstance().getUserInfo()
				.getEmployeeId();
		String areaId = PropertyService.getInstance().getUserInfo().getAreaId();
		int roomId = PropertyService.getInstance().getRoomInfo().getRoomId();
		PropertyService.getInstance().Arrears.clear();
		PropertyService.getInstance().TempArrears.clear();
		PropertyService.getInstance().PreArrears.clear();
		PropertyNetworkApi.getInstance().getArrearInfo(employeeId, areaId,
				String.valueOf(roomId), mArrearJsonHandler);
	}

	private JsonHttpResponseHandler mArrearJsonHandler = new JsonHttpResponseHandler() {
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e(TAG, arg1);
			Toast.makeText(getActivity(), arg1, Toast.LENGTH_SHORT).show();
			setFeesAdapter();
			countTotalPrice();
		}

		@Override
		public void onSuccess(JSONObject object) {
			Log.d(TAG, "get arrear return info :" + object.toString());
			try {
				int resultCode = object.getInt("ResultCode");
				String erroMsg = object.getString("ErrorMessage");
				if (resultCode != 1) {
					Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : "
							+ resultCode);
					Toast.makeText(getActivity(), erroMsg, Toast.LENGTH_SHORT)
							.show();
					return;
				}

				JSONObject dataObj = object.getJSONObject("Data");
				// get the Arrears info
				PropertyService.getInstance().Arrears.clear();
				JSONArray arrears = dataObj.getJSONArray("Arrears");
				for (int i = 0; i < arrears.length(); i++) {
					JSONObject arrearObj = arrears.getJSONObject(i);
					ArrearInfo arrearInfo = convertJSONObjectToArrear(arrearObj);
					PropertyService.getInstance().Arrears.add(arrearInfo);
				}

				// get temp Arrears info
				PropertyService.getInstance().TempArrears.clear();
				JSONArray tempArrears = dataObj.getJSONArray("TempArrears");
				for (int i = 0; i < tempArrears.length(); i++) {
					JSONObject arrearObj = tempArrears.getJSONObject(i);
					ArrearInfo arrearInfo = convertJSONObjectToArrear(arrearObj);
					PropertyService.getInstance().TempArrears.add(arrearInfo);
				}

				// get pre pay
				PropertyService.getInstance().PreArrears.clear();
				JSONArray prePays = dataObj.getJSONArray("PrePay");
				for (int i = 0; i < prePays.length(); i++) {
					JSONObject arrearObj = prePays.getJSONObject(i);
					ArrearInfo arrearInfo = convertJSONObjectToArrear(arrearObj);
					arrearInfo.setCount(0);
					arrearInfo.setEndDegree(0);
					arrearInfo.setPayEndDate("");
					PropertyService.getInstance().PreArrears.add(arrearInfo);
				}
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
			setFeesAdapter();
			// countTotalPrice();
			// mBtnCharge.setEnabled(true);
			mEditActualAmount.setText("0.00");
		}
	};

	private void setFeesAdapter() {
		mArrearsAdapter = new ArrearsAdapter(getActivity());
		mArrearsAdapter.setDataChangeCallback(new ChargeFragmentCallback() {

			@Override
			public void onDataChange() {
				mEditActualAmount.setText("0.00");
				mBtnCharge.setEnabled(false);
				countTotalPrice();
			}

			@Override
			public void editArrear(Intent intent) {
				startActivityForResult(intent, REQUEST_CODE_CONFIRM_PRINT);
			}

			@Override
			public void addOtherFee() {
				Intent intent = new Intent(getActivity(),
						AddOtherFeeActivity.class);
				startActivityForResult(intent, REQUEST_CODE_START_ADD_OTHER_FEE);
			}
		});
		mExpandableListView.setAdapter(mArrearsAdapter);
	}

	public static ArrearInfo convertJSONObjectToArrear(JSONObject arrearObj)
			throws JSONException {
		ArrearInfo arrearInfo = new ArrearInfo();
		arrearInfo.setInputTableId(arrearObj.getInt("InputTableID"));
		arrearInfo.setObjectType(arrearObj.getInt("ObjectType"));
		arrearInfo.setObjectID(arrearObj.getInt("ObjectID"));
		arrearInfo.setPrice(arrearObj.getDouble("Price"));
		arrearInfo.setAmount(arrearObj.getDouble("Amount"));
		Log.d(TAG, "StartDegree:" + arrearObj.getDouble("StartDegree"));
		arrearInfo.setStartDegree(arrearObj.getDouble("StartDegree"));
		arrearInfo.setEndDegree(arrearObj.getDouble("EndDegree"));
		try {
			arrearInfo.setPayStartDate(mFormatter.format(mFormatter
					.parse(arrearObj.getString("PayStartDate"))));
			arrearInfo.setPayEndDate(mFormatter.format(mFormatter
					.parse(arrearObj.getString("PayEndDate"))));
		} catch (ParseException e) {
			Log.d(TAG, "PayStartDate:" + arrearObj.getString("PayStartDate"));
			Log.d(TAG, "PayEndDate:" + arrearObj.getString("PayEndDate"));
			Log.e(TAG, e.getMessage());
		}

		arrearInfo.setStatus(arrearObj.getInt("Status"));
		arrearInfo.setFeeType(arrearObj.getInt("FeeType"));
		arrearInfo.setName(arrearObj.getString("Name"));
		arrearInfo.setFeeStandardID(arrearObj.getInt("FeeStandardID"));
		return arrearInfo;
	}

	// �л�С��
	public OnItemSelectedListener mOnSpinChangeAreaItemSelectListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// update the current area
			UserInfo userInfo = PropertyService.getInstance().getUserInfo();
			Area area = userInfo.getAreas().get(position);
			PropertyService.getInstance().getUserInfo()
					.setAreaId(area.getAreaId());
			PropertyService.getInstance().getUserInfo()
					.setAreaName(area.getAreaName());
			Log.d(TAG, "updated current area info:" + area.getAreaId()
					+ "area name:" + area.getAreaName());
			// TODO: clear all the values
			PropertyService.getInstance().setRoomInfo(null);
			mTxtRoomInfo.setText(null);
			mTxtTotalPrice.setText(null);
			PropertyService.getInstance().Arrears.clear();
			PropertyService.getInstance().TempArrears.clear();
			PropertyService.getInstance().PreArrears.clear();
			setFeesAdapter();
			mBtnCharge.setEnabled(false);

			PropertyNetworkApi.getInstance().getStandardFee(
					userInfo.getEmployeeId(), userInfo.getAreaId(),
					userInfo.getCompanyCode(), mStandardResponseHandler);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// Do nothing
		}
	};

	private JsonHttpResponseHandler mStandardResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e(TAG, arg1);
			Toast.makeText(getActivity(), arg1, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onSuccess(JSONObject object) {
			Log.d(TAG, "standard fee:" + object.toString());
			// PropertyService.getInstance().StandardFees
			try {
				int resultCode = object.getInt("ResultCode");
				String erroMsg = object.getString("ErrorMessage");
				if (resultCode != 1) {
					Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : "
							+ resultCode);
					Toast.makeText(getActivity(), erroMsg, Toast.LENGTH_SHORT)
							.show();
					return;
				}

				// get the Arrears info
				PropertyService.getInstance().StandardFees.clear();
				JSONArray standards = object.getJSONArray("Data");
				for (int i = 0; i < standards.length(); i++) {
					JSONObject standardObj = standards.getJSONObject(i);
					StandardFee standFee = convertJSONObjectToStandard(standardObj);
					PropertyService.getInstance().StandardFees.add(standFee);
				}
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	};

	private StandardFee convertJSONObjectToStandard(JSONObject standardObj)
			throws JSONException {
		StandardFee fee = new StandardFee();
		fee.setFeeStandardID(standardObj.getInt("FeeStandardID"));
		fee.setName(standardObj.getString("Name"));
		fee.setFeeType(standardObj.getInt("FeeType"));
		fee.setPrice(standardObj.getDouble("Price"));
		fee.setCompanyID(standardObj.getInt("CompanyID"));
		fee.setAreaID(standardObj.getInt("AreaID"));
		fee.setRelationArea(standardObj.getInt("RelationArea"));
		return fee;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_CONFIRM_PRINT) {
				mEditActualAmount.setText("0.00");
				mBtnCharge.setEnabled(false);
				mArrearsAdapter.notifyDataSetChanged();
				// countTotalPrice();
			} else if (requestCode == REQUEST_CODE_START_BLUETUTH) {
				// FragmentManager mFragmentManager =
				// getActivity().getSupportFragmentManager();
				// PrintFragment printFragment = (PrintFragment)
				// mFragmentManager
				// .findFragmentById(R.id.print_fragment);
				MainActivity activity = (MainActivity) getActivity();
				PrintFragment printFragment = activity.mPrintFragment;
				mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
				String addr = mPrefs.getString(EXTRA_KEY_PARED_ADDR, "");
				printFragment.connectAndPrint(addr);
			} else if (requestCode == REQUEST_CODE_START_ADD_OTHER_FEE) {
				mArrearsAdapter.notifyDataSetChanged();
				mEditActualAmount.setText("0.00");
				mBtnCharge.setEnabled(false);
			}
		}
	}

	private double countTotalPrice() {
		double totalPrice = 0;
		for (ArrearInfo area : PropertyService.getInstance().Arrears) {
			totalPrice += area.getAmount();
		}
		for (ArrearInfo area : PropertyService.getInstance().TempArrears) {
			totalPrice += area.getAmount();
		}
		for (ArrearInfo area : PropertyService.getInstance().PreArrears) {
			totalPrice += area.getAmount();
		}
		DecimalFormat df = new DecimalFormat("#.00");
		mTxtTotalPrice.setText(getString(R.string.txt_total_price_format_text,
				df.format(totalPrice)));
		mEditActualAmount.setText(df.format(totalPrice));
		return totalPrice;
	}

	private OnClickListener mOnBtnChargeClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if(!mArrearsAdapter.hasData()) {
				Toast.makeText(getActivity(), "收费项目为空", Toast.LENGTH_LONG).show();
				return;
			}
			
			mBtnCharge.setEnabled(false);
			new MyDialogFragment().show(getChildFragmentManager(), "dialog");
		}
	};

	private OnClickListener mOnBtnReprintClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// FragmentManager mFragmentManager =
			// getActivity().getSupportFragmentManager();
			// ChargeFragment mChargeFragment = (ChargeFragment)
			// mFragmentManager
			// .findFragmentById(R.id.charge_fragment);
			// PrintFragment printFragment = (PrintFragment) mFragmentManager
			// .findFragmentById(R.id.print_fragment);
			MainActivity activity = (MainActivity) getActivity();
			PrintFragment printFragment = activity.mPrintFragment;
			BlueToothService btService = printFragment.mBTService;
			if (btService.getState() != BlueToothService.STATE_CONNECTED
					&& !TextUtils.isEmpty(printFragment.printStr)) {
				mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
				String addr = mPrefs.getString(EXTRA_KEY_PARED_ADDR, "");
				if (!TextUtils.isEmpty(addr)) {
					Toast.makeText(getActivity(), "正在连接设备", Toast.LENGTH_LONG)
							.show();
					
					if (btService.IsOpen()) {
						// 蓝牙已经打开
						printFragment.connectAndPrint(addr);
					} else {
						startBlueTulth();
					}
				} else {
					Toast.makeText(getActivity(), "暂不能打印", Toast.LENGTH_SHORT)
							.show();
				}
				return;
			} else {
				printIfNesscary(btService, printFragment.printStr);
			}
		}
	};

	private void chargeAndPrint() {
		String employeeId = PropertyService.getInstance().getUserInfo()
				.getEmployeeId();
		String areaId = PropertyService.getInstance().getUserInfo().getAreaId();
		int roomId = PropertyService.getInstance().getRoomInfo().getRoomId();
		DecimalFormat df = new DecimalFormat("#.000");
		float actualAmount = 0;
		try {
			actualAmount = Float
					.valueOf(mEditActualAmount.getText().toString());
		} catch (NumberFormatException e) {
			Log.e(TAG, e.getMessage());
			Toast.makeText(getActivity(), "请输入正确的金额", Toast.LENGTH_LONG).show();
			return;
		}
		PropertyNetworkApi.getInstance().checkAndCharge(employeeId, areaId,
				String.valueOf(roomId), df.format(actualAmount),
				mCheckAndChargeResponseHandler);
	}

	private void startBlueTulth() {
		Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(mIntent, REQUEST_CODE_START_BLUETUTH);
	}

	private void printIfNesscary(BlueToothService btService, String message) {
		byte[] bt = new byte[3];
		bt[0] = 27;
		bt[1] = 56;
		bt[2] = 0;// 1,2//设置字体大小
		btService.write(bt);
		btService.PrintCharacters("\r\n" + message
				+ ".\r\n.\r\n.\r\n.\r\n.\r\n." + message
				+ ".\r\n.\r\n.\r\n.\r\n.\r\n.");

	}

	private JsonHttpResponseHandler mCheckAndChargeResponseHandler = new JsonHttpResponseHandler() {

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e(TAG, arg1);
			mBtnCharge.setEnabled(true);
			Toast.makeText(getActivity(), arg1, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onSuccess(JSONObject object) {
			Log.d(TAG, "CheckAndCharge:" + object.toString());
			try {
				int resultCode = object.getInt("ResultCode");
				String erroMsg = object.getString("ErrorMessage");
				if (resultCode != 1) {
					Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : "
							+ resultCode);
					Toast.makeText(getActivity(), erroMsg, Toast.LENGTH_SHORT)
							.show();
					return;
				}

				Log.d(TAG, "check and charge:" + object);
				Toast.makeText(getActivity(), "收费成功", Toast.LENGTH_SHORT).show();
				mTxtAccountAmount.setText(mTxtAccountAmount.getText() + " \n 收费成功");
				// FragmentManager mFragmentManager =
				// getActivity().getSupportFragmentManager();
				// ChargeFragment mChargeFragment = (ChargeFragment)
				// mFragmentManager
				// .findFragmentById(R.id.charge_fragment);
				// PrintFragment printFragment = (PrintFragment)
				// mFragmentManager
				// .findFragmentById(R.id.print_fragment);
				MainActivity activity = (MainActivity) getActivity();
				PrintFragment printFragment = activity.mPrintFragment;
				BlueToothService btService = printFragment.mBTService;
				String message = object.getString("Data");
				printFragment.printStr = message;
				if (btService != null
						&& btService.getState() == BlueToothService.STATE_CONNECTED) {
					// 如果已经连接，直接打印
					// FIXME: need debug
					printIfNesscary(btService, message);
				} else {
					mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
					String addr = mPrefs.getString(EXTRA_KEY_PARED_ADDR, "");
					if (!TextUtils.isEmpty(addr)) {
						Toast.makeText(getActivity(), "正在连接设备",
								Toast.LENGTH_LONG).show();
						
						if (btService.IsOpen()) {
							// 蓝牙已经打开
							printFragment.connectAndPrint(addr);
						} else {
							startBlueTulth();
						}
					} else {
						// FragmentTransaction transaction =
						// mFragmentManager.beginTransaction();
						// transaction.hide(mChargeFragment);
						// transaction.show(printFragment);
						// transaction.commit();
					}
				}
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	};
	private JsonHttpResponseHandler mCalFeeResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e(TAG, arg1);
			// mBtnCharge.setEnabled(true);
			Toast.makeText(getActivity(), arg1, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onSuccess(JSONObject object) {
			Log.d(TAG, "cal fee:" + object.toString());
			try {
				int resultCode = object.getInt("ResultCode");
				String erroMsg = object.getString("ErrorMessage");
				if (resultCode != 1) {
					Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : "
							+ resultCode);
					Toast.makeText(getActivity(), erroMsg, Toast.LENGTH_SHORT)
							.show();
					return;
				}

				Log.d(TAG, "cal fee:" + object);
				mBtnCharge.setEnabled(true);
				// mEditActualAmount.getText();
				DecimalFormat df = new DecimalFormat("#.00");
				JSONObject dataObj = object.getJSONObject("Data");
				double PerAmount = dataObj.getDouble("PerAmount");
				double GetAmount = dataObj.getDouble("GetAmount");
				double TotalAmount = dataObj.getDouble("TotalAmount");
				double ActualAmount = dataObj.getDouble("actualAmount");
				mTxtInfo.setText("预存款:" + PerAmount + "账户支出:" + GetAmount
						+ "应收金额:" + TotalAmount + "实收金额:" + ActualAmount);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	};

	public static class MyDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new AlertDialog.Builder(getActivity())
					.setTitle(R.string.dialog_confirm_title)
					.setPositiveButton(R.string.dialog_ok_title,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// 确定按钮do something
									// FragmentManager mFragmentManager =
									// getActivity()
									// .getSupportFragmentManager();
									// ChargeFragment mChargeActivity =
									// (ChargeFragment) mFragmentManager
									// .findFragmentById(R.id.charge_fragment);
									MainActivity activity = (MainActivity) getActivity();
									ChargeFragment mChargeActivity = activity.mChargeFragment;
									mChargeActivity.mBtnCharge
											.setEnabled(false);
									mChargeActivity.chargeAndPrint();
								}
							})
					.setNegativeButton(R.string.dialog_cancel_title,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// 取消按钮do something
									// FragmentManager mFragmentManager =
									// getActivity()
									// .getSupportFragmentManager();
									// ChargeFragment mChargeActivity =
									// (ChargeFragment) mFragmentManager
									// .findFragmentById(R.id.charge_fragment);
									MainActivity activity = (MainActivity) getActivity();
									ChargeFragment mChargeActivity = activity.mChargeFragment;
									mChargeActivity.mBtnCharge.setEnabled(true);
								}
							}).create();
		}
	}

}
