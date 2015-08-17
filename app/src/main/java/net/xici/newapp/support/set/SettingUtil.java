package net.xici.newapp.support.set;

import android.content.Context;

import com.google.gson.Gson;

import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.pojo.UserPing;
import net.xici.newapp.support.util.JsonUtils;


public class SettingUtil {
	
	private SettingUtil() {

	}
	
	public static void setAccountInit(long accountid) {
		SettingHelper.setEditor(getContext(), "init_"+accountid, true);
	}
	
	public static boolean getAccountInit(long accountid){
		return SettingHelper.getSharedPreferences(getContext(), "init_"+accountid, false);
	}
	
	public static boolean getShowWelcome(){
		return SettingHelper.getSharedPreferences(getContext(), "showwelcome", false);
	}
	
	public static void setShowWelcome(boolean show){
		SettingHelper.setEditor(getContext(), "showwelcome", show);
	}
	
	public static boolean getShowImage(){
		return !SettingHelper.getSharedPreferences(getContext(), "showimage", false);
	}
	
	public static int getupdatePhotoQuality(){
		return SettingHelper.getSharedPreferences(getContext(), "update_photo_quality", 1);
	}
	
	public static int getTestSizePost(){
		return SettingHelper.getSharedPreferences(getContext(), "text_size_post", 0);
	}
	
	public static boolean getPushEnable(){
		return SettingHelper.getSharedPreferences(getContext(), "push_switch", true);
	}
	
	private static Context getContext() {
		return XiciApp.getContext();
	}
	
	public static void setAccountMessageCount(long accountid,UserPing userping) {
		Gson gson = new Gson();
		
		SettingHelper.setEditor(getContext(), "msgcount_"+accountid, gson.toJson(userping));
	}
	
	public static UserPing getAccountMessageCount(long accountid){
		String json = SettingHelper.getSharedPreferences(getContext(), "msgcount_"+accountid, "");
		return JsonUtils.fromJson(json, UserPing.class);
	}
	
	/*
	 * 版本是否需要强制更新
	 */
	public static void setForceUpdateByVersion(String version,boolean forceupdate){
		SettingHelper.setEditor(getContext(), "forceupdate_"+version, forceupdate);
	}
	
	public static boolean getForceUpdateByVersion(String version){
		return SettingHelper.getSharedPreferences(getContext(), "forceupdate_"+version, false);
	}

}
