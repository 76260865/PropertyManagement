package com.jason.property.net;

import org.json.JSONObject;

import android.util.Log;

import com.jason.property.utils.MD5Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class PropertyNetworkApi {
    private static final String TAG = "PropertyApi";

    private static class SingletonHolder {
        static final PropertyNetworkApi INSTANCE = new PropertyNetworkApi();
    }

    private static final String URI_LOGIN_FORTMAT_STR = "http://try.hmwy.cn/api/login.ashx?username=%s&pwd=%s&companycode=%s&signature=%s";

    private static final String URI_GET_STANDARD_FORTMAT_STR = "http://try.hmwy.cn/api/GetStandardFee.ashx?EmployeeID=%s&AreaID=%s&signature=%s";

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
        String uri = String.format(URI_LOGIN_FORTMAT_STR, userName, pwd, companyCode, md5Str);
        mAsyncHttpClient.get(uri, handler);
    }

    public void getStandardFee(String employeeId, String areaId, JsonHttpResponseHandler handler) {
        String md5Str = MD5Util.getMD5Str(employeeId.concat(areaId));
        String uri = String.format(URI_GET_STANDARD_FORTMAT_STR, employeeId, areaId, md5Str);
        mAsyncHttpClient.get(uri, handler);
    }
}
