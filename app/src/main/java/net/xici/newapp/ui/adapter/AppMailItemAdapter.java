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
import net.xici.newapp.data.pojo.AppMailItem;
import net.xici.newapp.support.util.ImageUtils;
import net.xici.newapp.support.util.StringUtil;
import net.xici.newapp.support.widget.emojicon.EmojiconHandler;
/**
 * 飞语详情
 * @author bkmac
 *
 */
public class AppMailItemAdapter  extends CursorAdapter implements View.OnClickListener{
	
	private static final int TYPE_INBOX = 0;

	private static final int TYPE_OUTBOX = 1;
	

	private LayoutInflater mInflater;
	private MailDao mMailDao = null;
	protected Context context;
    private long userid;
	
	public AppMailItemAdapter(Context context, Cursor c) {
		super(context, c);

		mInflater = LayoutInflater.from(context);
		mMailDao = new MailDao(XiciApp.getContext());
		this.context = context;
	}
	
	public AppMailItem getAppMailItem(int position)
	{
		Cursor cursor=(Cursor) getItem(position);
		AppMailItem mail = mMailDao.getAppMailItemfromCursor(cursor);
		return mail;
	}
	
	public void setUserId(long id){
		userid = id;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		AppMailItem mail = mMailDao.getAppMailItemfromCursor(cursor);
		
		holder.content.setText(EmojiconHandler.replaceFace(context, StringUtil.fromhtml(mail.Mail_Memo)) );
		holder.createtime.setText(mail.Mail_date);
		if("inbox".equals(mail.type)){
			
			ImageUtils.displayAvatar(userid, holder.avatar);
			
		}else {
			
			ImageUtils.displayAvatar(XiciApp.getAccountId(),holder.avatar );
		}
		
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		int viewtype = getItemViewType(arg1.getPosition());
		ViewHolder holder = new ViewHolder();
		View view = null;
		if(viewtype == TYPE_INBOX){
			
			view = mInflater.inflate(R.layout.appmail_list_item_inbox, arg2, false);
			
			holder.content = (TextView)view.findViewById(R.id.content);
			holder.createtime = (TextView)view.findViewById(R.id.time);
			holder.avatar = (ImageView)view.findViewById(R.id.avatar);
			holder.avatar.setOnClickListener(this);
			view.setTag(holder);
			
		}else if(viewtype == TYPE_OUTBOX){
			
			view = mInflater.inflate(R.layout.appmail_list_item_outbox, arg2, false);
			
			holder.content = (TextView)view.findViewById(R.id.content);
			holder.createtime = (TextView)view.findViewById(R.id.time);
			holder.avatar = (ImageView)view.findViewById(R.id.avatar);
			
			view.setTag(holder);
		}

		return view;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.avatar.setTag(position);
		
		return view;
	}
	
	class ViewHolder {
		TextView content;
		TextView createtime;
		ImageView avatar;
	}
	
	@Override
	public void onClick(View v) {
		int position = 0;
		switch (v.getId()) {
		case R.id.avatar:
			position = (Integer) v.getTag();
			AppMailItem item = getAppMailItem(position);
			//UserProfileActivity.start(context, item.mUserName, userid);
			break;

		default:
			break;
		}
		
	}
	
    @Override
    public int getViewTypeCount()
    {
        return 2;
    }
	
	@Override
	public int getItemViewType(int position) {
		AppMailItem  mail= getAppMailItem(position);
		if("inbox".equals(mail.type)){
			return TYPE_INBOX;
		}else {
			return TYPE_OUTBOX;
		}
	}

}
