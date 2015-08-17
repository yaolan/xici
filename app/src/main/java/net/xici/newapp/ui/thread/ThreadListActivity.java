package net.xici.newapp.ui.thread;

import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import net.xici.newapp.R;
import net.xici.newapp.app.Constants;
import net.xici.newapp.data.pojo.Board;
import net.xici.newapp.ui.base.BaseActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
/**
 * 版详情，帖子列表
 * @author bkmac
 *
 */
public class ThreadListActivity extends BaseActionBarActivity {



	@Override
	protected int setLayoutResourceIdentifier() {
		return R.layout.main_frame;
	}

	@Override
	protected int getTitleToolBar() {
		return R.string.app_name;
	}

	public static void start(Context context,Board Board)
	{
		Intent intent = new Intent(context,ThreadListActivity.class);
		intent.putExtra("board", Board);
		context.startActivity(intent);
	}
	
	public static void start(Context context,String action,int boardid,String boardname)
	{
		Intent intent = new Intent(action);
		intent.putExtra("boardname", boardname);
		intent.putExtra("boardid", boardid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			Bundle b = null;
			String action = getIntent().getAction(); 
			if(Constants.BOARD_SHORTCUT_ACTION.equals(action)||
					Constants.BOARD_MAINPAGE_ACTION.equals(action)||Intent.ACTION_MAIN.equals(action))
			{
				Board board = new Board();
				board.boardName = getIntent().getStringExtra("boardname");
				board.boardUrl = getIntent().getIntExtra("boardid", 0);
				b=new Bundle();
				b.putSerializable("board", board);
			}
			else {
				b=getIntent().getExtras();
			}
			ft.replace(R.id.main_frame, ThreadListFragment.newInstance(b),
					ThreadListFragment.class.getName());
			ft.commit();
		}
	}
	
	UMSocialService mController = null;
	public UMSocialService getUMSocialService(){
		if(mController==null){
			mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		}
		return mController;
	}
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    /**使用SSO授权必须添加如下代码 */
	    UMSsoHandler ssoHandler = getUMSocialService().getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}
}
