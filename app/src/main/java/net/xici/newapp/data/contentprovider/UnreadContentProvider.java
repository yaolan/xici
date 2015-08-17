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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import net.xici.newapp.data.database.DbHelper;

import java.util.HashMap;

public class UnreadContentProvider extends ContentProvider {

	private DbHelper dbHelper;
	private static HashMap<String, String> UNREAD_PROJECTION_MAP;
	private static final String TABLE_NAME = "unread";
	private static final String AUTHORITY = "net.xici.newapp.data.contentprovider.unreadcontentprovider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);
	public static final Uri _ID_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase());
	public static final Uri TYPE_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/type");
	public static final Uri UNREAD_ID_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
					+ "/unread_id");
	public static final Uri COUNT_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/count");
	public static final Uri ACCOUNT_ID_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
					+ "/account_id");

	public static final String DEFAULT_SORT_ORDER = "_id ASC";

	private static final UriMatcher URL_MATCHER;

	private static final int UNREAD = 1;
	private static final int UNREAD__ID = 2;
	private static final int UNREAD_TYPE = 3;
	private static final int UNREAD_UNREAD_ID = 4;
	private static final int UNREAD_COUNT = 5;
	private static final int UNREAD_ACCOUNT_ID = 6;

	// Content values keys (using column names)
	public static final String _ID = "_id";
	public static final String TYPE = "type";
	public static final String UNREAD_ID = "unread_id";
	public static final String COUNT = "count";
	public static final String ACCOUNT_ID = "account_id";
	
	public static final String DROP_TABLE_SQL = "drop table if exists "+TABLE_NAME;
	public static final String CREATE_TABLE_SQL = "create table "+TABLE_NAME+ "(" + _ID
			  + " integer primary key autoincrement," + TYPE + " integer,"
			  + UNREAD_ID + " integer,"
			  + COUNT + " text,"
			  + ACCOUNT_ID + " integer"
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
		case UNREAD:
			qb.setTables(TABLE_NAME);
			qb.setProjectionMap(UNREAD_PROJECTION_MAP);
			break;
		case UNREAD__ID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("_id=" + url.getPathSegments().get(1));
			break;
		case UNREAD_TYPE:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("type='" + url.getPathSegments().get(2) + "'");
			break;
		case UNREAD_UNREAD_ID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("unread_id='" + url.getPathSegments().get(2) + "'");
			break;
		case UNREAD_COUNT:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("count='" + url.getPathSegments().get(2) + "'");
			break;
		case UNREAD_ACCOUNT_ID:
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
		case UNREAD:
			return "vnd.android.cursor.dir/vnd.net.xici.newapp.unread";
		case UNREAD__ID:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.unread";
		case UNREAD_TYPE:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.unread";
		case UNREAD_UNREAD_ID:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.unread";
		case UNREAD_COUNT:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.unread";
		case UNREAD_ACCOUNT_ID:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.unread";

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
		if (URL_MATCHER.match(url) != UNREAD) {
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		rowID = mDB.insert("unread", "unread", values);
		if (rowID > 0) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Failed to insert row into " + url);
	}

	public int delete(Uri url, String where, String[] whereArgs) {
		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		int count;
		String segment = "";
		switch (URL_MATCHER.match(url)) {
		case UNREAD:
			count = mDB.delete(TABLE_NAME, where, whereArgs);
			break;
		case UNREAD__ID:
			segment = url.getPathSegments().get(1);
			count = mDB.delete(TABLE_NAME,
					"_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case UNREAD_TYPE:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"type="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case UNREAD_UNREAD_ID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"unread_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case UNREAD_COUNT:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"count="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case UNREAD_ACCOUNT_ID:
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
		case UNREAD:
			count = mDB.update(TABLE_NAME, values, where, whereArgs);
			break;
		case UNREAD__ID:
			segment = url.getPathSegments().get(1);
			count = mDB.update(TABLE_NAME, values,
					"_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case UNREAD_TYPE:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"type="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case UNREAD_UNREAD_ID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"unread_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case UNREAD_COUNT:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"count="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case UNREAD_ACCOUNT_ID:
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
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(), UNREAD);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/#",
				UNREAD__ID);
		URL_MATCHER.addURI(AUTHORITY,
				TABLE_NAME.toLowerCase() + "/type" + "/*", UNREAD_TYPE);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/unread_id"
				+ "/*", UNREAD_UNREAD_ID);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/count"
				+ "/*", UNREAD_COUNT);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/account_id"
				+ "/*", UNREAD_ACCOUNT_ID);

		UNREAD_PROJECTION_MAP = new HashMap<String, String>();
		UNREAD_PROJECTION_MAP.put(_ID, "_id");
		UNREAD_PROJECTION_MAP.put(TYPE, "type");
		UNREAD_PROJECTION_MAP.put(UNREAD_ID, "unread_id");
		UNREAD_PROJECTION_MAP.put(COUNT, "count");
		UNREAD_PROJECTION_MAP.put(ACCOUNT_ID, "account_id");

	}
}
