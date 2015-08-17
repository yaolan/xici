package net.xici.newapp.ui.adapter;

import static android.view.View.inflate;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import net.xici.newapp.R;
import net.xici.newapp.data.pojo.TopPic;
import net.xici.newapp.support.util.ImageUtils;

public class TopPicAdapter extends PagerAdapter{
	
	private List<TopPic> mItems;
	private Context mContext;
	private OnItemClickListener mOnItemClickListener;

	public TopPicAdapter(Context context, List<TopPic> list) {
		mContext = context;
		mItems = new ArrayList<TopPic>();
		if (list != null) {
			mItems.addAll(list);
		}
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {

		mOnItemClickListener = listener;
		
	}

	public void add(List<TopPic> list, boolean append) {
		if (!append) {
			mItems.clear();
		}
		mItems.addAll(list);
		notifyDataSetChanged();
	}
	
	public void clear(){
		mItems.clear();
	}
	
	public void add(TopPic topPic){
		mItems.add(topPic);
	}
	
	public TopPic getItem(int position){
		if(position<mItems.size()){
			return mItems.get(position);
		}
		return null;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}
	
	@Override
	public Object instantiateItem(ViewGroup container,final int position) {
		final TopPic item = mItems.get(position);

		if (item == null) {
			return null;
		}
		View v = null;
		v = inflate(mContext, R.layout.ad_top_layout, null);
		ImageView iv = (ImageView) v.findViewById(R.id.iv_ad);
		ImageUtils.displaySplashPicImage(item.picurl, iv);
		iv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClick(position);
				}
			}
		});
		container.addView(v, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		return v;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object o) {
		return view == o;
	}
	
	public interface OnItemClickListener {
		void onItemClick(int postion);
	}

}
