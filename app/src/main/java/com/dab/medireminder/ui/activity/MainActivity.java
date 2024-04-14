package com.dab.medireminder.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import com.dab.medireminder.R;
import com.dab.medireminder.base.BaseActivity;
import com.dab.medireminder.constant.Constants;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

    }

    @Override
    public void setEvents() {

    }

    @Override
    public void setData() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.NAME_SHARED_PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_FIRST_OPEN_APP, true);
        editor.apply();
    }

    @OnClick({R.id.btn_timer, R.id.btn_medicine, R.id.btn_blood_pressure, R.id.user})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btn_timer:
                startActivity(new Intent(this, MedicineTimerActivity.class));
                break;

            case R.id.btn_medicine:
                startActivity(new Intent(this, MedicineActivity.class));
                break;
            case R.id.btn_blood_pressure:
                startActivity(new Intent(this, BloodPressureActivity.class));
                break;

            case R.id.user:
                SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                String name = sharedPreferences.getString("username","");
                if(name != "" && name != null)
                    startActivity(new Intent(this, LoginActivity.class));
                else startActivity(new Intent(this, SettingActivity.class));
                break;
        }
    }

}