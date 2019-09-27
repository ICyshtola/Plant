package com.example.icarus.plant.KnowledgeLib;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.icarus.plant.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.LogRecord;

public class ItemDetails extends AppCompatActivity {
    private static final String TAG = "ItemDetails";
    private String data;
    private LinearLayout layout_image;
    private LinearLayout layout_company;
    private LinearLayout layout_cover;
    private LinearLayout layout_source;
    private LinearLayout layout_type;
    private TextView tv_id;
    private TextView tv_name;
    private TextView tv_type;
    private TextView tv_content;
    private ImageView iv_image;
    private TextView tv_source;
    private TextView tv_publish;
    private TextView tv_cover;
    private TextView tv_company;
    private Button btn_back;

    private static Drawable drawable;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           switch (msg.what){
               case 1:
                   iv_image.setImageDrawable(drawable);
                   break;
                   default:
                       break;
           }
       }
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Intent intent = getIntent();
        data = intent.getStringExtra("dec");
        Log.d(TAG, "onCreate: " + data);
        initView();
        setInterface();
    }

    public void initView(){
        layout_company = findViewById(R.id.layout_company);
        layout_cover = findViewById(R.id.layout_cover);
        layout_image = findViewById(R.id.layout_image);
        layout_source = findViewById(R.id.layout_source);
        layout_type = findViewById(R.id.layout_type);
        tv_id = findViewById(R.id.id_id);
        tv_name = findViewById(R.id.id_name);
        tv_type = findViewById(R.id.id_type);
        tv_content = findViewById(R.id.id_content);
        iv_image = findViewById(R.id.id_image);
        tv_source = findViewById(R.id.id_source);
        tv_publish = findViewById(R.id.id_publish);
        tv_company = findViewById(R.id.id_company);
        tv_cover = findViewById(R.id.id_cover);
        btn_back = findViewById(R.id.id_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setInterface(){
        try{
            JSONObject jsonObject = new JSONObject(data);
            String kinds = jsonObject.optString("kinds");
            switch (kinds){
                case "crop":
                    layout_company.setVisibility(View.GONE);
                    layout_cover.setVisibility(View.GONE);
                    layout_image.setVisibility(View.VISIBLE);
                    layout_source.setVisibility(View.VISIBLE);
                    layout_type.setVisibility(View.GONE);
                    String id = jsonObject.optString("id");
                    tv_id.setText(id);
                    String name = jsonObject.optString("name");
                    tv_name.setText(name);
                    String content = jsonObject.optString("content");
                    tv_content.setText(content);
                    String source = jsonObject.optString("source");
                    tv_source.setText(source);
                    String time = jsonObject.optString("publish_time");
                    tv_publish.setText(time);
                    String image_url = jsonObject.optString("image");
                    getDrawable(image_url);
                    break;
                case "disease":
                    layout_company.setVisibility(View.GONE);
                    layout_cover.setVisibility(View.GONE);
                    layout_image.setVisibility(View.VISIBLE);
                    layout_source.setVisibility(View.VISIBLE);
                    layout_type.setVisibility(View.VISIBLE);
                    String d_id = jsonObject.optString("id");
                    tv_id.setText(d_id);
                    String d_ty = jsonObject.optString("big_category");
                    String d_type = d_ty + " " + jsonObject.optString("small_category");
                    tv_type.setText(d_type);
                    String d_name = jsonObject.optString("diseaseName");
                    tv_name.setText(d_name);
                    String d_c = jsonObject.optString("symptom");
                    String d_content = d_c + "\n\n解决方案:\n" + jsonObject.optString("treatment");
                    tv_content.setText(d_content);
                    String d_image = jsonObject.optString("image");
                    getDrawable(d_image);
                    String d_time = jsonObject.optString("publishTime");
                    tv_publish.setText(d_time);
                    String d_source = jsonObject.optString("source");
                    tv_source.setText(d_source);
                    break;
                case "fertilizer":
                    layout_company.setVisibility(View.VISIBLE);
                    layout_cover.setVisibility(View.VISIBLE);
                    layout_image.setVisibility(View.GONE);
                    layout_source.setVisibility(View.GONE);
                    layout_type.setVisibility(View.VISIBLE);
                    String f_id = jsonObject.optString("id");
                    tv_id.setText(f_id);
                    String f_name = jsonObject.optString("name");
                    tv_name.setText(f_name);
                    String f_type = jsonObject.optString("type");
                    tv_type.setText(f_type);
                    String f_company = jsonObject.optString("company");
                    tv_company.setText(f_company);
                    String f_cover = jsonObject.optString("crop_use");
                    tv_cover.setText(f_cover);
                    String f_content = jsonObject.optString("content");
                    tv_content.setText(f_content);
                    String f_time = jsonObject.optString("publishTime");
                    tv_publish.setText(f_time);
                    break;
                case "pesticide":
                    layout_company.setVisibility(View.GONE);
                    layout_cover.setVisibility(View.GONE);
                    layout_image.setVisibility(View.GONE);
                    layout_source.setVisibility(View.VISIBLE);
                    layout_type.setVisibility(View.VISIBLE);
                    String p_id = jsonObject.optString("id");
                    tv_id.setText(p_id);
                    String p_type = jsonObject.optString("type");
                    tv_type.setText(p_type);
                    String p_name = jsonObject.optString("name");
                    tv_name.setText(p_name);
                    String p_content = jsonObject.optString("content");
                    tv_content.setText(p_content);
                    String p_source = jsonObject.optString("fromSource");
                    tv_source.setText(p_source);
                    String p_time = jsonObject.optString("publishTime");
                    tv_publish.setText(p_time);
                    break;
                case "technology":
                    layout_company.setVisibility(View.GONE);
                    layout_cover.setVisibility(View.GONE);
                    layout_image.setVisibility(View.VISIBLE);
                    layout_source.setVisibility(View.VISIBLE);
                    layout_type.setVisibility(View.VISIBLE);
                    String t_id = jsonObject.optString("id");
                    tv_id.setText(t_id);
                    String t_type = jsonObject.optString("category");
                    tv_type.setText(t_type);
                    String t_name = jsonObject.optString("name");
                    tv_name.setText(t_name);
                    String t_content = jsonObject.optString("content");
                    tv_content.setText(t_content);
                    String t_image = jsonObject.optString("image");
                    getDrawable(t_image);
                    String t_source = jsonObject.optString("source");
                    tv_source.setText(t_source);
                    String t_time = jsonObject.optString("publish_time");
                    tv_publish.setText(t_time);
                    break;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getDrawable(final String urlpath){
        if (urlpath.equals("null")){
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
                    drawable = Drawable.createFromStream(in, "background.jpg");
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
