package com.example.administrator.android_training_course.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.example.administrator.android_training_course.R;
import com.example.administrator.android_training_course.fragment.ArticleFragment;
import com.example.administrator.android_training_course.fragment.HeadlinesFragment;

public class FragmentTestActivity extends FragmentActivity
        implements HeadlinesFragment.OnHeadlineSelectedListener {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_articles);
        // 确认 Activity 使用的布局版本包含 fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {
            // 不过，如果我们要从先前的状态还原，则无需执行任何操作而应返回，否则
            // 就会得到重叠的 Fragment。
            if (savedInstanceState != null) {
                return;
            }
            // 创建一个要放入 Activity 布局中的新 Fragment
            HeadlinesFragment firstFragment = new HeadlinesFragment();
            // 如果此 Activity 是通过 Intent 发出的特殊指令来启动的，
            // 请将该 Intent 的 extras 以参数形式传递给该 Fragment
            firstFragment.setArguments(getIntent().getExtras());
            // 将该 Fragment 添加到“fragment_container” FrameLayout 中
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    public void onArticleSelected(int position) {
        ArticleFragment articleFrag = (ArticleFragment)
                getSupportFragmentManager().findFragmentById(R.id.article_fragment);

        if (articleFrag != null) {
            // 若 articleFrag 有效，则表示我们正在处理两格布局（平板上）
            // 调用 ArticleFragment 的方法，以更新其内容
            articleFrag.updateArticleView(position);
        } else {
            // 创建 Fragment 并为其添加一个参数，用来指定应显示的文章
            ArticleFragment newFragment = new ArticleFragment();
            Bundle args = new Bundle();
            args.putInt(ArticleFragment.ARG_POSITION, position);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // 将 fragment_container View 中的内容替换为此 Fragment，
            // 然后将该事务添加到返回堆栈，以便用户可以向后导航
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            // 执行事务
            transaction.commit();
        }
    }
}
