package com.jason.property.net;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.jason.property.data.PropertyService;
import com.jason.property.model.ArrearInfo;
import com.jason.property.model.InputTable;
import com.jason.property.utils.MD5Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class PropertyNetworkApi {
	private static final String TAG = "PropertyApi";

	private static class SingletonHolder {
		static final PropertyNetworkApi INSTANCE = new PropertyNetworkApi();
	}

	private static final String BASE_URI = "http://try.hmwy.cn";

	private static final String URI_LOGIN_FORTMAT_STR = BASE_URI
			+ "/api/login.ashx";

	private static final String URI_GET_STANDARD_FORTMAT_STR = BASE_URI
			+ "/api/GetStandardFee.ashx";

	private static final String URI_GET_ROOM_INFO_FORTMAT_STR = BASE_URI
			+ "/api/GetRoomInfo.ashx";

	private static final String URI_GET_ARREAR_INFO_FORTMAT_STR = BASE_URI
			+ "/api/GetArrearInfo.ashx";

	private static final String URI_CHECK_AND_CHARGE_FORTMAT_STR = BASE_URI
			+ "/api/CheckAndCharge.ashx";

	private static final String URI_CAL_FEE_FORTMAT_STR = BASE_URI
			+ "/api/CalFee.ashx";

	private static final String URI_ADD_OTHER_FEE_FORTMAT_STR = BASE_URI
			+ "/api/AddOtherFee.ashx";
	private static final String URI_GET_INVOICE_FORTMAT_STR = BASE_URI
			+ "/api/GetInvoice.ashx";

	private static final String URI_REOKE_PAY_FORTMAT_STR = BASE_URI
			+ "/api/RevokePay.ashx";

	private static final String URI_GetInputTable_FORTMAT_STR = BASE_URI
			+ "/api/GetInputTable.ashx";
	
	private static final String URI_AddInputTable_FORMAT_STR = BASE_URI
			+ "/api/AddInputTable.ashx";
	
	private static final String URI_GetPreviousRoom_STR = BASE_URI
			+ "/api/GetPreviousRoom.ashx"; 
	
	private static final String URI_GetNextRoom_STR = BASE_URI
			+ "/api/GetNextRoom.ashx"; 
	
	private static final String URI_RepairPrint_FORMAT_STR = BASE_URI
			+"/api/RepairPrint.ashx";

	private AsyncHttpClient mAsyncHttpClient;

	public static PropertyNetworkApi getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private PropertyNetworkApi() {
		mAsyncHttpClient = new AsyncHttpClient();
	}

	/**
	 * Login
	 * 
	 * @param userName
	 * @param pwd
	 * @param companyCode
	 * @param handler
	 */
	public void Login(String userName, String pwd, String companyCode,
			JsonHttpResponseHandler handler) {
		String md5Str = MD5Util.getMD5Str(userName.concat(pwd).concat(
				companyCode));
		RequestParams params = new RequestParams();
		params.put("userName", userName);
		params.put("pwd", pwd);
		params.put("companyCode", companyCode);
		params.put("signature", md5Str);
		mAsyncHttpClient.post(URI_LOGIN_FORTMAT_STR, params, handler);
	}

	public void getStandardFee(String employeeId, String areaId,
			String companyCode, JsonHttpResponseHandler handler) {
		String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId).concat(
				companyCode));
		RequestParams params = new RequestParams();
		params.put("employeeId", employeeId);
		params.put("areaId", areaId);
		params.put("companyCode", companyCode);
		params.put("signature", md5Str);
		mAsyncHttpClient.post(URI_GET_STANDARD_FORTMAT_STR, params, handler);
	}

	public void getRoomInfo(String employeeId, String areaId, String roomCode,
			JsonHttpResponseHandler handler) {
		String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId).concat(
				roomCode));
		RequestParams params = new RequestParams();
		params.put("employeeId", employeeId);
		params.put("areaId", areaId);
		params.put("roomCode", roomCode);
		params.put("signature", md5Str);
		mAsyncHttpClient.post(URI_GET_ROOM_INFO_FORTMAT_STR, params, handler);
	}

	/**
	 * 获取住户欠费信息
	 * 
	 * @param employeeId
	 * @param areaId
	 * @param roomId
	 * @param handler
	 */
	public void getArrearInfo(String employeeId, String areaId, String roomId,
			JsonHttpResponseHandler handler) {
		String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId).concat(
				roomId));
		RequestParams params = new RequestParams();
		params.put("employeeId", employeeId);
		params.put("areaId", areaId);
		params.put("roomId", roomId);
		params.put("signature", md5Str);
		mAsyncHttpClient.post(URI_GET_ARREAR_INFO_FORTMAT_STR, params, handler);
	}

	public void checkAndCharge(String employeeId, String areaId, String roomId,
			String actualAmount, JsonHttpResponseHandler handler) {
		List<Integer> arrears = new ArrayList<Integer>();
		for (ArrearInfo arreaInfo : PropertyService.getInstance().Arrears) {
			arrears.add(arreaInfo.getInputTableId());
		}
		JSONArray arrayArrears = new JSONArray(arrears);
		Log.d(TAG, "arrayArrears:" + arrayArrears.toString());

		List<Integer> tempArrears = new ArrayList<Integer>();
		for (ArrearInfo arreaInfo : PropertyService.getInstance().TempArrears) {
			tempArrears.add(arreaInfo.getInputTableId());
		}
		JSONArray arrayTempArrears = new JSONArray(tempArrears);
		Log.d(TAG, "arrayTempArrears:" + arrayTempArrears.toString());

		JSONArray arrayPrePays = new JSONArray();
		DecimalFormat df = new DecimalFormat("#.000");
		for (ArrearInfo arreaInfo : PropertyService.getInstance().PreArrears) {
			if (arreaInfo.getCount() != 0) {
				JSONObject object = new JSONObject();
				try {
					object.put("ObjectType", arreaInfo.getObjectType());
					object.put("Name", arreaInfo.getName());
					object.put("ObjectID", arreaInfo.getObjectID());
					object.put("Price", arreaInfo.getPrice());
					object.put("Amount", df.format(arreaInfo.getAmount()));
					object.put("FeeType", arreaInfo.getFeeType());
					object.put("PayStartDate", arreaInfo.getPayStartDate());
					object.put("Quantity", arreaInfo.getCount());
					object.put("PayEndDate", arreaInfo.getPayEndDate());
					object.put("FeeStandardID", arreaInfo.getFeeStandardID());

					arrayPrePays.put(object);
				} catch (JSONException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}

		String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId)
				.concat(roomId).concat(actualAmount));
		RequestParams params = new RequestParams();
		params.put("employeeId", employeeId);
		params.put("areaId", areaId);
		params.put("roomId", roomId);
		params.put("signature", md5Str);
		params.put("Arrears", arrayArrears.toString());
		params.put("TempArrears", arrayTempArrears.toString());
		params.put("PrePay", arrayPrePays.toString());
		params.put("ActualAmount", actualAmount);
		mAsyncHttpClient
				.post(URI_CHECK_AND_CHARGE_FORTMAT_STR, params, handler);
	}

	public void calFee(String employeeId, String areaId, String roomId,
			String actualAmount, JsonHttpResponseHandler handler) {
		List<Integer> arrears = new ArrayList<Integer>();
		for (ArrearInfo arreaInfo : PropertyService.getInstance().Arrears) {
			arrears.add(arreaInfo.getInputTableId());
		}
		JSONArray arrayArrears = new JSONArray(arrears);
		Log.d(TAG, "arrayArrears:" + arrayArrears.toString());

		List<Integer> tempArrears = new ArrayList<Integer>();
		for (ArrearInfo arreaInfo : PropertyService.getInstance().TempArrears) {
			tempArrears.add(arreaInfo.getInputTableId());
		}
		JSONArray arrayTempArrears = new JSONArray(tempArrears);
		Log.d(TAG, "arrayTempArrears:" + arrayTempArrears.toString());

		JSONArray arrayPrePays = new JSONArray();
		DecimalFormat df = new DecimalFormat("#.000");
		for (ArrearInfo arreaInfo : PropertyService.getInstance().PreArrears) {
			if (arreaInfo.getCount() != 0) {
				JSONObject object = new JSONObject();
				try {
					object.put("ObjectType", arreaInfo.getObjectType());
					object.put("Name", arreaInfo.getName());
					object.put("ObjectID", arreaInfo.getObjectID());
					object.put("Price", arreaInfo.getPrice());
					object.put("Amount", df.format(arreaInfo.getAmount()));
					object.put("FeeType", arreaInfo.getFeeType());
					object.put("PayStartDate", arreaInfo.getPayStartDate());
					object.put("Quantity", arreaInfo.getCount());
					object.put("PayEndDate", arreaInfo.getPayEndDate());
					object.put("FeeStandardID", arreaInfo.getFeeStandardID());

					arrayPrePays.put(object);
				} catch (JSONException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}

		String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId)
				.concat(roomId).concat(actualAmount));
		RequestParams params = new RequestParams();
		params.put("employeeId", employeeId);
		params.put("areaId", areaId);
		params.put("roomId", roomId);
		params.put("signature", md5Str);
		params.put("Arrears", arrayArrears.toString());
		params.put("TempArrears", arrayTempArrears.toString());
		params.put("PrePay", arrayPrePays.toString());
		params.put("ActualAmount", actualAmount);
		Log.d(TAG, "ActualAmount:" + actualAmount);
		mAsyncHttpClient.post(URI_CAL_FEE_FORTMAT_STR, params, handler);
	}

	public void addOtherFee(String employeeId, String areaId, String roomId,
			int feeStandardID, int feeName, String startDate, String endDate,
			int quantity, int amount, JsonHttpResponseHandler handler) {
		String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId)
				.concat(roomId).concat(feeStandardID + "").concat(feeName + "")
				.concat(startDate).concat(endDate).concat(quantity + "")
				.concat(amount + ""));
		RequestParams params = new RequestParams();
		params.put("employeeId", employeeId);
		params.put("areaId", areaId);
		params.put("roomId", roomId);
		params.put("signature", md5Str);
		params.put("feeStandardID", feeStandardID + "");
		params.put("feeName", feeName + "");
		params.put("startDate", "2014-08-07");
		params.put("endDate", "2014-09-07");
		params.put("quantity", quantity + "");
		params.put("amount", amount + "");
		mAsyncHttpClient.post(URI_ADD_OTHER_FEE_FORTMAT_STR, params, handler);
	}

	public void GetInvoice(String employeeId, String areaId, String roomCode,
			String startDate, String endDate, JsonHttpResponseHandler handler) {
		String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId)
				.concat(roomCode).concat(startDate).concat(endDate));
		RequestParams params = new RequestParams();
		params.put("employeeId", employeeId);
		params.put("areaId", areaId);
		params.put("roomCode", roomCode);
		params.put("signature", md5Str);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		mAsyncHttpClient.post(URI_GET_INVOICE_FORTMAT_STR, params, handler);
	}

	public void AddOtherFee(String employeeId, String areaId, String roomId,
			String feeStandardID, String feeName, String startDate,
			String endDate, String quantity, String amount,
			JsonHttpResponseHandler handler) {
		String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId)
				.concat(roomId).concat(feeStandardID + "").concat(feeName)
				.concat(startDate).concat(endDate).concat(quantity + "")
				.concat(amount + ""));
		RequestParams params = new RequestParams();
		params.put("EmployeeID", employeeId);
		params.put("AreaID", areaId);
		params.put("RoomID", roomId);
		params.put("FeeStandardID", feeStandardID);
		params.put("FeeName", feeName);
		params.put("StartDate", startDate);
		params.put("EndDate", endDate);
		params.put("Quantity", quantity);
		params.put("Amount", amount);
		params.put("signature", md5Str);
		mAsyncHttpClient.post(URI_ADD_OTHER_FEE_FORTMAT_STR, params, handler);
	}

	public void RevokePay(String employeeId, String areaId, String roomId,
			String payId, String notes, JsonHttpResponseHandler handler) {
		String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId)
				.concat(roomId).concat(payId + ""));
		RequestParams params = new RequestParams();
		params.put("EmployeeID", employeeId);
		params.put("AreaID", areaId);
		params.put("RoomID", roomId);
		params.put("signature", md5Str);
		params.put("PayID", payId);
		params.put("Notes", notes);
		mAsyncHttpClient.post(URI_REOKE_PAY_FORTMAT_STR, params, handler);
	}

	public void getInputTable(String employeeId, String areaId, String roomId,
			JsonHttpResponseHandler handler) {
		String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId).concat(
				roomId));
		RequestParams params = new RequestParams();
		params.put("EmployeeID", employeeId);
		params.put("AreaID", areaId);
		params.put("RoomID", roomId);
		params.put("signature", md5Str);
		mAsyncHttpClient.post(URI_GetInputTable_FORTMAT_STR, params, handler);
	}
	
	public void addInputTable(String employeeId, String areaId, String roomId,
			ArrayList<InputTable> inputTables, JsonHttpResponseHandler handler) {
		String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId).concat(
				roomId));
		RequestParams params = new RequestParams();
		params.put("EmployeeID", employeeId);
		params.put("AreaID", areaId);
		params.put("RoomID", roomId);
		params.put("signature", md5Str);
		params.put("InputTables", new Gson().toJson(inputTables));
		mAsyncHttpClient.post(URI_AddInputTable_FORMAT_STR, params, handler);
	}
	
	public void getPreviousRoom(String employeeId, String areaId, String roomId,
			JsonHttpResponseHandler handler) {
		String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId).concat(
				roomId));
		RequestParams params = new RequestParams();
		params.put("EmployeeID", employeeId);
		params.put("AreaID", areaId);
		params.put("RoomID", roomId);
		params.put("signature", md5Str);
		mAsyncHttpClient.post(URI_GetPreviousRoom_STR, params, handler);
	}
	
	public void getNextRoom(String employeeId, String areaId, String roomId,
			JsonHttpResponseHandler handler) {
		String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId).concat(
				roomId));
		RequestParams params = new RequestParams();
		params.put("EmployeeID", employeeId);
		params.put("AreaID", areaId);
		params.put("RoomID", roomId);
		params.put("signature", md5Str);
		mAsyncHttpClient.post(URI_GetNextRoom_STR, params, handler);
	}

	public void repairPrint(String employeeId, String areaId, String roomId, String payID,
			JsonHttpResponseHandler handler) {
		String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId).concat(
				roomId).concat(payID));
		RequestParams params = new RequestParams();
		params.put("EmployeeID", employeeId);
		params.put("AreaID", areaId);
		params.put("RoomID", roomId);
		params.put("PayID", payID);
		params.put("signature", md5Str);
		mAsyncHttpClient.post(URI_RepairPrint_FORMAT_STR, params, handler);
	}
}
