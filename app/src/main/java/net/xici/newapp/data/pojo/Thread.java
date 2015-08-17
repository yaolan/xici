package net.xici.newapp.data.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.xici.newapp.support.request.XiciException;
import net.xici.newapp.support.util.StringUtil;

public class Thread extends Basedata {

	public long tid;

	public String title;
	/**
	 * 发贴人，name，id
	 */
	public User author;

	/**
	 * 查看量
	 */
	public int viewcount;

	/**
	 * 回复量
	 */
	public int replycount;

	/**
	 * 发帖时间
	 */
	public String createtime;

	/**
	 * 置顶 0为非置顶
	 */
	public int top;

	/**
	 * cool,0为非cool
	 */
	public int cool;

	/**
	 * 帖子类型:96 – m版; 97 – android; 98 – ios; XX – 花嫁
	 */
	public int type;

	/**
	 * 更新时间
	 */
	public String updatetime;

	/**
	 * 图片贴标示 0 – 无
	 */
	public int haseimg;

	/**
	 * 最新回帖人
	 */
	public String lastreply;

	/**
	 * 简介
	 */
	public String summary;

	/***
	 * 帖子长度
	 */
	public int len;

	/**
	 * 缩略图数组
	 */
	public List<String> imgs;

	public int arg0;

	public int arg1;

	public int arg2;

	public String arg3;
	
	public String zan;

	public Thread() {
		author = new User();
		imgs = new ArrayList<String>();
	}

	public static class ThreadsResult extends Basedata {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public int count;// 版内总帖数
		public int isbook;// 是否预定
		public int page;// 当前页数
		public List<Thread> threads;
		public Right right;
        public ThreadsResult(){
        	threads = new ArrayList<Thread>();
        	right = new Right();
        }
	}

	public static ThreadsResult parse(String str) throws XiciException {

		try {

			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			if (infojson.isNull("result"))
				return null;
			JSONObject resultjson = getResultJson(infojson);
			ThreadsResult result = new ThreadsResult();
			result.count = resultjson.optInt("count");
			// 列表
			JSONArray datajson = resultjson.optJSONArray("data");
			int num = datajson.length();
			for (int i = 0; i < num; i++) {
				String threadstr = datajson.optString(i);
				JSONArray array = new JSONArray(threadstr);
				Thread thread = new Thread();
				thread.tid = array.optLong(0);

				thread.title = StringUtil.fromhtml(array.optString(1));

				thread.author.userName = array.optString(2);

				thread.viewcount = array.optInt(3);

				thread.replycount = array.optInt(4);

				thread.createtime = array.optString(5);

				thread.top = array.optInt(6);

				thread.cool = array.optInt(7);

				thread.type = array.optInt(8);

				thread.updatetime = array.optString(9);

				thread.haseimg = array.optInt(10);

				thread.lastreply = array.optString(11);

				thread.author.userid = array.optLong(12);

				thread.summary = StringUtil.fromhtml(array.optString(13));

				thread.len = array.optInt(14);
                //缩略图列表
				if (thread.haseimg != 0) {
					JSONArray imgArray = array.optJSONArray(15);
					for (int j = 0; j < imgArray.length(); j++) {
						thread.imgs.add(imgArray.optString(j));
					}
				}
				
				if(array.length()>=20){
					thread.zan = array.optString(20);
				}
				
				result.threads.add(thread);
				
			}
			if (!resultjson.isNull("Right")){
				JSONObject rightJson = resultjson.optJSONObject("Right");
				result.right = Right.parse(rightJson);
			}
			
			return result;

		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}	catch (Exception e) {
			if(e instanceof XiciException)
			{
				throw (XiciException)e;
			}
			else {
				throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
			}
		
		}

	}
	
	public static class MineThreadsResult implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public int count;// 版内总帖数
		public List<Thread> threads;
        public MineThreadsResult(){
        	threads = new ArrayList<Thread>();
        }
	}
	
	public static MineThreadsResult mineThreadsparse(String str) throws XiciException{
		try {
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			if (infojson.isNull("result"))
				return null;
			JSONObject resultjson = getResultJson(infojson);
			MineThreadsResult result = new MineThreadsResult();
			result.count = resultjson.optInt("count");
			// 列表
			JSONArray datajson = resultjson.optJSONArray("data");
			int num = datajson.length();
			for (int i = 0; i <num; i++) {
				JSONObject threadjson = datajson.optJSONObject(i);
				Thread thread = new Thread();
				thread.tid = threadjson.optLong("doc_id");
				thread.createtime = threadjson.optString("doc_date");
				thread.title = threadjson.optString("doc_title");
				result.threads.add(thread);
			}
			return result;
		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}	catch (Exception e) {
			if(e instanceof XiciException)
			{
				throw (XiciException)e;
			}
			else {
				throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
			}
		
		}

	} 

}
