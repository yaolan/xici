package net.xici.newapp.data.pojo;

import net.xici.newapp.support.request.XiciException;

import org.json.JSONException;
import org.json.JSONObject;

public class XnbResult extends XiciResult{
	
	public static XnbResult tokenparse(String str) throws XiciException{
		try {
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			
			JSONObject resultjson = getResultJson(infojson);
			
			XnbResult result = new XnbResult();
			result.success = resultjson.optBoolean("auth");
			if(!result.success){
				throw new XiciException(XiciException.CODE_EXCEPTION_XNBTOKEN,"照片上传错误");
			}
			result.msg = resultjson.optString("token");
			return result;
			
		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}catch (Exception e) {
			if(e instanceof XiciException)
			{
				throw (XiciException)e;
			}
			else {
				throw new XiciException(XiciException.CODE_EXCEPTION_UNKNOW);
			}
		
		}
	} 
	
	public static XnbResult uploadparse(String str) throws XiciException{
		
		try {
			JSONObject Responsejson = new JSONObject(str);
			
			XnbResult result = new XnbResult();
			result.success = Responsejson.optBoolean("status");
			if(result.success){
				
				result.msg = Responsejson.optString("file");
				
			}else{
				
				result.msg = Responsejson.optString("error");	
			}
			
		
			return result;
			
		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}
	} 
	

}
