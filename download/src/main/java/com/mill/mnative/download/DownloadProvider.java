package com.mill.mnative.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import com.mill.mnative.utils.ContextUtils;
import com.mill.mnative.utils.LogUtils;
import com.mill.mnative.utils.SqliteUtils;


public class DownloadProvider {

    private final static String TAG = "DownloadProvider";

    private static final int DOWNLOADINFO = 1;
    private DatabaseHelper dbHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;


        public DatabaseHelper(Context context) {
            super(context, DownloadSqlConsts.DATABASE_NAME, null, DATABASE_VERSION);
        }

        public DatabaseHelper(Context context, DatabaseErrorHandler databaseErrorHandler) {
            super(context, DownloadSqlConsts.DATABASE_NAME, null, DATABASE_VERSION, databaseErrorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            LogUtils.d(TAG, "DatabaseHelper onCreate ");
            SqliteUtils.excSql(db, DownloadSqlConsts.createSqliteSql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
            if (LogUtils.isDebug()) {
                LogUtils.d(TAG, "DatabaseHelper onUpgrade ");
            }
        }
    }

    public DownloadProvider() {
    }


    public boolean onCreate() {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(ContextUtils.getApplicationContext(), new DefaultDatabaseErrorHandler());
        }
        return true;
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        LogUtils.d(TAG, "query ");
        SQLiteDatabase db = SqliteUtils.getReadableDatabase(dbHelper);
        if (db == null) {
            return null;
        }
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DownloadSqlConsts.DOWNLOADTABLENAME);
        return SqliteUtils.query(qb, db, projection, selection, selectionArgs, null, null, sortOrder);
    }


    public void replace(ContentValues values) {
        LogUtils.d(TAG, "insert");
        long rowId;
        SQLiteDatabase db = SqliteUtils.getWritableDatabase(dbHelper);
        rowId = SqliteUtils.replaceOrThrow(db, DownloadSqlConsts.DOWNLOADTABLENAME, null, values);
        LogUtils.d(TAG, "insert finish " + rowId);

    }

    public int update(ContentValues values, String selection, String[] selectionArgs) {
        int count;
        SQLiteDatabase db = SqliteUtils.getWritableDatabase(dbHelper);
        if (db == null) {
            return 0;
        }
        count = SqliteUtils.update(db, DownloadSqlConsts.DOWNLOADTABLENAME, values, selection, selectionArgs);
        LogUtils.d(TAG, "update() count: " + count);
        return count;
    }

    public int delete(String selection, String[] selectionArgs) {
        return deleteRecord(selectionArgs);
    }

    private int deleteRecord(String[] selectionArgs) {
        SQLiteDatabase db = SqliteUtils.getWritableDatabase(dbHelper);
        if (db != null && selectionArgs != null && selectionArgs[0] != null) {
            String sql = String.format(DownloadSqlConsts.DELSQL, selectionArgs[0]);
            LogUtils.d(TAG, "deleteRecord " + sql);
            SqliteUtils.excSql(db, sql);
        }
        return 0;
    }
}
