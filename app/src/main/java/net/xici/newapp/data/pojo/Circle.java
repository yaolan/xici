package net.xici.newapp.data.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.xici.newapp.data.pojo.Board.Stats;
import net.xici.newapp.support.request.XiciException;

public class Circle extends Basedata {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2071680239219189682L;

	public int id;

	public String name;

	public String bd_name_no_highlight;

	public int city_id;

	public String description;

	public int users_count;

	public int threads_count;

	public List<String> tags;

	public User creator;

	public static class CategoryCircle implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8418473431473840127L;

		public List<Circle> circles;

		public int total_pages;
		public int current_page;

		public CategoryCircle() {
			circles = new ArrayList<Circle>();
		}

		public static CategoryCircle parse(String str) throws XiciException {
			try {

				JSONObject Responsejson = new JSONObject(str);
				JSONObject infojson = Responsejson.optJSONObject("info");
				CheckError(infojson);

				if (infojson.isNull("data"))
					return null;

				CategoryCircle result = new CategoryCircle();
				// JSONObject jsonObject = new JSONObject(str);
				// checkstatus(jsonObject);

				JSONObject dataObject = infojson.optJSONObject("data");

				result.total_pages = dataObject.optInt("total_pages");
				result.current_page = dataObject.optInt("current_page");

				JSONArray circlesjson = dataObject.optJSONArray("circles");
				int num = circlesjson.length();
				for (int i = 0; i < num; i++) {
					JSONObject json = circlesjson.optJSONObject(i);
					Circle circle = new Circle();
					circle.id = json.optInt("id");
					circle.name = json.optString("name");
					circle.bd_name_no_highlight = json.optString("name");
					circle.users_count = json.optInt("users_count");
					circle.threads_count = json.optInt("threads_count");
					result.circles.add(circle);
				}
				return result;
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

	}

	public static class CircleSearchResult implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -919728818769601368L;

		public List<Circle> circles;

		public int total_pages;
		public int current_page;

		public CircleSearchResult() {
			circles = new ArrayList<Circle>();
		}

		public static CircleSearchResult parse(String str) throws XiciException {
			try {
				CircleSearchResult result = new CircleSearchResult();
				JSONObject jsonObject = new JSONObject(str);

				checkstatus(jsonObject);

				result.current_page = jsonObject.optInt("page");

				JSONObject circlesjson = jsonObject
						.optJSONObject("collections");
				
				JSONArray forums = jsonObject
						.optJSONArray("forums");
				
				for (int i = 0; i < forums.length(); i++) {
					String key = forums.optString(i);
					JSONObject json = circlesjson.optJSONObject(key);
					if(json!=null){
						Circle circle = new Circle();

						circle.id = json.optInt("bd_id");
						circle.name = json.optString("bd_name");
						circle.bd_name_no_highlight = circle.name.replaceAll(
								"<em>", "").replaceAll("</em>", "");
						circle.description = json.optString("bd_script");

						JSONArray tagsArray = json.optJSONArray("tags");
						circle.tags = new ArrayList<String>();
						for (int j = 0; j < tagsArray.length(); j++) {
							circle.tags.add(tagsArray.optString(j));
						}

						result.circles.add(circle);
					}
					
				}
				
				int totlecount =   jsonObject.optInt("total");
				int pagasize =   jsonObject.optInt("page_size");
				result.total_pages = totlecount/pagasize;
				if((pagasize%totlecount)>0){
					result.total_pages++;
				}
				
				return result;
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

	}

	public Board getBoard() {
		Board board = new Board();
		board.boardName = bd_name_no_highlight;
		board.boardUrl = id;
		board.stats = new Stats();
		return board;
	}

	private static void checkstatus(JSONObject json) throws XiciException {
		boolean status = json.optBoolean("status");
		if (!status) {
			throw new XiciException(XiciException.CODE_EXCEPTION_UNKNOW, "");
		}

	}

}
