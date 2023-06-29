package com.nxist.gaokao.Module;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nxist.gaokao.R;

import java.util.List;

/**
 * Created by 徐源茂 on 2018/3/29.
 */

public class CollegeItemAdapter extends RecyclerView.Adapter<CollegeItemAdapter.ViewHolder> {
    private List<CollegeItem> mCollegeItemList;
    TextView provinceName;
    TextView schoolName;
    static  class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView province;
        public ViewHolder(View view){
            super(view);
            name=(TextView)view.findViewById(R.id.collegeName);
            province=(TextView)view.findViewById(R.id.province);
        }
    }
    public CollegeItemAdapter(List<CollegeItem> collegeItemList,TextView provinceName,TextView schoolName){
        mCollegeItemList=collegeItemList;
        this.provinceName=provinceName;
        this.schoolName=schoolName;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.college_item,parent,false);
        final CollegeItemAdapter.ViewHolder holder=new CollegeItemAdapter.ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int positon=holder.getAdapterPosition();
                CollegeItem collegeItem=mCollegeItemList.get(positon);//获取点击项
                provinceName.setText(collegeItem.getProvince());
                schoolName.setText(collegeItem.getName());
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        CollegeItem collegeItem=mCollegeItemList.get(position);
        holder.name.setText(collegeItem.getName());
        holder.province.setText(collegeItem.getProvince());
    }
    @Override
    public  int getItemCount(){
        return mCollegeItemList.size();
    }
}
