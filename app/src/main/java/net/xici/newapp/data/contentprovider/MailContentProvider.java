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

import net.xici.newapp.data.database.DbHelper;

public class MailContentProvider extends ContentProvider {

	private DbHelper dbHelper;
	private static HashMap<String, String> MAIL_PROJECTION_MAP;
	private static final String TABLE_NAME = "mail";
	private static final String AUTHORITY = "net.xici.newapp.data.contentprovider.mailcontentprovider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);
	public static final Uri _ID_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase());
	public static final Uri MAIL_ID_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/mail_id");
	public static final Uri MAIL_OLD_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/mail_old");
	public static final Uri MAIL_TITLE_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
					+ "/mail_title");
	public static final Uri MAIL_TYPE_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
					+ "/mail_type");
	public static final Uri USER_NAME_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
					+ "/user_name");
	public static final Uri ACCOUNT_ID_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
					+ "/account_id");

	public static final String DEFAULT_SORT_ORDER = "_id ASC";

	private static final UriMatcher URL_MATCHER;

	private static final int MAIL = 1;
	private static final int MAIL__ID = 2;
	private static final int MAIL_MAIL_ID = 3;
	private static final int MAIL_MAIL_OLD = 4;
	private static final int MAIL_MAIL_TITLE = 5;
	private static final int MAIL_MAIL_TYPE = 6;
	private static final int MAIL_USER_NAME = 7;
	private static final int MAIL_ACCOUNT_ID = 8;

	// Content values keys (using column names)
	public static final String _ID = "_id";
	public static final String MAIL_ID = "mail_id";
	public static final String MAIL_OLD = "mail_old";
	public static final String MAIL_TITLE = "mail_title";
	public static final String MAIL_TYPE = "mail_type";
	public static final String USER_NAME = "user_name";
	public static final String MAIL_DATE = "mail_date";
	public static final String ACCOUNT_ID = "account_id";
	public static final String APPEND = "append";
	

	public final static int MAIL_TYPE_OUTBOX = 0;
	
	public final static int MAIL_TYPE_INBOX = 1;

	public final static int MAIL_TYPE_APPMAIL = 3;
	
	public final static int MAIL_TYPE_APPMAIL_DIALOGUE = 4;
	
	public static final String DROP_TABLE_SQL = "drop table if exists "+TABLE_NAME;
	public static final String CREATE_TABLE_SQL = "create table "+TABLE_NAME+ "(" + _ID
			  + " integer primary key autoincrement," + MAIL_ID + " integer,"
			  + MAIL_OLD + " integer,"
			  + MAIL_TITLE + " text,"
			  + MAIL_TYPE + " integer,"
			  + USER_NAME + " text,"
			  + MAIL_DATE + " text,"
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
		case MAIL:
			qb.setTables(TABLE_NAME);
			qb.setProjectionMap(MAIL_PROJECTION_MAP);
			break;
		case MAIL__ID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("_id=" + url.getPathSegments().get(1));
			break;
		case MAIL_MAIL_ID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("mail_id='" + url.getPathSegments().get(2) + "'");
			break;
		case MAIL_MAIL_OLD:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("mail_old='" + url.getPathSegments().get(2) + "'");
			break;
		case MAIL_MAIL_TITLE:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("mail_title='" + url.getPathSegments().get(2) + "'");
			break;
		case MAIL_MAIL_TYPE:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("mail_type='" + url.getPathSegments().get(2) + "'");
			break;
		case MAIL_USER_NAME:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("user_name='" + url.getPathSegments().get(2) + "'");
			break;
		case MAIL_ACCOUNT_ID:
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
		case MAIL:
			return "vnd.android.cursor.dir/vnd.net.xici.newapp.mail";
		case MAIL__ID:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.mail";
		case MAIL_MAIL_ID:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.mail";
		case MAIL_MAIL_OLD:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.mail";
		case MAIL_MAIL_TITLE:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.mail";
		case MAIL_MAIL_TYPE:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.mail";
		case MAIL_USER_NAME:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.mail";
		case MAIL_ACCOUNT_ID:
			return "vnd.android.cursor.item/vnd.net.xici.newapp.mail";

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
		if (URL_MATCHER.match(url) != MAIL) {
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		rowID = mDB.insert("mail", "mail", values);
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
		int type = 1;
		if (values.length > 0) {
			ContentValues contentValues = values[0];
			if (contentValues.containsKey(APPEND)
					&& contentValues.getAsInteger(APPEND) == 1) {
				append = true;
			}
			account_id = contentValues.getAsLong(ACCOUNT_ID);
			type = contentValues.getAsInteger(MAIL_TYPE);
		}

		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		mDB.beginTransaction();
		try {
			if (!append)
			{
				mDB.delete(TABLE_NAME, ACCOUNT_ID + "=? and "+MAIL_TYPE +"=? ",
						new String[] { String.valueOf(account_id) , String.valueOf(type) });
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
		case MAIL:
			count = mDB.delete(TABLE_NAME, where, whereArgs);
			break;
		case MAIL__ID:
			segment = url.getPathSegments().get(1);
			count = mDB.delete(TABLE_NAME,
					"_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MAIL_MAIL_ID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"mail_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MAIL_MAIL_OLD:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"mail_old="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MAIL_MAIL_TITLE:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"mail_title="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MAIL_MAIL_TYPE:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"mail_type="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MAIL_USER_NAME:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"user_name="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MAIL_ACCOUNT_ID:
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
		case MAIL:
			count = mDB.update(TABLE_NAME, values, where, whereArgs);
			break;
		case MAIL__ID:
			segment = url.getPathSegments().get(1);
			count = mDB.update(TABLE_NAME, values,
					"_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MAIL_MAIL_ID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"mail_id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MAIL_MAIL_OLD:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"mail_old="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MAIL_MAIL_TITLE:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"mail_title="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MAIL_MAIL_TYPE:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"mail_type="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MAIL_USER_NAME:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"user_name="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case MAIL_ACCOUNT_ID:
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
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(), MAIL);
		URL_MATCHER
				.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/#", MAIL__ID);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/mail_id"
				+ "/*", MAIL_MAIL_ID);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/mail_old"
				+ "/*", MAIL_MAIL_OLD);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/mail_title"
				+ "/*", MAIL_MAIL_TITLE);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/mail_type"
				+ "/*", MAIL_MAIL_TYPE);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/user_name"
				+ "/*", MAIL_USER_NAME);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/account_id"
				+ "/*", MAIL_ACCOUNT_ID);

		MAIL_PROJECTION_MAP = new HashMap<String, String>();
		MAIL_PROJECTION_MAP.put(_ID, "_id");
		MAIL_PROJECTION_MAP.put(MAIL_ID, "mail_id");
		MAIL_PROJECTION_MAP.put(MAIL_OLD, "mail_old");
		MAIL_PROJECTION_MAP.put(MAIL_TITLE, "mail_title");
		MAIL_PROJECTION_MAP.put(MAIL_TYPE, "mail_type");
		MAIL_PROJECTION_MAP.put(USER_NAME, "user_name");
		MAIL_PROJECTION_MAP.put(ACCOUNT_ID, "account_id");
		MAIL_PROJECTION_MAP.put(MAIL_DATE, "mail_date");

	}
}
