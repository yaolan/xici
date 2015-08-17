package net.xici.newapp.data.pojo;

import java.io.Serializable;

import net.xici.newapp.support.request.XiciException;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;

/**
 * 闪屏广告
 * 
 * @author bkmac
 *
 */
public class SplashPic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4488602191351127727L;

	public String id;

	public String url;

	public String name;

	public String img_url;

	public String link_url;

	public long starttime;

	public long endtime;

	public long updatetime;

	public String extinfo;

	public String operatorid;

	public static SplashPic parse(String str) throws XiciException {

		SplashPic splashPic = null;

		try {
			JSONObject resultJsonObject  =  new JSONObject(str);
			JSONObject Responsejson = resultJsonObject.optJSONObject("info");
			int status = Responsejson.optInt("status");

			if (status == 1) {

				JSONObject datajson = Responsejson.optJSONObject("content").optJSONObject("appurl");
				splashPic = new SplashPic();

				splashPic.id = datajson.optString("id");
				splashPic.url = datajson.optString("url");
				splashPic.name = datajson.optString("name");
				splashPic.img_url = datajson.optString("img_url");
				splashPic.link_url = datajson.optString("link_url");
				
				splashPic.starttime = datajson.optLong("starttime");
				splashPic.endtime = datajson.optLong("endtime");
				
				splashPic.extinfo = datajson.optString("extinfo");
				splashPic.operatorid = datajson.optString("operatorid");

			} else {
				return null;
			}

		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}
		return splashPic;
	}

}
