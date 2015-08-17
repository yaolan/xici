package net.xici.newapp.ui.adapter;

import java.util.HashSet;
import java.util.Set;

import net.xici.newapp.R;
import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.dao.ThreadDao;
import net.xici.newapp.data.dao.UnreadDao;
import net.xici.newapp.data.pojo.Thread;
import net.xici.newapp.support.util.DateUtils;
import net.xici.newapp.support.util.ImageUtils;
import net.xici.newapp.support.util.StringUtil;
import net.xici.newapp.support.util.UIUtil;
import net.xici.newapp.support.widget.emojicon.EmojiconHandler;


import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ThreadCursorAdapter extends CursorAdapter implements View.OnClickListener{
	
    private static final int TYPE_NOMAL = 0;

    private static final int TYPE_TOP = 1;
    
    private static final int MAXTOP = 2;
	
	private LayoutInflater mInflater;
	private ThreadDao threadDao = null;
	protected Context context;
	private Set<Long> selectedindex;
	private UnreadDao unreadDao;
	private long accountid = 0;
	
	private boolean showmoretop = false;
	
	public ThreadCursorAdapter(Context context, Cursor c) {
		super(context, c);
		mInflater = LayoutInflater.from(context);
		threadDao = new ThreadDao(XiciApp.getContext());
		this.context = context;
		selectedindex = new HashSet<Long>();
		unreadDao = new UnreadDao(XiciApp.getContext());
		accountid = XiciApp.getAccountId();
	}
	
	public Thread getThreadItem(int position)
	{
		Cursor cursor= (Cursor) getItem(position);
		Thread thread = threadDao.fromCursor(cursor);
		return thread;
	}
	
	public int getThreadItemTop(int position)
	{
		Cursor cursor= (Cursor) getItem(position);
		int top = threadDao.topfromCursor(cursor);
		return top;
	}
	
	public void addSelectedItem(int position){
		Thread thread = getThreadItem(position);
		selectedindex.add(thread.tid);
		notifyDataSetChanged();
	}
	
	public void clearSelectedItem(){
		selectedindex.clear();
		notifyDataSetChanged();
	}
	
	public String getSelectedItemID(){
		StringBuffer stringBuffer = new StringBuffer();
		for (Long id:selectedindex) {
			stringBuffer.append(id);
		}
		return stringBuffer.toString();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		Thread thread = threadDao.fromCursor(cursor);
		if(thread.top>0){
			//holder.title.setText(UIUtil.getThreadTagString(context, thread.top,thread.cool,StringUtil.fromhtml(thread.title),thread.imgs));
			
			holder.title.setText(UIUtil.getThreadTagString(context, thread.top,thread.cool,thread.title,thread.imgs));
			
			int positon = cursor.getPosition();
			holder.more_view.setVisibility(View.GONE);
			if(positon>MAXTOP&&!showmoretop){
				holder.container_layout.setVisibility(View.GONE);
				holder.divider.setVisibility(View.GONE);
			}else {
				holder.container_layout.setVisibility(View.VISIBLE);
				holder.divider.setVisibility(View.VISIBLE);
			}
			
			if(!cursor.isLast()){
				
				cursor.moveToNext();
				//Thread t = threadDao.fromCursor(cursor);
				if(threadDao.topfromCursor(cursor)>0){
					
					holder.margin_view.setVisibility(View.GONE);
					
				}else {
					holder.margin_view.setVisibility(View.GONE);
					
//					if(positon>MAXTOP&&!showmoretop){
//						holder.more_view.setVisibility(View.VISIBLE);
//					}
					holder.more_view.setVisibility(View.VISIBLE);
					if(showmoretop){
						holder.more_view_text.setText("收起置顶");
						holder.more_view_ic.setBackgroundResource(R.drawable.arrow_up);
						holder.more_divider_down.setVisibility(View.GONE);
						holder.more_divider_up.setVisibility(View.VISIBLE);
					}else {
						holder.more_view_text.setText("查看更多置顶");
						holder.more_view_ic.setBackgroundResource(R.drawable.arrow_down);
						holder.more_divider_down.setVisibility(View.VISIBLE);
						holder.more_divider_up.setVisibility(View.GONE);
					}
					
				}
			}
			
		}else {
			
//			holder.title.setText(UIUtil.getThreadTagString(context, thread.top,thread.cool, StringUtil.fromhtml(thread.title),thread.imgs));
//			holder.summary.setText(EmojiconHandler.replaceFace(mContext,StringUtil.fromhtml(thread.summary)));
			

			holder.title.setText(UIUtil.getThreadTagString(context, thread.top,thread.cool, thread.title,thread.imgs));
			holder.summary.setText(EmojiconHandler.replaceFace(mContext,thread.summary));
			
			holder.username.setText(thread.author.userName);
			
			if(thread.imgs!=null&&thread.imgs.size()>0){
				ImageUtils.displayWebImage(thread.imgs.get(0), holder.image);
				holder.image.setVisibility(View.VISIBLE);
			}else {
				holder.image.setVisibility(View.GONE);
			}
			
			holder.replycount.setText(thread.replycount+"");
			holder.zancount.setText(thread.zan);
			
			if(thread.createtime.length()<2){
				holder.createtime.setText(DateUtils.getRelativeDate(thread.updatetime));
			}else {
				holder.createtime.setText(DateUtils.getRelativeDate(thread.createtime));
			}
			
		}
		
		if(unreadDao.ConstainThread(thread.tid, accountid)){
			
			holder.title.setTextColor(context.getResources().getColorStateList(R.color.selectable_text_color_gray_dark));
			
		}else {
			
			holder.title.setTextColor(context.getResources().getColorStateList(R.color.selectable_text_color_black));
		}
		
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		int viewtype = getItemViewType(arg1.getPosition());
		ViewHolder holder = new ViewHolder();
		View view = null;
		if(viewtype == TYPE_NOMAL){
			view = mInflater.inflate(R.layout.thread_list_item, arg2, false);
			
			holder.title = (TextView)view.findViewById(R.id.title);
			holder.summary = (TextView)view.findViewById(R.id.summary);
			holder.username = (TextView)view.findViewById(R.id.username);
			holder.replycount = (TextView)view.findViewById(R.id.replycount);
			holder.createtime = (TextView)view.findViewById(R.id.createtime);
			//holder.avatar = (ImageView)view.findViewById(R.id.avatar);
			//holder.avatar.setOnClickListener(this);
			//holder.username.setOnClickListener(this);
			
			holder.image = (ImageView)view.findViewById(R.id.image);
			holder.zancount = (TextView)view.findViewById(R.id.zancount);
			view.setTag(holder);
			
		}else if(viewtype == TYPE_TOP){
			
			view = mInflater.inflate(R.layout.thread_top_list_item, arg2, false);
			holder.container_layout = view.findViewById(R.id.container_layout);
			holder.more_view = view.findViewById(R.id.more);
			holder.divider = view.findViewById(R.id.divider);
			holder.title = (TextView)view.findViewById(R.id.title);
			holder.margin_view  = view.findViewById(R.id.margin_view);
			holder.more_divider_up  = view.findViewById(R.id.more_divider_up);
			holder.more_divider_down  = view.findViewById(R.id.more_divider_down);
			holder.more_view_text = (TextView)view.findViewById(R.id.more_text);
			holder.more_view_ic = (ImageView)view.findViewById(R.id.more_ic);
			holder.more_view.setOnClickListener(this);
			view.setTag(holder);
		}

		return view;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
//		ViewHolder  holder = (ViewHolder) view.getTag();
//		if(holder.avatar!=null){
//			holder.avatar.setTag(position);
//		}
//		if(holder.username!=null){
//			holder.username.setTag(position);
//		}
		
		return view;
	}
	
	class ViewHolder {
		View container_layout;
		View divider;
		View more_view;
		TextView title;
		TextView summary;
		TextView username;
		TextView replycount;
		TextView createtime;
		View margin_view;
		ImageView image;
		TextView zancount;
		
		ImageView more_view_ic;
		TextView more_view_text;
		View more_divider_up;
		View more_divider_down;
		//ImageView avatar;
	}
	
    @Override
    public int getViewTypeCount()
    {
        return 2;
    }
	
	@Override
	public int getItemViewType(int position) {
//		Thread t = getThreadItem(position);
//		if(t.top>0){
//			return TYPE_TOP;
//		}else {
//			return TYPE_NOMAL;
//		}
		
		if(getThreadItemTop(position)>0){
			return TYPE_TOP;
		}else {
			return TYPE_NOMAL;
		}
	}

	@Override
	public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.avatar:
			case R.id.username:
				
				try {
					int index = (Integer)v.getTag();
					Thread t = getThreadItem(index);
					if(XiciApp.islogined()&&XiciApp.getAccountId()!=t.author.userid){
						//MailWriteActivity.start(context, t.author.userName);

//						UserProfileActivity.start(context, t.author.userName, t.author.userid);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				break;
			case R.id.more:
				
				MobclickAgent.onEvent(mContext, showmoretop?"gather_up":"check_up");
				
				showmoretop = !showmoretop;

				notifyDataSetChanged();
                break;
			default:
				break;
			}
			
	
		
	}

}
