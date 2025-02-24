package com.dab.medireminder.ui.activity;

import android.app.DatePickerDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dab.medireminder.R;
import com.dab.medireminder.base.BaseActivity;
import com.dab.medireminder.data.DBApp;
import com.dab.medireminder.data.model.BloodPressure;
import com.dab.medireminder.utils.AppUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

import static com.dab.medireminder.ui.activity.BloodPressureActivity.RESULT_ADD_BLOOD_PRESSURE;


public class AddBloodPressureActivity extends BaseActivity {

    private final String TAG = "AddBloodPressureActivity";

    @BindView(R.id.edt_max)
    EditText edtMax;

    @BindView(R.id.edt_min)
    EditText edtMin;

    @BindView(R.id.rl_main)
    View rlMain;

    @BindView(R.id.tv_timer)
    TextView tvTimer;

    private DBApp dbApp;
    private BloodPressure bloodPressure;
    private int month, year, day;

    @Override
    public int getLayout() {
        return R.layout.activity_add_blood_pressure;
    }

    @Override
    public void initView() {
        hideKeyboard(rlMain);
    }

    @Override
    public void setEvents() {

    }

    @Override
    public void setData() {
        dbApp = new DBApp(this);
        Calendar calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        AddBloodPressureActivity.this.tvTimer.setText(String.format("%02d/%02d/%d", AddBloodPressureActivity.this.day,
                AddBloodPressureActivity.this.month, AddBloodPressureActivity.this.year));
    }

    private void addBloodPressure() {
        String maxStr = edtMax.getText().toString();
        String minStr = edtMin.getText().toString();

        if (TextUtils.isEmpty(maxStr)) {
            AppUtils.toast(this, "Bạn chưa nhập chỉ số tối đa !");
            return;
        }

        if (TextUtils.isEmpty(minStr)) {
            AppUtils.toast(this, "Bạn chưa nhập chỉ số tối thiểu !");
            return;
        }

        int max = Integer.parseInt(maxStr);
        int min = Integer.parseInt(minStr);
        if (bloodPressure == null) {
            bloodPressure = new BloodPressure();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        bloodPressure.setMin(min);
        bloodPressure.setMax(max);
        bloodPressure.setTimer(calendar.getTimeInMillis());

        if (dbApp.addBloodPressure(bloodPressure)) {
            AppUtils.toast(this, "Bạn đã đo huyết áp ngày hôm nay !");
            setResult(RESULT_ADD_BLOOD_PRESSURE);
            finish();
        } else {
            AppUtils.toast(this, "Oops ! Không thể thêm chỉ số huyết áp mới này !");
            edtMax.requestFocus();
        }
    }

    @OnClick({R.id.btn_close, R.id.btn_save, R.id.tv_timer})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                setResult(RESULT_ADD_BLOOD_PRESSURE);
                finish();
                break;
            case R.id.btn_save:
                addBloodPressure();
                break;
            case R.id.tv_timer:
                showDatePicker();
                break;
        }
    }

    private void hideKeyboard(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                AppUtils.hideSoftKeyboard(v, AddBloodPressureActivity.this);
                return false;
            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                hideKeyboard(innerView);
            }
        }
    }

    private void showDatePicker() {
        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            AddBloodPressureActivity.this.day = dayOfMonth;
            AddBloodPressureActivity.this.month = monthOfYear + 1;
            AddBloodPressureActivity.this.year = year;

            AddBloodPressureActivity.this.tvTimer.setText(String.format("%02d/%02d/%d", AddBloodPressureActivity.this.day,
                    AddBloodPressureActivity.this.month, AddBloodPressureActivity.this.year));
        }, year, month - 1, day);
        dpd.show();
    }
}
