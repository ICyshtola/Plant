package com.example.icarus.plant.KnowledgeLib;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.icarus.plant.Login.LoginActivity;
import com.example.icarus.plant.R;
import com.example.icarus.plant.Register.RegisterActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KnowledgeLibrary extends AppCompatActivity {

    private static String kinds = "crop";
    private static String sname = "name";
    private static int msg_pages = 1;
    private int pages = 1;
    private int numbers = 12;
    private int lastVisibleItem = 0;
    private Spinner spinner;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private static final String TAG = "KnowledgeLibrary";
    private ItemAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<LibraryItem> mData = new ArrayList<>();
    private Handler mHandler = new Handler();
    private Button btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge_library);
        btn_back = findViewById(R.id.kl_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        spinner = findViewById(R.id.spinner_knowledge);
        String[] libs = getResources().getStringArray(R.array.knowledge_library);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,libs);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0: kinds = "crop"; sname = "name";break;
                    case 1: kinds = "disease"; sname = "diseaseName"; break;
                    case 2: kinds = "fertilizer"; sname = "name"; break;
                    case 3: kinds = "pesticide"; sname = "name"; break;
                    case 4: kinds = "technology"; sname = "name"; break;
                    default:
                        break;
                }
                changeSpinner();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        swipeRefreshLayout = findViewById(R.id.refresh_layout);
        recyclerView = findViewById(R.id.recycler_view);
        initValue();
//        try{
//            Thread.sleep(1000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        mAdapter = new ItemAdapter(getDatas(0,numbers),this,getDatas(0,numbers).size() > 0);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //添加分隔线
        recyclerView.addItemDecoration(new MyDecoration(this,MyDecoration.VERTICAL_LIST));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //加载adapter
        recyclerView.setAdapter(mAdapter);
        //监听refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                mAdapter.resetDatas();
                updateRecyclerView(0,numbers);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    if (!mAdapter.isFadeTips() && lastVisibleItem + 1 == mAdapter.getItemCount()){
                        initValue();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateRecyclerView(mAdapter.getRealLastPosition(),mAdapter.getRealLastPosition() + numbers);
                            }
                        }, 500);

                    }

                    if(!mAdapter.isFadeTips() && lastVisibleItem + 2 == mAdapter.getItemCount()){
                        initValue();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateRecyclerView(mAdapter.getRealLastPosition(),mAdapter.getRealLastPosition() + numbers);
                            }
                        }, 500);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView,int dx,int dy){
                super.onScrolled(recyclerView,dx,dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    public void initValue(){
        if(pages > msg_pages){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
//                Request request = new Request.Builder().url(LoginActivity.IP + "/auth/register" ).post(requestBody).build();
                Request request = new Request.Builder().url(LoginActivity.IP + "/" + kinds + "/" + "?pageNum="+ pages +"&pageSize="+numbers).build();
                Response response = null;
                try{
                    response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    String data_msg = jsonObject.optString("data");
                    JSONObject object = new JSONObject(data_msg);
                    String msg = object.optString("list");
                    String data_pages = object.optString("pages");
                    msg_pages = Integer.valueOf(data_pages);
                    pages++;
                    JSONArray jsonArray = new JSONArray(msg);
                    for (int i = 0;i < jsonArray.length();i++){
                        JSONObject jobject = jsonArray.getJSONObject(i);
                        jobject.put("kinds",kinds);
                        String msg_id = jobject.optString("id");
                        String msg_name = jobject.optString(sname);
                        LibraryItem libraryItem = new LibraryItem(msg_id,msg_name,jobject.toString());
//                        System.out.println(jobject.toString());
                        mData.add(libraryItem);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateRecyclerView(int fromIndex, int toIndex) {
        List<LibraryItem> newDatas = getDatas(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            mAdapter.updateList(newDatas, true);
        } else {
            mAdapter.updateList(null, false);
        }
    }

    private List<LibraryItem> getDatas(final int firstIndex, final int lastIndex) {
        List<LibraryItem> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < mData.size()) {
                resList.add(mData.get(i));
            }
        }
        return resList;
    }

    public void changeSpinner(){
        pages = 1;
        msg_pages = 1;
        mData.clear();
        initValue();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                mAdapter.resetDatas();
                updateRecyclerView(0,numbers);
            }
        }, 500);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 500);
    }
}
