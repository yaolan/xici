package net.xici.newapp.app;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.anupcowkur.reservoir.Reservoir;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import net.xici.newapp.data.dao.AccountDao;
import net.xici.newapp.data.pojo.Account;
import net.xici.newapp.support.util.MyTools;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class XiciApp extends Application {

	private static Context sContext;
	private static XiciApp mInstance = null;

	public static net.xici.newapp.data.pojo.Account account;
	public static AccountDao accountDao;

	public static String request_token = "";
	public static String xnb_token = "";

	public static boolean loadboadfragment = false;

	public static boolean relogin = false;

	public static boolean showSplashPic = false;

	private static DisplayMetrics dm = new DisplayMetrics();

	//private PushAgent mPushAgent;



	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		sContext = getApplicationContext();
		MyTools.setContext(sContext);
		accountDao = new AccountDao(sContext);
		initImageLoader(getApplicationContext());
		account = accountDao.getdefaultAccount();

		initCache();

//		mPushAgent = PushAgent.getInstance(this);
//		mPushAgent.setDebugMode(false);
//		/**
//		 * 该Handler是在BroadcastReceiver中被调用，故
//		 * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
//		 * */
//		UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
//
//			@Override
//			public void openActivity(Context context, UMessage msg) {
//
//				if ("net.xici.newapp.ui.thread.FloorListActivity"
//						.equals(msg.activity)) {
//
//					if (msg.extra.containsKey("tid")) {
//
//						net.xici.newapp.data.pojo.Thread thread = new net.xici.newapp.data.pojo.Thread();
//						thread.tid = Long.parseLong(msg.extra.get("tid"));
//						Intent intent = FloorListActivity.getIntent(context, 0,
//								thread);
//						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						context.startActivity(intent);
//
//					}
//
//				} else if ("net.xici.newapp.ui.web.UriLauncherActivity"
//						.equals(msg.activity)) {
//
//					if (msg.extra.containsKey("url")) {
//
//						String u = msg.extra.get("url");
//
//						if (msg.extra.containsKey("title")) {
//
//
//							String t = msg.extra.get("title");
//
//							Intent intent = WebViewActivity.getIntent(context, t, u);
//							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//							context.startActivity(intent);
//
//						} else {
//
//							try {
//
//								Uri data = Uri.parse(u);
//								Intent intent = UriLauncherActivity.getIntent(context, data);
//								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//								context.startActivity(intent);
//
//							} catch (Exception e) {
//								// TODO: handle exception
//							}
//
//						}
//
//					}
//
//				}
//
//			}
//
//		};
//		mPushAgent.setNotificationClickHandler(notificationClickHandler);
	}

	public static Context getContext() {
		return sContext;
	}

	public static XiciApp getInstance() {
		return mInstance;
	}

	public static Account getAccount() {
		return account;
	}

	public static long getAccountId() {
		Account account = getAccount();
		if (account == null) {
			return 0;
		} else {
			return account.userid;
		}

	}

	public static boolean islogined() {
		return account != null && account.userid != 0;
	}

	public void initImageLoader(Context context) {
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}

	// 缓存
	private void initCache() {

		// String cachePath = sContext.getCacheDir().getPath();
		// File cacheFile = new File(cachePath + File.separator +
		// Constants.PACKAGE_NAME);
		//
		// try {
		//
		// DiskCache diskCache = new DiskCache(cacheFile,
		// Constants.VERSION_CODE, 1024 * 1024 * 1);
		//
		// cacheManager = CacheManager.getInstance(diskCache);
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		try {
			Reservoir.init(this, 1024 * 1024);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isSDCardAvailable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public File getUploadImageOutputFile() {
		return new File(getWorkspaceImages(), Constants.UPLOAD_IMAGE_FILE);
	}

	public File getWorkspace() {
		File file = Environment.getExternalStorageDirectory();
		File workspace;

		workspace = new File(file, Constants.WORKSPACE);

		if (!workspace.exists()) {
			workspace.mkdirs();
		}
		return workspace;
	}

	public File getWorkspaceImages() {
		File workspace = getWorkspace();
		File imageWorkspace = new File(workspace, Constants.WORKSPACE_IMAGES);
		if (!imageWorkspace.exists()) {
			imageWorkspace.mkdirs();
		}
		return imageWorkspace;
	}

	public void savetoken(String accesstoken) {
		if (account != null) {
			account.accesstoken = accesstoken;
			saveaccount();
			// new Thread(){
			//
			// public void run() {
			// CacheUtil.saveObject(getApplicationContext(),
			// XiciApp.account, Constants.ACCOUNT);
			// };
			//
			// }.start();
		}
	}

	public void saveaccount() {
		accountDao.insertAccount(account);
	}

	public static DisplayMetrics getDisplayMetrics() {
		return dm;
	}

	private static Set<String> first = new HashSet<String>();

	public static void setfirst(String tag) {
		first.add(tag);
	}

	public static boolean getfirst(String tag) {
		if (first.contains(tag)) {
			return false;
		}
		return true;
	}

}
