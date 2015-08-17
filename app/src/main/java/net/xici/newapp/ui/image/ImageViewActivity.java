package net.xici.newapp.ui.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.xici.newapp.R;
import net.xici.newapp.support.util.FileUtil;
import net.xici.newapp.support.util.ImageUtils;
import net.xici.newapp.support.util.L;
import net.xici.newapp.support.widget.touchview.GalleryViewPager;
import net.xici.newapp.support.widget.touchview.TouchImageView;
import net.xici.newapp.ui.base.BaseActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ImageViewActivity extends BaseActivity implements
		View.OnClickListener {

	private static final String EXTRAS_IMAGES = "extras_images";
	private static final String EXTRAS_THUMBNAUL_URL = "extras_thumbnaul_url";
	private static final String EXTRAS_POSITION = "extras_position";

	public static Intent getIntent(Context from, ArrayList<String> images,
			int position) {
		Intent intent = new Intent(from, ImageViewActivity.class);
		intent.putExtra(EXTRAS_IMAGES, images);
		intent.putExtra(EXTRAS_POSITION, position);
		return intent;
	}

	public static Intent getIntent(Context from, ArrayList<String> images,ArrayList<String> thumbnailurls,
			int position) {
		Intent intent = new Intent(from, ImageViewActivity.class);
		intent.putExtra(EXTRAS_IMAGES, images);
		intent.putExtra(EXTRAS_POSITION, position);
		if(thumbnailurls!=null){

			intent.putExtra(EXTRAS_THUMBNAUL_URL, thumbnailurls);
		}
		return intent;
	}

	private ViewPager mViewPager;
	private TextView tv_index;
	private ArrayList<String> mImages;
	private ArrayList<String> mThumbnailURLs;
	private DisplayImageOptions mOptions;

	private ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_view);
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			L.e(this, "extras is null");
			finish();
			return;
		}

		mOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.default_img)
				.showImageOnFail(R.drawable.default_img)
				.resetViewBeforeLoading().cacheInMemory(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		mImages = extras.getStringArrayList(EXTRAS_IMAGES);
		mThumbnailURLs = extras.getStringArrayList(EXTRAS_THUMBNAUL_URL);
		
		int position = extras.getInt(EXTRAS_POSITION, 0);
		tv_index = (TextView) findViewById(R.id.tv_index);
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int arg0) {
						setIndex(arg0);
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {
						// TODO Auto-generated method stub

					}
				});
		mViewPager.setAdapter(new ImagePagerAdapter(mImages));
		mViewPager.setCurrentItem(position, false);
		setIndex(position);
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private ArrayList<String> images;
		private LayoutInflater inflater;
		private int mCurrentPosition = -1;

		ImagePagerAdapter(ArrayList<String> images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			super.setPrimaryItem(container, position, object);
			if (mCurrentPosition == position)
				return;
			GalleryViewPager galleryContainer = ((GalleryViewPager) container);
			if (galleryContainer.mCurrentView != null)
				galleryContainer.mCurrentView.resetScale();

			mCurrentPosition = position;
			galleryContainer.mCurrentView = (TouchImageView)((View)object).findViewById(R.id.image);
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.image_pager_item,
					view, false);
			imageLayout.findViewById(R.id.layout).setOnClickListener(ImageViewActivity.this);
			final TextView save = (TextView) imageLayout
					.findViewById(R.id.save);
			final ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);
			final ProgressBar progressbar = (ProgressBar) imageLayout
					.findViewById(R.id.loading);
			final ImageView thumbnailview = (ImageView) imageLayout
					.findViewById(R.id.thumbnail);

			save.setTag(position);
			save.setOnClickListener(ImageViewActivity.this);
			imageView.setOnClickListener(ImageViewActivity.this);
			
			String image = images.get(position);
			if(mThumbnailURLs!=null&&mThumbnailURLs.size()>position&&!TextUtils.isEmpty(mThumbnailURLs.get(position))){
				

				ImageUtils.displayWebImage("http://pan.xici.com/"+mThumbnailURLs.get(position), thumbnailview);
				
			}else {

				ImageUtils.displayWebImage(image, thumbnailview);
			}
			

			imageLoader.displayImage(ImageUtils.getBigUrl(image), imageView,
					mOptions, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							progressbar.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							super.onLoadingFailed(imageUri, view, failReason);
							String message = null;
							switch (failReason.getType()) {
							case IO_ERROR:
								message = "图片无法加载";
								break;
							case DECODING_ERROR:
								message = "Image can't be decoded";
								break;
							case NETWORK_DENIED:
								message = "Downloads are denied";
								break;
							case OUT_OF_MEMORY:
								message = "Out Of Memory error";
								break;
							case UNKNOWN:
								message = "Unknown error";
								break;
							}
							Toast.makeText(ImageViewActivity.this, message,
									Toast.LENGTH_SHORT).show();

							progressbar.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							super.onLoadingComplete(imageUri, view, loadedImage);
							progressbar.setVisibility(View.GONE);
							imageView.setVisibility(View.VISIBLE);
							save.setVisibility(View.VISIBLE);
							thumbnailview.setVisibility(View.GONE);
						}
					});

			((ViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}

	private void setIndex(int position) {
		tv_index.setText(String.format("%d/%d", position + 1, mImages.size()));
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.save:
			int index = (Integer) v.getTag();

			try {

				File infile = imageLoader.getDiskCache().get(
						ImageUtils.getBigUrl(mImages.get(index)));
				L.d(this, "file:" + infile.getAbsolutePath());
				File path = new File(
						Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
						"xici");
				if (!path.exists()) {
					path.mkdirs();
				}

				String outfilepath = path.getAbsolutePath() + File.separator
						+ "xici-" + System.currentTimeMillis() + ".jpg";
				File outFile = new File(outfilepath);

				FileUtil.copy(new FileInputStream(infile), new FileOutputStream(
						outFile));
				Toast.makeText(this, "已保存图片到：" + path.getAbsolutePath(),
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
			}
			
			break;
		case R.id.layout:
		case R.id.image:
			finish();
			overridePendingTransition(0,R.anim.activity_exit);
			break;
		default:
			break;
		}
		

	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// do nothing
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


}
