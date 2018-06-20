package com.nxist.gaokao.view;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nxist.gaokao.Module.BasicData;
import com.nxist.gaokao.Module.MajorItem;
import com.nxist.gaokao.Module.TreeHolder;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.NetworkConnect;
import com.nxist.gaokao.view.home.MajorDetailActivity;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MajorQuery extends AppCompatActivity implements TreeNode.TreeNodeClickListener {
    private List<MajorItem> majorItemList=new ArrayList<>();//存放多个院校
    private Button majorSearchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.major_query);
        initUI();
    }
    private void initUI(){

        //定义加载提示框
        final SweetAlertDialog pDialog = new SweetAlertDialog(MajorQuery.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("加载中...");
        pDialog.setCancelable(true);
        pDialog.show();

        majorSearchButton=(Button)findViewById(R.id.major_search);
        majorSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MajorQuery.this,MajorSearch.class);
                startActivity(intent);
            }
        });
        //查询数据库中所有专业
        NetworkConnect.getData(BasicData.SERVER_ADDRESS, "home/majorQuery", new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                initList(response.body());
                pDialog.hide();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
    //初始化列表数据
    private void initList(String jsonString){
        //使用Gson解析后台JSON字符串
        Gson gson=new Gson();
        TreeNode root = TreeNode.root();//定义一个树的根节点
        //构建majorItemList
        majorItemList=gson.fromJson(jsonString,new TypeToken<List<MajorItem>>(){}.getType());
        int size=majorItemList.size();
        for(int i=0;i<size;i++){//遍历返回来的json数据
            String presentMajorCode=majorItemList.get(i).getMajorCode();
            String presentMajorName=majorItemList.get(i).getMajorName();
            if(presentMajorCode.length()==2){//如果是大类（2位）
                //定义大类布局
                TreeHolder.IconTreeItem nodeItem = new TreeHolder.IconTreeItem(R.id.treeItemIcon,presentMajorCode,presentMajorName+"("+presentMajorCode+")");
                TreeNode bigCatagory = new TreeNode(nodeItem).setViewHolder(new TreeHolder(MajorQuery.this));
                //验证待添加大类是否重复
                Boolean isRepetition=false;
                for(TreeNode t:root.getChildren()){
                    if(((TreeHolder.IconTreeItem)t.getValue()).code.equals(presentMajorCode)){//判断是否重复
                        isRepetition=true;
                        break;
                    }
                }
                if(!isRepetition)
                    root.addChild(bigCatagory);
            }else
                if (presentMajorCode.length()==4){//如果是小类（4位）
                    //定义小类布局
                    TreeHolder.IconTreeItem nodeItem = new TreeHolder.IconTreeItem(R.id.treeItemIcon1,presentMajorCode,presentMajorName+"("+presentMajorCode+")");
                    TreeNode smallCatagory = new TreeNode(nodeItem).setViewHolder(new TreeHolder(MajorQuery.this));
                    int sizeBig=root.getChildren().size();//获得当前树中的大类个数
                    for(int j=0;j<sizeBig;j++){//遍历大类，以确定该加入哪个大类
                        if(presentMajorCode.startsWith(((TreeHolder.IconTreeItem)root.getChildren().get(j).getValue()).code)){
                            //验证待添加小类是否重复
                            Boolean isRepetition1=false;
                            for(TreeNode t:root.getChildren().get(j).getChildren()){
                                if(((TreeHolder.IconTreeItem)t.getValue()).code.equals(presentMajorCode)){//判断小类是否重复
                                    isRepetition1=true;
                                    break;
                                }
                            }
                            if(!isRepetition1)//不重复则添加之
                                root.getChildren().get(j).addChild(smallCatagory);
                            break;//找到待添加大类后停止遍历其它大类
                        }
                    }
                }else{//如果是项（至少6位）
                    //定义项布局
                    TreeHolder.IconTreeItem nodeItem = new TreeHolder.IconTreeItem(R.id.treeItemIcon2,presentMajorCode,presentMajorName+"("+presentMajorCode+")");
                    TreeNode majorItem = new TreeNode(nodeItem).setViewHolder(new TreeHolder(MajorQuery.this));
                    majorItem.setClickListener(this);
                    int sizeBig=root.getChildren().size();//获得当前树中的大类个数
                    for(int j=0;j<sizeBig;j++){//遍历大类，以确定该加入哪个大类
                        if(presentMajorCode.startsWith(((TreeHolder.IconTreeItem)root.getChildren().get(j).getValue()).code)){
                            TreeNode aimBigCatagory=root.getChildren().get(j);
                            int sizeSmall=aimBigCatagory.getChildren().size();//获得目标大类中小类个数
                            for(int k=0;k<sizeSmall;k++){//遍历待添加大类，以确定该加入哪个小类
                                if(presentMajorCode.startsWith(((TreeHolder.IconTreeItem)aimBigCatagory.getChildren().get(k).getValue()).code)){
                                    //验证待添加项是否重复
                                    Boolean isRepetition2=false;
                                    for(TreeNode t:aimBigCatagory.getChildren().get(k).getChildren()){
                                        if(((TreeHolder.IconTreeItem)t.getValue()).code.equals(presentMajorCode)){//判断项是否重复
                                            isRepetition2=true;
                                            break;
                                        }
                                    }
                                    if(!isRepetition2)//不重复则添加之
                                        aimBigCatagory.getChildren().get(k).addChildren(majorItem);//加入项
                                    break;//找到待添加小类后停止遍历当前大类下的其它小类
                                }
                            }
                            break;//找到待添加大类后停止遍历其它大类
                        }
                    }
                }
        }
        //将树加入布局
        AndroidTreeView androidTreeView = new AndroidTreeView(MajorQuery.this, root);
        ((LinearLayout)findViewById(R.id.out_layout)).addView(androidTreeView.getView());
    }

    @Override
    public void onClick(TreeNode node, Object value) {
        String majorCode=((TreeHolder.IconTreeItem)node.getValue()).code;
        Intent intent=new Intent(MajorQuery.this, MajorDetailActivity.class);
        intent.putExtra("majorCode",majorCode);
        startActivity(intent);
    }
}
