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
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothprinter.BlueToothService;
import com.jason.property.data.PropertyService;
import com.jason.property.model.Invoice;
import com.jason.property.net.PropertyNetworkApi;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TicketFragment extends ListFragment {

	protected static final String TAG = "TicketFragment";
	private TextView mTxtStartDate;
	private TextView mTextEndDate;
	private EditText mEditRoomNo;

	private Button mBtnQuery;
	private Button btnRevoke;
	private RadioButton rbtnSelected;
	private int selectPosition = -1;

	private DatePickerDialog mDatePickerDialog;
	private DatePickerDialog mDatePickerDialogEnd;

	private Calendar mCalendar = Calendar.getInstance();
	private TicketAdapter ticketAdapter = new TicketAdapter();

	private ArrayList<Invoice> invoices = new ArrayList<Invoice>();

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private LayoutInflater mLayoutInflater;
	private Button btnRepair;

	private String roomId;
	private ProgressDialog mProgressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ticket_layout,
				container, false);
		mTxtStartDate = (TextView) view.findViewById(R.id.txt_start_date);
		mTxtStartDate.setOnClickListener(new OnStartDateClickListener());
		mTextEndDate = (TextView) view.findViewById(R.id.txt_end_date);
		mTextEndDate.setOnClickListener(new OnEndDateClickListener());
		mBtnQuery = (Button) view.findViewById(R.id.btn_query);
		mEditRoomNo = (EditText) view.findViewById(R.id.edit_room_no);
		mBtnQuery.setOnClickListener(mOnBtnQueryClickListener);
		btnRevoke = (Button) view.findViewById(R.id.btn_revoke);
		btnRevoke.setOnClickListener(new OnBtnRevokeClickListener());
		btnRepair = (Button) view.findViewById(R.id.btn_repair);
		btnRepair.setOnClickListener(new OnBtnRepaireClickListener());

		return view;
	}

	private class OnBtnRepaireClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String employeeId = PropertyService.getInstance().getUserInfo()
					.getEmployeeId();
			String areaId = PropertyService.getInstance().getUserInfo()
					.getAreaId();
			if (selectPosition < 0) {
				Toast.makeText(getActivity(), "请选择需要补打的行", 1).show();
				return;
			}
			mProgressDialog.show();
			String payId = invoices.get(selectPosition).getPayId();
			String notes = invoices.get(selectPosition).getNotes();
			PropertyNetworkApi.getInstance().repairPrint(employeeId, areaId,
					roomId, payId, mRepairResponseHandler);
		}

	}

	private JsonHttpResponseHandler mRepairResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e(TAG, arg1);
			mProgressDialog.dismiss();
		}

		@Override
		public void onSuccess(JSONObject object) {
			Log.d(TAG, "mRepairResponseHandler :" + object.toString());
			try {
				int resultCode = object.getInt("ResultCode");
				String erroMsg = object.getString("ErrorMessage");
				if (resultCode != 1) {
					Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : "
							+ resultCode);
					return;
				}

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
					SharedPreferences mPrefs = getActivity().getPreferences(
							Context.MODE_PRIVATE);
					String addr = mPrefs.getString(
							ChargeFragment.EXTRA_KEY_PARED_ADDR, "");
					if (!TextUtils.isEmpty(addr)) {
						Toast.makeText(getActivity(), "正在连接设备",
								Toast.LENGTH_LONG).show();
						startBlueTulth();
						if (btService.IsOpen()) {
							// 蓝牙已经打开
							printFragment.connectAndPrint(addr);
						}
					}
				}
				mProgressDialog.dismiss();
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == ChargeFragment.REQUEST_CODE_START_BLUETUTH) {
				MainActivity activity = (MainActivity) getActivity();
				PrintFragment printFragment = activity.mPrintFragment;
				SharedPreferences mPrefs = getActivity().getPreferences(
						Context.MODE_PRIVATE);
				String addr = mPrefs.getString(
						ChargeFragment.EXTRA_KEY_PARED_ADDR, "");
				printFragment.connectAndPrint(addr);
			}
		}
	}

	private void startBlueTulth() {
		Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(mIntent,
				ChargeFragment.REQUEST_CODE_START_BLUETUTH);
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

	private class OnBtnRevokeClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String employeeId = PropertyService.getInstance().getUserInfo()
					.getEmployeeId();
			String areaId = PropertyService.getInstance().getUserInfo()
					.getAreaId();
			if (selectPosition < 0) {
				Toast.makeText(getActivity(), "请选择需要撤销的行", 1).show();
				return;
			}
			mProgressDialog.show();
			String payId = invoices.get(selectPosition).getPayId();
			String notes = invoices.get(selectPosition).getNotes();
			PropertyNetworkApi.getInstance().RevokePay(employeeId, areaId,
					roomId, payId, notes, mRevokeResponseHandler);
		}
	}

	private JsonHttpResponseHandler mRevokeResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e(TAG, arg1);
			mProgressDialog.dismiss();
		}

		@Override
		public void onSuccess(JSONObject object) {
			Log.d(TAG, "mRevokeResponseHandler :" + object.toString());
			try {
				int resultCode = object.getInt("ResultCode");
				String erroMsg = object.getString("ErrorMessage");
				if (resultCode != 1) {
					Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : "
							+ resultCode);
					Toast.makeText(getActivity(), "撤销成功", Toast.LENGTH_LONG)
							.show();
					return;
				}
				mProgressDialog.dismiss();
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	};

	private class OnStartDateClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			mDatePickerDialog.show();
		}
	}

	private class OnEndDateClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			mDatePickerDialogEnd.show();
		}
	}

	private OnClickListener mOnBtnQueryClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (TextUtils.isEmpty(mEditRoomNo.getText())
					|| TextUtils.isEmpty(mTxtStartDate.getText())
					|| TextUtils.isEmpty(mTextEndDate.getText())) {
				return;
			}

			if (mProgressDialog == null) {
				mProgressDialog = ProgressDialog.show(getActivity(), "Loading",
						"正在操作...", true, true);
			}
			mProgressDialog.show();

			getListView().setAdapter(ticketAdapter);
			String employeeId = PropertyService.getInstance().getUserInfo()
					.getEmployeeId();
			String areaId = PropertyService.getInstance().getUserInfo()
					.getAreaId();
			String roomCode = mEditRoomNo.getText().toString();
			String startDate = (String) mTxtStartDate.getText();
			String endDate = (String) mTextEndDate.getText();
			PropertyNetworkApi.getInstance().GetInvoice(employeeId, areaId,
					roomCode, startDate, endDate, mGetInvoiceResponseHandler);
		}
	};

	private JsonHttpResponseHandler mGetInvoiceResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e(TAG, arg1);
			mProgressDialog.dismiss();
		}

		@Override
		public void onSuccess(JSONObject object) {
			Log.d(TAG, "mGetInvoiceResponse :" + object.toString());
			try {
				int resultCode = object.getInt("ResultCode");
				String erroMsg = object.getString("ErrorMessage");
				if (resultCode != 1) {
					Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : "
							+ resultCode);
					return;
				}
				invoices.clear();
				JSONArray jsonArray = object.getJSONArray("Data");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject invoiceObject = jsonArray.getJSONObject(i);
					Invoice invoice = convertJSONObjectToInvoice(invoiceObject);
					invoices.add(invoice);
				}
				ticketAdapter.notifyDataSetChanged();
				Log.e(TAG, "invoices size is:" + invoices.size());

				String roomCode = mEditRoomNo.getText().toString();
				String employeeId = PropertyService.getInstance().getUserInfo()
						.getEmployeeId();
				String areaId = PropertyService.getInstance().getUserInfo()
						.getAreaId();
				PropertyNetworkApi.getInstance().getRoomInfo(employeeId,
						areaId, roomCode, mGetRoomInfoResponseHandler);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	};

	private JsonHttpResponseHandler mGetRoomInfoResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e(TAG, arg1);
			mProgressDialog.dismiss();
		}

		@Override
		public void onSuccess(JSONObject object) {
			Log.d(TAG, "mGetInvoiceResponse :" + object.toString());
			try {
				int resultCode = object.getInt("ResultCode");
				String erroMsg = object.getString("ErrorMessage");
				if (resultCode != 1) {
					Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : "
							+ resultCode);
					return;
				}
				roomId = object.getJSONObject("Data").getString("RoomID");
				Log.d(TAG, "roomId:" + roomId);
				mProgressDialog.dismiss();
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	};

	public static Invoice convertJSONObjectToInvoice(JSONObject invoiceObject)
			throws JSONException {
		Invoice invoice = new Invoice();
		invoice.setAccountAmount(invoiceObject.getString("AccountAmount"));
		invoice.setAmount(invoiceObject.getString("Amount"));
		invoice.setEmployeeName(invoiceObject.getString("EmployeeName"));
		invoice.setNotes(invoiceObject.getString("Notes"));
		invoice.setNumber(invoiceObject.getString("Number"));
		invoice.setOrderAmount(invoiceObject.getString("OrderAmount"));
		invoice.setPayDate(invoiceObject.getString("PayDate"));
		invoice.setPayType(invoiceObject.getString("PayType"));
		invoice.setPerAmount(invoiceObject.getString("PerAmount"));
		invoice.setStatus(invoiceObject.getString("Status"));
		invoice.setPayId(invoiceObject.getString("PayID"));
		return invoice;
	}

	private DatePickerDialog.OnDateSetListener mDateListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mCalendar.set(year, monthOfYear, dayOfMonth);
			mTxtStartDate.setText(mDateFormat.format(mCalendar.getTime()));
		}
	};
	private DatePickerDialog.OnDateSetListener mDateEndListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mCalendar.set(year, monthOfYear, dayOfMonth);
			mTextEndDate.setText(mDateFormat.format(mCalendar.getTime()));
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDatePickerDialog = new DatePickerDialog(getActivity(), mDateListener,
				mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
				mCalendar.get(Calendar.DAY_OF_MONTH));
		mDatePickerDialogEnd = new DatePickerDialog(getActivity(),
				mDateEndListener, mCalendar.get(Calendar.YEAR),
				mCalendar.get(Calendar.MONTH),
				mCalendar.get(Calendar.DAY_OF_MONTH));
		mLayoutInflater = LayoutInflater.from(getActivity());
	}

	private class TicketAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return invoices.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return invoices.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mLayoutInflater.inflate(R.layout.item_ticket_layout,
					null);
			TextView txtName = (TextView) convertView
					.findViewById(R.id.txt_name);
			TextView txtTicketNo = (TextView) convertView
					.findViewById(R.id.txt_ticket_no);
			TextView txtStatus = (TextView) convertView
					.findViewById(R.id.txt_status);
			TextView txtAmount = (TextView) convertView
					.findViewById(R.id.txt_amount);
			txtName.setText(invoices.get(position).getEmployeeName());
			txtTicketNo.setText(invoices.get(position).getNumber());
			txtStatus.setText(convertStatusToString(invoices.get(position)
					.getStatus()));
			txtAmount.setText(invoices.get(position).getAmount());

			final int p = position;
			RadioButton rbtnItem = (RadioButton) convertView
					.findViewById(R.id.rbtn_item);
			rbtnItem.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						selectPosition = p;
					} else {
						selectPosition = -1;
					}
					notifyDataSetChanged();
				}
			});
			if (selectPosition == position) {
				rbtnItem.setChecked(true);
			} else {
				rbtnItem.setChecked(false);
			}

			return convertView;
		}
	}

	private String convertStatusToString(String status) {
		String ret = "";
		if ("1".equals(status)) {
			ret = "预打票未收费";
		} else if ("2".equals(status)) {
			ret = "已收费";
		} else if ("-10".equals(status)) {
			ret = "废票";
		}
		return ret;
	}
}
