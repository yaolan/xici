package net.xici.newapp.data.pojo;

import net.xici.newapp.support.request.XiciException;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterResult extends XiciResult{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6913532144079372099L;
	
	public String token;
	
	public static RegisterResult parse(String str) throws XiciException{
		try {
			JSONObject responsejson = new JSONObject(str);
			
			RegisterResult result = new RegisterResult();
			
			result.success = responsejson.optBoolean("status");
			result.code = responsejson.optInt("code", 0);
			
			result.msg = responsejson.optString("message","");
			result.token = responsejson.optString("token","");
			 
//			if(!result.success){
//				throw new XiciException(XiciException.CODE_EXCEPTION_UNKNOW,result.msg);
//			}
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
	
	
	public static RegisterResult updateResultParse(String str) throws XiciException{
		try {
			JSONObject responsejson = new JSONObject(str);
			
			RegisterResult result = new RegisterResult();
			
			result.code = responsejson.optInt("error", 0);
			
			result.msg = responsejson.optString("msg","");
			
			result.success = result.code==0?true:false;
			 
			if(!result.success){
				throw new XiciException(XiciException.CODE_EXCEPTION_UNKNOW,result.msg);
			}
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
	

}
