package net.xici.newapp.support.widget.emojicon;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import net.xici.newapp.R;
import net.xici.newapp.ui.adapter.ArrayAdapter;

public class EmojiAdapter extends ArrayAdapter<Emojicon> {

	public EmojiAdapter(Context ctx, ArrayList<Emojicon> l) {
		super(ctx, l);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(R.layout.emojicon_item, parent,false);
			ViewHolder holder = new ViewHolder();
			holder.icon = (ImageView) v.findViewById(R.id.emojicon_icon);
			v.setTag(holder);
		}
		Emojicon emoji = getItem(position);
		ViewHolder holder = (ViewHolder) v.getTag();
		holder.icon.setImageResource(emoji.icon);
		return v;
	}

	class ViewHolder {
		ImageView icon;
	}

}
