package net.xici.newapp.support.service;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.xici.newapp.app.Constants;
import net.xici.newapp.data.pojo.SplashPic;
import net.xici.newapp.support.request.ApiClient;
import net.xici.newapp.support.request.XiciException;
import net.xici.newapp.support.reservoir.Reservoir;
import net.xici.newapp.support.util.CacheUtil;
import net.xici.newapp.support.util.ImageUtils;
import net.xici.newapp.support.util.L;
import net.xici.newapp.support.util.XiciResponseHandler;
import android.app.IntentService;
import android.content.Intent;
/**
 * 获取广告图片
 * @author bkmac
 *
 */
public class LoadSplashPIcService extends IntentService{
	
	public LoadSplashPIcService() {
		this(LoadSplashPIcService.class.getSimpleName());
	}

	public LoadSplashPIcService(String name) {
		super(name);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		L.d(this, "onCreate");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
//		SplashPic splashPic = new SplashPic();
//		splashPic.img_url = "http://pic.sucaibar.com/pic/201307/05/333b4c44e8.jpg";
//		try {
//			Reservoir.put(Constants.CACHEKEY_SPLASHPIC, splashPic);
//			ImageLoader.getInstance().loadImageSync(splashPic.img_url, ImageUtils.DISYPLAY_OPTION_SPLASH_IMAGE);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
				
		ApiClient.getSplashPic(this, new XiciResponseHandler(){
			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				
				try {
					SplashPic splashPic = SplashPic.parse(arg0);
					if(splashPic!=null){
						
						Reservoir.put(Constants.CACHEKEY_SPLASHPIC, splashPic);
						
						//CacheUtil.saveObject(getApplicationContext(), splashPic, Constants.CACHEKEY_SPLASHPIC);
						ImageLoader.getInstance().loadImageSync(splashPic.img_url, ImageUtils.DISYPLAY_OPTION_SPLASH_IMAGE);
						
					}else {
						Reservoir.delete(Constants.CACHEKEY_SPLASHPIC);
					}
					
				} catch (XiciException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
		
	}

}
