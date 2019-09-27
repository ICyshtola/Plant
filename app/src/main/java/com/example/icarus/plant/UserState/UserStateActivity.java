package com.example.icarus.plant.UserState;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.icarus.plant.Login.LoginActivity;
import com.example.icarus.plant.R;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserStateActivity extends AppCompatActivity {

    private TextView state_username;
    private TextView state_email;
    private TextView state_phone;
    private TextView state_permission;

    private static final String TAG = "UserStateActivity";
    private Button btn_chgemail;
    private Button btn_chgphone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_state);
        state_username = (TextView)findViewById(R.id.state_name);
        state_email = (TextView)findViewById(R.id.state_email);
        state_phone = (TextView)findViewById(R.id.state_phone);
        state_permission = (TextView) findViewById(R.id.state_permission);
        btn_chgemail = (Button)findViewById(R.id.chgemail);
        btn_chgphone = (Button)findViewById(R.id.chgphone);
        btn_chgemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_chgphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        pageflush();
    }


    public void pageflush(){
    new  Thread(new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            OkHttpClient client = new OkHttpClient();
            final RequestBody requestBody = new FormBody.Builder().add("name", LoginActivity.User_Id).build();
            Request request = new Request.Builder().url(LoginActivity.IP + "/user/getinfo").post(requestBody).build();
            Call call = client.newCall(request);
            Response response = null;
            try{
                response = call.execute();
                String responseData = response.body().string();
                /****t添加返回值判断*****/
                Log.d(TAG, responseData);
            }catch (Exception e){
                e.printStackTrace();
            }
            Looper.loop();
        }
    }).start();
    }
}
