package net.xici.newapp.data.pojo;

import java.util.List;

import net.xici.newapp.support.request.XiciException;
import net.xici.newapp.support.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.reflect.TypeToken;

public class AppMailItem extends Basedata{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5184262208731507487L;
	
	public String Mail_date;
	
	public int Mail_id;
	
	public int Mail_old;
	
	public String Mail_Title;
	
	public int Mail_type;
	
	public String mUserName;
	
	public String type;
	
	public String Mail_Memo;
	
	public static List<AppMailItem> parse(String str) throws XiciException
	{
		try {
			
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			
			
			List<AppMailItem> mails = null;
			
			mails = JsonUtils.fromJson(infojson.optString("data"), new TypeToken<List<AppMailItem>>(){});
			
			return mails;
			
		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}
		catch (Exception e) {
			if(e instanceof XiciException)
			{
				throw (XiciException)e;
			}
			else {
				throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
			}
		
		}
	}

}
