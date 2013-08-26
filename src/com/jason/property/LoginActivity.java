package com.jason.property;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jason.property.data.PropertyService;
import com.jason.property.model.Area;
import com.jason.property.model.UserInfo;
import com.jason.property.net.PropertyNetworkApi;
import com.loopj.android.http.JsonHttpResponseHandler;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";

    private Button mBtnLogin;

    private EditText mEditUserName;

    private EditText mEditPwd;

    private EditText mEditCompanyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mEditUserName = (EditText) findViewById(R.id.edit_user_name);
        mEditPwd = (EditText) findViewById(R.id.edit_pwd);
        mEditCompanyCode = (EditText) findViewById(R.id.edit_company_code);
        mBtnLogin.setOnClickListener(mOnBtnLoginClickListener);

        // client.get(
        // "http://try.hmwy.cn/api/login.ashx?username=wq&pwd=waqa&companycode=00800&signature=84a88221f7c98939c6ca2e26ebf3af64",
        // new JsonHttpResponseHandler() {
        //
        // @Override
        // public void onSuccess(JSONObject arg0) {
        // Log.d(TAG, arg0.toString());
        // }
        // });

        PropertyNetworkApi.getInstance().getStandardFee("aff18c401793434b849c747981cdc6dd", "249",
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(JSONObject arg0) {
                        Log.d(TAG, arg0.toString());
                    }
                });
    }

    private OnClickListener mOnBtnLoginClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            final String userName = mEditUserName.getText().toString();
            final String pwd = mEditPwd.getText().toString();
            final String companyCode = mEditCompanyCode.getText().toString();

            PropertyNetworkApi.getInstance().Login(userName, pwd, companyCode, mLoginJsonHandler);
        }
    };

    private JsonHttpResponseHandler mLoginJsonHandler = new JsonHttpResponseHandler() {

        @Override
        public void onSuccess(JSONObject object) {
            Log.d(TAG, "login return info :" + object.toString());
            try {
                int resultCode = object.getInt("ResultCode");
                String erroMsg = object.getString("ErrorMessage");
                if (resultCode != 1) {
                    Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : " + resultCode);
                    Toast.makeText(getApplicationContext(), erroMsg, Toast.LENGTH_SHORT).show();
                    return;
                }

                // first init the user name, password and company code info
                final String userName = mEditUserName.getText().toString();
                final String pwd = mEditPwd.getText().toString();
                final String companyCode = mEditCompanyCode.getText().toString();

                UserInfo userInfo = new UserInfo();
                userInfo.setUserName(userName);
                userInfo.setPwd(pwd);
                userInfo.setCompanyCode(companyCode);
                JSONObject dataObj = object.getJSONObject("Data");
                userInfo.setAreaId(dataObj.getString("AreaID"));
                userInfo.setAreaName(dataObj.getString("AreaName"));
                userInfo.setEmployeeId(dataObj.getString("EmployeeID"));

                JSONArray areas = dataObj.getJSONArray("Areas");
                for (int i = 0; i < areas.length(); i++) {
                    JSONObject area = areas.getJSONObject(i);
                    userInfo.getAreas().add(convertJSONObjectToArea(area));
                }
                PropertyService.getInstance().setUserInfo(userInfo);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }

            // login success navigate to charge activity
            Intent intent = new Intent(LoginActivity.this, ChargeActivity.class);
            startActivity(intent);
        }
    };

    /**
     * convert the area json object to a area object
     * 
     * @param object
     *            a json object
     * @return a area object
     * @throws JSONException
     */
    private Area convertJSONObjectToArea(JSONObject object) throws JSONException {
        Area area = new Area();
        object.getString("AreaID");
        object.getString("AreaName");
        return area;
    }
}
