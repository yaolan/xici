package net.xici.newapp.data.dao;

import net.xici.newapp.data.contentprovider.AccountContentProvider;
import net.xici.newapp.data.pojo.Account;
import net.xici.newapp.support.util.SerializableUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class AccountDao extends BaseDataHelper {

	public AccountDao(Context context) {
		super(context);
	}

	@Override
	protected Uri getContentUri() {
		return AccountContentProvider.CONTENT_URI;
	}

	public ContentValues getContentValues(Account account) {
		ContentValues cv = new ContentValues();
		cv.put(AccountContentProvider.USERID, account.userid);
		cv.put(AccountContentProvider.USERNAME, account.username);
		cv.put(AccountContentProvider.PASSWORD, "");
		cv.put(AccountContentProvider.ACCESSTOKEN, "");
		cv.put(AccountContentProvider.ACOUNT_BLOB,
				SerializableUtils.toByteArray(account));
		cv.put(AccountContentProvider.ISDEFAULT,
				AccountContentProvider.ISDEFAULT_YES);
		return cv;
	}

	public Account fromCursor(Cursor cursor) {
		return SerializableUtils.fromBytes(cursor.getBlob(cursor
				.getColumnIndexOrThrow(AccountContentProvider.ACOUNT_BLOB)));
	}

	public Account getdefaultAccount() {

		Cursor cursor = query(null,
				AccountContentProvider.ISDEFAULT + " =?",
				new String[] { String.valueOf(AccountContentProvider.ISDEFAULT_YES), }, null);
		if(cursor!=null&&cursor.getCount()>0){
			cursor.moveToFirst();
			return fromCursor(cursor);
		}
		return null;
	}
	
	public void insertAccount(Account account){
		if(account==null)
			return;
		clearDefaultAccount();
		if(constainAccount(account.userid)){
			updateAccount(account);
		}else {
			insert(getContentValues(account));
		}
	}
	
	public void updateAccount(Account account){
		ContentValues cv = new ContentValues();
		cv.put(AccountContentProvider.ISDEFAULT,
				AccountContentProvider.ISDEFAULT_YES);
		cv.put(AccountContentProvider.ACOUNT_BLOB,
				SerializableUtils.toByteArray(account));
		update(cv,AccountContentProvider.USERID + "=?",
				new String[] { String.valueOf(account.userid)});
	}
	
	
	public void clearDefaultAccount(){
		ContentValues cv = new ContentValues();
		cv.put(AccountContentProvider.ISDEFAULT,
				AccountContentProvider.ISDEFAULT_NO);
		update(cv, null, null);
	}
	
	public boolean constainAccount(long accountid) {
		Cursor cursor = query(
				new String[] { AccountContentProvider._ID },
				AccountContentProvider.USERID + " =?",
				new String[] { String.valueOf(accountid) },
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

}
