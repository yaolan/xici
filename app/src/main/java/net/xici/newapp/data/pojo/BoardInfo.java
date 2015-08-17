package net.xici.newapp.data.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import net.xici.newapp.data.pojo.Board.Stats;
import net.xici.newapp.support.request.XiciException;

/**
 * 讨论版详情
 * 
 * @author bk
 * 
 */
public class BoardInfo extends Basedata {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8003439650899666940L;

	public List<User> admins;

	public List<Board> sub_boards;

	public int id;

	public String logourl;

	public String bd_script;

	public String bd_name;

	public String bd_name_no_highlight;

	public String bd_users;

	public Stats stats;

	public long bd_docupdate;
	
	public String users;

	public Board getBoard() {
		Board board = new Board();
		board.boardName = bd_name_no_highlight;
		board.boardUrl = id;
		board.stats = new Stats();
		return board;
	}

	public static BoardInfo parse(String str) throws XiciException {
		BoardInfo boardInfo = null;
		try {
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			
			if (infojson.isNull("result"))
				return null;
			JSONObject resultjson = getResultJson(infojson);
			
			boardInfo = new BoardInfo();
			int num = 0;
			boardInfo.admins = new ArrayList<User>();
			
			//版主
			JSONArray bdadminjArray = resultjson.optJSONArray("Bd_admin");
			if(bdadminjArray!=null){
				 num = bdadminjArray.length();
				 for (int i = 0; i < num; i++) {
					 String bdadminstr = bdadminjArray.optString(i);
					 JSONArray bdadminJsonObject = new JSONArray(bdadminstr);
					 
					 User bdadmin = new User();
					 bdadmin.userName = bdadminJsonObject.optString(0);
					 bdadmin.userid = bdadminJsonObject.optLong(1);
					 
					 boardInfo.admins.add(bdadmin);
				}
			}
			
			//版名
			boardInfo.bd_name = resultjson.optString("Bd_name");
			//描述,版规
			boardInfo.bd_script = resultjson.optString("bd_script");
			//24小时发帖回帖数
			boardInfo.stats = new Stats();
			JSONObject statsjJsonObject = resultjson.optJSONObject ("stats");
			boardInfo.stats.F = statsjJsonObject.optInt("puts24");
			boardInfo.stats.G = statsjJsonObject.optInt("replys24");
			
			//子板
			boardInfo.sub_boards = new ArrayList<Board>();
			JSONArray subboardsArray = resultjson.optJSONArray("Subboard");
			if(subboardsArray!=null){
				 num = subboardsArray.length();
				 for (int i = 0; i < num; i++) {
					 String suboardstr = subboardsArray.optString(i);
					 JSONArray suboardstrJsonObject = new JSONArray(suboardstr);
					 
					 Board subboard = new Board();
					 subboard.boardUrl = suboardstrJsonObject.optInt(0);
					 subboard.boardName = suboardstrJsonObject.optString(1);
					 
					 boardInfo.sub_boards.add(subboard);
				}
			}
			
			boardInfo.users = resultjson.optString("users");
			

		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		} catch (Exception e) {
			if (e instanceof XiciException) {
				throw (XiciException) e;
			} else {
				throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
			}
		}
		return boardInfo;
	}

	public static class Page implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3022425013867609422L;

		public int[] num;
		public int firstpage;
		public int lastpage;
		public int totalpage;
	}

	public static class CircleResult implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3057828617805440955L;

		public List<BoardInfo> boardInfos;
		public int total_pages;
		public int current_page;

		public CircleResult() {
			boardInfos = new ArrayList<BoardInfo>();
		}

		public static CircleResult parse(String str) throws XiciException {
			try {
				CircleResult result = new CircleResult();
				JSONObject jsonObject = new JSONObject(str);

				result.total_pages = jsonObject.optInt("total_pages");
				result.current_page = jsonObject.optInt("current_page");

				JSONArray circlesjson = jsonObject.optJSONArray("circles");
				int num = circlesjson.length();
				for (int i = 0; i < num; i++) {
					JSONObject json = circlesjson.optJSONObject(i);
					BoardInfo boardInfo = new BoardInfo();
					boardInfo.id = json.optInt("id");
					boardInfo.bd_name = json.optString("name");
					boardInfo.bd_name_no_highlight = json.optString("name");
					result.boardInfos.add(boardInfo);
				}

				return result;
			} catch (JSONException e) {
				throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
			}
		}

	}

}
