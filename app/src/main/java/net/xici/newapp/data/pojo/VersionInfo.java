package net.xici.newapp.data.pojo;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.xici.newapp.support.request.XiciException;

public class VersionInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4657960308806177438L;
	
	public boolean is_have_new;//是否需要升级
	
	public boolean is_must_update;//是否需要强制升级
	
	public String app_version;//新版本号
	
	public String log;//更新日志
	
	public String download;//下载地址
	
	public static VersionInfo parse(String str)throws XiciException{
		VersionInfo info = null;
		try {
			JSONObject responsejson = new JSONObject(str.toString());
			info = new VersionInfo();
			
			boolean status = responsejson.optBoolean("status");
			
			if(status){
				
				JSONObject datajson = responsejson.optJSONObject("data");
				
				info.is_have_new = datajson.optInt("is_have_new")==1?true:false;
				info.is_must_update = "1".equals(datajson.optString("is_must_update"))?true:false;
				info.app_version = datajson.optString("app_version");
				info.log = datajson.optString("log");
				info.download = datajson.optString("download");
				
			}else {
				
				throw new XiciException(XiciException.CODE_EXCEPTION_UNKNOW);
				
			}
			
			
		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}
		
		return info;
	}

}
