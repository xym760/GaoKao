package com.nxist.gaokao.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nxist.gaokao.Module.BasicData;
import com.nxist.gaokao.Module.PCAdapter;
import com.nxist.gaokao.Module.PCItem;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.UpdateUserInfo;
import com.nxist.gaokao.view.personalcenter.UserInfoActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 徐源茂 on 2018/3/18.
 */

public class PersonalCenterFragment extends Fragment implements View.OnClickListener {
    private List<PCItem> PCList=new ArrayList<>();
    private CircleImageView profileImage;
    private RecyclerView recyclerView;
    private PCAdapter adapter;
    SharedPreferences sp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view=inflater.inflate(R.layout.personal_center,container,false);
        initUI(view);
        return view;
    }

    private void initUI(View view){
        profileImage=(CircleImageView)view.findViewById(R.id.profile_image_update);
        profileImage.setOnClickListener(this);
        //初始化滚动列表
        initList();
        recyclerView=(RecyclerView)view.findViewById(R.id.personalCenterList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        adapter=new PCAdapter(PCList);
        recyclerView.setAdapter(adapter);
        //设置头像
        setAvatar();
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.profile_image_update:
                Intent intent=new Intent(getActivity().getApplicationContext(),UserInfoActivity.class);
                startActivity(intent);
                break;
                default:break;
        }
    }

    private void initList(){
        //从SharedPreferences中读取用户数据
        sp=getActivity().getApplicationContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        PCList.removeAll(PCList);
        PCItem subject=new PCItem("科目",R.drawable.ic_category,sp.getString("subject",""));
        PCList.add(subject);
        PCItem region=new PCItem("地区",R.drawable.ic_map,sp.getString("province",""));
        PCList.add(region);
        PCItem grade;
        if(sp.getInt("score",0)==-1)
            grade=new PCItem("分数",R.drawable.ic_viewlist,"");//如果用户登录时没有指定分数，则为空
        else
            grade=new PCItem("分数",R.drawable.ic_viewlist,String.valueOf(sp.getInt("score",0)));
        PCList.add(grade);
        PCItem favorite=new PCItem("关注",R.drawable.ic_favorites_filling,"");
        PCList.add(favorite);
        PCItem feedback=new PCItem("意见反馈",R.drawable.ic_comments,"");
        PCList.add(feedback);
        PCItem set=new PCItem("设置",R.drawable.ic_set,"");
        PCList.add(set);
        PCItem help=new PCItem("帮助",R.drawable.ic_help,"");
        PCList.add(help);
    }

    private void setAvatar(){
        sp=getActivity().getApplicationContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        if(sp.getString("avatar","").equals("")){//用户服务器端未设置头像

        }else{
            String urlImg= BasicData.USER_AVATAR_URL+sp.getInt("userId",0)+"_"+sp.getString("avatar","");
            Picasso.with(getActivity().getApplicationContext()).load(urlImg).placeholder(R.drawable.ic_help).into(profileImage);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
        setAvatar();//从服务器获取头像

        sp=getActivity().getApplicationContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        UpdateUserInfo.updateUser(sp);//将本地用户数据同步到服务器
        adapter.notifyDataSetChanged();
    }
}
