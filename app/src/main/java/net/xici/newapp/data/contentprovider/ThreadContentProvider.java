/*
 ******************************************************************************
 * Parts of this code sample are licensed under Apache License, Version 2.0   *
 * Copyright (c) 2009, Android Open Handset Alliance. All rights reserved.    *
 *																			  *																			*
 * Except as noted, this code sample is offered under a modified BSD license. *
 * Copyright (C) 2010, Motorola Mobility, Inc. All rights reserved.           *
 * 																			  *
 * For more details, see MOTODEV_Studio_for_Android_LicenseNotices.pdf        * 
 * in your installation folder.                                               *
 ******************************************************************************
 */
package net.xici.newapp.data.contentprovider;

import java.util.*;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;
import android.provider.BaseColumns;
import android.text.*;

import net.xici.newapp.data.database.*;

public class ThreadContentProvider extends ContentProvider {

	private DbHelper dbHelper;
	private static HashMap<String, String> THREAD_PROJECTION_MAP;
	private static final String TABLE_NAME = "thread";
	private static final String AUTHORITY = "net.xici.newapp.data.contentprovider.threadcontentprovider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);
	public static final Uri _ID_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase());
	public static final Uri ACCOUNT_ID_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
					+ "/account_id");
	public static final Uri BOARD_ID_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/board_id");
	public static final Uri SORTTYPE_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/sorttype");
	public static final Uri THREAD_BLOB_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
					+ "/thread_blob");

	public static final String DEFAULT_SORT_ORDER = "_id ASC";

	private static final UriMatcher URL_MATCHER;

	private static final int THREAD = 1;
	private static final int THREAD__ID = 2;
	private static final int THREAD_ACCOUNT_ID = 3;
	private static final int THREAD_BOARD_ID = 4;
	private static final int THREAD_SORTTYPE = 5;
	private static final int THREAD_THREAD_BLOB = 6;

	// Content values keys (using column names)
	public static final String _ID = "_id";
	public static final String ACCOUNT_ID = "account_id";
	public static final String BOARD_ID = "board_id";
	public static final String TOP = "top";
	public static final String COOL = "cool";
	public static final String SORTTYPE = "sorttype";
	public static final String THREAD_BLOB = "thread_blob";
	public static final String THREAD_ID = "thread_id";
	public static final String APPEND = "append";
	
	public static final String DROP_TABLE_SQL = "drop table if exists "+TABLE_NAME;
	public static final String CREATE_TABLE_SQL = "create table "+TABLE_NAME+ "(" + _ID
			  + " integer primary key autoincrement," + ACCOUNT_ID + " integer,"
			  + BOARD_ID + " integer,"
			  + TOP + " integer,"
			  + COOL + " integer,"
			  + SORTTYPE + " integer,"
			  + THREAD_BLOB + " BLOB,"
			  + THREAD_ID + " integer"
			  + ");";

	public boolean onCreate() {
		dbHelper = DbHelper.getinstance(getContext());//new DbHelper(getContext());
		return (dbHelper == null) ? false : true;
	}

	public Cursor query(Uri url, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteDatabase mDB = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (URL_MATCHER.match(url)) {
		case THREAD:
			qb.setTables(TABLE_NAME);
			qb.setProjectionMap(THREAD_PROJECTION_MAP);
			break;
		case THREAD__ID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("_id=" + url.getPathSegments().get(1));
			break;
		case THREAD_ACCOUNT_ID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("account_id='" + url.getPathSegments().get(2) + "'");
			break;
		case THREAD_BOARD_ID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("board_id='" + url.getPathSegments().get(2) + "'");
			break;
		case THREAD_SORTTYPE:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("sorttype='" + url.getPathSegments().get(2) + "'");
			break;
		case THREAD_THREAD_BLOB:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("thread_blob='" + url.getPathSegments().get(2) + "'");
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
		String orderBy = "";
		if (TextUtils.isEmpty(sort)) {
			orderBy = DEFAULT_SORT_ORDER;
		} else {
			orderBy = sort;
		}
		Cursor c = qb.query(mDB, projection, selection, selectionArgs, null,
				null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), url);
		return c;
	}

	public String getType(Uri url) {
		switch (URL_MATCHER.match(url)) {
		case THREAD:
			return "vnd.android.cursor.dir/vnd.net.xici.newapp.data.contentprovider.thread";
		case THREAD__ID:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.thread";
		case THREAD_ACCOUNT_ID:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.thread";
		case THREAD_BOARD_ID:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.thread";
		case THREAD_SORTTYPE:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.thread";
		case THREAD_THREAD_BLOB:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.thread";

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
	}

	public Uri insert(Uri url, ContentValues initialValues) {
		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		long rowID;
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		if (URL_MATCHER.match(url) != THREAD) {
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		rowID = mDB.insert("thread", "thread", values);
		if (rowID > 0) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Failed to insert row into " + url);
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		boolean append = false;
		long account_id = 0;
		long board_id = 0;
		int sort = 0;
		if (values.length > 0) {
			ContentValues contentValues = values[0];
			if (contentValues.containsKey(APPEND)
					&& contentValues.getAsInteger(APPEND) == 1) {
				append = true;
			}
			account_id = contentValues.getAsLong(ACCOUNT_ID);
			board_id = contentValues.getAsLong(BOARD_ID);
			sort = contentValues.getAsInteger(SORTTYPE);
		}

		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		mDB.beginTransaction();
		try {
			if (!append)
			{
				mDB.delete(TABLE_NAME, ACCOUNT_ID + "=? and "+ BOARD_ID +"=? and "+SORTTYPE+"=?",
						new String[] { String.valueOf(account_id),String.valueOf(board_id),String.valueOf(sort) });
			}
			for (ContentValues contentValues : values) {
				if (contentValues.containsKey(APPEND)) {
					contentValues.remove(APPEND);
				}
				mDB.insertWithOnConflict(TABLE_NAME, BaseColumns._ID,
						contentValues, SQLiteDatabase.CONFLICT_IGNORE);
			}
			mDB.setTransactionSuccessful();
			getContext().getContentResolver().notifyChange(uri, null);
			return values.length;
		} catch (Exception e) {
		} finally {
			mDB.endTransaction();
		}
		return 0;
	}

	public int delete(Uri url, String where, String[] whereArgs) {
		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		int count;
		String segment = "";
		switch (URL_MATCHER.match(url)) {
		case THREAD:
			count = mDB.delete(TABLE_NAME, where, whereArgs);
			break;
		case THREAD__ID:
			segment = url.getPathSegments().get(1);
			count = mDB.delete(TABLE_NAME,
					"_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case THREAD_ACCOUNT_ID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"account_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case THREAD_BOARD_ID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"board_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case THREAD_SORTTYPE:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"sorttype="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case THREAD_THREAD_BLOB:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"thread_blob="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	public int update(Uri url, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		int count;
		String segment = "";
		switch (URL_MATCHER.match(url)) {
		case THREAD:
			count = mDB.update(TABLE_NAME, values, where, whereArgs);
			break;
		case THREAD__ID:
			segment = url.getPathSegments().get(1);
			count = mDB.update(TABLE_NAME, values,
					"_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case THREAD_ACCOUNT_ID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"account_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case THREAD_BOARD_ID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"board_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case THREAD_SORTTYPE:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"sorttype="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case THREAD_THREAD_BLOB:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"thread_blob="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	static {
		URL_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(), THREAD);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/#",
				THREAD__ID);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/account_id"
				+ "/*", THREAD_ACCOUNT_ID);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/board_id"
				+ "/*", THREAD_BOARD_ID);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/sorttype"
				+ "/*", THREAD_SORTTYPE);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/thread_blob"
				+ "/*", THREAD_THREAD_BLOB);

		THREAD_PROJECTION_MAP = new HashMap<String, String>();
		THREAD_PROJECTION_MAP.put(_ID, "_id");
		THREAD_PROJECTION_MAP.put(ACCOUNT_ID, "account_id");
		THREAD_PROJECTION_MAP.put(BOARD_ID, "board_id");
		THREAD_PROJECTION_MAP.put(TOP, "top");
		THREAD_PROJECTION_MAP.put(COOL, "cool");
		THREAD_PROJECTION_MAP.put(SORTTYPE, "sorttype");
		THREAD_PROJECTION_MAP.put(THREAD_BLOB, "thread_blob");

	}
}
