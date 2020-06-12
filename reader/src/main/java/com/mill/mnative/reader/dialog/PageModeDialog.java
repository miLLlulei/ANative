package com.mill.mnative.reader.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.mill.mnative.reader.R;
import com.mill.mnative.reader.util.ReaderConfig;


/**
 * Created by Administrator on 2016/8/30 0030.
 */
public class PageModeDialog extends Dialog implements View.OnClickListener{

    TextView tv_simulation;
    TextView tv_cover;
    TextView tv_slide;
    TextView tv_none;

    private ReaderConfig config;
    private PageModeListener pageModeListener;

    private PageModeDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
    }

    public PageModeDialog(Context context) {
        this(context, R.style.setting_dialog);
    }

    public PageModeDialog(Context context, int themeResId) {
        super(context, themeResId);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_pagemode);
        // 初始化View注入
        tv_simulation = findViewById(R.id.tv_simulation);
        tv_cover = findViewById(R.id.tv_cover);
        tv_slide = findViewById(R.id.tv_slide);
        tv_none = findViewById(R.id.tv_none);

        tv_simulation.setOnClickListener(this);
        tv_cover.setOnClickListener(this);
        tv_slide.setOnClickListener(this);
        tv_none.setOnClickListener(this);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);

        config = ReaderConfig.getInstance();
        selectPageMode(config.getPageMode());
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_simulation) {
            selectPageMode(ReaderConfig.PAGE_MODE_SIMULATION);
            setPageMode(ReaderConfig.PAGE_MODE_SIMULATION);
        } else if (id == R.id.tv_cover) {
            selectPageMode(ReaderConfig.PAGE_MODE_COVER);
            setPageMode(ReaderConfig.PAGE_MODE_COVER);
        } else if (id == R.id.tv_slide) {
            selectPageMode(ReaderConfig.PAGE_MODE_SLIDE);
            setPageMode(ReaderConfig.PAGE_MODE_SLIDE);
        } else if (id == R.id.tv_none) {
            selectPageMode(ReaderConfig.PAGE_MODE_NONE);
            setPageMode(ReaderConfig.PAGE_MODE_NONE);
        }
    }

    //设置翻页
    public void setPageMode(int pageMode) {
        config.setPageMode(pageMode);
        if (pageModeListener != null) {
            pageModeListener.changePageMode(pageMode);
        }
    }
    //选择怕翻页
    private void selectPageMode(int pageMode) {
        if (pageMode == ReaderConfig.PAGE_MODE_SIMULATION) {
            setTextViewSelect(tv_simulation, true);
            setTextViewSelect(tv_cover, false);
            setTextViewSelect(tv_slide, false);
            setTextViewSelect(tv_none, false);
        } else if (pageMode == ReaderConfig.PAGE_MODE_COVER) {
            setTextViewSelect(tv_simulation, false);
            setTextViewSelect(tv_cover, true);
            setTextViewSelect(tv_slide, false);
            setTextViewSelect(tv_none, false);
        } else if (pageMode == ReaderConfig.PAGE_MODE_SLIDE) {
            setTextViewSelect(tv_simulation, false);
            setTextViewSelect(tv_cover, false);
            setTextViewSelect(tv_slide, true);
            setTextViewSelect(tv_none, false);
        } else if (pageMode == ReaderConfig.PAGE_MODE_NONE) {
            setTextViewSelect(tv_simulation, false);
            setTextViewSelect(tv_cover, false);
            setTextViewSelect(tv_slide, false);
            setTextViewSelect(tv_none, true);
        }
    }

    //设置按钮选择的背景
    private void setTextViewSelect(TextView textView, Boolean isSelect) {
        if (isSelect) {
            textView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_select_bg));
            textView.setTextColor(getContext().getResources().getColor(R.color.read_dialog_button_select));
        } else {
            textView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_bg));
            textView.setTextColor(getContext().getResources().getColor(R.color.white));
        }
    }

    public void setPageModeListener(PageModeListener pageModeListener) {
        this.pageModeListener = pageModeListener;
    }

    public interface PageModeListener {
        void changePageMode(int pageMode);
    }
}
