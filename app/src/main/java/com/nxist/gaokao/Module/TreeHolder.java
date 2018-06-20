package com.nxist.gaokao.Module;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.nxist.gaokao.R;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by xym760 on 2018/3/30.
 */

public class TreeHolder extends TreeNode.BaseNodeViewHolder<TreeHolder.IconTreeItem> {
    @Override
    public View createNodeView(TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        //判断加载哪个布局
        int i=R.layout.tree_item2;
        if(value.icon==R.id.treeItemIcon)
            i=R.layout.tree_item;
        else if(value.icon==R.id.treeItemIcon1)
            i=R.layout.tree_item1;
        else
            i=R.layout.tree_item2;
        final View view = inflater.inflate(i, null, false);
        TextView tvValue = (TextView) view.findViewById(R.id.treeItemName);
        tvValue.setText(value.text);
        return view;
    }
    public static class IconTreeItem {
        public int icon;
        public String code;
        public String text;
        public IconTreeItem(int icon,String code,String text){
            this.icon=icon;
            this.code=code;
            this.text=text;
        }
    }
    public TreeHolder(Context context){
        super(context);
    }
}
