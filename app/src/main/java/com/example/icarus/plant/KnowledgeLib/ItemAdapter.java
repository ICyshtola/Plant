package com.example.icarus.plant.KnowledgeLib;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icarus.plant.Login.LoginActivity;
import com.example.icarus.plant.R;

import java.util.List;
import android.os.Handler;
import java.util.logging.LogRecord;

import static android.support.constraint.Constraints.TAG;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<LibraryItem> mItemList;
    private Context context;

    private int normalType = 0;
    private int footType = 1;
    private boolean hasMore = true;
    private boolean fadeTips = false;

    public ItemAdapter(List<LibraryItem> mItemList, Context context, boolean hasMore) {
        this.mItemList = mItemList;
        this.context = context;
        this.hasMore = hasMore;
    }

    static class NormalViewHolder extends RecyclerView.ViewHolder{
        View Item;
        TextView id;
        TextView name;
        public NormalViewHolder(View view){
            super(view);
            Item = view;
            id = view.findViewById(R.id.tv_id);
            name = view.findViewById(R.id.tv_name);
        }
    }

    static class FootHolder extends RecyclerView.ViewHolder{
        TextView tips;
        LinearLayout linearLayout;
        public FootHolder(View view){
            super(view);
            tips = view.findViewById(R.id.tips);
            linearLayout = view.findViewById(R.id.layout_tips);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if(viewType == normalType){
            final  NormalViewHolder holder = new NormalViewHolder(LayoutInflater.from(context).inflate(R.layout.library_item,null));
            holder.Item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    LibraryItem libraryItem = mItemList.get(position);
                    /**添加页面跳转**/
                    Intent intent = new Intent(context,ItemDetails.class);
                    intent.putExtra("dec",libraryItem.getDec());
                    context.startActivity(intent);
                    /**通过获取context来实现页面的跳转**/
                }
            });
            return holder;
        }else {
            return new FootHolder(LayoutInflater.from(context).inflate(R.layout.footview,null));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,int position){
//        LibraryItem libraryItem = mItemList.get(position);
//        holder.id.setText(libraryItem.getId());
//        holder.name.setText(libraryItem.getName());
        if(holder instanceof NormalViewHolder){
            LibraryItem libraryItem = mItemList.get(position);
            ((NormalViewHolder) holder).id.setText(libraryItem.getId());
            ((NormalViewHolder) holder).name.setText(libraryItem.getName());
        }else{
            ((FootHolder) holder).linearLayout.setVisibility(View.VISIBLE);
            if(hasMore){
                fadeTips = false;
                if (mItemList.size() > 0){
                    ((FootHolder) holder).tips.setText("正在加载更多...");
                }
            }else{
                if (mItemList.size() > 0){
                    Log.d(TAG, "onBindViewHolder:没有更多数据" );
                    ((FootHolder) holder).tips.setText("没有更多数据");

                    try{
                        Thread.sleep(500);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    new Handler().postDelayed(new Runnable(){
                        public void run() {
                            ((FootHolder)holder).linearLayout.setVisibility(View.GONE);
                            fadeTips = true;
                            hasMore = true;
                        }
                    },1000);

                }
            }
        }
    }

    @Override
    public int getItemCount(){
        return mItemList.size() + 1;
    }

    //自定义方法，获取列表中数据源的最后一个位置，比getItemCount少1，因为不计上footView
    public int getRealLastPosition() {
        return mItemList.size();
    }

    @Override
    public int getItemViewType(int position){
        if (position == getItemCount() - 1) {
            return footType;
        } else {
            return normalType;
        }
    }

    public boolean isFadeTips(){
        return fadeTips;
    }

    public void resetDatas(){
        mItemList.clear();
    }
    public void updateList(List<LibraryItem> newData,boolean hasMore){
        if (newData != null){
            mItemList.addAll(newData);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

}
