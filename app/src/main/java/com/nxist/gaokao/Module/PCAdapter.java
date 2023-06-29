package com.nxist.gaokao.Module;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.nxist.gaokao.R;
import com.nxist.gaokao.services.UpdateUserInfo;
import com.nxist.gaokao.view.personalcenter.CollectActivity;
import com.nxist.gaokao.view.personalcenter.FeedbackActivity;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.util.List;

/**
 * Created by 徐源茂 on 2018/3/27.
 */

public class PCAdapter extends RecyclerView.Adapter<PCAdapter.ViewHolder>{
    private List<PCItem> mPCItemList;
    private String[] subjectData={"文科","理科"};
    static  class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;//保存子项最外层布局的实例
        TextView itemName;
        ImageView itemImage;
        TextView itemResult;
        public ViewHolder(View view){
            super(view);
            itemView=view;
            itemName=(TextView)view.findViewById(R.id.personalCenterTextViewItem);
            itemImage=(ImageView) view.findViewById(R.id.personalCenterImageViewItem);
            itemResult=(TextView)view.findViewById(R.id.item_result);
        }
    }
    public PCAdapter(List<PCItem> PCItemList){
        mPCItemList=PCItemList;
    }
    @Override
    public PCAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.personal_center_item,parent,false);
        final PCAdapter.ViewHolder holder=new PCAdapter.ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int positon=holder.getAdapterPosition();
                switch(positon){
                    case 0://点击科目
                        clickSubject(v);break;
                    case 1://点击地区
                        clickRegion(v);break;
                    case 2://点击分数
                        clickGrade(v);break;
                    case 3://点击关注
                        clickCollect(v);break;
                    case 4://点击意见反馈
                        clickFeedback(v);break;
                    default:break;
                }
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(PCAdapter.ViewHolder holder, int position){
        PCItem pCItem=mPCItemList.get(position);
        holder.itemName.setText(pCItem.getName());
        holder.itemImage.setImageResource(pCItem.getImageId());
        holder.itemResult.setText(pCItem.getResult());
    }
    @Override
    public  int getItemCount(){
        return mPCItemList.size();
    }

    private void clickSubject(final View v){//点击科目
        //定义弹出适配器
        ArrayAdapter<String> subjectAdapter=new ArrayAdapter<String>(v.getContext(),android.R.layout.simple_list_item_1,subjectData);
        //利用DialogPlus弹出对话框
        DialogPlus dialog = DialogPlus.newDialog(v.getContext())
                .setAdapter(subjectAdapter)//设置适配器
                .setGravity(Gravity.CENTER)//设置位置
                .setCancelable(true)//设为可取消
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        //将用户选择的科目进行存储
                        SharedPreferences.Editor editor=v.getContext().getSharedPreferences("userinfo",MODE_PRIVATE).edit();
                        editor.putString("subject",subjectData[position]);
                        editor.apply();
                        //刷新RecyclerView
                        mPCItemList.get(0).setResult(subjectData[position]);
                        notifyDataSetChanged();
                        //取消对话框
                        dialog.dismiss();
                        //更新用户数据到服务器
                        UpdateUserInfo.updateUser(v.getContext().getSharedPreferences("userinfo",MODE_PRIVATE));
                    }
                })
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
    }

    private void clickRegion(final View v){//点击地区
        //定义弹出适配器
        ArrayAdapter<String> regionAdapter=new ArrayAdapter<String>(v.getContext(),android.R.layout.simple_list_item_1,BasicData.PROVINCE);
        //利用DialogPlus弹出对话框
        DialogPlus dialog = DialogPlus.newDialog(v.getContext())
                .setAdapter(regionAdapter)//设置适配器
                .setGravity(Gravity.CENTER)//设置位置
                .setCancelable(true)//设为可取消
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        //将用户选择的科目进行存储
                        SharedPreferences.Editor editor=v.getContext().getSharedPreferences("userinfo",MODE_PRIVATE).edit();
                        editor.putString("province",BasicData.PROVINCE[position]);
                        editor.apply();
                        //刷新RecyclerView
                        mPCItemList.get(1).setResult(BasicData.PROVINCE[position]);
                        notifyDataSetChanged();
                        //取消对话框
                        dialog.dismiss();
                        //更新地区到服务器
                        UpdateUserInfo.updateUser(v.getContext().getSharedPreferences("userinfo",MODE_PRIVATE));
                    }
                })
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
    }

    private void clickGrade(final View v){//点击分数
        //定义弹出适配器
        //利用DialogPlus弹出对话框
        DialogPlus dialog = DialogPlus.newDialog(v.getContext())
                .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.grade_item))//设置适配器
                .setGravity(Gravity.BOTTOM)//设置位置
                .setContentBackgroundResource(R.drawable.bg1)
                .setCancelable(true)//设为可取消
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if(view.getId()==R.id.gradeConfirm){
                            TextView inputGrade=(TextView)dialog.findViewById(R.id.inputGrade);
                            //将用户设置的分数进行存储
                            SharedPreferences.Editor editor=v.getContext().getSharedPreferences("userinfo",MODE_PRIVATE).edit();
                            int gradeValue=Integer.parseInt(inputGrade.getText().toString());
                            if(gradeValue>0&&gradeValue<=750)
                                editor.putInt("score",gradeValue);
                            else{
                                Toast.makeText(v.getContext(),"请输入0~750之间的数字",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            editor.apply();
                            //刷新RecyclerView
                            mPCItemList.get(2).setResult(inputGrade.getText().toString());
                            notifyDataSetChanged();
                            dialog.dismiss();
                            UpdateUserInfo.updateUser(v.getContext().getSharedPreferences("userinfo",MODE_PRIVATE));
                        }
                    }
                })
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
    }

    private void clickCollect(View v){//点击关注
        Intent intent=new Intent(v.getContext(), CollectActivity.class);
        v.getContext().startActivity(intent);
    }

    private void clickFeedback(View v){//点击意见反馈
        Intent intent=new Intent(v.getContext(), FeedbackActivity.class);
        v.getContext().startActivity(intent);
    }
}
