package com.jason.property;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jason.property.data.PropertyService;
import com.jason.property.model.InputTable;
import com.jason.property.net.PropertyNetworkApi;
import com.loopj.android.http.JsonHttpResponseHandler;

public class MeterReadFragment extends Fragment {

	protected static final String TAG = "MeterReadFragment";
	private EditText editRoomNo;
	private String roomId;
	private TextView txtRoomInfo;
	private TextView txtAccountAmount;
	private LayoutInflater mLayoutInflater;
	private ListView listInputTable;
	private InputTableAdapter adapter;
	private Button btnAddInputTable;
	private Button btnPrev;
	private Button btnNext;

	private ArrayList<InputTable> inputTables = new ArrayList<InputTable>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLayoutInflater = LayoutInflater.from(getActivity());
		adapter = new InputTableAdapter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_meter_layout, null);
		Button btnQuery = (Button) view.findViewById(R.id.btn_query);
		btnQuery.setOnClickListener(mOnClickListener);
		btnPrev = (Button) view.findViewById(R.id.btn_prev);
		btnPrev.setOnClickListener(mOnBtnPrevClickListener);
		btnNext = (Button) view.findViewById(R.id.btn_next);
		btnNext.setOnClickListener(mOnBtnNextClickListener);
		editRoomNo = (EditText) view.findViewById(R.id.edit_room_no);
		txtRoomInfo = (TextView) view.findViewById(R.id.txt_room_info);
		txtAccountAmount = (TextView) view
				.findViewById(R.id.txt_account_amount);
		btnAddInputTable = (Button) view.findViewById(R.id.btn_add_input_table);
		btnAddInputTable.setOnClickListener(OnBtnAddClickListener);
		listInputTable = (ListView) view.findViewById(R.id.list_meter_read);
		listInputTable.setAdapter(adapter);
		return view;
	}

	private OnClickListener OnBtnAddClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String employeeId = PropertyService.getInstance().getUserInfo()
					.getEmployeeId();
			String areaId = PropertyService.getInstance().getUserInfo()
					.getAreaId();
			PropertyNetworkApi.getInstance().addInputTable(employeeId, areaId,
					roomId, inputTables, addInputTableResponseHandler);
		}
	};

	private JsonHttpResponseHandler addInputTableResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e(TAG, arg1);
		}

		@Override
		public void onSuccess(JSONObject object) {
			Log.d(TAG, "addInputTableResponseHandler:" + object.toString());
			try {
				int resultCode = object.getInt("ResultCode");
				String erroMsg = object.getString("ErrorMessage");
				if (resultCode != 1) {
					Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : "
							+ resultCode);
					Toast.makeText(getActivity(), "抄表失败" + erroMsg,
							Toast.LENGTH_LONG).show();
					return;
				}

				Toast.makeText(getActivity(), "抄表成功", Toast.LENGTH_LONG).show();

			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	};

	private OnClickListener mOnBtnPrevClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			inputTables.clear();
			String roomCode = editRoomNo.getText().toString();
			String employeeId = PropertyService.getInstance().getUserInfo()
					.getEmployeeId();
			String areaId = PropertyService.getInstance().getUserInfo()
					.getAreaId();
			PropertyNetworkApi.getInstance().getRoomInfo(employeeId, areaId,
					roomCode, mGetPrevRoomInfoResponseHandler);
		}
	};

	private OnClickListener mOnBtnNextClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			inputTables.clear();
			String roomCode = editRoomNo.getText().toString();
			String employeeId = PropertyService.getInstance().getUserInfo()
					.getEmployeeId();
			String areaId = PropertyService.getInstance().getUserInfo()
					.getAreaId();
			PropertyNetworkApi.getInstance().getRoomInfo(employeeId, areaId,
					roomCode, mGetNextRoomInfoResponseHandler);
		}
	};

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			inputTables.clear();
			String roomCode = editRoomNo.getText().toString();
			String employeeId = PropertyService.getInstance().getUserInfo()
					.getEmployeeId();
			String areaId = PropertyService.getInstance().getUserInfo()
					.getAreaId();
			PropertyNetworkApi.getInstance().getRoomInfo(employeeId, areaId,
					roomCode, mGetRoomInfoResponseHandler);
		}
	};

	private JsonHttpResponseHandler getInputTableResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e(TAG, arg1);
		}

		@Override
		public void onSuccess(JSONObject object) {
			Log.d(TAG, "getInputTableResponseHandler:" + object.toString());
			try {
				int resultCode = object.getInt("ResultCode");
				String erroMsg = object.getString("ErrorMessage");
				if (resultCode != 1) {
					Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : "
							+ resultCode);
					return;
				}

				JSONArray jsonArray = object.getJSONArray("Data");
				for (int i = 0; i < jsonArray.length(); i++) {
					InputTable table = convertJSONObjectToInvoice(jsonArray
							.getJSONObject(i));
					inputTables.add(table);
				}

				adapter.notifyDataSetChanged();
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	};

	private InputTable convertJSONObjectToInvoice(JSONObject invoiceObject)
			throws JSONException {
		InputTable table = new InputTable();
		table.setEndDegree(invoiceObject.getString("EndDegree"));
		table.setEquipmentNumber(invoiceObject.getString("EquipmentNumber"));
		table.setFeeStandardID(invoiceObject.getString("FeeStandardID"));
		table.setFeeType(invoiceObject.getString("FeeType"));
		table.setName(invoiceObject.getString("Name"));
		table.setObjectID(invoiceObject.getString("ObjectID"));
		table.setObjectType(invoiceObject.getString("ObjectType"));
		table.setPrice(invoiceObject.getString("Price"));
		table.setStartDegree(invoiceObject.getString("StartDegree"));

		return table;
	}

	private JsonHttpResponseHandler mGetRoomInfoResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e(TAG, arg1);
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
				JSONObject data = object.getJSONObject("Data");
				roomId = data.getString("RoomID");
				Log.d(TAG, "roomId:" + roomId);

				txtRoomInfo.setText(data.getString("OwnerName") + " , "
						+ data.getString("BuildArea"));
				txtAccountAmount.setText("账户余额:"
						+ data.getString("AccountAmount"));

				String employeeId = PropertyService.getInstance().getUserInfo()
						.getEmployeeId();
				String areaId = PropertyService.getInstance().getUserInfo()
						.getAreaId();
				PropertyNetworkApi.getInstance().getInputTable(employeeId,
						areaId, roomId, getInputTableResponseHandler);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	};
	private JsonHttpResponseHandler mGetPrevRoomInfoResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e(TAG, arg1);
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
				JSONObject data = object.getJSONObject("Data");
				roomId = data.getString("RoomID");
				Log.d(TAG, "roomId:" + roomId);

				txtRoomInfo.setText(data.getString("OwnerName") + " , "
						+ data.getString("BuildArea"));
				txtAccountAmount.setText("账户余额:"
						+ data.getString("AccountAmount"));

				String employeeId = PropertyService.getInstance().getUserInfo()
						.getEmployeeId();
				String areaId = PropertyService.getInstance().getUserInfo()
						.getAreaId();
				PropertyNetworkApi.getInstance().getInputTable(employeeId,
						areaId, roomId, getInputTableResponseHandler);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	};

	private JsonHttpResponseHandler mGetNextRoomInfoResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onFailure(Throwable arg0, String arg1) {
			super.onFailure(arg0, arg1);
			Log.e(TAG, arg1);
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
				JSONObject data = object.getJSONObject("Data");
				roomId = data.getString("RoomID");
				Log.d(TAG, "roomId:" + roomId);

				txtRoomInfo.setText(data.getString("OwnerName") + " , "
						+ data.getString("BuildArea"));
				txtAccountAmount.setText("账户余额:"
						+ data.getString("AccountAmount"));

				String employeeId = PropertyService.getInstance().getUserInfo()
						.getEmployeeId();
				String areaId = PropertyService.getInstance().getUserInfo()
						.getAreaId();
				PropertyNetworkApi.getInstance().getInputTable(employeeId,
						areaId, roomId, getInputTableResponseHandler);
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	};

	private class InputTableAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return inputTables.size();
		}

		@Override
		public Object getItem(int position) {
			return inputTables.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			convertView = mLayoutInflater.inflate(
					R.layout.item_input_table_layout, null);
			TextView txtFeeType = (TextView) convertView
					.findViewById(R.id.txt_fee_type);
			EditText editStart = (EditText) convertView
					.findViewById(R.id.edit_start_degree);
			EditText txtEnd = (EditText) convertView
					.findViewById(R.id.edit_end_degree);

			txtFeeType.setText(inputTables.get(position).getName());
			editStart.setText(inputTables.get(position).getStartDegree());
			txtEnd.setText(inputTables.get(position).getEndDegree());

			txtEnd.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					final InputTable table = inputTables.get(position);
					table.setEndDegree(s.toString());
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});

			return convertView;
		}
	}
}
