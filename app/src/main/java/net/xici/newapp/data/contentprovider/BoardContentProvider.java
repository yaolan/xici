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

public class BoardContentProvider extends ContentProvider {

	private DbHelper dbHelper;
	private static HashMap<String, String> BOARD_PROJECTION_MAP;
	private static final String TABLE_NAME = "board";
	private static final String AUTHORITY = "net.xici.newapp.data.contentprovider.boardcontentprovider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);
	public static final Uri _ID_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase());
	public static final Uri BOARDID_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/boardid");
	public static final Uri BOARDNAME_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
					+ "/boardname");
	public static final Uri BOARD_BLOB_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
					+ "/board_blob");
	public static final Uri ACCOUNT_ID_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
					+ "/account_id");

	public static final String DEFAULT_SORT_ORDER = "_id ASC";

	private static final UriMatcher URL_MATCHER;

	private static final int BOARD = 1;
	private static final int BOARD__ID = 2;
	private static final int BOARD_BOARDID = 3;
	private static final int BOARD_BOARDNAME = 4;
	private static final int BOARD_BOARD_BLOB = 5;
	private static final int BOARD_ACCOUNT_ID = 6;

	// Content values keys (using column names)
	public static final String _ID = "_id";
	public static final String BOARDID = "boardid";
	public static final String BOARDNAME = "boardname";
	public static final String BOARD_BLOB = "board_blob";
	public static final String ACCOUNT_ID = "account_id";
	public static final String TYPE = "type";
	public static final String SORT = "SORT";
	public static final String APPEND = "append";
	
	public static final String DROP_TABLE_SQL = "drop table if exists "+TABLE_NAME;
	public static final String CREATE_TABLE_SQL = "create table "+TABLE_NAME+ "(" + _ID
			  + " integer primary key autoincrement," + BOARDID + " integer,"
			  + BOARDNAME + " text,"
			  + BOARD_BLOB + " BLOB,"
			  + TYPE + " integer,"
			  + ACCOUNT_ID + " integer,"
			  + SORT + " integer"+ ");";

	public boolean onCreate() {
		dbHelper = DbHelper.getinstance(getContext());//new DbHelper(getContext());
		return (dbHelper == null) ? false : true;
	}

	public Cursor query(Uri url, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteDatabase mDB = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (URL_MATCHER.match(url)) {
		case BOARD:
			qb.setTables(TABLE_NAME);
			qb.setProjectionMap(BOARD_PROJECTION_MAP);
			break;
		case BOARD__ID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("_id=" + url.getPathSegments().get(1));
			break;
		case BOARD_BOARDID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("boardid='" + url.getPathSegments().get(2) + "'");
			break;
		case BOARD_BOARDNAME:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("boardname='" + url.getPathSegments().get(2) + "'");
			break;
		case BOARD_BOARD_BLOB:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("board_blob='" + url.getPathSegments().get(2) + "'");
			break;
		case BOARD_ACCOUNT_ID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("account_id='" + url.getPathSegments().get(2) + "'");
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
		case BOARD:
			return "vnd.android.cursor.dir/vnd.net.xici.newapp.data.contentprovider.board";
		case BOARD__ID:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.board";
		case BOARD_BOARDID:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.board";
		case BOARD_BOARDNAME:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.board";
		case BOARD_BOARD_BLOB:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.board";
		case BOARD_ACCOUNT_ID:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.board";

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
		if (URL_MATCHER.match(url) != BOARD) {
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		rowID = mDB.insert("board", "board", values);
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
		int type = 0;
		if (values.length > 0) {
			ContentValues contentValues = values[0];
			if (contentValues.containsKey(APPEND)
					&& contentValues.getAsInteger(APPEND) == 1) {
				append = true;
			}
			account_id = contentValues.getAsLong(ACCOUNT_ID);
			type = contentValues.getAsInteger(TYPE);
		}

		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		mDB.beginTransaction();
		try {
			if (!append)
			{
				mDB.delete(TABLE_NAME, ACCOUNT_ID + "=? and "+TYPE+"=?",
						new String[] { String.valueOf(account_id),String.valueOf(type) });
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
		case BOARD:
			count = mDB.delete(TABLE_NAME, where, whereArgs);
			break;
		case BOARD__ID:
			segment = url.getPathSegments().get(1);
			count = mDB.delete(TABLE_NAME,
					"_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case BOARD_BOARDID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"boardid="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case BOARD_BOARDNAME:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"boardname="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case BOARD_BOARD_BLOB:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"board_blob="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case BOARD_ACCOUNT_ID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"account_id="
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
		case BOARD:
			count = mDB.update(TABLE_NAME, values, where, whereArgs);
			break;
		case BOARD__ID:
			segment = url.getPathSegments().get(1);
			count = mDB.update(TABLE_NAME, values,
					"_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case BOARD_BOARDID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"boardid="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case BOARD_BOARDNAME:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"boardname="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case BOARD_BOARD_BLOB:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"board_blob="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case BOARD_ACCOUNT_ID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"account_id="
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
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(), BOARD);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/#",
				BOARD__ID);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/boardid"
				+ "/*", BOARD_BOARDID);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/boardname"
				+ "/*", BOARD_BOARDNAME);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/board_blob"
				+ "/*", BOARD_BOARD_BLOB);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/account_id"
				+ "/*", BOARD_ACCOUNT_ID);

		BOARD_PROJECTION_MAP = new HashMap<String, String>();
		BOARD_PROJECTION_MAP.put(_ID, "_id");
		BOARD_PROJECTION_MAP.put(BOARDID, "boardid");
		BOARD_PROJECTION_MAP.put(BOARDNAME, "boardname");
		BOARD_PROJECTION_MAP.put(BOARD_BLOB, "board_blob");
		BOARD_PROJECTION_MAP.put(ACCOUNT_ID, "account_id");

	}
}
