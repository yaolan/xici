package net.xici.newapp.data.pojo;

import java.util.ArrayList;
import java.util.List;

import net.xici.newapp.data.pojo.Mail.MailResult;
import net.xici.newapp.support.request.XiciException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FloorFollow extends Basedata {
	
	public final static String TYPE_ATME = "atme";

	public final static String TYPE_FOLLOW = "follow";

	/**
	 * 
	 */
	private static final long serialVersionUID = 4712367097610589782L;
	
	public long bd_id;
	
	public String bd_name;
	
	public long doc_id;

	public String doc_title;
	
	public String doccontent;
	
	public int floor_id;
	
	public String time;

	public long user_id;
	
	public String user_name;
	
	public static class FloorFollowResult {
		public List<FloorFollow> floors;

		public FloorFollowResult() {
			floors = new ArrayList<FloorFollow>();
		}
	}

	public static FloorFollowResult parse(String str) throws XiciException {
		try {

			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			FloorFollowResult floorFollowResult = new FloorFollowResult();
			if (infojson.isNull("result"))
				return floorFollowResult;
			JSONObject resultjson = getResultJson(infojson);
		
		    JSONArray datajson = resultjson.optJSONArray("data");
			int num = datajson.length();
			for (int i = 0; i < num; i++) {
				FloorFollow floorFollow = new FloorFollow();
				JSONObject mailjson = datajson.optJSONObject(i);
				
				floorFollow.bd_name = mailjson.optString("bd_name");
				floorFollow.bd_id = mailjson.optInt("bd_id");
				
				floorFollow.doc_title = mailjson.optString("doc_title");
				floorFollow.doc_id = mailjson.optLong("doc_id");
				floorFollow.doccontent = mailjson.optString("doccontent");

				floorFollow.time = mailjson.optString("time");
				floorFollow.floor_id = mailjson.optInt("floor_id");

				floorFollow.user_name = mailjson.optString("user_name");
				floorFollow.user_id = mailjson.optInt("user_id");
				
				floorFollowResult.floors.add(floorFollow);
			}
			return floorFollowResult;

		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		} catch (Exception e) {
			if (e instanceof XiciException) {
				throw (XiciException) e;
			} else {
				throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
			}

		}
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        FloorFollow other = (FloorFollow) o;
        
        if((user_id == other.user_id)&&(doc_id == other.doc_id)){
        	return true;
        }else {
			return false;
		}
        
	}

}
