package com.nxist.gaokao.Module;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nxist.gaokao.R;
import com.nxist.gaokao.services.UpdateUserInfo;
import com.nxist.gaokao.view.personalcenter.UpdatePasswordActivity;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class UserItemAdapter extends RecyclerView.Adapter<UserItemAdapter.ViewHolder>{
    private List<UserItem> mUserItemList;
    static  class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;//保存子项最外层布局的实例
        TextView itemName;
        TextView itemValue;
        public ViewHolder(View view){
            super(view);
            itemView=view;
            itemName=(TextView)view.findViewById(R.id.userInfo_itemName);
            itemValue=(TextView)view.findViewById(R.id.userInfo_itemValue);
        }
    }
    public UserItemAdapter(List<UserItem> userItemList){
        mUserItemList=userItemList;
    }
    @Override
    public UserItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_info_item,parent,false);
        final UserItemAdapter.ViewHolder holder=new UserItemAdapter.ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int positon=holder.getAdapterPosition();
                switch(positon){
                    case 1://点击用户名
                        clickUserName(v);break;
                    case 5://点击修改密码
                        clickFeedback(v);break;
                    default:break;
                }
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(UserItemAdapter.ViewHolder holder, int position){
        UserItem userItem=mUserItemList.get(position);
        holder.itemName.setText(userItem.getItemName());
        holder.itemValue.setText(userItem.getItemValue());
    }
    @Override
    public  int getItemCount(){
        return mUserItemList.size();
    }

    private void clickUserName(final View v){//点击用户名
        //定义弹出适配器
        //利用DialogPlus弹出对话框
        DialogPlus dialog = DialogPlus.newDialog(v.getContext())
                .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.userinfo_update_name))//设置适配器
                .setGravity(Gravity.BOTTOM)//设置位置
                .setContentBackgroundResource(R.drawable.bg2)
                .setCancelable(true)//设为可取消
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        if(view.getId()==R.id.userNameConfirm){
                            TextView inputUserName=(TextView)dialog.findViewById(R.id.inputUserName);
                            //将用户更改的用户名进行存储
                            SharedPreferences.Editor editor=v.getContext().getSharedPreferences("userinfo",MODE_PRIVATE).edit();
                            editor.putString("name",inputUserName.getText().toString());
                            editor.apply();
                            //刷新RecyclerView
                            mUserItemList.get(1).setItemValue(inputUserName.getText().toString());
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

    private void clickFeedback(View v){//点击修改密码
        Intent intent=new Intent(v.getContext(), UpdatePasswordActivity.class);
        v.getContext().startActivity(intent);
    }
}
