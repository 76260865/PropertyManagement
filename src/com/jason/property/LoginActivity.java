package com.jason.property;

import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.jason.property.data.PropertyService;
import com.jason.property.encrypte.DesEncrypter;
import com.jason.property.model.Area;
import com.jason.property.model.UserInfo;
import com.jason.property.net.PropertyNetworkApi;
import com.loopj.android.http.JsonHttpResponseHandler;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";

    private static final String ENCRPTE_KEY = "asd1a3s6da7s8d8a8s9e34r2wer4wefvcxcv";

    private static final String EXTRA_KEY_COMPANY_CODE = "company_code";

    private static final String EXTRA_KEY_USER_NAME = "user_name";

    private static final String EXTRA_KEY_PWD = "pwd";

    private Button mBtnLogin;

    private EditText mEditUserName;

    private EditText mEditPwd;

    private EditText mEditCompanyCode;

    private CheckBox mChkRemberPwd;

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mEditUserName = (EditText) findViewById(R.id.edit_user_name);
        mEditPwd = (EditText) findViewById(R.id.edit_pwd);
        mEditCompanyCode = (EditText) findViewById(R.id.edit_company_code);
        mChkRemberPwd = (CheckBox) findViewById(R.id.chk_remember);
        mBtnLogin.setOnClickListener(mOnBtnLoginClickListener);

        mPrefs = getPreferences(Context.MODE_PRIVATE);
        if (mPrefs.contains(EXTRA_KEY_PWD)) {
            mEditCompanyCode.setText(mPrefs.getString(EXTRA_KEY_COMPANY_CODE, ""));
            mEditUserName.setText(mPrefs.getString(EXTRA_KEY_USER_NAME, ""));
            try {
                mEditPwd.setText(DesEncrypter.deCrypto(mPrefs.getString(EXTRA_KEY_PWD, ""),
                        ENCRPTE_KEY));
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }
            mChkRemberPwd.setChecked(true);
            Log.d(TAG, mPrefs.getString(EXTRA_KEY_PWD, ""));
        }
    }

    private OnClickListener mOnBtnLoginClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            mBtnLogin.setText(R.string.btn_login_loading_text);
            mBtnLogin.setEnabled(false);
            final String userName = mEditUserName.getText().toString();
            final String pwd = mEditPwd.getText().toString();
            final String companyCode = mEditCompanyCode.getText().toString();
            if (mChkRemberPwd.isChecked()) {
                try {
                    mPrefs.edit().putString(EXTRA_KEY_COMPANY_CODE, companyCode)
                            .putString(EXTRA_KEY_USER_NAME, userName)
                            .putString(EXTRA_KEY_PWD, DesEncrypter.enCrypto(pwd, ENCRPTE_KEY))
                            .commit();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }
            } else {
                mPrefs.edit().clear().commit();
            }
            PropertyNetworkApi.getInstance().Login(userName, pwd, companyCode, mLoginJsonHandler);
        }
    };

    private JsonHttpResponseHandler mLoginJsonHandler = new JsonHttpResponseHandler() {

        @Override
        public void onFailure(Throwable arg0, String arg1) {
            Log.d(TAG, arg1);
            mBtnLogin.setText(R.string.btn_login_text);
            mBtnLogin.setEnabled(true);
            Toast.makeText(getApplicationContext(), R.string.btn_login_failure_text,
                    Toast.LENGTH_SHORT).show();
            super.onFailure(arg0, arg1);
        }

        @Override
        public void onSuccess(JSONObject object) {
            Log.d(TAG, "login return info :" + object.toString());
            try {
                int resultCode = object.getInt("ResultCode");
                String erroMsg = object.getString("ErrorMessage");
                if (resultCode != 1) {
                    Log.d(TAG, "ErrorMessage:" + erroMsg + "\n resultCode : " + resultCode);
                    Toast.makeText(getApplicationContext(), erroMsg, Toast.LENGTH_SHORT).show();
                    mBtnLogin.setText(R.string.btn_login_text);
                    mBtnLogin.setEnabled(true);
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
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                mBtnLogin.setText(R.string.btn_login_text);
                mBtnLogin.setEnabled(true);
                return;
            }

            // login success navigate to charge activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
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
        area.setAreaId(object.getString("AreaID"));
        area.setAreaName(object.getString("AreaName"));
        return area;
    }
}
