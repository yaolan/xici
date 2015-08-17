package net.xici.newapp.data.pojo;

import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.pojo.User.UserResult;
import net.xici.newapp.support.request.XiciException;
import net.xici.newapp.support.set.SettingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class UserPing extends Basedata{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4001082319792702314L;
	
	public int atme;
	public int fans;
	public int flyme;
	public int follow;
	public int invite;
	public int newmail;
	public int notice;
	
	public static UserPing parse(String str) throws XiciException
	{
		try {
			
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			JSONObject resultjson = getResultJson(infojson);
			
			UserPing result = new UserPing();
			if(resultjson!=null){
				result.atme = resultjson.optInt("atme");
				result.fans = resultjson.optInt("fans");
				result.flyme = resultjson.optInt("flyme");
				result.follow = resultjson.optInt("follow");
				result.invite = resultjson.optInt("invite");
				result.newmail = resultjson.optInt("newmail");
				result.notice = resultjson.optInt("notice");
			}
			
			return result;
			
		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}
	}
	
	public static void postevent(UserPing ping){

		SettingUtil.setAccountMessageCount(XiciApp.getAccountId(), ping);
		EventBus.getDefault().post(new UserPing());
	}
	

}
