package net.xici.newapp.data.pojo;

import java.io.Serializable;

import net.xici.newapp.support.request.XiciException;

import org.json.JSONException;
import org.json.JSONObject;

public class User extends Basedata{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7252809745355470099L;
	
	public String city;
	
	public long userid;
	
	public int userlevel; // 8 或者30 为注册网友，需升级为真实网友后才能发帖 
	
	public String userName;
	
	public static class UserResult implements Serializable{
		public String accesstoken;
		public User user;
		
		public UserResult()
		{
			user = new User();
		}
	}
	
	public static UserResult parse(String str) throws XiciException
	{
		try {
			
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			JSONObject resultjson = getResultJson(infojson);
			UserResult result = new UserResult();
			result.accesstoken = infojson.optString("accesstoken");
			if(resultjson!=null){
				result.user.city = resultjson.optString("city");
				result.user.userid = resultjson.optLong("userid");
				result.user.userlevel = resultjson.optInt("userlevel");
				result.user.userName = resultjson.optString("userName");
			}
			
			return result;
			
		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}
	}
	
	public static UserResult Refisterparse(String str) throws XiciException
	{
		try {
			
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			JSONObject resultjson = getResultJson(infojson);
			UserResult result = new UserResult();
			result.accesstoken = infojson.optString("accesstoken");
			result.user.userid = resultjson.optLong("userid");
			result.user.userlevel = resultjson.optInt("userlevel");
			result.user.userName = resultjson.optString("username");
			
			return result;
			
		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}
	}
	
	
	
}
