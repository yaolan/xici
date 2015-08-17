package net.xici.newapp.ui.thread;

import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import net.xici.newapp.R;
import net.xici.newapp.data.pojo.Thread;
import net.xici.newapp.ui.base.BaseActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
/**
 * 帖子详情，回复列表
 * @author bkmac
 *
 */
public class FloorListActivity extends BaseActionBarActivity {
	
	FloorListFragment fragment;

	@Override
	protected int setLayoutResourceIdentifier() {
		return R.layout.main_frame;
	}
	@Override
	protected int getTitleToolBar() {
		return R.string.app_name;
	}
	public static void start(Context context,int boardid,Thread thread)
	{
		context.startActivity(getIntent(context,boardid,thread));
	}
	
	public static Intent getIntent(Context context,int boardid,Thread thread){
		
		return getIntent(context, boardid, thread, -1);
	}
	
	public static Intent getIntent(Context context,int boardid,Thread thread,int floorfollowindex){
		
		Intent intent = new Intent(context,FloorListActivity.class);
		intent.putExtra("boardid", boardid);
		intent.putExtra("thread", thread);
		intent.putExtra("floorfollowindex", floorfollowindex);
		return intent;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			fragment = FloorListFragment.newInstance(getIntent().getExtras());
			ft.replace(R.id.main_frame, fragment,
					FloorListFragment.class.getName());
			ft.commit();
		}
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0&&fragment!=null) { // 按下的如果是BACK，同时没有重复
			// do something here
			boolean result  = fragment.onkeyback();
			if(result){
				return result;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    /**使用SSO授权必须添加如下代码 */
	    UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}
	

}