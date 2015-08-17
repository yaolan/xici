package net.xici.newapp.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.ImageSize;

import net.xici.newapp.R;
import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.dao.FloorDao;
import net.xici.newapp.data.pojo.Floor;
import net.xici.newapp.data.pojo.ThumbnailSize;
import net.xici.newapp.support.set.SettingUtil;
import net.xici.newapp.support.util.DateUtils;
import net.xici.newapp.support.util.DeviceUtil;
import net.xici.newapp.support.util.ImageUtils;
import net.xici.newapp.support.util.L;
import net.xici.newapp.support.util.StringUtil;
import net.xici.newapp.support.util.UIUtil;
import net.xici.newapp.support.widget.FloorImageView;
import net.xici.newapp.support.widget.emojicon.EmojiconHandler;
import net.xici.newapp.ui.image.ImageViewActivity;

import java.util.ArrayList;
import java.util.List;

public class FloorCursorNewAdapter extends CursorAdapter implements
		View.OnClickListener {

	private LayoutInflater mInflater;
	private FloorDao floorDao = null;
	protected Context context;
	private boolean showimage = true;
	ImageSize targetSize = new ImageSize(150, 150);
	private float textsize=0;
	
	private OnFloorClickListener mOnFloorClickListener;
	private int floorTotalCount = 0;
	
	private int floorReplyCount = 0;
	
	private float imagewight = 0;

	private int minimagewight = 0;
	

	public FloorCursorNewAdapter(Context context, Cursor c) {
		super(context, c);
		mInflater = LayoutInflater.from(context);
		floorDao = new FloorDao(XiciApp.getContext());
		this.context = context;
		//showimage = SettingUtil.getShowImage();
		int textsizeres = R.dimen.text_size_post_small;
		switch (SettingUtil.getTestSizePost()) {
		case 0:
			textsizeres = R.dimen.text_size_post_small;
			break;
        case 1:
        	textsizeres = R.dimen.text_size_post_medium;
			break;
        case 2:
        	textsizeres = R.dimen.text_size_post_large;
			break;

		default:
			break;
		}
		textsize = context.getResources().getDimension(textsizeres);
		
		imagewight = DeviceUtil.getScreenWidth() - 2*context.getResources().getDimensionPixelSize(R.dimen.margin_large);
		minimagewight = DeviceUtil.dp2px(150);
	}

	public Floor getFloorItem(int position) {
		Object object = getItem(position);
		Floor floor = null;
		if(object!=null){
			Cursor cursor = (Cursor) getItem(position);
			floor = floorDao.fromCursor(cursor);
		}
		return floor;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		Floor floor = floorDao.fromCursor(cursor);
		ImageUtils.displayAvatar(floor.user.userid, holder.avatar);
		holder.username.setText(floor.user.userName);
		holder.time.setText(DateUtils.getRelativeDate(floor.updatetime));
		holder.floornum.setText(floor.getfloornum());
		
		holder.zancount.setText(floor.up_num+"");
		if(floor.up_status>0){
			
			holder.zancount.setBackgroundResource(R.drawable.zan_count_on_bg);
			
		}else {
			
			holder.zancount.setBackgroundResource(R.drawable.zan_count_bg);
		}
		
		holder.replycount.setText("");
		
//		if (floor.index < 2) {
//			holder.blank_view.setVisibility(View.GONE);
//		} else {
//			holder.blank_view.setVisibility(View.VISIBLE);
//		}
		holder.content.removeAllViews();

		List<String> textArr = floor.textList;

		for (int i = 0; i < textArr.size(); i++) {
			if (textArr.get(i).contains("[img]")) {

				String str = textArr
						.get(i)
						.substring("[img]".length(),
								textArr.get(i).indexOf("[/img]"))
						.replace("attachment", "").trim();
				if (UIUtil.isNumeric1(str)) {
					int pos = Integer.valueOf(str);
					if (pos >= 1&&floor.thumbnail!=null&&floor.thumbnail.size()>=pos) {
						FloorImageView floorimageView = new FloorImageView(
								mContext);
						final ImageView iv = floorimageView
								.getContentImageView();
						
//						if (showimage) {
//							ImageUtils.displayWebImage(
//									floor.thumbnail.get(pos - 1), iv);
//						} else {
//							floorimageView.getContentImageBtn().setVisibility(
//									View.GONE);
//						}

						floorimageView.getContentImageBtn().setTag(pos - 1);
						floorimageView.getContentImageBtn().setOnClickListener(
								this);

						iv.setTag(pos - 1);
						iv.setOnClickListener(this);
						

						android.widget.LinearLayout.LayoutParams layoutParams = new android.widget.LinearLayout.LayoutParams(DeviceUtil.dp2px(150),DeviceUtil.dp2px(150));
						
						if(floor.thumbnail_size!=null&&floor.thumbnail_size.size()>=pos){
							ThumbnailSize size = floor.thumbnail_size.get(pos-1);
							
							
							if(size.width>minimagewight){
								
								int height = (int) ((imagewight/size.width)*((float)size.height));
								
								layoutParams = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT,height);
								
							}else {
								
								layoutParams = new android.widget.LinearLayout.LayoutParams(size.width,size.height);
								
							}
							
							String imgurl = "";
							
							if(!TextUtils.isEmpty(size.url)){
								imgurl = "http://pan.xici.com/"+size.url;
							}else {
								imgurl = floor.thumbnail.get(pos - 1);
							}
							
							if (showimage) {
								ImageUtils.displayWebImage(
										imgurl, iv);
							} else {
								floorimageView.getContentImageBtn().setVisibility(
										View.GONE);
							}
							
						}
					
						holder.content.addView(floorimageView,layoutParams);
					}
				}

			} else {

				TextView textView = (TextView) mInflater.inflate(
						R.layout.floor_list_item_text, holder.content, false);
				textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textsize);
				
                String str = textArr.get(i).replace("\r\n", "<br>");
				//textView.setText(EmojiconHandler.replaceFace(mContext,textArr.get(i)));
				textView.setText(EmojiconHandler.replaceFace(mContext,StringUtil.fromhtml(str)));
				holder.content.addView(textView);
			}
		}

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		View view = mInflater
				.inflate(R.layout.floor_list_item_new, arg2, false);
		ViewHolder holder = new ViewHolder();
		holder.avatar = (net.xici.newapp.support.widget.CircularImageView) view.findViewById(R.id.avatar);
		holder.username = (TextView) view.findViewById(R.id.username);
		holder.time = (TextView) view.findViewById(R.id.time);
		holder.floornum = (TextView) view.findViewById(R.id.floornum);
		
		holder.zancount = (TextView) view.findViewById(R.id.zancount);
		holder.replycount = (TextView) view.findViewById(R.id.replycount);
		
		holder.content = (LinearLayout) view.findViewById(R.id.content);
		holder.blank_view = view.findViewById(R.id.blank_view);
		holder.avatar.setOnClickListener(this);
		holder.username.setOnClickListener(this);
		holder.zancount.setOnClickListener(this);
		holder.replycount.setOnClickListener(this);
		
		view.setTag(holder);
		return view;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.username.setTag(position);
		holder.avatar.setTag(position);
		holder.content.setTag(position);
		holder.zancount.setTag(position);
		holder.replycount.setTag(position);
		
		if(position==0){
			holder.replycount.setText(floorReplyCount+"");
		}else {
			holder.replycount.setText("");
		}
		return view;
	}

	class ViewHolder {
		net.xici.newapp.support.widget.CircularImageView avatar;
		TextView username;
		TextView time;
		TextView floornum;
		LinearLayout content;

		View blank_view;
		
		TextView zancount;
		TextView replycount;
	}

	class ImageViewTag {
		public int floorposition;
		public int imageposition;
	}

	@Override
	public void onClick(View v) {
		int floornum = 0;
		int position = 0;
		Floor floor = null;
		switch (v.getId()) {
		case R.id.content_image_btn:
			position = (Integer) v.getTag();
			try {
				View content = findContentView(v);
				// content = (View)(v.getParent().getParent());
				if (content != null) {
					floornum = (Integer) content.getTag();
					floor = getFloorItem(floornum);
					if (floor.thumbnail != null && floor.thumbnail.size() > 0) {
						
						if(floor.thumbnail_size!=null){
							
							ArrayList<String> thumbnailurl = new ArrayList<String>();
							
							for (ThumbnailSize size:floor.thumbnail_size) {
								
								thumbnailurl.add(size.url);
							}
							
							context.startActivity(ImageViewActivity.getIntent(
									context, (ArrayList<String>) floor.thumbnail,
									(ArrayList<String>) thumbnailurl, position));
						}else{
							
							context.startActivity(ImageViewActivity.getIntent(
									context, (ArrayList<String>) floor.thumbnail,
									position));
							
						}
					
						
					}
				}
			} catch (Exception e) {
				L.d(context, e.getMessage());
			}

			break;
		case R.id.avatar:
		case R.id.username:
			floornum = (Integer) v.getTag();
			floor = getFloorItem(floornum);
			if (XiciApp.islogined()) {
//				if(XiciApp.getAccountId() == floor.user.userid){
//					context.startActivity(new Intent(context, MyTheadActivity.class));
//				}else {
//					UserProfileActivity.start(context, floor.user.userName, floor.user.userid);
//				}
			}
			break;
		case R.id.replycount:
			floornum = (Integer) v.getTag();
			floor = getFloorItem(floornum);
			if(XiciApp.islogined()&&mOnFloorClickListener!=null){
				floornum = (Integer) v.getTag();
				floor = getFloorItem(floornum);
				mOnFloorClickListener.onReplyClick(floor);
			}
			break;
		case R.id.zancount:
			if(mOnFloorClickListener!=null){
				floornum = (Integer) v.getTag();
				floor = getFloorItem(floornum);
				mOnFloorClickListener.onZanClick(floor);
			}
			
			break;
		case R.id.content_image:
			break;
		}
	}

	private View findContentView(View v) {
		if (v.getId() == R.id.content) {
			return v;
		}
		View view = (View) v.getParent();
		if (view != null) {
			return findContentView(view);
		}
		return null;
	}

	private FloorImageView findFloorImageView(View v) {
		if (v instanceof FloorImageView) {
			return (FloorImageView) v;
		}
		View view = (View) v.getParent();
		if (view != null) {
			return findFloorImageView(view);
		}
		return null;
	}
	
	public int getFloorTotalCount() {
		return floorTotalCount;
	}

	public void setFloorTotalCount(int floorTotalCount) {
		this.floorTotalCount = floorTotalCount;
		floorReplyCount = floorTotalCount>0?(floorTotalCount-1):floorTotalCount;
	}
	
	public OnFloorClickListener getOnFloorClickListener() {
		return mOnFloorClickListener;
	}

	public void setOnFloorClickListener(OnFloorClickListener OnFloorClickListener) {
		this.mOnFloorClickListener = OnFloorClickListener;
	}
	
	public interface OnFloorClickListener{
		public void onReplyClick(Floor floor);
		public void onZanClick(Floor floor);
	}

}
