package com.example.icarus.plant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icarus.plant.KnowledgeLib.KnowledgeLibrary;
import com.example.icarus.plant.LineChartSet.MyLineChart;
import com.example.icarus.plant.Login.LoginActivity;
import com.example.icarus.plant.Mina.MinaThread;
import com.example.icarus.plant.UserState.UserStateActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.icarus.plant.Mina.MinaClientHandler.mina_msg;
import static com.example.icarus.plant.Mina.MinaThread.session;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private ImageView imageView;
    private TextView textView;
    private TextView tv_stp;
    private TextView tv_smt;
    private TextView tv_stds;
    private TextView tv_sec;
    private TextView tv_tp;
    private TextView tv_hum;
    private TextView tv_ill;
    private TextView tv_co2;
    private TextView tv_time;
    private Timer timer = new Timer(true);
    private Drawable drawable;
    private Handler mHandler = new Handler();
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    imageView.setImageDrawable(drawable);
                    break;
                default:
                    break;
            }
        }
    } ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (session == null || session.isClosing()){
            MinaThread mThread = new MinaThread();
            mThread.start();
        }
        initquery();
        getDrawable(LoginActivity.ImageUrl);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myflush();
            }
        }, 1000);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "请告诉我们你的需求.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hv = navigationView.getHeaderView(0);
        textView = (TextView) hv.findViewById(R.id.textname);
        textView.setText(LoginActivity.User_Id);
        imageView = (ImageView) hv.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转至状态界面
                Intent intent = new Intent(MainActivity.this, UserStateActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        System.out.println("定时器关闭");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //处理操作栏项。只要你指定一个AndroidManifest.xml父活动，操作栏会自动处理单击Home/Up按钮。
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_history) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());// HH:mm:ss//获取当前时间
            long mill = System.currentTimeMillis();
            Date date = new Date(mill);
            Date date1 = new Date(mill - (24 * 3600 * 1000));
            String time = simpleDateFormat.format(date);
            String time1 = simpleDateFormat.format(date1);
            try{
                if (session != null && session.isConnected()) {
                    JSONObject jobject = new JSONObject();
                    jobject.put("terminal", "app");
                    jobject.put("msgType", "history");
                    jobject.put("deviceID", 2001);
                    jobject.put("start", time1);
                    jobject.put("end", time);
//                    jobject.put("start", "2019-5-24 00:00:00");
//                    jobject.put("end", "2019-5-25 00:00:00");
                    session.write(jobject);
                } else {
                    System.out.println("命令发送失败");
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("客户端链接异常...");
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, MyLineChart.class));
                }
            }, 1000);
        } else if (id == R.id.nav_change_password) {

        } else if (id == R.id.nav_knowledge) {
            Intent intent = new Intent(MainActivity.this, KnowledgeLibrary.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_return) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initquery(){
        tv_co2 = findViewById(R.id.tv_co2);
        tv_hum = findViewById(R.id.tv_hum);
        tv_ill = findViewById(R.id.tv_ill);
        tv_sec = findViewById(R.id.tv_sec);
        tv_smt = findViewById(R.id.tv_smt);
        tv_stds = findViewById(R.id.tv_stds);
        tv_tp = findViewById(R.id.tv_tp);
        tv_stp = findViewById(R.id.tv_stp);
        tv_time = findViewById(R.id.tv_time);
    }

    public void generateValues(){
        try {
            if(mina_msg.equals("")){
                System.out.println("无数据");
                Toast.makeText(this,"网络连接异常，请重试",Toast.LENGTH_SHORT).show();
                return;
            }
            JSONObject qjobject = new JSONObject(mina_msg);
            String qmsg = qjobject.optString("content");
//            Log.d(TAG, "generateValues: " + qmsg);
            if (qmsg == null || qmsg.equals("null")){
                Toast.makeText(this,"数据库异常，请重试",Toast.LENGTH_SHORT).show();
                return;
            }
            JSONArray jsonArray = new JSONArray(qmsg);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());// HH:mm:ss//获取当前时间
            long mill = System.currentTimeMillis();
            Date date = new Date(mill);
            String time = simpleDateFormat.format(date);
            tv_time.setText(time);
            for (int i = 0; i < jsonArray.length();i++) {
                JSONObject jobject = jsonArray.getJSONObject(i);
                String type = jobject.optString("type");
                float value = (float) jobject.optDouble("value");
                String fvalue = Float.toString(value);
                switch (type){
                    case "soilTemp":
                        tv_stp.setText(fvalue);
                        break;
                    case "soilMoist":
                        tv_smt.setText(fvalue);
                        break;
                    case "soilTDS":
                        tv_stds.setText(fvalue);
                        break;
                    case "soilEC":
                        tv_sec.setText(fvalue);
                        break;
                    case "temp":
                        tv_tp.setText(fvalue);
                        break;
                    case "co2":
                        tv_co2.setText(fvalue);
                        break;
                    case "humi":
                        tv_hum.setText(fvalue);
                        break;
                    case "illu":
                        tv_ill.setText(fvalue);
                        break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void myflush(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try{
                    if (session != null && session.isConnected()) {
                        JSONObject jobject = new JSONObject();
                        jobject.put("terminal", "app");
                        jobject.put("msgType", "comm");
                        jobject.put("deviceID", 2001);
                        jobject.put("to", "*****");
                        jobject.put("order", "query");
//                      System.out.println(jobject);
                        session.write(jobject);
                    } else {
                        System.out.println("命令发送失败");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("客户端链接异常...");
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        generateValues();
                    }
                }, 1000);
            }
        };
        timer.schedule(timerTask,0,60 * 1000);
    }

    public void getDrawable(final String urlpath){
        if (urlpath.equals("null") || urlpath.equals("")){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlpath);
                    URLConnection conn = url.openConnection();
                    conn.connect();
                    InputStream in;
                    in = conn.getInputStream();
                    drawable = Drawable.createFromStream(in, "header.jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }).start();
    }

}
