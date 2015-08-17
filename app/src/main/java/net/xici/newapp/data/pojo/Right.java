package net.xici.newapp.data.pojo;

import org.json.JSONObject;

public class Right extends Basedata {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8352774638507130339L;

	public boolean isAddNew;
	public boolean isAdminBd;
	public boolean isAdminBdUsers;
	public boolean isAdminCool;
	public boolean isAdminDeleted;
	public boolean isAdminRecomm;
	public boolean isAdminRecovered;
	public boolean isAppend;
	public boolean isBoardUser;
	public boolean isDelDocs;
	public boolean isEditOwnDoc;
	public boolean islogin;
	public boolean isRealUser;
	public int lUserPower;
	
	public static Right parse(JSONObject rightJson){
		Right right = new Right();
		right.isAddNew = rightJson.optBoolean("isAddNew");
		right.isAdminBd = rightJson.optBoolean("isAdminBd");
		right.isAdminBdUsers = rightJson.optBoolean("isAdminBdUsers");
		right.isAdminCool = rightJson.optBoolean("isAdminCool");
		right.isAdminDeleted = rightJson.optBoolean("isAdminDeleted");
		right.isAdminRecomm = rightJson.optBoolean("isAdminRecomm");
		right.isAdminRecovered = rightJson.optBoolean("isAdminRecovered");
		right.isAppend = rightJson.optBoolean("isAppend");
		right.isBoardUser = rightJson.optBoolean("isBoardUser");
		right.isDelDocs = rightJson.optBoolean("isDelDocs");
		right.isEditOwnDoc = rightJson.optBoolean("isEditOwnDoc");
		right.islogin = rightJson.optBoolean("islogin");
		right.isRealUser = rightJson.optBoolean("isRealUser");
		right.lUserPower = rightJson.optInt("lUserPower");
		return right;
	}
}
