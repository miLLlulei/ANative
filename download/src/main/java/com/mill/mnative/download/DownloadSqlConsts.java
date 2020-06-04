package com.mill.mnative.download;

public class DownloadSqlConsts {
	public static final String DATABASE_NAME = "download.db";

	public static final String DOWNLOADTABLENAME = "download";

	// 下载核心key
	public static final String COLUMN_DOWNLOAD_ID = "id";
	public static final String COLUMN_DOWNLOAD_URL = "downloadUrl";
	public static final String COLUMN_SAVED_PATH = "savedPath";
	public static final String COLUMN_CURRENT_BYTES = "currentBytes";
	public static final String COLUMN_TOTALBYTES = "totalBytes";
	public static final String COLUMN_START_DOWNLOAD_TIME = "startDownloadTime";
	public static final String COLUMN_DOWNLOAD_STATUS = "downloadStatus";
	public static final String COLUMN_EXTRA = "extra";


	private static final String CREATEDOWNLOADDBSQL = "CREATE TABLE IF NOT EXISTS download(" +
            "%1$s TEXT PRIMARY KEY, " +
			"%2$s TEXT, " +
			"%3$s TEXT, " +
			"%4$s BIGINT, " +
			"%5$s BIGINT, " +
            "%6$s BIGINT, " +
			"%7$s INTEGER, " +
			"%8$s TEXT" +
			");";


	public static final String createSqliteSql = String.format(DownloadSqlConsts.CREATEDOWNLOADDBSQL,
			// 下载核心key
			DownloadSqlConsts.COLUMN_DOWNLOAD_ID,
			DownloadSqlConsts.COLUMN_DOWNLOAD_URL,
			DownloadSqlConsts.COLUMN_SAVED_PATH,
			DownloadSqlConsts.COLUMN_CURRENT_BYTES,
			DownloadSqlConsts.COLUMN_TOTALBYTES, //5
			DownloadSqlConsts.COLUMN_START_DOWNLOAD_TIME,
			DownloadSqlConsts.COLUMN_DOWNLOAD_STATUS,
			DownloadSqlConsts.COLUMN_EXTRA
	);

	public static final String DELSQL = "delete from download where id = \'%1$s\';";
}
