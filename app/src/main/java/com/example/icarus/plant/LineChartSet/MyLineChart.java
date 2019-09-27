package com.example.icarus.plant.LineChartSet;

import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.icarus.plant.R;
import com.github.mikephil.charting.charts.LineChart;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.icarus.plant.Mina.MinaClientHandler.mina_msg;


public class MyLineChart extends AppCompatActivity {

    private LineChart lineChart;
    private LineChartManager lineChartManager;
    private List<IncomeBean> stp = new ArrayList<>();
    private List<IncomeBean> smt = new ArrayList<>();
    private List<IncomeBean> stds = new ArrayList<>();
    private List<IncomeBean> sec = new ArrayList<>();
    private List<IncomeBean> tp = new ArrayList<>();
    private List<IncomeBean> hum = new ArrayList<>();
    private List<IncomeBean> ill= new ArrayList<>();
    private List<IncomeBean> co2 = new ArrayList<>();
    private List<IncomeBean> val = new ArrayList<>();
    private ConstraintLayout cl_stp;
    private ConstraintLayout cl_smt;
    private ConstraintLayout cl_stds;
    private ConstraintLayout cl_sec;
    private ConstraintLayout cl_tp;
    private ConstraintLayout cl_hum;
    private ConstraintLayout cl_ill;
    private ConstraintLayout cl_co2;
    private View vi_stp;
    private View vi_smt;
    private View vi_stds;
    private View vi_sec;
    private View vi_tp;
    private View vi_hum;
    private View vi_ill;
    private View vi_co2;
    private boolean bl_stp = true;
    private boolean bl_smt = true;
    private boolean bl_stds = true;
    private boolean bl_sec = true;
    private boolean bl_tp = true;
    private boolean bl_hum = true;
    private boolean bl_ill = true;
    private boolean bl_co2 = true;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try{
            Thread.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }
        generateValues();
        initlc();
    }

    public void initlc(){
        lineChart = findViewById(R.id.lineChart);
        lineChartManager = new LineChartManager(lineChart);
        cl_stp = findViewById(R.id.layout_stp);
        cl_co2 = findViewById(R.id.layout_co2);
        cl_hum = findViewById(R.id.layout_hum);
        cl_ill = findViewById(R.id.layout_ill);
        cl_sec = findViewById(R.id.layout_sec);
        cl_smt = findViewById(R.id.layout_smt);
        cl_stds = findViewById(R.id.layout_stds);
        cl_tp = findViewById(R.id.layout_tp);
        vi_co2 = findViewById(R.id.view_co2);
        vi_hum = findViewById(R.id.view_hum);
        vi_ill = findViewById(R.id.view_ill);
        vi_sec = findViewById(R.id.view_sec);
        vi_smt = findViewById(R.id.view_smt);
        vi_stds = findViewById(R.id.view_stds);
        vi_stp = findViewById(R.id.view_stp);
        vi_tp = findViewById(R.id.view_tp);
        btn_back = findViewById(R.id.lc_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cl_tp.setOnClickListener(listener);
        cl_stds.setOnClickListener(listener);
        cl_smt.setOnClickListener(listener);
        cl_sec.setOnClickListener(listener);
        cl_ill.setOnClickListener(listener);
        cl_hum.setOnClickListener(listener);
        cl_co2.setOnClickListener(listener);
        cl_stp.setOnClickListener(listener);
        /**添加折线**/
        lineChartManager.showLineChart(stp,"土壤温度", ContextCompat.getColor(this,R.color.bg_blue));
        lineChartManager.addLine(smt,"土壤湿度",ContextCompat.getColor(this,R.color.orange));
        lineChartManager.addLine(stds,"TDS",ContextCompat.getColor(this,R.color.app_black));
        lineChartManager.addLine(sec,"EC",ContextCompat.getColor(this,R.color.text_yellow));
        lineChartManager.addLine(tp,"空气温度",ContextCompat.getColor(this,R.color.colorPrimaryDark));
        lineChartManager.addLine(hum,"空气湿度",ContextCompat.getColor(this,R.color.violet));
        lineChartManager.addLine(ill,"光照强度",ContextCompat.getColor(this,R.color.green));
        lineChartManager.addLine(co2,"co2浓度",ContextCompat.getColor(this,R.color.blue));
        lineChartManager.setMarkerView(this);
    }

    public void generateValues(){
        try {
            if(mina_msg.equals("") || mina_msg.equals("null")){
                System.out.println("无数据");
                Toast.makeText(this,"网络连接异常，请重试",Toast.LENGTH_SHORT).show();
                IncomeBean tmp = new IncomeBean();
                tmp.setDate("nodata");
                tmp.setValue(0.0);
                stp.add(tmp);
                smt.add(tmp);
                stds.add(tmp);
                sec.add(tmp);
                tp.add(tmp);
                hum.add(tmp);
                ill.add(tmp);
                co2.add(tmp);
                return;
            }
            JSONArray jsonArray = new JSONArray(mina_msg);
            for (int i = 0; i < jsonArray.length();i++) {
                JSONObject jobject = jsonArray.getJSONObject(i);
                String type = jobject.optString("type");
                String time = jobject.optString("time");
                time = truncateHeadString(time,5);
                float value = (float) jobject.optDouble("value");
                switch (type){
                    case "soilTemp":IncomeBean sp = new IncomeBean();
                        sp.setDate(time);
                        sp.setValue(value);
                        stp.add(sp);
                        sp.setValue(0);
                        val.add(sp);
                        break;
                    case "soilMoist":IncomeBean st = new IncomeBean();
                        st.setDate(time);
                        st.setValue(value);
                        smt.add(st);
                        break;
                    case "soilTDS":IncomeBean ss = new IncomeBean();
                        ss.setDate(time);
                        ss.setValue(value);
                        stds.add(ss);
                        break;
                    case "soilEC":
                        IncomeBean sc= new IncomeBean();
                        sc.setDate(time);
                        sc.setValue(value);
                        sec.add(sc);
                        break;
                    case "temp":
                        IncomeBean tep = new IncomeBean();
                        tep.setDate(time);
                        tep.setValue(value);
                        tp.add(tep);
                        break;
                    case "co2":
                        IncomeBean co = new IncomeBean();
                        co.setDate(time);
                        co.setValue(value / 10.0);
                        co2.add(co);
                        break;
                    case "humi":
                        IncomeBean hi = new IncomeBean();
                        hi.setDate(time);
                        hi.setValue(value);
                        hum.add(hi);
                        break;
                    case "illu":
                        IncomeBean iu = new IncomeBean();
                        iu.setDate(time);
                        iu.setValue(value / 1000.0);
                        ill.add(iu);
                        break;
                }
                }
            }catch (Exception e){
            e.printStackTrace();
        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.layout_stp:
                    if (bl_stp){
                        bl_stp = false;
                        lineChartManager.resetLine(0,val,"土壤温度", ContextCompat.getColor(MyLineChart.this,R.color.bg_blue));
                        vi_stp.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_gray));
                    }else {
                        bl_stp = true;
                        lineChartManager.resetLine(0,stp,"土壤温度", ContextCompat.getColor(MyLineChart.this,R.color.bg_blue));
                        vi_stp.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_blue));
                    }
                    break;
                case R.id.layout_smt:
                    if (bl_smt){
                        bl_smt = false;
                        lineChartManager.resetLine(1,val,"土壤湿度", ContextCompat.getColor(MyLineChart.this,R.color.orange));
                        vi_smt.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_gray));
                    }else {
                        bl_smt = true;
                        lineChartManager.resetLine(1,smt,"土壤湿度", ContextCompat.getColor(MyLineChart.this,R.color.orange));
                        vi_smt.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_orange));
                    }
                    break;
                case R.id.layout_stds:
                    if (bl_stds){
                        bl_stds = false;
                        lineChartManager.resetLine(2,val,"土壤TDS", ContextCompat.getColor(MyLineChart.this,R.color.app_black));
                        vi_stds.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_gray));
                    }else {
                        bl_stds = true;
                        lineChartManager.resetLine(2,stds,"土壤TDS", ContextCompat.getColor(MyLineChart.this,R.color.app_black));
                        vi_stds.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_black));
                    }
                    break;
                case R.id.layout_sec:
                    if (bl_sec){
                        bl_sec = false;
                        lineChartManager.resetLine(3,val,"土壤EC", ContextCompat.getColor(MyLineChart.this,R.color.text_yellow));
                        vi_sec.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_gray));
                    }else {
                        bl_sec = true;
                        lineChartManager.resetLine(3,sec,"土壤EC", ContextCompat.getColor(MyLineChart.this,R.color.text_yellow));
                        vi_sec.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_yellow));
                    }
                    break;
                case R.id.layout_tp:
                    if (bl_tp){
                        bl_tp = false;
                        lineChartManager.resetLine(4,val,"空气温度", ContextCompat.getColor(MyLineChart.this,R.color.colorPrimaryDark));
                        vi_tp.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_gray));
                    }else {
                        bl_tp = true;
                        lineChartManager.resetLine(4,tp,"空气温度", ContextCompat.getColor(MyLineChart.this,R.color.colorPrimaryDark));
                        vi_tp.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_dark));
                    }
                    break;
                case R.id.layout_hum:
                    if (bl_hum){
                        bl_hum = false;
                        lineChartManager.resetLine(5,val,"空气湿度", ContextCompat.getColor(MyLineChart.this,R.color.violet));
                        vi_hum.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_gray));
                    }else {
                        bl_hum = true;
                        lineChartManager.resetLine(5,hum,"空气湿度", ContextCompat.getColor(MyLineChart.this,R.color.violet));
                        vi_hum.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_violet));
                    }
                    break;
                case R.id.layout_ill:
                    if (bl_ill){
                        bl_ill = false;
                        lineChartManager.resetLine(6,val,"光照强度", ContextCompat.getColor(MyLineChart.this,R.color.green));
                        vi_ill.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_gray));
                    }else {
                        bl_ill = true;
                        lineChartManager.resetLine(6,ill,"光照强度", ContextCompat.getColor(MyLineChart.this,R.color.green));
                        vi_ill.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_green));
                    }
                    break;
                case R.id.layout_co2:
                    if (bl_co2){
                        bl_co2 = false;
                        lineChartManager.resetLine(7,val,"co2浓度", ContextCompat.getColor(MyLineChart.this,R.color.blue));
                        vi_co2.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_gray));
                    }else {
                        bl_co2 = true;
                        lineChartManager.resetLine(7,co2,"co2浓度", ContextCompat.getColor(MyLineChart.this,R.color.blue));
                        vi_co2.setBackground(ContextCompat.getDrawable(MyLineChart.this,R.drawable.shape_round_deepblue));
                    }
                    break;
                    default:
                        break;
            }
        }
    };
    public static String truncateHeadString(String origin, int count) {
        if (origin == null || origin.length() < count) {
            return null;
        }
        char[] arr = origin.toCharArray();
        char[] ret = new char[arr.length - count];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = arr[i + count];
        }
        return String.copyValueOf(ret);
    }
}
