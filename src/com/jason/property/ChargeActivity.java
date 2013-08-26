package com.jason.property;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jason.property.adapter.ArrearsAdapter;
import com.jason.property.data.PropertyService;
import com.jason.property.model.Area;
import com.jason.property.model.ArrearInfo;
import com.jason.property.model.Equipment;
import com.jason.property.model.RoomInfo;
import com.jason.property.net.PropertyNetworkApi;
import com.loopj.android.http.JsonHttpResponseHandler;

public class ChargeActivity extends Activity {
    private static final String TAG = "ChargeActivity";

    private Button mBtnQuery;

    private EditText mEditRoomNo;

    private TextView mTxtRoomInfo;

    private Button mBtnArrear;

    private Spinner mSpinChangeArea;

    private TextView mTxtCurrentArea;

    private ListView mListArrears;

    private ListView mListOtherFees;

    private ArrearsAdapter mArrearsAdapter;

    private ArrearsAdapter mOtherFeesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        mBtnQuery = (Button) findViewById(R.id.btn_query);
        mBtnQuery.setOnClickListener(mOnBtnQueryClickListener);
        mEditRoomNo = (EditText) findViewById(R.id.edit_room_no);
        mTxtRoomInfo = (TextView) findViewById(R.id.txt_room_info);
        mBtnArrear = (Button) findViewById(R.id.btn_arrear);
        mBtnArrear.setOnClickListener(mOnBtnArrearClickListener);
        mListArrears = (ListView) findViewById(R.id.lst_arrears);
        mListOtherFees = (ListView) findViewById(R.id.lst_other_fees);
        mTxtCurrentArea = (TextView) findViewById(R.id.txt_current_area);
        mTxtCurrentArea.setText(getResources().getString(R.string.txt_current_area_format_text,
                PropertyService.getInstance().getUserInfo().getAreaName()));
        mSpinChangeArea = (Spinner) findViewById(R.id.spin_change_area);
        mSpinChangeArea.setOnItemSelectedListener(mOnSpinChangeAreaItemSelectListener);

        // bind the data to spinner
        setAreaAdapter();
    }

    private void setAreaAdapter() {
        List<String> list = new ArrayList<String>();
        int index = 0;
        for (int i = 0; i < PropertyService.getInstance().getUserInfo().getAreas().size(); i++) {
            Area area = PropertyService.getInstance().getUserInfo().getAreas().get(i);
            list.add(area.getAreaName());
            if (area.getAreaId().equals(PropertyService.getInstance().getUserInfo().getAreaId())) {
                index = i;
            }
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinChangeArea.setAdapter(dataAdapter);
        mSpinChangeArea.setSelection(index);
    }

    private OnClickListener mOnBtnQueryClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            String employeeId = PropertyService.getInstance().getUserInfo().getEmployeeId();
            String areaId = PropertyService.getInstance().getUserInfo().getAreaId();
            String roomCode = mEditRoomNo.getText().toString();

            PropertyNetworkApi.getInstance().getRoomInfo(employeeId, areaId, roomCode,
                    mRoomInfoJsonHandler);
        }
    };

    private JsonHttpResponseHandler mRoomInfoJsonHandler = new JsonHttpResponseHandler() {

        @Override
        public void onSuccess(JSONObject object) {
            Log.d(TAG, "get room info :" + object.toString());

            try {
                int resultCode = object.getInt("ResultCode");

                String erroMsg = object.getString("ErrorMessage");
                if (resultCode != 1) {
                    Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : " + resultCode);
                    Toast.makeText(getApplicationContext(), erroMsg, Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject dataObj = object.getJSONObject("Data");
                RoomInfo roomInfo = new RoomInfo();
                roomInfo.setRoomId(dataObj.getInt("RoomID"));
                roomInfo.setRoomCode(dataObj.getString("RoomCode"));
                roomInfo.setBuildArea(dataObj.getLong("BuildArea"));
                roomInfo.setUseArea(dataObj.getLong("UseArea"));
                roomInfo.setOwnerName(dataObj.getString("OwnerName"));

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
                mTxtRoomInfo.setText(getResources().getString(R.string.txt_room_info_format_text,
                        roomInfo.getRoomCode(), roomInfo.getOwnerName(), "äº¤æˆ¿æ—¥æœŸ",
                        roomInfo.getBuildArea()));
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }

            getArrearsInfo();
        }
    };

    private OnClickListener mOnBtnArrearClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            getArrearsInfo();
        }
    };

    private void getArrearsInfo() {
        String employeeId = PropertyService.getInstance().getUserInfo().getEmployeeId();
        String areaId = PropertyService.getInstance().getUserInfo().getAreaId();
        int roomId = PropertyService.getInstance().getRoomInfo().getRoomId();

        PropertyNetworkApi.getInstance().getArrearInfo(employeeId, areaId, String.valueOf(roomId),
                mArrearJsonHandler);
    }

    private JsonHttpResponseHandler mArrearJsonHandler = new JsonHttpResponseHandler() {

        @Override
        public void onSuccess(JSONObject object) {
            Log.d(TAG, "get arrear return info :" + object.toString());

            try {
                int resultCode = object.getInt("ResultCode");

                String erroMsg = object.getString("ErrorMessage");
                if (resultCode != 1) {
                    Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : " + resultCode);
                    Toast.makeText(getApplicationContext(), erroMsg, Toast.LENGTH_SHORT).show();
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
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }

            setFeesAdapter();
        }
    };

    private void setFeesAdapter() {
        mArrearsAdapter = new ArrearsAdapter(getApplicationContext(),
                PropertyService.getInstance().Arrears);
        mListArrears.setAdapter(mArrearsAdapter);

        mOtherFeesAdapter = new ArrearsAdapter(getApplicationContext(),
                PropertyService.getInstance().TempArrears);
        mListOtherFees.setAdapter(mOtherFeesAdapter);
    }

    private ArrearInfo convertJSONObjectToArrear(JSONObject arrearObj) throws JSONException {
        ArrearInfo arrearInfo = new ArrearInfo();
        arrearInfo.setInputTableId(arrearObj.getInt("InputTableID"));
        arrearInfo.setObjectType(arrearObj.getInt("ObjectType"));
        arrearInfo.setObjectID(arrearObj.getInt("ObjectID"));
        arrearInfo.setPrice(arrearObj.getLong("Price"));
        arrearInfo.setAmount(arrearObj.getLong("Amount"));
        arrearInfo.setStartDegree(arrearObj.getLong("StartDegree"));
        arrearInfo.setEndDegree(arrearObj.getLong("EndDegree"));
        arrearInfo.setPayStartDate(arrearObj.getString("PayStartDate"));
        arrearInfo.setPayEndDate(arrearObj.getString("PayEndDate"));
        arrearInfo.setStatus(arrearObj.getInt("Status"));
        arrearInfo.setFeeType(arrearObj.getInt("FeeType"));
        arrearInfo.setName(arrearObj.getString("Name"));
        return arrearInfo;
    }

    // ÇÐ»»Ð¡Çø
    private OnItemSelectedListener mOnSpinChangeAreaItemSelectListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // update the current area
            Area area = PropertyService.getInstance().getUserInfo().getAreas().get(position);
            PropertyService.getInstance().getUserInfo().setAreaId(area.getAreaId());
            PropertyService.getInstance().getUserInfo().setAreaName(area.getAreaName());
            mTxtCurrentArea.setText(getResources().getString(R.string.txt_current_area_format_text,
                    PropertyService.getInstance().getUserInfo().getAreaName()));
            Log.d(TAG,
                    "updated current area info:" + area.getAreaId() + "area name:"
                            + area.getAreaName());
            // TODO: clear all the values
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // Do nothing
        }
    };
}
