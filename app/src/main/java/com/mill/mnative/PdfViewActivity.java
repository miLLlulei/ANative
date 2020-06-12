package com.mill.mnative;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.mill.mnative.utils.ContextUtils;
import com.mill.mnative.utils.MagicFileChooser;
import com.mill.mnative.utils.SPUtils;

import java.io.File;

public class PdfViewActivity extends BaseActivity {
    private PDFView pdfView;
    private File pickFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_pdf);
        getActionBar().setTitle("选择 pdf 打开");

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 0);
            }
        }
        pdfView = findViewById(R.id.pdfView);
        open(getSavedFile());
    }

    private int getSavedPage() {
        return SPUtils.getInt(null, ContextUtils.getApplicationContext(), SP_KEY_PAGE + (pickFile == null ? "" : pickFile.getName()), 0);
    }

    private File getSavedFile() {
        String path = SPUtils.getString(null, ContextUtils.getApplicationContext(), SP_KEY_FILE, null);
        return TextUtils.isEmpty(path) ? null : new File(path);
    }

    private void open(File file) {
        if (file == null) {
            return;
        }
        pickFile = file;
        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        getActionBar().setTitle("选择 pdf 打开(" + page + "/" + pageCount + ")");
                    }
                })
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText(ContextUtils.getApplicationContext(), "出错啦", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                })
                .defaultPage(getSavedPage())
                .load();
    }

    @Override
    protected void onPause() {
        SPUtils.setInt(null, ContextUtils.getApplicationContext(), SP_KEY_PAGE + (pickFile == null ? "" : pickFile.getName()), pdfView.getCurrentPage());
        SPUtils.setString(null, ContextUtils.getApplicationContext(), SP_KEY_FILE, (pickFile == null ? "" : pickFile.getAbsolutePath()));
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pdf, menu);
        return true;
    }

    public static final int CHOOSE_FILE_CODE = 0;
    private static final String SP_KEY_PAGE = "PDF_P_";
    private static final String SP_KEY_FILE = "PDF_F_";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            startFileChooser(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void startFileChooser(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*").addCategory(Intent.CATEGORY_OPENABLE);
        try {
            activity.startActivityForResult(Intent.createChooser(intent, "Choose File"), CHOOSE_FILE_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ContextUtils.getApplicationContext(), "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSE_FILE_CODE) {
                File pickFile = MagicFileChooser.getFileFromUri(this, data.getData(), false);
                open(pickFile);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
