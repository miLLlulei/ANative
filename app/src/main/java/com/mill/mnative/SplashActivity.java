package com.mill.mnative;

import android.content.Intent;
import android.os.Bundle;

import com.mill.mnative.reader.activity.ReadActivity;
import com.mill.mnative.reader.db.BookList;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_splash);

//        String aty = SPUtils.getString(null, ContextUtils.getApplicationContext(), SP_KEY_S_A_C, PdfViewActivity.class.getName());
//        Intent intent = new Intent();
//        intent.setClassName(getPackageName(), aty);
//        startActivity(intent);

//        BookList bean = new BookList();
//        bean.setBookpath("/sdcard/恐怖广播.txt");
//        bean.setBookname("恐怖广播");
//        ReadActivity.openBook(bean, this);

        Intent intent = new Intent();
        intent.setClassName(getPackageName(), MainActivity.class.getName());
        startActivity(intent);


        finish();
    }
}
