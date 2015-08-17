package net.xici.newapp.app;


import android.app.Activity;

public interface Constants {
	
	
	public static int PAGESIZE = 15;
	public static int MINIPAGESIZE = 1;
	public String ACCOUNT = "account";
	
	public String WORKSPACE = "/xici/";
	public String WORKSPACE_IMAGES = "images/";
	public String WORKSPACE_VOIDE = "voide/";
	public String UPLOAD_IMAGE_FILE = "upload.jpg";
	
    public static String HOST = "http://apps.xici.net";
	public static String URL_USER_FORGET_3G = "http://3g.xici.net/?method=user.forget.step1";
	//public static String HOST = "http://apps.mobile.3g.ixici.info/";
	
	public static String DOMAIN = ".xici.net";
	
	//public static String URL_AD_SPLASH = "http://apps.3g.ixici.info/xiapi.php";
	//http://apps.xici.net/xiapi.php?&method=cms.getappurl
	
	
	public static String API_URL = HOST+"/xiapi.php";
	
	public static String MASTER_3G_URL = HOST+"/3gmaster";
	
	public static String AD_PIC_URL = "http://apps.xici.net/3gmaster/";
	
	public static String XNB_UPLOAD_URL = "http://upload.xici.com";
	
	public static String DEV_TYPE = "AND";
	
	public static String DEV = "xici-and";
	
	public static String VER = "1.6.4";
	
	public static int VERSION_CODE = 10604;

	public static String PACKAGE_NAME = "net.xici.newapp";
	
	public static int ATTACHMENT_MAX_SIZE = 9;  //帖子附件数
	
	public static String X_Request_Token = "X-Request-Token";
	
	public static String METHOD = "method";
	
	public static String PARTNERID_QQ = "qq";
	
	public static String PARTNERID_WEIBO = "weibo";
	
	public static String PARTNERID_WEIXIN = "weixin";
	
	
	//qq
	public static final String QQ_APP_ID = "100402915";
	
	public static final String QQ_APP_KEY = "341470077a5d7f07372a87f9b1738617";
	
    public static final String WEIBO_APP_KEY  = "4212187452";

   // public static final String WEIBO_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    
    public static final String WEIBO_REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
    

    public static final String WEIBO_SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
    

    public static final String WEIXIN_APPID = "wx68cdefd85e05cfa9";

    public static final String WEIXIN_APPSECRET = "ccc09c4e245a05b1db55477dcc9f2909";
	
	public interface method{
		
		public static String USER_LOGIN = "user.login"; //登录

		//注册绑定
		public static String USER_PHONE_CAPTCHA = "user.phone.captcha";//发送手机验证码
		
		public static String USER_VERIFY = "user.verify";//验证码验证

		public static String USER_REGISTER = "user.register"; //注册
		
		public static String USER_THIRD_BIND = "user.third.bind"; //绑定第三方帐号
		
		//升级真实用户
		public static String USER_CODE_VERIFY  = "user.code.verify";
		
		public static String USER_UPDATE  = "user.update";
		
		public static String USER_AUTH = "user.auth"; //token认证
		
		public static String USER_CHECKUSERNAME = "checkUsername"; //验证用户名是否重复
		
		public static String USER_ICONUPLOAD = "user.iconupload"; //修改头像
		
		public static String USER_FAV_LIST = "user.fav.list"; //用户收藏夹列表
		public static String USER_FAV_LIST_UNSIGN = "user.fav.list.unsign"; //用户收藏夹列表
		
		public static String USER_FAV_GROUP = "user.fav.group"; //用户收藏夹分组
		
		public static String USER_MAILS = "user.mails"; //用户飞语

		public static String USER_APPMAILS = "user.appmails"; //飞语对话用户列表
		
		public static String USER_APPMAILS_DIALOGUE = "user.appfeiyu.dialogue"; //飞语对话
		
		public static String USER_MAIL = "user.mail"; //飞语详细
		
		public static String USER_SEND_MAIL = "user.sendmail"; //发送飞语

		public static String USER_MAILS_DEL = "user.mails.del"; //删除飞语
		
		public static String USER_THREADS = "user.threads"; //用户发帖列表

		public static String USER_THREADS_UNSIGN = "user.threads.unsign"; //用户发帖列表

		public static String USER_PING = "user.ping"; //消息通知
		
		public static String BOARD_INFO = "board.info"; //讨论版信息
		
		public static String BOARD_SUBSCRIBE = "board.subscribe"; //预定讨论版
		
		public static String BOARD_UNSUBSCRIBE = "board.unsubscribe";//讨论版退订
		
		public static String BOARD_THREADS = "board.threads";//帖子列表
		
		public static String BOARD_BLACKLIST = "board.blacklist";//讨论版黑名单
		
		public static String DOC_COOL = "doc.cool";//帖子加酷
		
		public static String DOC_DELETE = "doc.delete";//删除帖子
		
		public static String DOC_RECOVER = "doc.recover";//帖子回收
		
		public static String DOC_RECOMM = "doc.recomm";//帖子置顶
		
		public static String DOC_VIEW = "doc.view";//帖子明细
		
		public static String DOC_PUT = "doc.put";//发贴
		
		public static String DOC_REPLY = "doc.reply";//回帖
		
		public static String DOC_PRAISE = "doc.praise";//回帖
		
		public static String DOC_REPLY_DELETE = "doc.reply.delete";//删除回复
		
		public static String DOC_REPLY_RECOVER = "doc.reply.recover";//恢复删除的回复
		
		public static String DOC_COLLECT = "doc.collect"; //帖子收藏
		
		public static String DOC_COLLECTDEL = "doc.collectdel"; //帖子取消收藏
		
		public static String SEARCH_BOARD = "search.board";//搜版
		
		public static String SEARCH_DOC = "search.doc";//搜帖子
		
		public static String USER_MAINPAGE = "user.mainpage";//胡同口
		
		public static String NEW_CATEGORY = "new.category";//板块分类
		
		public static String NEW_CATEGORY_BOARD = "new.category.board";//分裂详情
		
		public static String NEW_HOT = "new.hot";//热门分类
		
		public static String XNB_TOKEN = "xnb.token";//获取xnb token
		
		
		public static String AD_SPLASH = "3gTopPic";//广告闪屏
		public static String AD_PICLIST = "3gPicList";//广告条
		
		
		public static String APPCONFIG = "appConfig";//版本信息
		
		public static String USER_ATLIST = "user.atlist";//跟帖
		
		public static String CMS_GETAPPURL = "cms.getappurl";//闪屏
		
		
		
	}
	
	public static String XICICMS_HOST = "http://api.xici.com/v1/";//xici cms
	public static String BOARD_CATEGORIES = XICICMS_HOST+"cms/categories";
	public static String BOARD_CATEGORIES_BYID = XICICMS_HOST+"cms/categories/%d/circles";
	public static String CIRCLES_SEACH = XICICMS_HOST+"search/circles";
	
	
	
	public final static String BOARD_SHORTCUT_ACTION = "net.xici.newapp.board.shortcut";
	public final static String BOARD_MAINPAGE_ACTION = "net.xici.newapp.board.mainpage";
	public final static String BOARD_VIEW_ACTION = "net.xici.newapp.board.VIEW";
	public final static String THREAD_VIEW_ACTION = "net.xici.newapp.thread.VIEW";
	public final static String HOME_EXIT = "net.xici.newapp.home.exit";
	
	public final static int REQUEST_CODE_LOGIN = 100; 
	public final static int REQUEST_CODE_STORY = 101; 
	public  final static  int ACTIVITY_REQUEST_CODE_PICTURE_LIBRARY = 1;
	public  final static  int ACTIVITY_REQUEST_CODE_TAKE_PHOTO = 2;
	public  final static  int ACTIVITY_REQUEST_CODE_EDIT_PHOTO = 3;
	
	public  final static  int ACTIVITY_REQUEST_CODE_CHANGE_AVATAR = 4;
	
	//第三方登录未注册，返回
	public final static int RESULT_USER_OPENLOGIN_UNBIND_ERROR = Activity.RESULT_FIRST_USER+1;
	
	public final static int  REQUEST_USERUPDATE = 10;
	
	//cache
	public final static String CACHEKEY_SPLASHPIC = "splashpic";
	public final static String CACHEKEY_TOPPIC = "toppic";

}
