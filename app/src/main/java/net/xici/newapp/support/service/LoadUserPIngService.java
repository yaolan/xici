package net.xici.newapp.support.service;

import net.xici.newapp.data.pojo.UserPing;
import net.xici.newapp.support.request.ApiClient;
import net.xici.newapp.support.request.XiciException;
import net.xici.newapp.support.util.L;
import net.xici.newapp.support.util.XiciResponseHandler;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
/**
 * 获取用户未读消息数字
 * @author bkmac
 *
 */
public class LoadUserPIngService extends IntentService{
	
	public LoadUserPIngService() {
		this(LoadUserPIngService.class.getSimpleName());
	}

	public LoadUserPIngService(String name) {
		super(name);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		L.d(this, "onCreate");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		handler.removeMessages(MESSAGE_USERPING);
		handler.sendEmptyMessageDelayed(MESSAGE_USERPING, 2000);
	}
	
	private final static int MESSAGE_USERPING = 1;
	
	private  Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_USERPING:
				doUserPing();
				break;

			default:
				break;
			}
		};
		
	};
	
	private void doUserPing(){
		
		ApiClient.userPing(this, new XiciResponseHandler(){
			
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				
				try {
					
					UserPing userping = UserPing.parse(arg0);
					if(userping!=null){
						UserPing.postevent(userping);
					}
					
				} catch (XiciException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
	}

}
