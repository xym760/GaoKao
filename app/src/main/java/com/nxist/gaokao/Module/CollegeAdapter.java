package com.nxist.gaokao.Module;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nxist.gaokao.R;

import java.util.List;

/**
 * 学校查询，展示具体某个学校的适配器
 * Created by 徐源茂 on 2018/3/26.
 */

public class CollegeAdapter extends RecyclerView.Adapter<CollegeAdapter.ViewHolder> {
    private List<CollegeDetailItem> mCollegeDetailItemList;
    static  class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemName;
        TextView itemContent;
        public ViewHolder(View view){
            super(view);
            itemName=(TextView)view.findViewById(R.id.collegeItemName);
            itemContent=(TextView)view.findViewById(R.id.collegeItemContent);
        }
    }
    public CollegeAdapter(List<CollegeDetailItem> collegeDetailItemList){
        mCollegeDetailItemList=collegeDetailItemList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.college_detail_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        CollegeDetailItem collegeDetailItem=mCollegeDetailItemList.get(position);
        holder.itemName.setText(collegeDetailItem.getItemName());
        holder.itemContent.setText(collegeDetailItem.getItemContent());
    }
    @Override
    public  int getItemCount(){
        return mCollegeDetailItemList.size();
    }
}
