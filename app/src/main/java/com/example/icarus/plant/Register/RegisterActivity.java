package com.example.icarus.plant.Register;

import android.os.Looper;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.icarus.plant.Login.LoginActivity;
import com.example.icarus.plant.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class RegisterActivity extends AppCompatActivity {

    private AutoCompleteTextView username;
    private EditText password;
    private EditText repassword;
    private Button button;
    private static final String TAG = "RegisterActivity";
    private Spinner spinner;
    private static String reg_role = "";
    public static final MediaType FORM_CONTENT_TYPE = MediaType.parse("application/json;charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (AutoCompleteTextView) findViewById(R.id.regusername);
        password = (EditText) findViewById(R.id.regpassword);
        repassword = (EditText) findViewById(R.id.regrepassword);
        button = (Button) findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               attemption();
            }
        });
        Button button1 = (Button) findViewById(R.id.regback);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                finish();
            }
        });
        spinner = findViewById(R.id.reg_spinner);
        String[] role = getResources().getStringArray(R.array.role);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,role);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reg_role = "";
                switch (position){
                    case 0: reg_role = "ROLE_ADMIN"; break;
                    case 1: reg_role = "ROLE_EXPERT"; break;
                    case 2: reg_role = "ROLE_USER"; break;
                    default:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void attemption(){
        String usernamestring = username.getText().toString();
        String passwordstring = password.getText().toString();
        String repasswordstring = repassword.getText().toString();
        View focusView = null;
        username.setError(null);
        password.setError(null);
        repassword.setError(null);
        boolean flag = true;
        if (usernamestring.length() < 3){
            username.setError("用户名太短");
            focusView = username;
            flag = false;
        }
        if (passwordstring.length() < 6){
            password.setError("密码太短");
            focusView = password;
            flag = false;
        }
        if (!passwordstring.equals(repasswordstring)){
            repassword.setError("密码不一致");
            focusView = repassword;
            flag = false;
        }
        if (flag){
            mysend(usernamestring,passwordstring);
        }
        else {
            focusView.requestFocus();
        }
    }

    private void mysend(String usernamestring,String passwordstring){
      final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username",usernamestring);
            jsonObject.put("password",passwordstring);
            jsonObject.put("role",reg_role);
        }catch (JSONException e){
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(LoginActivity.IP + "/auth/register" ).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),jsonObject.toString())).build();
                Response response = null;
                try{
                    response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    /****t添加返回值判断*****/
                    Log.d(TAG, responseData);
                    JSONObject jobject = new JSONObject(responseData);
                    String msg = jobject.optString("msg");
                    if(msg.equals("成功")){
                        Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }else if (msg.equals("用户已存在")){
                        Toast.makeText(RegisterActivity.this,"用户名已存在",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(RegisterActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }
}
