package com.jason.property;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.jason.property.net.PropertyNetworkApi;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncHttpClient client = new AsyncHttpClient();
        // client.get(
        // "http://try.hmwy.cn/api/login.ashx?username=wq&pwd=waqa&companycode=00800&signature=84a88221f7c98939c6ca2e26ebf3af64",
        // new JsonHttpResponseHandler() {
        //
        // @Override
        // public void onSuccess(JSONObject arg0) {
        // Log.d(TAG, arg0.toString());
        // }
        // });

        PropertyNetworkApi.getInstance().Login("wq", "waqa", "00800",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(JSONObject arg0) {
                        Log.d(TAG, arg0.toString());
                    }
                });

        PropertyNetworkApi.getInstance().getStandardFee("aff18c401793434b849c747981cdc6dd", "249",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);
                    }

                    @Override
                    public void onSuccess(JSONObject arg0) {
                        Log.d(TAG, arg0.toString());
                    }
                });
    }
}
