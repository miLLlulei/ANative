package com.mill.mnative.reader.activity;

import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mill.mnative.reader.R;
import com.mill.mnative.reader.base.BaseActivity;
import com.mill.mnative.reader.fragment.CatalogFragment;
import com.mill.mnative.reader.util.FileUtils;
import com.mill.mnative.reader.util.PageFactory;


/**
 *
 */
public class MarkActivity extends BaseActivity {

    Toolbar toolbar;
    AppBarLayout appbar;
    private PageFactory pageFactory;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_mark;
    }

    @Override
    protected void initView() {
        appbar = findViewById(R.id.appbar);
        toolbar = findViewById(R.id.toolbar);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    @Override
    protected void initData() {
        pageFactory = PageFactory.getInstance();

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(FileUtils.getFileName(pageFactory.getBookPath()));
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fl, CatalogFragment.newInstance(pageFactory.getBookPath())).commit();
    }

    @Override
    protected void initListener() {

    }

}
