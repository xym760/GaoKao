package com.nxist.gaokao.Module;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nxist.gaokao.R;
import com.nxist.gaokao.view.personalcenter.ClickCollectMajorActivity;

import java.util.List;

public class CollectMajorItemAdapter extends RecyclerView.Adapter<CollectMajorItemAdapter.ViewHolder> {
    private List<MajorCollectItem> mMajorItemList;
    static  class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView date;
        public ViewHolder(View view){
            super(view);
            name=(TextView)view.findViewById(R.id.collect_major_name);
            date=(TextView)view.findViewById(R.id.collect_major_date);
        }
    }
    public CollectMajorItemAdapter(List<MajorCollectItem> majorItemList){
        mMajorItemList=majorItemList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pc_collect_major_item,parent,false);
        final CollectMajorItemAdapter.ViewHolder holder=new CollectMajorItemAdapter.ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.e("v.getContext()","点击");
                int positon=holder.getAdapterPosition();
                MajorCollectItem majorItem=mMajorItemList.get(positon);//获取点击项
                Intent intent=new Intent(v.getContext(), ClickCollectMajorActivity.class);
                //intent.putExtra("province",collegeItem.getProvince());
                //intent.putExtra("collegeName",collegeItem.getName());
                intent.putExtra("majorCode",majorItem.getMajorCode());
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        MajorCollectItem majorCollectItem=mMajorItemList.get(position);
        holder.name.setText(majorCollectItem.getMajorName());
        holder.date.setText(majorCollectItem.getCollectDate());
    }
    @Override
    public  int getItemCount(){
        return mMajorItemList.size();
    }
}
