package com.mill.mnative.reader.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mill.mnative.reader.R;
import com.mill.mnative.reader.util.ReaderConfig;
import com.mill.mnative.reader.util.DisplayUtils;
import com.mill.mnative.reader.view.CircleImageView;

/**
 * Created by Administrator on 2016/7/26 0026.
 */
public class SettingDialog extends Dialog implements View.OnClickListener{

    TextView tv_dark;
    SeekBar sb_brightness;
    TextView tv_bright;
    TextView tv_xitong;
    TextView tv_subtract;
    TextView tv_size;
    TextView tv_add;
    TextView tv_qihei;
    TextView tv_default;
    CircleImageView iv_bg_default;
    CircleImageView iv_bg1;
    CircleImageView iv_bg2;
    CircleImageView iv_bg3;
    CircleImageView iv_bg4;
    TextView tv_size_default;
    TextView tv_fzxinghei;
    TextView tv_fzkatong;
    TextView tv_bysong;


    private ReaderConfig config;
    private Boolean isSystem;
    private SettingListener mSettingListener;
    private int FONT_SIZE_MIN;
    private int FONT_SIZE_MAX;
    private int currentFontSize;

    private SettingDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
    }

    public SettingDialog(Context context) {
        this(context, R.style.setting_dialog);
    }

    public SettingDialog(Context context, int themeResId) {
        super(context, themeResId);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_setting);
        // 初始化View注入
        tv_bysong = findViewById(R.id.tv_bysong);
        tv_fzkatong = findViewById(R.id.tv_fzkatong);
        tv_fzxinghei = findViewById(R.id.tv_fzxinghei);
        tv_size_default = findViewById(R.id.tv_size_default);
        iv_bg4 = findViewById(R.id.iv_bg_4);
        iv_bg3 = findViewById(R.id.iv_bg_3);
        iv_bg2 = findViewById(R.id.iv_bg_2);
        iv_bg1 = findViewById(R.id.iv_bg_1);
        iv_bg_default = findViewById(R.id.iv_bg_default);
        tv_qihei = findViewById(R.id.tv_qihei);
        tv_add = findViewById(R.id.tv_add);
        tv_size = findViewById(R.id.tv_size);
        tv_subtract = findViewById(R.id.tv_subtract);
        tv_xitong = findViewById(R.id.tv_xitong);
        sb_brightness = findViewById(R.id.sb_brightness);
        tv_bright = findViewById(R.id.tv_bright);
        tv_dark = findViewById(R.id.tv_dark);
        tv_default = findViewById(R.id.tv_default);


        tv_dark.setOnClickListener(this);
        tv_bright.setOnClickListener(this);
        tv_xitong.setOnClickListener(this);
        tv_subtract.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        tv_size_default.setOnClickListener(this);
        tv_qihei.setOnClickListener(this);
        tv_fzxinghei.setOnClickListener(this);
        tv_fzkatong.setOnClickListener(this);
        tv_bysong.setOnClickListener(this);
        tv_default.setOnClickListener(this);
        iv_bg_default.setOnClickListener(this);
        iv_bg1.setOnClickListener(this);
        iv_bg2.setOnClickListener(this);
        iv_bg3.setOnClickListener(this);
        iv_bg4.setOnClickListener(this);


        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);

        FONT_SIZE_MIN = (int) getContext().getResources().getDimension(R.dimen.reading_min_text_size);
        FONT_SIZE_MAX = (int) getContext().getResources().getDimension(R.dimen.reading_max_text_size);

        config = ReaderConfig.getInstance();

        //初始化亮度
        isSystem = config.isSystemLight();
        setTextViewSelect(tv_xitong, isSystem);
        setBrightness(config.getLight());

        //初始化字体大小
        currentFontSize = (int) config.getFontSize();
        tv_size.setText(currentFontSize + "");

        //初始化字体
        tv_default.setTypeface(config.getTypeface(ReaderConfig.FONTTYPE_DEFAULT));
        tv_qihei.setTypeface(config.getTypeface(ReaderConfig.FONTTYPE_QIHEI));
//        tv_fzxinghei.setTypeface(config.getTypeface(Config.FONTTYPE_FZXINGHEI));
        tv_fzkatong.setTypeface(config.getTypeface(ReaderConfig.FONTTYPE_FZKATONG));
        tv_bysong.setTypeface(config.getTypeface(ReaderConfig.FONTTYPE_BYSONG));
//        tv_xinshou.setTypeface(config.getTypeface(Config.FONTTYPE_XINSHOU));
//        tv_wawa.setTypeface(config.getTypeface(Config.FONTTYPE_WAWA));
        selectTypeface(config.getTypefacePath());

        selectBg(config.getBookBgType());

        sb_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 10) {
                    changeBright(false, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //选择背景
    private void selectBg(int type) {
        switch (type) {
            case ReaderConfig.BOOK_BG_DEFAULT:
                iv_bg_default.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
                iv_bg1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                break;
            case ReaderConfig.BOOK_BG_1:
                iv_bg_default.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg1.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
                iv_bg2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                break;
            case ReaderConfig.BOOK_BG_2:
                iv_bg_default.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg2.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
                iv_bg3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                break;
            case ReaderConfig.BOOK_BG_3:
                iv_bg_default.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg3.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
                iv_bg4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                break;
            case ReaderConfig.BOOK_BG_4:
                iv_bg_default.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                iv_bg4.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
                break;
        }
    }

    //设置字体
    public void setBookBg(int type) {
        config.setBookBg(type);
        if (mSettingListener != null) {
            mSettingListener.changeBookBg(type);
        }
    }

    //选择字体
    private void selectTypeface(String typeface) {
        if (typeface.equals(ReaderConfig.FONTTYPE_DEFAULT)) {
            setTextViewSelect(tv_default, true);
            setTextViewSelect(tv_qihei, false);
            setTextViewSelect(tv_fzxinghei, false);
            setTextViewSelect(tv_fzkatong, false);
            setTextViewSelect(tv_bysong, false);
//            setTextViewSelect(tv_xinshou, false);
//            setTextViewSelect(tv_wawa, false);
        } else if (typeface.equals(ReaderConfig.FONTTYPE_QIHEI)) {
            setTextViewSelect(tv_default, false);
            setTextViewSelect(tv_qihei, true);
            setTextViewSelect(tv_fzxinghei, false);
            setTextViewSelect(tv_fzkatong, false);
            setTextViewSelect(tv_bysong, false);
//            setTextViewSelect(tv_xinshou, false);
//            setTextViewSelect(tv_wawa, false);
        } else if (typeface.equals(ReaderConfig.FONTTYPE_FZXINGHEI)) {
            setTextViewSelect(tv_default, false);
            setTextViewSelect(tv_qihei, false);
            setTextViewSelect(tv_fzxinghei, true);
            setTextViewSelect(tv_fzkatong, false);
            setTextViewSelect(tv_bysong, false);
//            setTextViewSelect(tv_xinshou, true);
//            setTextViewSelect(tv_wawa, false);
        } else if (typeface.equals(ReaderConfig.FONTTYPE_FZKATONG)) {
            setTextViewSelect(tv_default, false);
            setTextViewSelect(tv_qihei, false);
            setTextViewSelect(tv_fzxinghei, false);
            setTextViewSelect(tv_fzkatong, true);
            setTextViewSelect(tv_bysong, false);
//            setTextViewSelect(tv_xinshou, false);
//            setTextViewSelect(tv_wawa, true);
        } else if (typeface.equals(ReaderConfig.FONTTYPE_BYSONG)) {
            setTextViewSelect(tv_default, false);
            setTextViewSelect(tv_qihei, false);
            setTextViewSelect(tv_fzxinghei, false);
            setTextViewSelect(tv_fzkatong, false);
            setTextViewSelect(tv_bysong, true);
//            setTextViewSelect(tv_xinshou, false);
//            setTextViewSelect(tv_wawa, true);
        }
    }

    //设置字体
    public void setTypeface(String typeface) {
        config.setTypeface(typeface);
        Typeface tface = config.getTypeface(typeface);
        if (mSettingListener != null) {
            mSettingListener.changeTypeFace(tface);
        }
    }

    //设置亮度
    public void setBrightness(float brightness) {
        sb_brightness.setProgress((int) (brightness * 100));
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

    private void applyCompat() {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
    }

    public Boolean isShow() {
        return isShowing();
    }



    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_dark) {
        } else if (id == R.id.tv_bright) {
        } else if (id == R.id.tv_xitong) {
            isSystem = !isSystem;
            changeBright(isSystem, sb_brightness.getProgress());
        } else if (id == R.id.tv_subtract) {
            subtractFontSize();
        } else if (id == R.id.tv_add) {
            addFontSize();
        } else if (id == R.id.tv_size_default) {
            defaultFontSize();
        } else if (id == R.id.tv_qihei) {
            selectTypeface(ReaderConfig.FONTTYPE_QIHEI);
            setTypeface(ReaderConfig.FONTTYPE_QIHEI);
        } else if (id == R.id.tv_fzxinghei) {
            selectTypeface(ReaderConfig.FONTTYPE_FZXINGHEI);
            setTypeface(ReaderConfig.FONTTYPE_FZXINGHEI);
        } else if (id == R.id.tv_fzkatong) {
            selectTypeface(ReaderConfig.FONTTYPE_FZKATONG);
            setTypeface(ReaderConfig.FONTTYPE_FZKATONG);
        } else if (id == R.id.tv_bysong) {
            selectTypeface(ReaderConfig.FONTTYPE_BYSONG);
            setTypeface(ReaderConfig.FONTTYPE_BYSONG);
        } else if (id == R.id.tv_default) {
            selectTypeface(ReaderConfig.FONTTYPE_DEFAULT);
            setTypeface(ReaderConfig.FONTTYPE_DEFAULT);
        } else if (id == R.id.iv_bg_default) {
            setBookBg(ReaderConfig.BOOK_BG_DEFAULT);
            selectBg(ReaderConfig.BOOK_BG_DEFAULT);
        } else if (id == R.id.iv_bg_1) {
            setBookBg(ReaderConfig.BOOK_BG_1);
            selectBg(ReaderConfig.BOOK_BG_1);
        } else if (id == R.id.iv_bg_2) {
            setBookBg(ReaderConfig.BOOK_BG_2);
            selectBg(ReaderConfig.BOOK_BG_2);
        } else if (id == R.id.iv_bg_3) {
            setBookBg(ReaderConfig.BOOK_BG_3);
            selectBg(ReaderConfig.BOOK_BG_3);
        } else if (id == R.id.iv_bg_4) {
            setBookBg(ReaderConfig.BOOK_BG_4);
            selectBg(ReaderConfig.BOOK_BG_4);
        }
    }

    //变大书本字体
    private void addFontSize() {
        if (currentFontSize < FONT_SIZE_MAX) {
            currentFontSize += 1;
            tv_size.setText(currentFontSize + "");
            config.setFontSize(currentFontSize);
            if (mSettingListener != null) {
                mSettingListener.changeFontSize(currentFontSize);
            }
        }
    }

    private void defaultFontSize() {
        currentFontSize = (int) getContext().getResources().getDimension(R.dimen.reading_default_text_size);
        tv_size.setText(currentFontSize + "");
        config.setFontSize(currentFontSize);
        if (mSettingListener != null) {
            mSettingListener.changeFontSize(currentFontSize);
        }
    }

    //变小书本字体
    private void subtractFontSize() {
        if (currentFontSize > FONT_SIZE_MIN) {
            currentFontSize -= 1;
            tv_size.setText(currentFontSize + "");
            config.setFontSize(currentFontSize);
            if (mSettingListener != null) {
                mSettingListener.changeFontSize(currentFontSize);
            }
        }
    }

    //改变亮度
    public void changeBright(Boolean isSystem, int brightness) {
        float light = (float) (brightness / 100.0);
        setTextViewSelect(tv_xitong, isSystem);
        config.setSystemLight(isSystem);
        config.setLight(light);
        if (mSettingListener != null) {
            mSettingListener.changeSystemBright(isSystem, light);
        }
    }

    public void setSettingListener(SettingListener settingListener) {
        this.mSettingListener = settingListener;
    }

    public interface SettingListener {
        void changeSystemBright(Boolean isSystem, float brightness);

        void changeFontSize(int fontSize);

        void changeTypeFace(Typeface typeface);

        void changeBookBg(int type);
    }

}