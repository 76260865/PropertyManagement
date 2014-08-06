package com.jason.property.net;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.jason.property.data.PropertyService;
import com.jason.property.model.ArrearInfo;
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

    private static final String URI_LOGIN_FORTMAT_STR = BASE_URI + "/api/login.ashx";

    private static final String URI_GET_STANDARD_FORTMAT_STR = BASE_URI
            + "/api/GetStandardFee.ashx";

    private static final String URI_GET_ROOM_INFO_FORTMAT_STR = BASE_URI + "/api/GetRoomInfo.ashx";

    private static final String URI_GET_ARREAR_INFO_FORTMAT_STR = BASE_URI
            + "/api/GetArrearInfo.ashx";

    private static final String URI_CHECK_AND_CHARGE_FORTMAT_STR = BASE_URI
            + "/api/CheckAndCharge.ashx";

    private static final String URI_CAL_FEE_FORTMAT_STR = BASE_URI + "/api/CalFee.ashx";

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
        String md5Str = MD5Util.getMD5Str(userName.concat(pwd).concat(companyCode));
        RequestParams params = new RequestParams();
        params.put("userName", userName);
        params.put("pwd", pwd);
        params.put("companyCode", companyCode);
        params.put("signature", md5Str);
        mAsyncHttpClient.post(URI_LOGIN_FORTMAT_STR, params, handler);
    }

    public void getStandardFee(String employeeId, String areaId, String companyCode,
            JsonHttpResponseHandler handler) {
        String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId).concat(companyCode));
        RequestParams params = new RequestParams();
        params.put("employeeId", employeeId);
        params.put("areaId", areaId);
        params.put("companyCode", companyCode);
        params.put("signature", md5Str);
        mAsyncHttpClient.post(URI_GET_STANDARD_FORTMAT_STR, params, handler);
    }

    public void getRoomInfo(String employeeId, String areaId, String roomCode,
            JsonHttpResponseHandler handler) {
        String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId).concat(roomCode));
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
        String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId).concat(roomId));
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

        String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId).concat(roomId)
                .concat(actualAmount));
        RequestParams params = new RequestParams();
        params.put("employeeId", employeeId);
        params.put("areaId", areaId);
        params.put("roomId", roomId);
        params.put("signature", md5Str);
        params.put("Arrears", arrayArrears.toString());
        params.put("TempArrears", arrayTempArrears.toString());
        params.put("PrePay", arrayPrePays.toString());
        params.put("ActualAmount", actualAmount);
        mAsyncHttpClient.post(URI_CHECK_AND_CHARGE_FORTMAT_STR, params, handler);
    }

    public void calFee(String employeeId, String areaId, String roomId, String actualAmount,
            JsonHttpResponseHandler handler) {
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

        String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId).concat(roomId)
                .concat(actualAmount));
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
}
