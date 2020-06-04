package com.mill.mnative.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Build;

import com.mill.mnative.utils.hideapi.HideApiHelper;


public class SqliteUtils {

    public static SQLiteDatabase openDBByName(String DBName, Context context) {
        SQLiteDatabase db;

        db = context.openOrCreateDatabase(DBName, Context.MODE_PRIVATE, null);
        return db;
    }


    public static int getDbVer(String DBName) {
        int dbVer = 0;
        SQLiteDatabase db = null;
        try {
            db = openDBByName(DBName, ContextUtils.getApplicationContext());
            dbVer = db.getVersion();
        } finally {
            close(db);
        }
        return dbVer;
    }

    public static Cursor getDataByTableName(SQLiteDatabase db_, String TableName) {
        String sql = "select * from " + TableName + ";";
        try {
            return db_.rawQuery(sql, null);
        } catch (SQLiteException e) {
            return null;
        }

    }

    public static void deleteTableByDBName(String DBName, String TableName, Context context) {
        SQLiteDatabase db = null;
        try {
            db = openDBByName(DBName, context);
            db.delete(TableName, null, null);
        } finally {
            close(db);
        }
    }

    public static void close(SQLiteDatabase db_) {
        if (db_ != null)
            db_.close();
    }

    public static int queryCursorIntVal(Cursor c, String key) {
        try {
            return c.getInt(c.getColumnIndexOrThrow(key));
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }

    public static long queryCursorLongVal(Cursor c, String key) {
        try {
            return c.getLong(c.getColumnIndexOrThrow(key));
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

    public static String queryCursorStringVal(Cursor c, String key) {
        try {
            return c.getString(c.getColumnIndexOrThrow(key));
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    public static SQLiteDatabase getReadableDatabase(SQLiteOpenHelper databaseHelper) {
        SQLiteDatabase db = null;
        try {
            db = databaseHelper.getReadableDatabase();
        } catch (SQLException | StackOverflowError e) {
            if (!isStackOverflowError(e)) {
                handleSqliteException(null, e);
            }
        } catch (NullPointerException e) {
            try {
                db = databaseHelper.getWritableDatabase();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (db != null) {
                try {
                    db = databaseHelper.getReadableDatabase();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            if (db == null) {
                String val = null;
                try {
                    val = HideApiHelper.SystemProperties.get("debug.sqlite.wal.syncmode", "FULL1");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                String val2 = null;
                try {
                    val2 = HideApiHelper.SystemProperties.get("debug.sqlite.syncmode", "FULL2");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return db;
    }

    public static SQLiteDatabase getWritableDatabase(SQLiteOpenHelper databaseHelper) {
        SQLiteDatabase db = null;
        try {
            db = databaseHelper.getWritableDatabase();
        } catch (SQLException e) {
            if (!isStackOverflowError(e)) {
                handleSqliteException(null, e);
            }
        }
        return db;
    }

    private static boolean isStackOverflowError(Throwable e) {
        if (e instanceof StackOverflowError
                && Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//            Android4.0.3的系统bug，如果openDatabase(path, factory, flags, errorHandler, connectionNum)内部一直发生SQLiteDatabaseCorruptException，会循环调用openDatabase方法；
//            java.lang.StackOverflowError
//            at java.lang.StringBuffer.append(StringBuffer.java:278)
            return true;
        }
        return false;
    }

    public static void excSql(SQLiteDatabase db, String sql) {
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            handleSqliteException(db, e);
        }
    }

    public static Cursor query(SQLiteQueryBuilder qb, SQLiteDatabase db, String[] projectionIn,
                               String selection, String[] selectionArgs, String groupBy,
                               String having, String sortOrder) {
        if (qb == null || db == null) {
            return null;
        }
        try {
            return qb.query(db, projectionIn, selection, selectionArgs, groupBy, having, sortOrder);
        } catch (SQLException e) {
            handleSqliteException(db, e);
        }
        return null;
    }

    public static long replaceOrThrow(SQLiteDatabase db, String table, String nullColumnHack, ContentValues initialValues) {
        if (db == null) {
            return 0;
        }
        try {
            return db.replaceOrThrow(table, nullColumnHack, initialValues);
        } catch (SQLException e) {
            handleSqliteException(db, e);
        }
        return 0;
    }

    public static int update(SQLiteDatabase db, String table, ContentValues values, String whereClause, String[] whereArgs) {
        if (db == null) {
            return 0;
        }
        try {
            return db.update(table, values, whereClause, whereArgs);
        } catch (SQLException e) {
            handleSqliteException(db, e);
        }
        return 0;
    }

    private static void handleSqliteException(SQLiteDatabase db, Throwable e) {
        if (LogUtils.isDebug()) {
            LogUtils.e(SqliteUtils.class.getName(), "handleSqliteException.msg = " + " " + e);
        }
    }
}