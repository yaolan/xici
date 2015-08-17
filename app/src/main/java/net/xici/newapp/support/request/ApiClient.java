package net.xici.newapp.support.request;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.text.TextUtils;
import net.xici.newapp.app.Constants;
import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.pojo.Account;
import net.xici.newapp.support.util.L;

public class ApiClient implements Constants,Constants.method{
	
	public static void cancel(Context context, boolean interrupt) {
		HttpUtil.cancel(context, interrupt);
	}
	
	public static void login(Context context,Account account,AsyncHttpResponseHandler handler)
	{
		
		if(account.logintype==3){
			login(context, "", account.partnerid, account.openid, handler);
		}else if(account.logintype==0){
			login(context, String.valueOf(account.userid), account.password, account.logintype, handler);
		}else {
			login(context, account.username, account.password, account.logintype, handler);
		}
	}
	
	/**
	 * 登录验证
	 * @param context
	 * @param username
	 * @param password
	 * @param handler
	 */
	public static void login(Context context,String username,String password,AsyncHttpResponseHandler handler)
	{
		RequestParams params = getparams();
		params.put(METHOD, USER_LOGIN);
		params.put("username", username);
		params.put("password", password);
		params.put("type", "1");
		HttpUtil.get(context, API_URL, params, handler);
	}
	/**
	 * 
	 * @param context
	 * @param username
	 * @param password
	 * @param type  0ID登录 1用户名登录
	 * @param handler
	 */
	public static void login(Context context,String username,String password,int type,AsyncHttpResponseHandler handler)
	{
		RequestParams params = getparams();
		params.put(METHOD, USER_LOGIN);
		params.put("username", username);
		params.put("password", password);
		params.put("type", String.valueOf(type));
		HttpUtil.get(context, API_URL, params, handler);
	}
	
	
	/**
	 * 
	 * @param context
	 * @param opentoken
	 * @param partnerid
	 * @param openkey
	 * @param username
	 * @param handler
	 */
	public static void login(Context context,String opentoken,
			String partnerid,String openid,AsyncHttpResponseHandler handler)
	{
		RequestParams params = getparams();
		params.put(METHOD, USER_LOGIN);
		params.put("username", openid);
		params.put("password", opentoken);
		params.put("opentoken", opentoken);
		params.put("partnerid", partnerid);
		params.put("type", "3");
		HttpUtil.get(context, API_URL, params, handler);
	}
	
	/**
	 * 注册
	 * @param context
	 * @param username
	 * @param password
	 * @param opentoken
	 * @param partner
	 * @param openid
	 * @param handler
	 */
	public static void register(Context context,String username,String password,String opentoken,
			String partner,String openid,AsyncHttpResponseHandler handler)
	{
		RequestParams params = getparams();
		params.put(METHOD, USER_REGISTER);
		params.put("uname", username);
		params.put("pwd", password);
		params.put("partner", partner);
		params.put("opentoken", opentoken);
		params.put("partnerid", openid);
		params.put("type", "3");
		HttpUtil.get(context, API_URL, params, handler);
	}
	
	/**
	 * 验证用户名
	 * @param context
	 * @param username
	 * @param handler
	 */
	public static void checkUsername(Context context,String username,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_CHECKUSERNAME);
		params.put("username", username);
		HttpUtil.get(context, API_URL, params, handler);
	}
	
	/**
	 * 获取手机验证码，token
	 * @param context
	 * @param phone
	 * @param handler
	 */
	public static void getcaptcha(Context context,String phone,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_PHONE_CAPTCHA);
		params.put("phone", phone);
		HttpUtil.get(context, API_URL, params, handler);
	}
	
	/**
	 * 
	 * @param context
	 * @param phone    手机号
	 * @param captcha  验证码
	 * @param token    上一步返回的token
	 * @param handler
	 */
	public static void verifycaptcha(Context context,String phone,String captcha,String token,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_VERIFY);
		params.put("phone", phone);
		params.put("captcha", captcha);
		params.put("token", token);
		HttpUtil.get(context, API_URL, params, handler);
	}
	 
	/**
	 * 
	 * @param context
	 * @param uername  用户名
	 * @param pwd      密码
	 * @param phone    手机号
	 * @param captcha  验证码
	 * @param token    上一步返回的token
	 * @param handler
	 * @param handler
	 */
	public static void registerByUsername(Context context,String username,String pwd
			,String phone,String captcha,String token,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_REGISTER);
		params.put("uname", username);
		params.put("pwd", pwd);
		params.put("phone", phone);
		params.put("captcha", captcha);
		params.put("token", token);
		params.put("ip", "192.168.1.1");
		HttpUtil.get(context, API_URL, params, handler);
	}
	
	/**
	 * 升级用户，获取验证码
	 * @param context
	 * @param phone
	 * @param handler
	 */
	public static void getUpdateCaptcha(Context context,String phone,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_CODE_VERIFY);
		params.put("phone", phone);
		HttpUtil.get(context, API_URL, params, handler);
	}
	
	/**
	 * 升级用户
	 * @param context
	 * @param phone
	 * @param handler
	 */
	public static void userUpdate(Context context,String phone,String captcha,long uid,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_UPDATE);
		params.put("phone", phone);
		params.put("captcha", captcha);
		params.put("uid", String.valueOf(uid));
		HttpUtil.get(context, API_URL, params, handler);
	}
	
	/**
	 * 第三方用户绑定
	 * @param context
	 * @param platform
	 * @param open_id
	 * @param user_id
	 * @param screen_name
	 * @param token
	 * @param ip
	 * @param handler
	 */
	public static void user_third_bind(Context context,String platform,String open_id,
			String user_id,String screen_name,String token,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_THIRD_BIND);
		params.put("platform", platform);
		params.put("open_id", open_id);
		params.put("user_id", user_id);
		params.put("screen_name", screen_name);
		params.put("token", token);
		params.put("ip", "192.168.1.1");
		HttpUtil.get(context, API_URL, params, handler);
	}
	
	
	
	public static void user_iconupload(Context context,String filepath,AsyncHttpResponseHandler handler)
	{
		RequestParams params = getparams();
		params.put(METHOD, USER_ICONUPLOAD);
		//文件
		if(!TextUtils.isEmpty(filepath))
		{
			File file = new File(filepath);
			try {
				params.put("file", file);
			} catch (FileNotFoundException e) {
				L.e(context, filepath+" NotFound");
			}
		}
		
		post(context, params, handler,true);
	}
	
	
	/**
	 * 用户收藏讨论版
	 * @param context
	 * @param uid
	 * @param type
	 * @param group
	 * @param stats
	 * @param handler
	 */
	public static void user_fav_list(Context context,long userid,String type,String group,int stats,AsyncHttpResponseHandler handler)
	{
		RequestParams params = getparams();
		
		if(XiciApp.account!=null)
		{
			params.put("token",String.valueOf(XiciApp.account.accesstoken));
			
			params.put("uid",String.valueOf(userid));
			
			params.put(METHOD,userid==XiciApp.account.userid?USER_FAV_LIST:USER_FAV_LIST_UNSIGN);
			
		}
		
		params.put("type", type);
		params.put("group", group);
		params.put("stats", String.valueOf(stats));
		get(context, params, handler, false);
	}
	/**
	 * 用户收藏帖子列表
	 * @param context
	 * @param handler
	 */
	public static void user_fav_list_doc(Context context,AsyncHttpResponseHandler handler){
		user_fav_list(context,XiciApp.account.userid,"doc","",0,handler);
	}
	
	/**
	 * 消息通知
	 * @param context
	 * @param handler
	 */
	public static void userPing(Context context,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_PING);
		if(XiciApp.account!=null){
			params.put("token",String.valueOf(XiciApp.account.accesstoken));
		}
		HttpUtil.get(context, API_URL, params, handler);
	}
	
	
	/**
	 * 帖子列表
	 * @param context
	 * @param bid
	 * @param page
	 * @param category
	 * @param tag
	 * @param sort
	 * @param needtoken
	 * @param handler
	 */
	public static void board_threads(Context context,int bid,int page,String category,String tag,int sort,
			boolean needtoken,AsyncHttpResponseHandler handler)
	{
		RequestParams params = getparams();
		params.put(METHOD, BOARD_THREADS);
		params.put("bid", String.valueOf(bid));
		params.put("pn", String.valueOf(PAGESIZE));
		params.put("page", String.valueOf(page));
		params.put("category", category);
		params.put("tag", tag);
		params.put("sort", String.valueOf(sort));
		if(needtoken)
		{
			get(context, params, handler, true);
		}
		else {
			params.put("uid", "0");
			get(context, params, handler);
		}
	}
	/**
	 * 看贴
	 * @param context
	 * @param bid
	 * @param page
	 * @param tid
	 * @param person
	 * @param needtoken
	 * @param handler
	 */
	public static void doc_view(Context context,int bid,int page,long tid,String person,
			boolean needtoken,AsyncHttpResponseHandler handler)
	{
		RequestParams params = getparams();
		params.put(METHOD, DOC_VIEW);
		params.put("bid", String.valueOf(bid));
		params.put("pn", String.valueOf(PAGESIZE));
		params.put("page", String.valueOf(page));
		params.put("tid", String.valueOf(tid));
		params.put("person", person);
		if(needtoken)
		{
			get(context, params, handler, true);
		}
		else {
			params.put("uid", "0");
			get(context, params, handler);
		}
	}
	/**
	 * 讨论版发贴
	 * @param context
	 * @param bid
	 * @param content
	 * @param title
	 * @param filepath
	 * @param handler
	 */
	public static void doc_put(Context context,int bid,String content,String title,String filepath,AsyncHttpResponseHandler handler)
	{
		RequestParams params = getparams();
		params.put(METHOD, DOC_PUT);
		params.put("bid", String.valueOf(bid));
		params.put("type", DEV_TYPE);
		params.put("content", content);
		params.put("title", title);
		//文件
		if(!TextUtils.isEmpty(filepath))
		{
			File file = new File(filepath);
			try {
				params.put("file", file);
			} catch (FileNotFoundException e) {
				L.e(content, filepath+" NotFound");
			}
		}
		
		post(context, params, handler,true);
	}
	/**
	 * 发帖
	 * @param context
	 * @param bid
	 * @param content
	 * @param title
	 * @param filepath
	 * @param handler
	 * @return
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public static String doc_put(int bid,String content,String title) throws HttpException, IOException
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put(METHOD, DOC_PUT);
		params.put("bid", String.valueOf(bid));
		params.put("type", DEV_TYPE);
		params.put("content", content);
		params.put("title", title);
		if(XiciApp.account!=null)
		{
			params.put("uid",String.valueOf(XiciApp.account.userid));
			params.put("token",String.valueOf(XiciApp.account.accesstoken));
		}
		return HttpUtil.postRequest(API_URL, params);
	}
	/**
	 * 帖子回复
	 * @param context
	 * @param bid
	 * @param content
	 * @param tid
	 * @param userid
	 * @param filepath
	 * @param handler
	 */
	public static void doc_reply(Context context,int bid,String content,int tid,long userid,String filepath,AsyncHttpResponseHandler handler)
	{
		RequestParams params = getparams();
		params.put(METHOD, DOC_REPLY);
		params.put("bid", String.valueOf(bid));
		params.put("content", content);
		params.put("tid", String.valueOf(tid));
		params.put("type", DEV_TYPE);
		if(userid!=0)
		{
			params.put("userid", String.valueOf(userid));
		}
		//文件
		if(!TextUtils.isEmpty(filepath))
		{
			File file = new File(filepath);
			try {
				params.put("file", file);
			} catch (FileNotFoundException e) {
				L.e(context, filepath+" NotFound");
			}
		}
		
		post(context, params, handler,true);
	}
	
	public static String doc_reply(int bid,String content,int tid,long userid) throws HttpException, IOException
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put(METHOD, DOC_REPLY);
		params.put("bid", String.valueOf(bid));
		params.put("content", content);
		params.put("tid", String.valueOf(tid));
		params.put("type", DEV_TYPE);
		if(userid!=0)
		{
			params.put("userid", String.valueOf(userid));
		}
		if(XiciApp.account!=null)
		{
			params.put("uid",String.valueOf(XiciApp.account.userid));
			params.put("token",String.valueOf(XiciApp.account.accesstoken));
		}
		return HttpUtil.postRequest(API_URL, params);
	}
	
	/**
	 * 收藏帖子
	 * @param context
	 * @param title
	 * @param tid
	 * @param handler
	 */
	public static void doc_collect(Context context,String title,int tid,AsyncHttpResponseHandler handler){
		
		RequestParams params = getparams();
		params.put(METHOD, DOC_COLLECT);
		params.put("title", title);
		params.put("tid", String.valueOf(tid));
		get(context, params, handler,true);
	}
	
	/**
	 * 取消收藏帖子
	 * @param context
	 * @param title
	 * @param tid
	 * @param handler
	 */
	public static void doc_collectdel(Context context,int tid,AsyncHttpResponseHandler handler){
		
		RequestParams params = getparams();
		params.put(METHOD, DOC_COLLECTDEL);
		params.put("tid", String.valueOf(tid));
		get(context, params, handler,true);
	}
	/**
	 * 
	 * @param context
	 * @param tid
	 * @param tuserid
	 * @param index 楼层
	 * @param flag 0-取消点赞；1-点赞
	 * @param handler
	 */
	public static void doc_praise(Context context,long tid,long tuserid,int index,int flag,
			AsyncHttpResponseHandler handler){
		
		RequestParams params = getparams();
		params.put(METHOD, DOC_PRAISE);
		params.put("tid", String.valueOf(tid));
		params.put("tuserid", String.valueOf(tuserid));
		params.put("index", String.valueOf(index));
		params.put("flag", String.valueOf(flag));
		if(XiciApp.getAccount()!=null){
			params.put("uname", XiciApp.getAccount().username);
		}
		
		post(context, params, handler,true);
	}
	
	/**
	 * 搜版
	 * @param context
	 * @param page
	 * @param tid
	 * @param handler
	 */
	public static void search_board(Context context,int page,String key,int sort,AsyncHttpResponseHandler handler)
	{
		RequestParams params = getparams();
		params.put(METHOD, SEARCH_BOARD);
		//params.put("pn", String.valueOf(PAGESIZE));
		params.put("page", String.valueOf(page));
		params.put("key", key);
		params.put("sort", String.valueOf(sort));
		get(context, params, handler);
	} 
	
	public static void change_board_subscribe_state(boolean subscribed,Context context,int bid,String group,AsyncHttpResponseHandler handler)
	{
		if(subscribed)
		{
			board_unsubscribe(context,bid,group,handler);
		}
		else {
			board_subscribe(context,bid,group,handler);
		}
	}
	
	/**
	 * 讨论版预定
	 * @param context
	 * @param bid
	 * @param group
	 * @param handler
	 */
	public static void board_subscribe(Context context,int bid,String group,AsyncHttpResponseHandler handler)
	{
		RequestParams params = getparams();
		params.put(METHOD, BOARD_SUBSCRIBE);
		params.put("bid", String.valueOf(bid));
		params.put("group", group);
		get(context, params, handler,true);
	} 
	/**
	 * 讨论版退订
	 * @param context
	 * @param bid 讨论版id
	 * @param group 分组名
	 * @param handler
	 */
	public static void board_unsubscribe(Context context,int bid,String group,AsyncHttpResponseHandler handler)
	{
		RequestParams params = getparams();
		params.put(METHOD, BOARD_UNSUBSCRIBE);
		params.put("bid", String.valueOf(bid));
		params.put("group", group);
		get(context, params, handler,true);
	} 
	
	/**
	 * 讨论版详情
	 * @param context
	 * @param bid
	 * @param handler
	 */
	public static void board_info(Context context,int bid,AsyncHttpResponseHandler handler)
	{
		RequestParams params = getparams();
		params.put(METHOD, BOARD_INFO);
		params.put("bid", String.valueOf(bid));
		params.put("items", "admin,bd_script,Bd_name,users,stats,Subboard");
		get(context, params, handler,false);
	} 
	
	
	
	/**
	 * 删除帖子
	 */
	public static void doc_belete(Context context,int bid,String tids,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, DOC_DELETE);
		params.put("bid", String.valueOf(bid));
		params.put("tids", tids);
		get(context, params, handler,true);
	}
	
	/**
	 * 置顶帖子
	 * @param context
	 * @param type 0取消置顶 1置顶推荐，2 顶一 ，3 顶二，4 顶三，5 顶四，6 顶 7
	 * @param bid
	 * @param tids
	 * @param handler
	 */
	public static void doc_recomm(Context context,int type,int bid,
			String tids,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, DOC_RECOMM);
		params.put("type", String.valueOf(type));
		params.put("bid", String.valueOf(bid));
		params.put("tids", tids);
		get(context, params, handler,true);
	}
	
	public static void doc_cool(Context context,int type,int bid,
			String tids,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, DOC_COOL);
		params.put("type", String.valueOf(type));
		params.put("bid", String.valueOf(bid));
		params.put("tids", tids);
		get(context, params, handler,true);
	}
	
	
	/**
	 * 删除回复
	 * @param context
	 * @param bid
	 * @param tid
	 * @param floors 逗号分隔，从1开始，楼层数，不是index
	 * @param handler
	 */
	public static void doc_reply_delete(Context context,int bid,
			long tid,String floors,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, DOC_REPLY_DELETE);
		params.put("bid", String.valueOf(bid));
		params.put("tid", String.valueOf(tid));
		params.put("floors", floors);
		get(context, params, handler,true);
	}
	
	
	/**
	 * 飞语
	 * @param context
	 * @param handler
	 */
	public static void user_mails(Context context,int page,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_MAILS);
		params.put("pn", String.valueOf(PAGESIZE));
		params.put("page", String.valueOf(page));
		params.put("filter", String.valueOf(2));
		params.put("stats", String.valueOf(0));
		get(context, params, handler,true);
	}
    /**
     * 飞语联系人列表
     * @param context
     * @param page
     * @param handler
     */
	public static void user_appmails(Context context,int page,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_APPMAILS);
		params.put("pn", String.valueOf(PAGESIZE));
		params.put("page", String.valueOf(page));
		params.put("filter", String.valueOf(2));
		params.put("stats", String.valueOf(1));
		params.put("type", "inbox");
		get(context, params, handler,true);
	}
	
	/**
	 * 飞语对话列表
	 * @param context
	 * @param username
	 * @param handler
	 */
	public static void user_appmails_dialogue(Context context,int page,String username,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_APPMAILS_DIALOGUE);
		params.put("username", username);
		params.put("page", String.valueOf(page));
		params.put("pn", Constants.PAGESIZE);
		get(context, params, handler,true);
	}
	
	/**
	 * 飞语
	 * @param context
	 * @param mid  飞语id
	 * @param handler
	 */
	public static void user_mail(Context context,int mid,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_MAIL);
		params.put("mid", String.valueOf(mid));
		get(context, params, handler,true);
	}
	
	
	/**
	 * 发送飞语
	 * @param context
	 * @param handler
	 */
	public static void user_sendmail(Context context,String username,String content,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_SEND_MAIL);
		params.put("username", username);
		params.put("content", content);
		get(context, params, handler,true);
	}
	/**
	 * 删除飞语
	 * @param context
	 * @param handler
	 */
	public static void user_mail_del(Context context,String username,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_MAILS_DEL);
		params.put("username", username);
		get(context, params, handler,true);
	}
	
	/**
	 * 胡同口
	 * @param context
	 * @param handler
	 */
	public static void mainPage(Context context,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_MAINPAGE);
		get(context, params, handler,true);
	}
	
	/**
	 * 我的发帖
	 * @param context
	 * @param handler
	 */
	public static void user_threads(Context context,int page,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_THREADS);
		params.put("page",String.valueOf(page));
		params.put("filter",String.valueOf(2));
		params.put("stats",String.valueOf(0));
		get(context, params, handler,true);
	}
	
	public static void user_threads(Context context,long userid,int page,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		
		if(XiciApp.account!=null)
		{
			params.put("token",String.valueOf(XiciApp.account.accesstoken));
			
			params.put("uid",String.valueOf(userid));
			
			params.put(METHOD,userid==XiciApp.account.userid?USER_THREADS:USER_THREADS_UNSIGN);
			
		}
		params.put("page",String.valueOf(page));
		params.put("filter",String.valueOf(2));
		params.put("stats",String.valueOf(0));
		params.put("pn",String.valueOf(Constants.PAGESIZE));
		HttpUtil.get(context, API_URL, params, handler);
		//get(context, params, handler,true);
	}
	
	
	/**
	 * 获取板块分类
	 * @param context
	 * @param handler
	 */
	public static void board_category(Context context,AsyncHttpResponseHandler handler){
		//HttpUtil.get(context, BOARD_CATEGORIES, handler);
		RequestParams params = getparams();
		params.put(METHOD, NEW_CATEGORY);
		get(context, params, handler,false);
	}
	
	public static void board_category_by_id(Context context,int id,int page,int pagesize,AsyncHttpResponseHandler handler){
		
		RequestParams params = getparams();
		if(id==0){
			params.put(METHOD, NEW_HOT);
			params.put("cmsid", String.valueOf(1));
		}else {
			params.put(METHOD, NEW_CATEGORY_BOARD);
			params.put("classifyid", String.valueOf(id));
		}
		params.put("page", String.valueOf(page));
		params.put("perpage", String.valueOf(pagesize));
		get(context, params, handler,false);
	}
	
	public static void circle_search(Context context,int page,String key,int pagesize,AsyncHttpResponseHandler handler){
		RequestParams params = new RequestParams();
		params.put("q", key);
		params.put("page", String.valueOf(page));
		params.put("page_size", String.valueOf(pagesize));
		params.put("m","api");
		HttpUtil.get(context, "http://ss.xici.net/", params,handler);
	}
	
	/**
	 * 获取xnb token
	 * @param context
	 * @param username
	 * @param password
	 * @param handler
	 */
	public static void xnb_token(Context context,String username,String password,AsyncHttpResponseHandler handler){
		RequestParams params = new RequestParams();
		params.put(METHOD, XNB_TOKEN);
		params.put("username", username);
		params.put("password", password);
		get(context, params, handler, false);
	}
	
	public static String xnb_token(String username,String password) throws HttpException, IOException{
		Map<String, String> params = new HashMap<String, String>();
		params.put(METHOD, XNB_TOKEN);
		params.put("username", username);
		params.put("password", password);
		return HttpUtil.postRequest(API_URL, params);
	}
	
	public static String xnb_token(long userid) throws HttpException, IOException{
        return xnb_token("", "", 2, userid);
	}
	
	public static String xnb_token(String username,String password,int type,long userid) throws HttpException, IOException{
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(METHOD, XNB_TOKEN);
		if(type==2){
			
			params.put("type", "2");
			params.put("userid", String.valueOf(userid));
			
		}else {
		
			params.put("username", username);
			params.put("password", password);
			
		}
		
		return HttpUtil.postRequest(API_URL, params);
	}
	
	/**
	 * 上传图片文件到xnb
	 * @param token
	 * @param file
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String xnb_upload(String token,String file) throws HttpException, IOException{
		Map<String, String> params = new HashMap<String, String>();
		params.put("from", "xnb");
		params.put("token", token);
		params.put(HttpUtil.FILE_KEY, file);
		return HttpUtil.postRequest(XNB_UPLOAD_URL, params);
	}
	
	/**
	 * 获取闪屏图片
	 * @param context
	 * @param handler
	 */
	public static void getSplashPic(Context context,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, CMS_GETAPPURL);
		HttpUtil.get(context, API_URL, params, handler);
	}
	
	/**
	 * 获取广告条图片
	 * @param context
	 * @param handler
	 */
	public static void getTopPic(Context context,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, AD_PICLIST);
		HttpUtil.get(context, AD_PIC_URL, params, handler);
	}
	
	/**
	 * 获取应用版本信息
	 * @param context
	 * @param version
	 * @param handler
	 */
	public static void getAppVersionInfo(Context context,String version,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, APPCONFIG);
		params.put("type", "android");
		params.put("id", "1");
		params.put("version", version);
		HttpUtil.get(context, MASTER_3G_URL, params, handler);
	}
	
	/**
	 * 跟帖
	 * @param context
	 * @param type
	 * @param page
	 * @param pagesize
	 * @param handler
	 */
	public static void userAtlist(Context context,String type,int page,AsyncHttpResponseHandler handler){
		RequestParams params = getparams();
		params.put(METHOD, USER_ATLIST);
		params.put("type", type);
		params.put("page", String.valueOf(PAGESIZE) );
		params.put("pn",String.valueOf(page));
		get(context, params, handler,true);
	}
	
	

	
	
	private static void get(Context context,
			RequestParams params, AsyncHttpResponseHandler handler)
	{
		get(context, params, handler,false);
	}
	
	private static void get(Context context,
			RequestParams params, AsyncHttpResponseHandler handler,boolean needtoken)
	{
		if(needtoken&&XiciApp.account!=null)
		{
			params.put("uid",String.valueOf(XiciApp.account.userid));
			params.put("token",String.valueOf(XiciApp.account.accesstoken));
		}
		HttpUtil.get(context, API_URL, params, handler);
	}
	
	private static void post(Context context,
			RequestParams params, AsyncHttpResponseHandler handler)
	{
		post(context, params, handler,false);
	}
	
	private static void post(Context context,
			RequestParams params, AsyncHttpResponseHandler handler,boolean needtoken)
	{
		if(needtoken&&XiciApp.account!=null)
		{
			params.put("uid",String.valueOf(XiciApp.account.userid));
			params.put("token",String.valueOf(XiciApp.account.accesstoken));
		}
		HttpUtil.post(context, API_URL, params, handler);
	}
	
	public static RequestParams getparams()
	{
		RequestParams params = new RequestParams();
		params.put("dev", DEV);
		params.put("ver", VER);
		return params;
	}
	

}
