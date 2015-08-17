package net.xici.newapp.data.pojo;

import java.util.ArrayList;
import java.util.List;

import net.xici.newapp.support.request.XiciException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.provider.Telephony.Mms.Inbox;


public class Mail extends Basedata {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6678865601476018463L;

	public String date;

	public int id;

	public int old;

	public String title;

	public int type; //1 收件， 0发件

	public String memo;

	public String username;

	public static class MailResult {
		public List<Mail> mails;

		public MailResult() {
			mails = new ArrayList<Mail>();
		}
	}

	public static MailResult parse(String str) throws XiciException {
		try {

			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			MailResult mailResult = new MailResult();
			if (infojson.isNull("result"))
				return mailResult;
			JSONObject resultjson = getResultJson(infojson);
			JSONArray mailsjson = resultjson.optJSONArray("xici.user.mails");
			int num = mailsjson.length();
			for (int i = 0; i < num; i++) {
				Mail mail = new Mail();
				JSONObject mailjson = mailsjson.optJSONObject(i);
				mail.date = mailjson.optString("Mail_date");
				mail.id = mailjson.optInt("Mail_id");
				mail.old = mailjson.optInt("Mail_old");
				mail.title = mailjson.optString("Mail_Title");
				mail.type = mailjson.optInt("Mail_type");
				mail.username = mailjson.optString("mUserName");
				mailResult.mails.add(mail);
			}
			return mailResult;

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

	public static Mail simpleparse(String str) throws XiciException {
		try {

			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
            Mail mail = new Mail();
            JSONObject resultjson = getResultJson(infojson);
            mail.date = resultjson.optString("MailDate");
            mail.memo = resultjson.optString("MailMemo");
            mail.title = resultjson.optString("MailTitle");
            mail.type = resultjson.optInt("MailType");
            mail.username = resultjson.optString("mUserName");
			return mail;

		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}catch (Exception e) {
			if (e instanceof XiciException) {
				throw (XiciException) e;
			} else {
				throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
			}

		}

	}

}
