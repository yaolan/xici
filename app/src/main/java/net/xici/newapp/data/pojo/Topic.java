package net.xici.newapp.data.pojo;

import java.io.Serializable;

import org.json.JSONObject;

public class Topic implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6075703349872052642L;
	
	public static final String COLLECTDEL = "0";
	public static final String COLLECTED = "1";
	
	public long author_id;
	public int bd_good;
	public int bd_hide;
	public int bd_id;
	public int doc_permission;
	public String doc_title;
	public String doc_update;
	public String iscollect;
	
	public static Topic parse(JSONObject topicJson){
		Topic topic = new Topic();
		topic.author_id = topicJson.optLong("author_id");
		topic.bd_good = topicJson.optInt("bd_good");
		topic.bd_hide = topicJson.optInt("bd_hide");
		topic.bd_id = topicJson.optInt("bd_id");
		topic.doc_permission = topicJson.optInt("doc_permission");
		topic.doc_title = topicJson.optString("doc_title");
		topic.doc_update = topicJson.optString("doc_update");
		topic.iscollect = topicJson.optString("iscollect");
		return topic;
	}

	public boolean isCollect(){
		if(COLLECTED.equals(iscollect)){
			return true;
		}else {
			return false;
		}
	}
}
