package net.xici.newapp.data.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.xici.newapp.support.request.XiciException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 广告条
 * @author bkmac
 *
 */
public class TopPic implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7660487167871046436L;
	
	public String id;
	public String picname;
	public String picurl;
	public String created;
	public String start;
	public String end;
	public String link;
	
	public static class TopPicSer implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 7017081169907959126L;
		public List<TopPic> list;
		public int count;
		
	}
	
	public static TopPicSer parse(String str)throws XiciException{
		TopPicSer result = null;
		result = new TopPicSer();
		result.list = new ArrayList<TopPic>();
		
		try {

			JSONObject Responsejson = new JSONObject(str.toString());
			
			boolean status = Responsejson.optBoolean("status");

			if (status) {

				JSONArray datajson = Responsejson.optJSONArray("data");
				
				result.count = datajson.length();
				for (int i = 0; i < result.count; i++) {

					JSONObject picjson = datajson.optJSONObject(i);
					TopPic topPic = new TopPic();
					topPic.id = picjson.optString("id");
					topPic.picname = picjson.optString("picname");
					topPic.picurl = picjson.optString("picurl");
					topPic.created = picjson.optString("created");
					topPic.start = picjson.optString("start");
					topPic.end = picjson.optString("end");
					topPic.link = picjson.optString("link");

					result.list.add(topPic);

				}

			} 

		} catch (JSONException e) {
			e.printStackTrace();
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}
		return result;
	}
	
	
	
}
