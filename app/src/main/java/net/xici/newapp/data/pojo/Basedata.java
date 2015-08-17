package net.xici.newapp.data.pojo;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import net.xici.newapp.app.XiciApp;
import net.xici.newapp.support.request.XiciException;

public abstract class Basedata implements Serializable {

	// public int strlen;

	// public double timespend;

	// public String accesstoken;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4425475872185599257L;

	protected static void CheckError(JSONObject json) throws XiciException {
		if (!json.isNull("accesstoken")){
			String accesstoken = json.optString("accesstoken", "");
			if(!TextUtils.isEmpty(accesstoken)){
				//更新token
				XiciApp.getInstance().savetoken(accesstoken);
			}
		}
		if (json.isNull("error"))
			return;
		JSONObject errorjson = json.optJSONObject("error");
		throw new XiciException(errorjson.optInt("code"),
				errorjson.optString("msg"));

	}

	public static JSONObject getResultJson(JSONObject json)
			throws XiciException {

		try {

			return json.optJSONObject("result");

		} catch (Exception e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}

	}

	public static JSONArray getResultJSONArray(JSONObject json)
			throws XiciException {

		try {

			return json.optJSONArray("result");

		} catch (Exception e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}

	}

}
