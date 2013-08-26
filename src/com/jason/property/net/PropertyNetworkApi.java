package com.jason.property.net;

import com.jason.property.utils.MD5Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class PropertyNetworkApi {
    private static final String TAG = "PropertyApi";

    private static class SingletonHolder {
        static final PropertyNetworkApi INSTANCE = new PropertyNetworkApi();
    }

    private static final String URI_LOGIN_FORTMAT_STR = "http://try.hmwy.cn/api/login.ashx";

    private static final String URI_GET_STANDARD_FORTMAT_STR = "http://try.hmwy.cn/api/GetStandardFee.ashx";

    private static final String URI_GET_ROOM_INFO_FORTMAT_STR = "http://try.hmwy.cn/api/GetRoomInfo.ashx";

    private static final String URI_GET_ARREAR_INFO_FORTMAT_STR = "http://try.hmwy.cn/api/GetArrearInfo.ashx";

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
}
