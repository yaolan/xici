package net.xici.newapp.data.pojo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.xici.newapp.support.request.XiciException;

public class MainPageThread extends Basedata{
	
	public int bd_id;
	
	public String bd_name;
	
	public long doc_id;
	
	public String doc_title;
	
	public static List<MainPageThread> parse(String str) throws XiciException{
		try {
			
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			
			if (infojson.isNull("result"))
				return null;
			List<MainPageThread> threads = new ArrayList<MainPageThread>();
			JSONArray resultjson = getResultJSONArray(infojson);
			int num = resultjson.length();
			for (int i = 0; i < num; i++) {
				MainPageThread thread = new MainPageThread();
				JSONObject threadjson = resultjson.optJSONObject(i);
				thread.bd_id = threadjson.optInt("bd_id");
				thread.bd_name = threadjson.optString("bd_name","");
				thread.doc_id = threadjson.optLong("doc_id");
				thread.doc_title = threadjson.optString("doc_title");
				threads.add(thread);
			}
			return threads;
			
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
