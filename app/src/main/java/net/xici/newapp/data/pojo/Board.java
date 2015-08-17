package net.xici.newapp.data.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.xici.newapp.support.request.XiciException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 讨论版
 * @author bk
 *
 */
public class Board extends Basedata{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5262673385100457709L;
	
	public String boardName;
	
	public int boardType;
	
	public String gpName;
	
	public int boardUrl;
	
	public Stats stats;
	
	public static class Stats implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -7988951772607158083L;
		public int F;
		public int G;
	}
	
	public static List<Board> parse(String str) throws XiciException
	{
		try {
			
			JSONObject Responsejson = new JSONObject(str);
			JSONObject infojson = Responsejson.optJSONObject("info");
			CheckError(infojson);
			
			if (infojson.isNull("result"))
				return null;
			List<Board> boards = new ArrayList<Board>();
			JSONArray resultjson = getResultJSONArray(infojson);
			int num = resultjson.length();
			for (int i = 0; i < num; i++) {
				Board board = new Board();
				JSONObject boardson = resultjson.optJSONObject(i);
				board.boardName = boardson.optString("boardName");
				board.boardType = boardson.optInt("boardType");
				board.gpName = boardson.optString("gpName","");
				board.boardUrl = boardson.optInt("boardUrl");
				if(boardson.has("Stats"))
				{
					JSONObject statsson = boardson.optJSONObject("Stats");
					board.stats = new Stats();
					board.stats.F = statsson.optInt("F");
					board.stats.G = statsson.optInt("G");
				}
				boards.add(board);
			}
			
			return boards;
			
		} catch (JSONException e) {
			throw new XiciException(XiciException.CODE_EXCEPTION_JSON);
		}
		catch (Exception e) {
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
