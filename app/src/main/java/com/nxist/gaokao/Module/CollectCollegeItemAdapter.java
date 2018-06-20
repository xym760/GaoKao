package com.nxist.gaokao.Module;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nxist.gaokao.R;
import com.nxist.gaokao.view.exchange.CommentActivity;
import com.nxist.gaokao.view.personalcenter.ClickCollectCollegeActivity;

import java.util.List;

/**
 * Created by 徐源茂 on 2018/3/29.
 */

public class CollectCollegeItemAdapter extends RecyclerView.Adapter<CollectCollegeItemAdapter.ViewHolder> {
    private List<CollegeItem> mCollegeItemList;
    static  class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView date;
        public ViewHolder(View view){
            super(view);
            name=(TextView)view.findViewById(R.id.collect_college_name);
            date=(TextView)view.findViewById(R.id.collect_date);
        }
    }
    public CollectCollegeItemAdapter(List<CollegeItem> collegeItemList){
        mCollegeItemList=collegeItemList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pc_collect_college_item,parent,false);
        final CollectCollegeItemAdapter.ViewHolder holder=new CollectCollegeItemAdapter.ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.e("v.getContext()","点击");
                int positon=holder.getAdapterPosition();
                CollegeItem collegeItem=mCollegeItemList.get(positon);//获取点击项
                Intent intent=new Intent(v.getContext(), ClickCollectCollegeActivity.class);
                intent.putExtra("province",collegeItem.getProvince());
                intent.putExtra("collegeName",collegeItem.getName());
                intent.putExtra("collegeId",collegeItem.getCollegeId());
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        CollegeItem collegeItem=mCollegeItemList.get(position);
        holder.name.setText(collegeItem.getName());
        holder.date.setText(collegeItem.getCollectDate());
    }
    @Override
    public  int getItemCount(){
        return mCollegeItemList.size();
    }
}
