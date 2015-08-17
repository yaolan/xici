package net.xici.newapp.data.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.xici.newapp.app.Constants;
import net.xici.newapp.support.request.XiciException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.google.gson.JsonArray;

/**
 * 楼层
 * 
 * @author bk
 * 
 */
public class Floor extends Basedata {

	public int Emote;// 帖子类型

	public int index;// 楼数

	public int isDel;// 是否删除

	public String text;// 内容
	
	/**
	 * 楼内容
	 */
	public List<String> textList;

	public List<String> thumbnail;

	public String updatetime;// 更新时间

	public User user;
	
	public int up_num;
	public int up_status;
	

	public List<ThumbnailSize> thumbnail_size;

	public Floor() {
		user = new User();
		thumbnail = new ArrayList<String>();
		thumbnail_size = new ArrayList<ThumbnailSize>();
	}

	public String getfloornum() {
		if (index < 2) {
			return "楼主";
		} else {

			return index + " 楼";
		}
	}

	public String getContent() {
		String newtext = text;
		Pattern imgPattern = Pattern
				.compile("\\[img\\]attachment(\\d+)\\[/img]");
		Matcher matcher = imgPattern.matcher(text);
		while (matcher.find()) {
			String img = matcher.group();
			// String num = matcher.group(1);
			newtext = newtext.replace(img, "");
		}
		return newtext;
	}

	public static class FloorResult {
		public int count;// 总楼数
		public List<Floor> floors;
		public Topic topic;
		public Right right;

		public FloorResult() {
			floors = new ArrayList<Floor>();
			topic = new Topic();
			right = new Right();
		}
	}

	public static FloorResult parse(String str) throws XiciException {

		try {

			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			if (infojson.isNull("result"))
				return null;
			JSONObject resultjson = getResultJson(infojson);
			FloorResult result = new FloorResult();
			result.count = resultjson.optInt("count");
			// 列表
			JSONArray datajson = resultjson.optJSONArray("data");
			int num = datajson.length();
			for (int i = 0; i < num; i++) {
				Floor floor = new Floor();
				JSONObject floorjson = datajson.optJSONObject(i);
				floor.Emote = floorjson.optInt("Emote");
				floor.index = floorjson.optInt("index");
				floor.isDel = floorjson.optInt("isDel");
				floor.text = floorjson.optString("text");
				floor.up_num = floorjson.optInt("up_num");
				floor.up_status = floorjson.optInt("up_status");
				
				//start test
				String[] tempArr = floor.text.split("\r\n");
				StringBuffer buffer = new StringBuffer();
				floor.textList = new ArrayList<String>();
				boolean isImg = false;
				boolean tempImg = false;
				if (tempArr != null)
				{
					int count = 0;
					for (String temp : tempArr)
					{
						count++;
						if (temp.contains("[img]"))
						{
							int imgtagstart = temp.indexOf("[img]");
//							if(imgtagstart!=0){
//								
//								buffer.append( temp.substring(0, imgtagstart) );
//								floor.textList.add(buffer.toString());
//								buffer.delete(0, buffer.length());
//								
//							}
							
							temp = temp.substring(imgtagstart+"[img]".length(), temp.indexOf("[/img]"));
							temp = "[img]"+temp+"[/img]";
							
							isImg = true;
						} else
						{
							isImg = false;
						}
						
						
						if (tempImg != isImg && isImg)
						{
							floor.textList.add(buffer.toString());
							buffer.delete(0, buffer.length());
						}
						
						if (!isImg)
						{
						
							if(!TextUtils.isEmpty(temp)){

	      					    buffer.append(temp).append("\r\n");
							}
							
 //                         buffer.append(temp);
						} else
						{
//							rPost.textList.add(temp + "\r\n");
							floor.textList.add(temp);
						}
						if (count == tempArr.length && !isImg)
						{
							floor.textList.add(buffer.toString());
							buffer.delete(0, buffer.length());
						}
						tempImg = isImg;

					}
				}
				
				//end test
				
				floor.updatetime = floorjson.optString("updatetime");
				floor.user.userid = floorjson.optLong("UserID");
				floor.user.userName = floorjson.optString("Username");

				JSONArray imgArray = floorjson.optJSONArray("thumbnail");
				if (imgArray != null && imgArray.length() > 0) {
					for (int j = 0; j < imgArray.length(); j++) {
						floor.thumbnail.add(imgArray.optString(j));
					}
				}
				
				JSONArray sizeArray = floorjson.optJSONArray("thumbnail_size");
				
				if (sizeArray != null && sizeArray.length() > 0) {
					for (int j = 0; j < sizeArray.length(); j++) {
						
						JSONObject sizJsonObject = sizeArray.getJSONObject(j);
						ThumbnailSize size = new ThumbnailSize();
						size.url = sizJsonObject.optString("url");
						size.width = sizJsonObject.optInt("width");
						size.height = sizJsonObject.optInt("height");
						 
						floor.thumbnail_size.add(size);
					}
				}
				
				result.floors.add(floor);
			}
			
			//topic
			if (!resultjson.isNull("topic")){
				JSONObject topicJson = resultjson.optJSONObject("topic");
				result.topic = Topic.parse(topicJson);
			}
			//right
			if (!resultjson.isNull("right")){
				JSONObject rightJson = resultjson.optJSONObject("right");
				result.right = Right.parse(rightJson);
			}

			return result;

		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		} catch (Exception e) {
			if(e instanceof XiciException)
			{
				throw (XiciException)e;
			}
			else {
				throw new XiciException(XiciException.CODE_EXCEPTION_UNKNOW);
			}
		
		}
	}
	
	/**
	 * 根据楼层获取所在页数，页码和楼层都从1开始
	 * @param index
	 * @return
	 */
	public static int getpage(int index){
		int i = --index;
		
		int page = i/Constants.PAGESIZE;
		
		page++;
		return page;
		
	}
}
