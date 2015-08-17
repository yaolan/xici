package net.xici.newapp.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.xici.newapp.R;
import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.dao.MailDao;
import net.xici.newapp.data.pojo.AppMail;
import net.xici.newapp.support.util.ImageUtils;
import net.xici.newapp.support.util.StringUtil;
import net.xici.newapp.support.widget.emojicon.EmojiconHandler;

public class AppMailCursorAdapter  extends CursorAdapter{
	
	private LayoutInflater mInflater;
	private MailDao mailDao = null;
	protected Context context;

	public AppMailCursorAdapter(Context context, Cursor c) {
		super(context, c);
		mInflater = LayoutInflater.from(context);
		mailDao = new MailDao(XiciApp.getContext());
		this.context = context;
	}
	
	public AppMail getMailItem(int position)
	{
		Cursor cursor=(Cursor) getItem(position);
		AppMail mail = mailDao.getAppMailfromCursor(cursor);
		return mail;
	}

	@Override
	public void bindView(View view, Context arg1, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		AppMail mail = mailDao.getAppMailfromCursor(cursor);
		
		holder.name.setText(StringUtil.fromhtml(mail.mUserName));
		holder.title.setText(EmojiconHandler.replaceFace(context, StringUtil.fromhtml(mail.newest.title)));
		holder.date.setText(mail.newest.date);
		ImageUtils.displayAvatar(mail.uid, holder.avatar);
		
		holder.unread.setVisibility(mail.unread_flag?View.VISIBLE:View.GONE);
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		View view = mInflater.inflate(R.layout.appmail_list_item, arg2, false);
		ViewHolder holder = new ViewHolder();
		holder.name = (TextView) view.findViewById(R.id.name);
		holder.title = (TextView) view.findViewById(R.id.title);
		holder.date = (TextView) view.findViewById(R.id.date);
		holder.avatar = (ImageView) view.findViewById(R.id.avatar);
		holder.unread = (ImageView) view.findViewById(R.id.unread);
		view.setTag(holder);
		
		return view;
	}
	

	class ViewHolder {
		TextView name;
		TextView title;
		TextView date;
		ImageView avatar;
		ImageView unread;
	}


}
