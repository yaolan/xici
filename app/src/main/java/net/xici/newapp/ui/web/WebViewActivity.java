package net.xici.newapp.ui.web;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import net.xici.newapp.R;
import net.xici.newapp.ui.base.BaseActionBarActivity;


public class WebViewActivity extends BaseActionBarActivity {

	public static void start(Context context,String title,String url) {
		Intent intent = new Intent(context,WebViewActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("url", url);
		context.startActivity(intent);
	}
	

	public static void start(Context context,String title,String url,boolean refreshenable) {
		Intent intent = new Intent(context,WebViewActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("url", url);
		intent.putExtra("refreshenable", refreshenable);
		context.startActivity(intent);
	}
	
	public static Intent getIntent(Context context,String title,String url) {
		Intent intent = new Intent(context,WebViewActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("url", url);
		return intent;
	}

	WebViewFragment fragment;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			Bundle bundle = getIntent().getExtras();
			getActionBar().setTitle(bundle.getString("title"));
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			fragment = (WebViewFragment)getSupportFragmentManager().findFragmentByTag(WebViewFragment.class.getName());
			if(fragment==null){
				fragment = WebViewFragment.newInstance(getIntent().getExtras());
			}
			ft.replace(R.id.content, fragment,
					WebViewFragment.class.getName());
			ft.commit();
		}

		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && fragment!=null &&fragment.goback()) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected int setLayoutResourceIdentifier() {
		return R.layout.main_frame;
	}

	@Override
	protected int getTitleToolBar() {
		return R.string.app_name;
	}


}
