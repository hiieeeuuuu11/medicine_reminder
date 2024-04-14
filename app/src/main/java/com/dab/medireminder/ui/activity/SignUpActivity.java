package com.dab.medireminder.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dab.medireminder.R;
import com.dab.medireminder.base.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.OnClick;

public class SignUpActivity extends BaseActivity {

    Button btnSignUp;
    RequestQueue requestQueue;
    EditText username,password;

    @Override
    public int getLayout() {
        return R.layout.activity_sign_up;
    }

    @Override
    public void initView() {

    }

    @Override
    public void setEvents() {

    }

    @Override
    public void setData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnSignUp = findViewById(R.id.btn_signup);
        requestQueue = Volley.newRequestQueue(this);
        username = findViewById(R.id.uname);
        password = findViewById(R.id.password);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u = username.getText().toString().trim();
                String p = password.getText().toString().trim();

                if (!u.isEmpty() && !p.isEmpty()) {
                    sendSignUpRequest(u, p);
                } else {
                    Toast.makeText(SignUpActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void sendSignUpRequest(String username, String password) {
        String url = "http://192.168.1.9:8080/user/signup";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
            System.out.println(jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String uname;
                        try {
                            uname = response.getString("username");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", uname);
                        editor.apply();
                        Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                        Toast.makeText(SignUpActivity.this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(request);
    }

    @OnClick(R.id.btn_close)
    public void closeScreen() {
        finish();
    }
}