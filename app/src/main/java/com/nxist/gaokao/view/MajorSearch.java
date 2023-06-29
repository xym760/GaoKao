package com.nxist.gaokao.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.nxist.gaokao.R;

import me.gujun.android.taggroup.TagGroup;

public class MajorSearch extends AppCompatActivity implements TagGroup.OnTagClickListener {
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.major_search);
        TagGroup mTagGroup = (TagGroup) findViewById(R.id.tag_group);
        mTagGroup.setTags(new String[]{"计算机科学与技术", "软件工程", "Unity3D"});
        mTagGroup.setOnTagClickListener(this);
        searchView=(SearchView)findViewById(R.id.searchMajorView) ;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private String TAG = getClass().getSimpleName();

            /*
             * 在输入时触发的方法，当字符真正显示到searchView中才触发，像是拼音，在舒服法组词的时候不会触发
             *
             * @param queryText
             *
             * @return false if the SearchView should perform the default action
             * of showing any suggestions if available, true if the action was
             * handled by the listener.
             */
            @Override
            public boolean onQueryTextChange(String queryText) {
                Log.d(TAG, "onQueryTextChange = " + queryText);

                return true;
            }

            /*
             * 输入完成后，提交时触发的方法，一般情况是点击输入法中的搜索按钮才会触发。表示现在正式提交了
             *
             * @param queryText
             *
             * @return true to indicate that it has handled the submit request.
             * Otherwise return false to let the SearchView handle the
             * submission by launching any associated intent.
             */
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                Log.d(TAG, "onQueryTextSubmit = " + queryText);

                if (searchView != null) {
                    // 得到输入管理对象
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
                    }
                    searchView.clearFocus(); // 不获取焦点
                }
                android.content.Intent intent=new android.content.Intent(MajorSearch.this, com.nxist.gaokao.view.home.MajorDetailActivity.class);
                intent.putExtra("majorName",queryText);
                startActivity(intent);
                return true;
            }});
    }

    @Override
    public void onTagClick(String s){
        android.content.Intent intent=new android.content.Intent(MajorSearch.this, com.nxist.gaokao.view.home.MajorDetailActivity.class);
        intent.putExtra("majorName",s);
        startActivity(intent);
    }
}
