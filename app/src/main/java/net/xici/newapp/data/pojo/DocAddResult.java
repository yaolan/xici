package net.xici.newapp.data.pojo;

import net.xici.newapp.support.request.XiciException;

import org.json.JSONException;
import org.json.JSONObject;

public class DocAddResult extends Basedata{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5146845704171997834L;
	
	
	public String act;//isNew，isAppend
	
	public long bd_id;
	
	public long doc_id;
	
	public String msg;
	
	public String server_id;
	
	public int subdoc;  //楼层。0、楼主   1、一楼
	
	public Floor floor;
	
	public static DocAddResult parse(String str) throws XiciException
	{
		try {
			
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			JSONObject resultjson = getResultJson(infojson);
			DocAddResult result = new DocAddResult();
			result.act = resultjson.optString("act");
			result.bd_id = resultjson.optLong("bd_id");
			result.doc_id = resultjson.optLong("doc_id");
			result.msg = resultjson.optString("msg");
			result.subdoc = resultjson.optInt("subdoc");
			
			if (!resultjson.isNull("data")){
				result.floor = new Floor();
				JSONObject dataJson = resultjson.optJSONObject("data");
				if(dataJson!=null){
					result.floor.user.userid = dataJson.optLong("UserID");
					result.floor.user.userName = dataJson.optString("Username");
					result.floor.index = dataJson.optInt("index");
					result.floor.isDel = dataJson.optInt("isDel");
					result.floor.updatetime = dataJson.optString("updatetime");
				}
				
			}
			
			return result;
			
		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}
		catch (Exception e) {
			if(e instanceof XiciException)
			{
				throw (XiciException)e;
			}
			else {
				throw new XiciException(XiciException.CODE_EXCEPTION_UNKNOW);
			}
		
		}
	}
	
	

}
