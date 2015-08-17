package net.xici.newapp.data.dao;

import java.util.List;

import com.google.gson.Gson;

import net.xici.newapp.data.contentprovider.BoardContentProvider;
import net.xici.newapp.data.contentprovider.FloorContentProvider;
import net.xici.newapp.data.contentprovider.MailContentProvider;
import net.xici.newapp.data.pojo.AppMail;
import net.xici.newapp.data.pojo.AppMailItem;
import net.xici.newapp.data.pojo.Board;
import net.xici.newapp.data.pojo.Mail;
import net.xici.newapp.data.pojo.UserPing;
import net.xici.newapp.support.set.SettingUtil;
import net.xici.newapp.support.util.DateUtils;
import net.xici.newapp.support.util.JsonUtils;
import net.xici.newapp.support.util.SerializableUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

public class MailDao extends BaseDataHelper {

	public MailDao(Context context) {
		super(context);
	}

	@Override
	protected Uri getContentUri() {
		return MailContentProvider.CONTENT_URI;
	}

	public ContentValues getContentValues(Mail mail, long accountid, int append) {
		ContentValues cv = new ContentValues();
		cv.put(MailContentProvider.MAIL_ID, mail.id);
		cv.put(MailContentProvider.MAIL_OLD, mail.old);
		cv.put(MailContentProvider.MAIL_TITLE, mail.title);
		cv.put(MailContentProvider.MAIL_TYPE, mail.type);
		cv.put(MailContentProvider.MAIL_DATE, mail.date);
		cv.put(MailContentProvider.USER_NAME, mail.username);
		cv.put(MailContentProvider.ACCOUNT_ID, accountid);
		if (append != -1) {
			cv.put(MailContentProvider.APPEND, append);
		}
		return cv;
	}

	public Mail fromCursor(Cursor cursor) {
		Mail mail = new Mail();
		mail.id = cursor.getInt(cursor
				.getColumnIndexOrThrow(MailContentProvider.MAIL_ID));
		mail.old = cursor.getInt(cursor
				.getColumnIndexOrThrow(MailContentProvider.MAIL_OLD));
		mail.title = cursor.getString(cursor
				.getColumnIndexOrThrow(MailContentProvider.MAIL_TITLE));
		mail.type = cursor.getInt(cursor
				.getColumnIndexOrThrow(MailContentProvider.MAIL_TYPE));
		mail.date = cursor.getString(cursor
				.getColumnIndexOrThrow(MailContentProvider.MAIL_DATE));
		mail.username = cursor.getString(cursor
				.getColumnIndexOrThrow(MailContentProvider.USER_NAME));
		return mail;
	}

	public int bulkInsert(List<Mail> mails, long accountid, int append) {
		if (mails == null || mails.size() == 0) {
			// 插入数据为空，判断是否情况数据
			if (append == 0) {
				deleteByAccount(accountid);
			}
			return 0;
		}
		ContentValues[] valueArray = new ContentValues[mails.size()];
		for (int i = 0; i < mails.size(); i++) {
			Mail mail = mails.get(i);
			ContentValues values = getContentValues(mail, accountid, append);
			valueArray[i] = values;
		}
		return bulkInsert(valueArray);
	}

	/**
	 * 飞语联系人列表
	 * 
	 * @param mails
	 * @param accountid
	 * @param append
	 * @return
	 */
	public int bulkInsertAppMails(List<AppMail> mails, long accountid,
			int append) {
		if (mails == null || mails.size() == 0) {
			// 插入数据为空，判断是否情况数据
			if (append == 0) {
				deleteByAccountType(accountid,
						MailContentProvider.MAIL_TYPE_APPMAIL);

			}
			return 0;
		}
		ContentValues[] valueArray = new ContentValues[mails.size()];
		Gson gson = new Gson();
		// for(int i=mails.size()-1;i>=0;i--)
		for (int i = 0; i < mails.size(); i++) {
			AppMail mail = mails.get(i);
			ContentValues cv = new ContentValues();
			cv.put(MailContentProvider.ACCOUNT_ID, accountid);

			cv.put(MailContentProvider.USER_NAME, mail.mUserName);
			cv.put(MailContentProvider.MAIL_TYPE,
					MailContentProvider.MAIL_TYPE_APPMAIL);
			String mailstr = gson.toJson(mail);
			cv.put(MailContentProvider.MAIL_TITLE, mailstr);
			if (append != -1) {
				cv.put(MailContentProvider.APPEND, append);
			}
			valueArray[i] = cv;
		}
		return bulkInsert(valueArray);
	}

	public AppMail getAppMailfromCursor(Cursor cursor) {

		String mailstr = cursor.getString(cursor
				.getColumnIndexOrThrow(MailContentProvider.MAIL_TITLE));

		return JsonUtils.fromJson(mailstr, AppMail.class);
	}

	public void updateAppMailUnRead(long accountid, String username) {

		Cursor cursor = query(
				null,
				MailContentProvider.ACCOUNT_ID + " =? and "
						+ MailContentProvider.USER_NAME + " =? and "
						+ MailContentProvider.MAIL_TYPE + "=?",
				new String[] { String.valueOf(accountid), username,
						String.valueOf(MailContentProvider.MAIL_TYPE_APPMAIL) },
				null);
		AppMail mail = null;

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			mail = getAppMailfromCursor(cursor);
			cursor.close();
		}

		if (mail != null && mail.unread_flag) {

			//数字显示减1
			UserPing ping = SettingUtil.getAccountMessageCount(accountid);
			if (ping == null) {
				ping = new UserPing();
			}

			ping.newmail--;
			if (ping.newmail < 0) {
				ping.newmail = 0;
			}
			UserPing.postevent(ping);

			//未读状态修改
			mail.unread_flag = false;
			Gson gson = new Gson();
			ContentValues cv = new ContentValues();
			String mailstr = gson.toJson(mail);
			cv.put(MailContentProvider.MAIL_TITLE, mailstr);

			update(cv,
					MailContentProvider.ACCOUNT_ID + " =? and "
							+ MailContentProvider.USER_NAME + " =? and "
							+ MailContentProvider.MAIL_TYPE + "=?",
					new String[] {
							String.valueOf(accountid),
							username,
							String.valueOf(MailContentProvider.MAIL_TYPE_APPMAIL) });
		}

	}

	/**
	 * 飞语列表
	 * 
	 * @param mails
	 * @param accountid
	 * @param append
	 * @return
	 */
	public int bulkInsertAppMailItem(List<AppMailItem> mails, long accountid,
			String username, int append) {
		if (mails == null || mails.size() == 0) {
			// 插入数据为空，判断是否情况数据
			if (append == 0) {
				deleteByAccountType(accountid,
						MailContentProvider.MAIL_TYPE_APPMAIL);

			}
			return 0;
		}
		ContentValues[] valueArray = new ContentValues[mails.size()];
		Gson gson = new Gson();
		for (int i = 0; i < mails.size(); i++) {
			AppMailItem mail = mails.get(i);
			ContentValues cv = new ContentValues();
			cv.put(MailContentProvider.ACCOUNT_ID, accountid);
			cv.put(MailContentProvider.MAIL_ID, mail.Mail_id);
			cv.put(MailContentProvider.USER_NAME, username);
			cv.put(MailContentProvider.MAIL_DATE, mail.Mail_date);
			cv.put(MailContentProvider.MAIL_TYPE,
					MailContentProvider.MAIL_TYPE_APPMAIL_DIALOGUE);
			cv.put(MailContentProvider.MAIL_TITLE, gson.toJson(mail));
			if (append != -1) {
				cv.put(MailContentProvider.APPEND, append);
			}
			valueArray[i] = cv;
		}
		return bulkInsert(valueArray);
	}

	public void insertAppMailItem(long accountid, String username,
			String content) {

		ContentValues cv = new ContentValues();
		Gson gson = new Gson();
		AppMailItem mail = new AppMailItem();
		mail.Mail_Title = content;
		mail.Mail_Memo = content;
		mail.mUserName = username;
		mail.type = "outbox";
		mail.Mail_date = DateUtils.getNowTime();
		cv.put(MailContentProvider.ACCOUNT_ID, accountid);
		cv.put(MailContentProvider.USER_NAME, username);
		cv.put(MailContentProvider.MAIL_DATE, mail.Mail_date);
		cv.put(MailContentProvider.MAIL_TYPE,
				MailContentProvider.MAIL_TYPE_APPMAIL_DIALOGUE);
		cv.put(MailContentProvider.MAIL_TITLE, gson.toJson(mail));
		insert(cv);

	}

	public AppMailItem getAppMailItemfromCursor(Cursor cursor) {

		String mailstr = cursor.getString(cursor
				.getColumnIndexOrThrow(MailContentProvider.MAIL_TITLE));

		return JsonUtils.fromJson(mailstr, AppMailItem.class);
	}

	/**
	 * 收件箱
	 * 
	 * @param accountid
	 * @param senduser
	 * @param content
	 */
	public void insertOutbox(long accountid, String senduser, String content) {

		ContentValues cv = new ContentValues();
		cv.put(MailContentProvider.MAIL_ID, 0);
		cv.put(MailContentProvider.MAIL_OLD, 1);
		cv.put(MailContentProvider.MAIL_TITLE, content);
		cv.put(MailContentProvider.MAIL_TYPE,
				MailContentProvider.MAIL_TYPE_OUTBOX);
		cv.put(MailContentProvider.MAIL_DATE, DateUtils.getNowTime());
		cv.put(MailContentProvider.USER_NAME, senduser);
		cv.put(MailContentProvider.ACCOUNT_ID, accountid);
		insert(cv);

	}

	public CursorLoader getCursorLoader(long accountid, int type) {

		return new CursorLoader(getContext(), getContentUri(), null,
				MailContentProvider.ACCOUNT_ID + "=? and "
						+ MailContentProvider.MAIL_TYPE + "=?", new String[] {
						String.valueOf(accountid), String.valueOf(type) },
				type == MailContentProvider.MAIL_TYPE_INBOX
						|| type == MailContentProvider.MAIL_TYPE_APPMAIL ? null
						: MailContentProvider._ID + " desc");

	}

	public CursorLoader getCursorLoaderByUsername(long accountid,
			String username, int type) {

		return new CursorLoader(getContext(), getContentUri(), null,
				MailContentProvider.ACCOUNT_ID + "=? and "
						+ MailContentProvider.USER_NAME + "=? and "
						+ MailContentProvider.MAIL_TYPE + "=?", new String[] {
						String.valueOf(accountid), username,
						String.valueOf(type) }, MailContentProvider.MAIL_ID +  " ASC");

	}
	
	public CursorLoader getCursorLoaderAppmailDialogue(long accountid,
			String username, int type) {

		return new CursorLoader(getContext(), getContentUri(), null,
				MailContentProvider.ACCOUNT_ID + "=? and "
						+ MailContentProvider.USER_NAME + "=? and "
						+ MailContentProvider.MAIL_TYPE + "=?", new String[] {
						String.valueOf(accountid), username,
						String.valueOf(type) }, MailContentProvider.MAIL_DATE +  " ASC");

	}
	
	

	public void deleteByAccount(long accountid) {
		delete(MailContentProvider.ACCOUNT_ID + "=? and "
				+ MailContentProvider.MAIL_TYPE + "=?",
				new String[] { String.valueOf(accountid),
						String.valueOf(MailContentProvider.MAIL_TYPE_INBOX) });
	}

	public void deleteByAccountType(long accountid, int type) {
		delete(MailContentProvider.ACCOUNT_ID + "=? and "
				+ MailContentProvider.MAIL_TYPE + "=?",
				new String[] { String.valueOf(accountid), String.valueOf(type) });
	}
	
	public void deleteByAccountUsername(long accountid,String username) {
		delete(MailContentProvider.ACCOUNT_ID + "=? and "
				+ MailContentProvider.MAIL_TYPE + "=? and "+MailContentProvider.USER_NAME+"=?",
				new String[] { String.valueOf(accountid), String.valueOf(MailContentProvider.MAIL_TYPE_APPMAIL),username });
		
		delete(MailContentProvider.ACCOUNT_ID + "=? and "
				+ MailContentProvider.MAIL_TYPE + "=? and "+MailContentProvider.USER_NAME+"=?",
				new String[] { String.valueOf(accountid), String.valueOf(MailContentProvider.MAIL_TYPE_APPMAIL_DIALOGUE),username });
	}
}
