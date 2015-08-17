package net.xici.newapp.data.pojo;

import net.xici.newapp.support.request.XiciException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 统一返回参数
 * @author bk
 *
 */
public class XiciResult extends Basedata{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1867532511219299685L;
	
	public boolean success;
	
	public int code;
	
	public String msg;
	
	/**
	 * 退出讨论版
	 * @param str
	 * @return
	 * @throws XiciException
	 */
	public static XiciResult quitbdparse(String str) throws XiciException
	{
		return parse(str,"isquitbd");
	}
	
	/**
	 * 预定讨论版
	 * @param str
	 * @return
	 * @throws XiciException
	 */
	public static XiciResult bookbdparse(String str) throws XiciException
	{
		return parse(str,"isbook");
	}
	/**
	 * 删除帖子
	 * @param str
	 * @return
	 * @throws XiciException
	 */
	public static XiciResult docDeleteparse(String str) throws XiciException{
		return parse(str,"deldocs");
	}
	/**
	 * 取消收藏帖子
	 * @param str
	 * @return
	 * @throws XiciException
	 */
	public static XiciResult CollectDelparse(String str) throws XiciException{
		return parse(str,"CollectDel");
	}
	
	/**
	 * 收藏帖子
	 * @param str
	 * @return
	 * @throws XiciException
	 */
	public static XiciResult Collectparse(String str) throws XiciException{
		return parse(str,"Collect");
	}
	/**
	 * 置顶
	 * @param str
	 * @return
	 * @throws XiciException
	 */
	public static XiciResult DocRecommparse(String str) throws XiciException{
		return parse(str,"recommDocs");
	}
	
	/**
	 * 加酷
	 * @param str
	 * @return
	 * @throws XiciException
	 */
	public static XiciResult DocCoolparse(String str) throws XiciException{
		return parse(str,"CoolDocs");
	}
	
	
	
	/**
	 * 发送飞语结果
	 * @param str
	 * @return
	 * @throws XiciException
	 */
	public static XiciResult sendMailparse(String str) throws XiciException{
		return parse(str,"issend");
	}
	
	
	/**
	 * 删除飞语结果
	 * @param str
	 * @return
	 * @throws XiciException
	 */
	public static XiciResult deleteMailparse(String str) throws XiciException{
		return parse(str,"status");
	}
	
	
	public static XiciResult parse(String str,String key) throws XiciException{
		try {
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			JSONObject resultjson = getResultJson(infojson);
			XiciResult result = new XiciResult();
			result.success = resultjson.optBoolean(key);
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
	
	public static XiciResult simpleparse(String str) throws XiciException{
		try {
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			JSONObject resultjson = getResultJson(infojson);
			XiciResult result = new XiciResult();
			result.success = true;
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
	

	public static XiciResult checkUsernema(String str) throws XiciException{
		try {
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			

			if (!infojson.isNull("error")){

				JSONObject errorjson = infojson.optJSONObject("error");
				throw new XiciException(errorjson.optInt("code"),
						errorjson.optString("msg"));
			}
				
			
			JSONObject resultjson = getResultJson(infojson);
			XiciResult result = new XiciResult();
			
			String code = resultjson.optString("code");
			String msg =  resultjson.optString("msg");
			 
			if("no".equals(code)){
				result.success = true;
			}else {
				
				throw new XiciException(XiciException.CODE_EXCEPTION_UNKNOW,msg);
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
	
	
	public static XiciResult docpraiseParse(String str) throws XiciException{
		try {
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			
			XiciResult result = new XiciResult();
			
			String code = infojson.optString("code");
			String msg =  infojson.optString("msg");
			 
			if("0".equals(code)){
				result.success = true;
			}else {
				
				throw new XiciException(XiciException.CODE_EXCEPTION_UNKNOW,msg);
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
	
	public static XiciResult userthirdbindparse(String str) throws XiciException{
		
		try {
			JSONObject Responsejson = new JSONObject(str);
			
			XnbResult result = new XnbResult();
			result.success = Responsejson.optBoolean("status");
		    
			result.code = Responsejson.optInt("code");
			
			if(!Responsejson.isNull("message"))
			{

				result.msg = Responsejson.optString("message");
			}
			
		
			return result;
			
		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}
	} 
	

}
