package com.mill.mnative;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.mill.mnative.reader.activity.ReadActivity;
import com.mill.mnative.reader.db.BookList;
import com.mill.mnative.reader.util.Fileutil;
import com.mill.mnative.utils.MagicFileChooser;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 0);
            }
        }

        findViewById(R.id.tv_pdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), PdfViewActivity.class.getName());
                startActivity(intent);
            }
        });

        findViewById(R.id.tv_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BookList> list = DataSupport.findAll(BookList.class);
                if (list != null && list.size() > 0) {
                    for (BookList item : list) {
                        if (new File(item.getBookpath()).exists()) {
                            ReadActivity.openBook(item, MainActivity.this);
                            return;
                        } else {
                            item.delete();
                        }
                    }
                }
                PdfViewActivity.startFileChooser(MainActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PdfViewActivity.CHOOSE_FILE_CODE) {
                File pickFile = MagicFileChooser.getFileFromUri(this, data.getData(), false);
                BookList bean = new BookList();
                bean.setBookpath(pickFile.getAbsolutePath());
                bean.setBookname(Fileutil.getFileNameNoEx(bean.getBookpath()));
                ReadActivity.openBook(bean, MainActivity.this);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
