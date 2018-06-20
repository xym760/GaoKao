package com.nxist.gaokao.Module;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.nxist.gaokao.R;
import com.nxist.gaokao.services.NetworkConnect;
import com.nxist.gaokao.services.UpdateUserInfo;
import com.nxist.gaokao.services.UserService;
import com.nxist.gaokao.view.LoginActivity;
import com.nxist.gaokao.view.exchange.CommentActivity;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by xym760 on 2018/4/2.
 */

public class TopicAdapter extends BaseAdapter {
    private List<TopicData> orders;
    private Context context;
    //定义九宫格适配器
    private NineGridImageViewAdapter<String> mAdapter = new NineGridImageViewAdapter<String>() {
        @Override
        protected void onDisplayImage(Context context, ImageView imageView, String imgUrl) {
            Picasso.with(context)
                    .load(imgUrl)
                    .resize(400,400)
                    .placeholder(R.drawable.ic_default_image)
                    .into(imageView);
        }
        @Override
        protected ImageView generateImageView(Context context) {
            return super.generateImageView(context);
        }
        @Override
        protected void onItemImageClick(Context context,ImageView imageView, int index, List<String> photoList) {
            super.onItemImageClick(context,imageView,index,photoList);
            //showBigPicture(context, photoList.get(index).getBigUrl());
        }
    };

    public TopicAdapter(Context context, List<TopicData> orders) {
        this.context = context;
        this.orders = orders;
    }

    public void addOrder(List<TopicData> orders) {
        this.orders = orders;

    }

    @Override
    public int getCount() {
        if (orders == null)
            return 0;
        else
            return orders.size();
    }

    @Override
    public Object getItem(int i) {
        return orders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.space_list, null);
            holder = new ViewHolder();
            holder.userName = (TextView) view.findViewById(R.id.spaceListName);
            holder.date = (TextView) view.findViewById(R.id.spaceListTime);
            holder.content = (TextView) view.findViewById(R.id.spaceListSay);
            holder.userIcon = (ImageView) view.findViewById(R.id.spaceListIcon);
            holder.nineGrid = (NineGridImageView) view.findViewById(R.id.nineGrid);
            holder.like=(ImageView)view.findViewById(R.id.like);
            holder.likeCount=(TextView)view.findViewById(R.id.like_count);
            holder.comment=(ImageView)view.findViewById(R.id.comment);
            holder.commentCount=(TextView) view.findViewById(R.id.comment_count);
            if(orders.get(i).getIsLike())
                holder.likeFlag=true;
            else
                holder.likeFlag=false;
            holder.likeNumber=orders.get(i).getLikeNumber();
            view.setTag(holder);//将ViewHolder存储在view中
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Picasso.with(context).load(BasicData.USER_AVATAR_URL+orders.get(i).getUserId()+"_"+orders.get(i).getUserIcon()).into(holder.userIcon);
        String content = orders.get(i).getContent();

        if (content == null || content.length() <= 0) {
            holder.content.setVisibility(View.GONE);
        } else {
            holder.content.setVisibility(View.VISIBLE);
            holder.content.setText(content);
        }
        holder.userName.setText(orders.get(i).getUserName());
        holder.date.setText(orders.get(i).getDate());
        if (orders.get(i).getIsHavePicture()) {//判断是否有图片
            holder.nineGrid.setAdapter(mAdapter);
            holder.nineGrid.setImagesData(orders.get(i).getPictureList());
        } else {
            holder.nineGrid.setVisibility(View.GONE);
        }
        //点赞点击事件
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!UserService.IsLogin(v.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE))){//如果未登录
                    Intent loginIntent=new Intent(v.getContext(),LoginActivity.class);
                    v.getContext().startActivity(loginIntent);
                    return;
                }
                if(!holder.likeFlag){
                    holder.like.setImageResource(R.drawable.ic_good_filling);
                    holder.likeFlag=true;
                    holder.likeNumber++;
                    holder.likeCount.setText(String.valueOf(holder.likeNumber));
                }
                else{
                    holder.like.setImageResource(R.drawable.ic_good);
                    holder.likeFlag=false;
                    holder.likeNumber--;
                    holder.likeCount.setText(String.valueOf(holder.likeNumber));
                }
                Like like=new Like();
                SharedPreferences sharedPreferences=v.getContext().getSharedPreferences("userinfo",MODE_PRIVATE);
                like.setUserId(sharedPreferences.getInt("userId",-1));
                like.setTopicId(orders.get(i).getTopicId());
                Gson gson=new Gson();
                String likeString=gson.toJson(like);
                NetworkConnect.postData("exchange/like",likeString,new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {

                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {

                    }

                });
            }
        });
        if(orders.get(i).getIsLike())//如果查询到用户已点赞，则更换为点赞图片
            holder.like.setImageResource(R.drawable.ic_good_filling);
        else
            holder.like.setImageResource(R.drawable.ic_good);
        holder.likeCount.setText(String.valueOf(orders.get(i).getLikeNumber()));
        //点击评论
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!UserService.IsLogin(v.getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE))){//如果未登录
                    Intent loginIntent=new Intent(v.getContext(),LoginActivity.class);
                    v.getContext().startActivity(loginIntent);
                    return;
                }
                Intent intent=new Intent(v.getContext(), CommentActivity.class);
                intent.putExtra("topicId",orders.get(i).getTopicId());
                Log.d("v.getContext()", "点击topicId: "+orders.get(i).getTopicId());
                v.getContext().startActivity(intent);
            }
        });
        holder.commentCount.setText(String.valueOf(orders.get(i).getReplyNumber()));
        return view;
    }

    private class ViewHolder {
        private TextView userName;
        private TextView date;
        private TextView content;
        private ImageView userIcon;
        private NineGridImageView nineGrid;
        private ImageView like;
        private TextView likeCount;
        private ImageView comment;
        private TextView commentCount;
        private Boolean likeFlag;
        private int likeNumber;
    }
}
