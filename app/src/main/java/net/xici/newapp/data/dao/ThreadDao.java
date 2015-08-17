package net.xici.newapp.data.dao;

import java.util.ArrayList;
import java.util.List;

import net.xici.newapp.data.contentprovider.BoardContentProvider;
import net.xici.newapp.data.contentprovider.ThreadContentProvider;
import net.xici.newapp.data.pojo.Thread;
import net.xici.newapp.support.util.SerializableUtils;
import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

public class ThreadDao extends BaseDataHelper {

	public ThreadDao(Context context) {
		super(context);
	}

	@Override
	protected Uri getContentUri() {
		return ThreadContentProvider.CONTENT_URI;
	}

	public ContentValues getContentValues(Thread thread, long accountid,
			int sort, int boarid, int append) {
		ContentValues cv = new ContentValues();
		cv.put(ThreadContentProvider.ACCOUNT_ID, accountid);
		cv.put(ThreadContentProvider.BOARD_ID, boarid);
		cv.put(ThreadContentProvider.TOP, thread.top);
		cv.put(ThreadContentProvider.COOL, thread.cool);
		cv.put(ThreadContentProvider.SORTTYPE, sort);
		cv.put(ThreadContentProvider.THREAD_BLOB,
				SerializableUtils.toByteArray(thread));
		cv.put(ThreadContentProvider.THREAD_ID, thread.tid);
		if (append != -1) {
			cv.put(ThreadContentProvider.APPEND, append);
		}
		return cv;
	}

	public Thread fromCursor(Cursor cursor) {
		return SerializableUtils.fromBytes(cursor.getBlob(cursor
				.getColumnIndexOrThrow(ThreadContentProvider.THREAD_BLOB)));
	}
	
	public int topfromCursor(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndexOrThrow(ThreadContentProvider.TOP));
	}

	public int bulkInsert(List<Thread> threads, long accountid, int sort,
			int boarid, int append) {
		if (threads == null || threads.size() == 0) {
			// 插入数据为空，判断是否情况数据
			if (append == 0) {
				deleteByAccountAndBoard(accountid, boarid);
			}
			return 0;
		}
		List<ContentValues> valueList = new ArrayList<ContentValues>();
		
		ContentValues[] valueArray ;//= new ContentValues[]{};
		for (int i = 0; i < threads.size(); i++) {
			Thread thread = threads.get(i);
			if(append==0||!constainThread(thread, accountid, sort, boarid)){
				ContentValues values = getContentValues(thread, accountid, sort,
						boarid, append);
				//valueArray[i] = values;
				valueList.add(values);
			}
		}
		valueArray = valueList.toArray(new ContentValues[valueList.size()]);
		if(valueArray!=null&&valueArray.length>0){
			return bulkInsert(valueArray);
		}else {
			return 0;
		}
		
	}

	public boolean constainThread(Thread t, long accountid, int sort,
			int boarid) {
		Cursor cursor = query(
				new String[] { ThreadContentProvider._ID },
				ThreadContentProvider.ACCOUNT_ID + "=? and "
						+ ThreadContentProvider.BOARD_ID + "=? and "
						+ ThreadContentProvider.SORTTYPE + "=? and "
						+ ThreadContentProvider.THREAD_ID + "=?",
				new String[] { String.valueOf(accountid), String.valueOf(boarid),
						String.valueOf(sort),String.valueOf(t.tid) }, null);
		if (cursor != null ) {
			if(cursor.getCount() > 0){
				cursor.close();
				return true;
			}
			cursor.close();
		}
		return false;
	}

	public CursorLoader getCursorLoader(long accountid, int boarid) {
		return new CursorLoader(getContext(), getContentUri(), null,
				ThreadContentProvider.ACCOUNT_ID + "=? and "
						+ ThreadContentProvider.BOARD_ID + "=?", new String[] {
						String.valueOf(accountid), String.valueOf(boarid) },
				null);
	}

	public CursorLoader getCursorLoaderBysort(long accountid, int boarid,
			int sort) {
		return new CursorLoader(getContext(), getContentUri(), null,
				ThreadContentProvider.ACCOUNT_ID + "=? and "
						+ ThreadContentProvider.BOARD_ID + "=? and "
						+ ThreadContentProvider.SORTTYPE + "=?", new String[] {
						String.valueOf(accountid), String.valueOf(boarid),
						String.valueOf(sort) }, null);
	}

	public void deleteByAccountAndBoard(long accountid, int boardid) {
		delete(ThreadContentProvider.ACCOUNT_ID + "=? and "
				+ ThreadContentProvider.BOARD_ID + "=?",
				new String[] { String.valueOf(accountid),
						String.valueOf(boardid) });
	}

	public void deleteByID(long accountid, int boardid, long tid) {
		delete(ThreadContentProvider.ACCOUNT_ID + "=? and "
				+ ThreadContentProvider.BOARD_ID + "=? and "
				+ ThreadContentProvider.THREAD_ID + "=?",
				new String[] { String.valueOf(accountid),
						String.valueOf(boardid), String.valueOf(tid) });
	}

	public void deleteALl() {
		delete(null, null);
	}

	public void updateThreadReplyCount(long accountid, int boardid,
			Thread thread) {
		ContentValues cv = new ContentValues();
		cv.put(ThreadContentProvider.THREAD_BLOB,
				SerializableUtils.toByteArray(thread));
		update(cv,
				ThreadContentProvider.ACCOUNT_ID + "=? and "
						+ ThreadContentProvider.BOARD_ID + "=? and "
						+ ThreadContentProvider.THREAD_ID + "=?",
				new String[] { String.valueOf(accountid),
						String.valueOf(boardid), String.valueOf(thread.tid) });
	}

}
