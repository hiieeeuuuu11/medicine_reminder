package com.dab.medireminder.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dab.medireminder.R;
import com.dab.medireminder.base.BaseActivity;
import com.dab.medireminder.data.DBApp;
import com.dab.medireminder.data.model.MedicineTimer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    private DBApp dbApp;

    TextView uname;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("username","");
        uname = findViewById(R.id.uname);
        uname.setText(name);
        requestQueue = Volley.newRequestQueue(this);
    }

    private void sendDongBoRequest(){
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("username","");
        String url = "http://192.168.1.9:8080/medicine-timers/getbyusername/"+name;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<String> stringList = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                MedicineTimer medicineTimer = new MedicineTimer();
                                medicineTimer.setId(jsonObject.getString("appid"));
                                medicineTimer.setName(jsonObject.getString("name"));
                                medicineTimer.setRepeat(jsonObject.getString("repeatValue"));
                                medicineTimer.setDose(jsonObject.getString("dose"));
                                medicineTimer.setIcon(jsonObject.getString("icon"));
                                dbApp.addTimer(medicineTimer);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(LoginActivity.this,"Đồng bộ thành công",Toast.LENGTH_SHORT).show();
                        // Xử lý danh sách chuỗi (stringList) ở đây
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                    }
                });

        requestQueue.add(request);
    }

    private void sendSaoLuuRequest() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("username","");
        String url = "http://192.168.1.9:8080/medicine-timers";


        List<MedicineTimer> medicineTimers = dbApp.getMedicineTimer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            medicineTimers.forEach(item ->{
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("appid",item.getId());
                    jsonBody.put("name",item.getName());
                    jsonBody.put("dose",item.getDose());
                    jsonBody.put("repeatValue",item.getRepeat());
                    jsonBody.put("icon",item.getIcon());
                    jsonBody.put("timer",item.getTimer());
                    jsonBody.put("appusername",name);
                    send(jsonBody,url);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        }

    }

    private void send(JSONObject jsonObject,String url){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(LoginActivity.this, "Sao lưu thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(request);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
    }

    @Override
    public void setEvents() {
    }

    @Override
    public void setData() {
        dbApp = new DBApp(this);
    }




    @OnClick({R.id.saoluu, R.id.dongbo,R.id.dangxuat})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.saoluu:
                sendSaoLuuRequest();
                break;

            case R.id.dongbo:
                sendDongBoRequest();
                break;

            case R.id.dangxuat:
                SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("username");
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                break;

        }
    }

    @OnClick(R.id.btn_close)
    public void closeScreen() {
        finish();
    }

}
