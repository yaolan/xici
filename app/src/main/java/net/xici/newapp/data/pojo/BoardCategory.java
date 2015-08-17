package net.xici.newapp.data.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.xici.newapp.support.request.XiciException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 板块分类
 * @author gmz
 *
 */
public class BoardCategory extends Basedata{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -928945251179060258L;

	public int id;
	
	public String name;
	
	public String description;
	
	public static List<BoardCategory> parse(String str) throws XiciException
	{
		try {
//			JSONObject jsonObject = new JSONObject(str);
//			checkstatus(jsonObject);
			
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			
			if (infojson.isNull("data"))
				return null;
			
			List<BoardCategory> categories = new ArrayList<BoardCategory>();
			JSONArray resultjson =  infojson.optJSONArray("data");
			int num = resultjson.length();
			for (int i = 0; i < num; i++) {
				BoardCategory category = new BoardCategory();
				JSONObject categoryjson = resultjson.optJSONObject(i);
				category.name = categoryjson.optString("name");
				category.id = categoryjson.optInt("id");
				category.description = categoryjson.optString("description");
				
				categories.add(category);
			}
			
			return categories;
			
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
	
	private static void  checkstatus(JSONObject json) throws XiciException{
		int code = json.optInt("code");
		if(code!=0){
			throw new XiciException(code, json.optString("message"));
		}
		
	}
	
	

}
