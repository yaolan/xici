package net.xici.newapp.support.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.xici.newapp.R;

public class FloorImageView extends RelativeLayout {
	
	private ImageView contentImageView;
	private View contentImageBtn;

	public FloorImageView(Context context) {
		super(context);
		init();
	}
	
	public FloorImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.setContentImageView((ImageView)findViewById(R.id.content_image));
		this.setContentImageBtn(findViewById(R.id.content_image_btn));
	}
	
	private void init(){
		
		LayoutInflater.from(getContext()).inflate(R.layout.floor_list_item_image, this, true);
		this.setContentImageView((ImageView)findViewById(R.id.content_image));
		this.setContentImageBtn(findViewById(R.id.content_image_btn));
	}

	public ImageView getContentImageView() {
		return contentImageView;
	}

	public void setContentImageView(ImageView contentImageView) {
		this.contentImageView = contentImageView;
	}

	public View getContentImageBtn() {
		return contentImageBtn;
	}

	public void setContentImageBtn(View contentImageBtn) {
		this.contentImageBtn = contentImageBtn;
	}





}
