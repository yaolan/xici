package net.xici.newapp.data.dao;

import net.xici.newapp.data.contentprovider.MailContentProvider;
import net.xici.newapp.data.contentprovider.UnreadContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class UnreadDao extends BaseDataHelper {

	public final static int UNREADTYPE_MAIL = 0;
	public final static int UNREADTYPE_THREAD = 1;

	public UnreadDao(Context context) {
		super(context);
	}

	@Override
	protected Uri getContentUri() {
		return UnreadContentProvider.CONTENT_URI;
	}

	public boolean ConstainMail(int mid, long accountid) {
		Cursor cursor = query(
				new String[] { UnreadContentProvider._ID },
				UnreadContentProvider.ACCOUNT_ID + " =? and "
						+ UnreadContentProvider.TYPE + " =? and "
						+ UnreadContentProvider.UNREAD_ID + "=?",
				new String[] { String.valueOf(accountid),
						String.valueOf(UNREADTYPE_MAIL), String.valueOf(mid) },
				null);
		if (cursor != null ) {
			if(cursor.getCount() > 0){
				cursor.close();
				return true;
			}
			cursor.close();
		}
		return false;
	}

	public void insertMail(int mid, long accountid) {
		if (!ConstainMail(mid, accountid)) {
			ContentValues cv = new ContentValues();
			cv.put(UnreadContentProvider.ACCOUNT_ID, accountid);
			cv.put(UnreadContentProvider.TYPE, UNREADTYPE_MAIL);
			cv.put(UnreadContentProvider.UNREAD_ID, mid);
			insert(cv);
			mContext.getContentResolver().notifyChange(MailContentProvider.CONTENT_URI, null);
		}
	}
	
	public boolean ConstainThread(long tid, long accountid) {
		Cursor cursor = query(
				new String[] { UnreadContentProvider._ID },
				UnreadContentProvider.ACCOUNT_ID + " =? and "
						+ UnreadContentProvider.TYPE + " =? and "
						+ UnreadContentProvider.UNREAD_ID + "=?",
				new String[] { String.valueOf(accountid),
						String.valueOf(UNREADTYPE_THREAD), String.valueOf(tid) },
				null);
		if (cursor != null ) {
			if(cursor.getCount() > 0){
				cursor.close();
				return true;
			}
			cursor.close();
		}
		return false;
	}

	public void insertThread(long tid, long accountid) {
		if (!ConstainThread(tid, accountid)) {
			ContentValues cv = new ContentValues();
			cv.put(UnreadContentProvider.ACCOUNT_ID, accountid);
			cv.put(UnreadContentProvider.TYPE, UNREADTYPE_THREAD);
			cv.put(UnreadContentProvider.UNREAD_ID, tid);
			insert(cv);
			mContext.getContentResolver().notifyChange(MailContentProvider.CONTENT_URI, null);
		}
	}

	public void deleteByAccount(long accountid) {
		delete(UnreadContentProvider.ACCOUNT_ID + "=?", new String[] { String.valueOf(accountid) });
	}
}
