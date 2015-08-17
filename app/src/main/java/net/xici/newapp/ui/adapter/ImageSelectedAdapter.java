package net.xici.newapp.ui.adapter;

import java.util.ArrayList;

import net.xici.newapp.R;
import net.xici.newapp.data.pojo.AddMediaItem;
import net.xici.newapp.support.util.ImageUtils;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageSelectedAdapter extends ArrayAdapter<AddMediaItem> implements OnClickListener{

	public ImageSelectedAdapter(Context ctx, ArrayList<AddMediaItem> l) {
		super(ctx, l);
	}
	
	
	public int getCount() {
		if(mObjects.size() == 9){
			return 9;
		}
		return (mObjects.size() + 1);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(R.layout.image_item_thumbnail, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.image = (ImageView) v.findViewById(R.id.image);
			holder.delete = (ImageView)v.findViewById(R.id.delete);
			holder.delete.setOnClickListener(this);
			v.setTag(holder);
		}
		ViewHolder holder = (ViewHolder) v.getTag();
		if(position<mObjects.size()||mObjects.size()==9){
			AddMediaItem item = getItem(position);
			ImageUtils.displayWebImageCacheMemory("file:///" + item.originalUri, holder.image);
			holder.delete.setTag(position);
			holder.delete.setVisibility(View.VISIBLE);
			
		}else if(position==mObjects.size()&&mObjects.size()<9){
			holder.delete.setVisibility(View.GONE);
			ImageUtils.cancelDisplayTask(holder.image);
			holder.image.setImageResource(R.drawable.btn_image_add);
		}
		
		
		return v;
	}

	class ViewHolder {
		ImageView image;
		ImageView delete;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.delete:
			int index = (Integer) v.getTag();
			mObjects.remove(index);
			notifyDataSetChanged();
			break;

		default:
			break;
		}
		
	}
	
}
