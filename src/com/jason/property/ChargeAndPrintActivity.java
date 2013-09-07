package com.jason.property;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.jason.property.data.PropertyService;

public class ChargeAndPrintActivity extends FragmentActivity {

    private FragmentManager mFragmentManager;

    private ChargeFragment mChargeFragment;

    private PrintFragment mPrintFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_print_layout);

        mFragmentManager = getSupportFragmentManager();
        mChargeFragment = (ChargeFragment) mFragmentManager.findFragmentById(R.id.charge_fragment);
        mPrintFragment = (PrintFragment) mFragmentManager.findFragmentById(R.id.print_fragment);
        mPrintFragment.setCallback(new ConnectFailedCallBack() {

            @Override
            public void onConnectedFailed() {
                if (mPrintFragment.isHidden()) {
                    FragmentTransaction transaction = mFragmentManager.beginTransaction();
                    transaction.hide(mChargeFragment);
                    transaction.show(mPrintFragment);
                    transaction.commit();
                }
            }
        });

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.hide(mPrintFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mPrintFragment.isVisible()) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.hide(mPrintFragment);
            transaction.show(mChargeFragment);
            transaction.commit();
        } else {
            super.onBackPressed();
        }
    }

    public interface ConnectFailedCallBack {

        void onConnectedFailed();
    }
}
