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
import android.text.*;

import net.xici.newapp.data.database.*;

public class AccountContentProvider extends ContentProvider {

	private DbHelper dbHelper;
	private static HashMap<String, String> ACCOUNT_PROJECTION_MAP;
	private static final String TABLE_NAME = "account";
	private static final String AUTHORITY = "net.xici.newapp.data.contentprovider.accountcontentprovider";
                                    
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);
	public static final Uri _ID_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase());
	public static final Uri USERNAME_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/username");
	public static final Uri PASSWORD_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/password");
	public static final Uri USERID_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/userid");
	public static final Uri ACCESSTOKEN_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
					+ "/accesstoken");
	public static final Uri ISDEFAULT_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
					+ "/isdefault");
	public static final Uri ACOUNT_BLOB_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
					+ "/acount_blob");

	public static final String DEFAULT_SORT_ORDER = "_id ASC";

	private static final UriMatcher URL_MATCHER;

	private static final int ACCOUNT = 1;
	private static final int ACCOUNT__ID = 2;
	private static final int ACCOUNT_USERNAME = 3;
	private static final int ACCOUNT_PASSWORD = 4;
	private static final int ACCOUNT_USERID = 5;
	private static final int ACCOUNT_ACCESSTOKEN = 6;
	private static final int ACCOUNT_ISDEFAULT = 7;
	private static final int ACCOUNT_ACOUNT_BLOB = 8;

	// Content values keys (using column names)
	public static final String _ID = "_id";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String USERID = "userid";
	public static final String ACCESSTOKEN = "accesstoken";
	public static final String ISDEFAULT = "isdefault";
	public static final String ACOUNT_BLOB = "acount_blob";
	
	public static final int ISDEFAULT_YES = 1;
	public static final int ISDEFAULT_NO = 0;
	
	public static final String DROP_TABLE_SQL = "drop table if exists "+TABLE_NAME;
	public static final String CREATE_TABLE_SQL = "create table "+TABLE_NAME+ "(" + _ID
			  + " integer primary key autoincrement," + USERNAME + " text,"
			  + PASSWORD + " text,"
			  + USERID + " integer,"
			  + ACCESSTOKEN + " text,"
			  + ISDEFAULT + " integer,"
			  + ACOUNT_BLOB + " BLOB" + ");";
	

	public boolean onCreate() {
		dbHelper = DbHelper.getinstance(getContext());//new DbHelper(getContext());
		return (dbHelper == null) ? false : true;
	}

	public Cursor query(Uri url, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteDatabase mDB = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (URL_MATCHER.match(url)) {
		case ACCOUNT:
			qb.setTables(TABLE_NAME);
			qb.setProjectionMap(ACCOUNT_PROJECTION_MAP);
			break;
		case ACCOUNT__ID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("_id=" + url.getPathSegments().get(1));
			break;
		case ACCOUNT_USERNAME:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("username='" + url.getPathSegments().get(2) + "'");
			break;
		case ACCOUNT_PASSWORD:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("password='" + url.getPathSegments().get(2) + "'");
			break;
		case ACCOUNT_USERID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("userid='" + url.getPathSegments().get(2) + "'");
			break;
		case ACCOUNT_ACCESSTOKEN:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("accesstoken='" + url.getPathSegments().get(2) + "'");
			break;
		case ACCOUNT_ISDEFAULT:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("isdefault='" + url.getPathSegments().get(2) + "'");
			break;
		case ACCOUNT_ACOUNT_BLOB:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("acount_blob='" + url.getPathSegments().get(2) + "'");
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
		case ACCOUNT:
			return "vnd.android.cursor.dir/vnd.net.xici.newapp.data.contentprovider.account";
		case ACCOUNT__ID:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.account";
		case ACCOUNT_USERNAME:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.account";
		case ACCOUNT_PASSWORD:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.account";
		case ACCOUNT_USERID:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.account";
		case ACCOUNT_ACCESSTOKEN:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.account";
		case ACCOUNT_ISDEFAULT:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.account";
		case ACCOUNT_ACOUNT_BLOB:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.data.contentprovider.account";

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
		if (URL_MATCHER.match(url) != ACCOUNT) {
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		rowID = mDB.insert("account", "account", values);
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
		case ACCOUNT:
			count = mDB.delete(TABLE_NAME, where, whereArgs);
			break;
		case ACCOUNT__ID:
			segment = url.getPathSegments().get(1);
			count = mDB.delete(TABLE_NAME,
					"_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case ACCOUNT_USERNAME:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"username="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case ACCOUNT_PASSWORD:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"password="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case ACCOUNT_USERID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"userid="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case ACCOUNT_ACCESSTOKEN:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"accesstoken="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case ACCOUNT_ISDEFAULT:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"isdefault="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case ACCOUNT_ACOUNT_BLOB:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"acount_blob="
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
		case ACCOUNT:
			count = mDB.update(TABLE_NAME, values, where, whereArgs);
			break;
		case ACCOUNT__ID:
			segment = url.getPathSegments().get(1);
			count = mDB.update(TABLE_NAME, values,
					"_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case ACCOUNT_USERNAME:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"username="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case ACCOUNT_PASSWORD:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"password="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case ACCOUNT_USERID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"userid="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case ACCOUNT_ACCESSTOKEN:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"accesstoken="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case ACCOUNT_ISDEFAULT:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"isdefault="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case ACCOUNT_ACOUNT_BLOB:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"acount_blob="
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
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(), ACCOUNT);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/#",
				ACCOUNT__ID);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/username"
				+ "/*", ACCOUNT_USERNAME);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/password"
				+ "/*", ACCOUNT_PASSWORD);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/userid"
				+ "/*", ACCOUNT_USERID);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/accesstoken"
				+ "/*", ACCOUNT_ACCESSTOKEN);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/isdefault"
				+ "/*", ACCOUNT_ISDEFAULT);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/acount_blob"
				+ "/*", ACCOUNT_ACOUNT_BLOB);

		ACCOUNT_PROJECTION_MAP = new HashMap<String, String>();
		ACCOUNT_PROJECTION_MAP.put(_ID, "_id");
		ACCOUNT_PROJECTION_MAP.put(USERNAME, "username");
		ACCOUNT_PROJECTION_MAP.put(PASSWORD, "password");
		ACCOUNT_PROJECTION_MAP.put(USERID, "userid");
		ACCOUNT_PROJECTION_MAP.put(ACCESSTOKEN, "accesstoken");
		ACCOUNT_PROJECTION_MAP.put(ISDEFAULT, "isdefault");
		ACCOUNT_PROJECTION_MAP.put(ACOUNT_BLOB, "acount_blob");

	}
}
