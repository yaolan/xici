package net.xici.newapp.data.pojo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.xici.newapp.support.request.XiciException;
import net.xici.newapp.support.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;

import com.google.gson.reflect.TypeToken;

public class AppMail extends Basedata{

	/**
	 * 
	 */
	private static final long serialVersionUID = 831220714798749477L;
	
	public String mUserName;
	
	public Mail newest;
	
	public long uid;
	
	public boolean unread_flag = false;
	
	public static List<AppMail> parse(String str) throws XiciException
	{
		try {
			
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			
			
			List<AppMail> mails = null;
			
			mails = JsonUtils.fromJson(infojson.optString("data"), new TypeToken<List<AppMail>>(){});
			
		
			
			if (mails != null&&mails.size()>0) {
				
				JSONObject idsjson = infojson.optJSONObject("ids");
				Map<String, Long> idmap = new HashMap<String, Long>();
				int num = idsjson.length();
				Iterator it =  idsjson.keys();
				while (it.hasNext()) {
					
					String key = (String) it.next();  
	                long value = idsjson.optLong(key);
	                idmap.put(key, value);
					
				}
				
				for (AppMail mail : mails) {
					Long id = idmap.get(mail.mUserName);
					if (id != null) {
						mail.uid = id;
					} else {
						mail.uid = 0;
					}

				}
			}
//			int num = idsjson.length();
//			Map<String, Long> idmap = new HashMap<String, Long>();
//			for (int i = 0; i < num; i++) {
//				String idstr = idsjson.optString(i);
//				JSONArray array = new JSONArray(idstr);
//				idmap.put(array.optString(0), array.optLong(1));
//			}
//			
//			if(mails!=null){
//				for (AppMail mail:mails) {
//					Long id = idmap.get(mail.mUserName);
//					if(id!=null){
//						mail.uid = id;
//					}else{
//						mail.uid = 0;
//					}
//						
//				}
//			}
//			
			
			
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
