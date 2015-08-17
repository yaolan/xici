package net.xici.newapp.data.dao;

import java.util.ArrayList;
import java.util.List;

import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.contentprovider.BoardContentProvider;
import net.xici.newapp.data.pojo.Board;
import net.xici.newapp.data.pojo.BoardCategory;
import net.xici.newapp.data.pojo.BoardSort;
import net.xici.newapp.data.pojo.Board.Stats;
import net.xici.newapp.support.util.SerializableUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

public class BoardDao extends BaseDataHelper {

	public static final String CACHEKEY = "boardsort_";

	public static final int TYPE_BOARD = 0;
	public static final int TYPE_DOC = 1;
	public static final int TYPE_BOARDSORT = 2;
	
	public static final int TYPE_BOARDCATEGORYT = 3;
	public static final int TYPE_BOARDCATEGORYT_BYID = 4;

	public BoardDao(Context context) {
		super(context);
	}

	@Override
	protected Uri getContentUri() {
		return BoardContentProvider.CONTENT_URI;
	}

	public ContentValues getContentValues(Board board, long accountid,
			int append, int type, int sort) {
		ContentValues cv = new ContentValues();
		cv.put(BoardContentProvider.BOARDID, board.boardUrl);
		cv.put(BoardContentProvider.BOARDNAME, board.boardName);
		cv.put(BoardContentProvider.BOARD_BLOB,
				SerializableUtils.toByteArray(board));
		cv.put(BoardContentProvider.ACCOUNT_ID, accountid);
		cv.put(BoardContentProvider.TYPE, type);
		cv.put(BoardContentProvider.SORT, sort);
		if (append != -1) {
			cv.put(BoardContentProvider.APPEND, append);
		}
		return cv;
	}

	public Board fromCursor(Cursor cursor) {
		return SerializableUtils.fromBytes(cursor.getBlob(cursor
				.getColumnIndexOrThrow(BoardContentProvider.BOARD_BLOB)));
	}
	
	public BoardCategory fromCursor(Cursor cursor,int type) {
		if(type==TYPE_BOARDCATEGORYT){
			return SerializableUtils.fromBytes(cursor.getBlob(cursor
					.getColumnIndexOrThrow(BoardContentProvider.BOARD_BLOB)));
		}
		return null;
	}

	public int bulkInsert(List<Board> boards, long accountid, int append,
			int type) {
		if (boards == null || boards.size() == 0) {
			// 插入数据为空，判断是否情况数据
			if (append == 0) {
				deleteByAccountAndType(accountid, type);
			}
			return 0;
		}
		ContentValues[] valueArray = new ContentValues[boards.size()];
		BoardSort sort = null;
		if (type == TYPE_BOARD) {
			sort = getBoardSort(accountid);//(BoardSort) CacheUtil.readObject(mContext, getCacheKey());
		}
		for (int i = 0; i < boards.size(); i++) {
			Board board = boards.get(i);
			ContentValues values = getContentValues(board, accountid, append,
					type, getsort(sort, board));
			valueArray[i] = values;
		}
		return bulkInsert(valueArray);
	}

	private int getsort(BoardSort sort, Board board) {
		if (sort == null || board == null) {
			return 0;
		}
		if (sort.sortMap.containsKey(String.valueOf(board.boardUrl))) {
			return sort.sortMap.get(String.valueOf(board.boardUrl));
		}
		return 0;
	}

	public int bulkInsert(List<Board> boards, long accountid, int append) {
		return bulkInsert(boards, accountid, append, TYPE_BOARD);
	}
	/**
	 * 插入 板块分类
	 * @param categorys
	 * @param append
	 * @return
	 */
	public int bulkInsert(List<BoardCategory> categorys,int append){
		
		ContentValues[] valueArray = new ContentValues[categorys.size()];
		
		for (int i = 0; i < categorys.size(); i++) {
			BoardCategory category = categorys.get(i);
			ContentValues cv = new ContentValues();
			cv.put(BoardContentProvider.BOARD_BLOB,
					SerializableUtils.toByteArray(category));
			cv.put(BoardContentProvider.TYPE, TYPE_BOARDCATEGORYT);
			cv.put(BoardContentProvider.ACCOUNT_ID, 0);
			if (append != -1) {
				cv.put(BoardContentProvider.APPEND, append);
			}
			valueArray[i] = cv;
		}
		return bulkInsert(valueArray);
	}

	public Uri insert(Board board, long accountid, int type) {
		if (board.stats == null)
			board.stats = new Stats();
		ContentValues values = getContentValues(board, accountid, -1, type, 0);
		return insert(values);
	}

	public Uri insert(Board board, long accountid) {
		return insert(board, accountid, TYPE_BOARD);
	}

	public CursorLoader getCursorLoader(long accountid, int type) {
		return new CursorLoader(getContext(), getContentUri(), null,
				BoardContentProvider.ACCOUNT_ID + "=? and "
						+ BoardContentProvider.TYPE + "=?", new String[] {
						String.valueOf(accountid), String.valueOf(type) },
				BoardContentProvider.SORT + " DESC");
	}
	
	public int getCount(long accountid, int type){
		Cursor cursor = query(null, BoardContentProvider.ACCOUNT_ID + "=? and "
						+ BoardContentProvider.TYPE + "=?",  new String[] {
						String.valueOf(accountid), String.valueOf(type) }, null);
		int count = 0;
		if(cursor!=null){
			count = cursor.getCount();
			cursor.close();
		}
		return count;
	}

	public CursorLoader getCursorLoader(long accountid) {
		return getCursorLoader(accountid, TYPE_BOARD);
	}
	
	public CursorLoader getCursorLoader(int type) {
		return new CursorLoader(getContext(), getContentUri(), null,
				BoardContentProvider.TYPE + "=?", new String[] {String.valueOf(type) },
				null);
	}

	public void deleteByAccountAndType(long accountid, int type) {
		delete(BoardContentProvider.ACCOUNT_ID + "=? and "
				+ BoardContentProvider.TYPE + "=?",
				new String[] { String.valueOf(accountid), String.valueOf(type) });
	}

	public void deleteByAccount(long accountid) {
		delete(BoardContentProvider.ACCOUNT_ID + "=?",
				new String[] { String.valueOf(accountid) });
	}

	public void deleteByAccountAndId(long accountid, int boardid, int type) {
		delete(BoardContentProvider.ACCOUNT_ID + " =? AND "
				+ BoardContentProvider.BOARDID + "=? AND "
				+ BoardContentProvider.TYPE + "=?",
				new String[] { String.valueOf(accountid),
						String.valueOf(boardid), String.valueOf(type) });
	}

	public void deleteByAccountAndId(long accountid, int boardid) {
		deleteByAccountAndId(accountid, boardid, TYPE_BOARD);
	}

	public boolean issubscribe(long accountid, int boardid) {
		boolean isfav = false;
		Cursor cursor = getList(
				null,
				BoardContentProvider.ACCOUNT_ID + "=? AND "
						+ BoardContentProvider.BOARDID + "=? AND "
						+ BoardContentProvider.TYPE + "=?",
				new String[] { String.valueOf(accountid),
						String.valueOf(boardid), String.valueOf(TYPE_BOARD) },
				null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				isfav = true;
			}
			cursor.close();
		}
		return isfav;
	}

	public void topByAccountAndId(long accountid, int boardid) {

//		BoardSort sort = (BoardSort) CacheUtil.readObject(mContext,
//				getCacheKey());
		BoardSort sort = getBoardSort(accountid);
		if (sort == null) {
			sort = new BoardSort();
		}
		sort.maxindex++;
		sort.sortMap.put(String.valueOf(boardid), sort.maxindex);

		ContentValues cv = new ContentValues();
		cv.put(BoardContentProvider.SORT, sort.maxindex);
		update(cv,
				BoardContentProvider.ACCOUNT_ID + "=? and "
						+ BoardContentProvider.BOARDID + "=? and "
						+ BoardContentProvider.TYPE + "=?",
				new String[] { String.valueOf(accountid),
						String.valueOf(boardid), String.valueOf(TYPE_BOARD) });
        insertBoardSort(accountid,sort);
	}
	
	public void updateStateByAccountAndId(long accountid, int boardid) {

		Cursor cursor = query(
				null,
				BoardContentProvider.ACCOUNT_ID + " =? and "
						+ BoardContentProvider.BOARDID+" =? and "+ BoardContentProvider.TYPE+"=?",
				new String[] { String.valueOf(accountid),String.valueOf(boardid),
						String.valueOf(TYPE_BOARD) }, null);
		Board board = null;
		if (cursor != null && cursor.getCount() > 0) {
			
			cursor.moveToFirst();
			board =  SerializableUtils.fromBytes(cursor.getBlob(cursor
					.getColumnIndexOrThrow(BoardContentProvider.BOARD_BLOB)));
			 cursor.close();
		}
		if(board!=null){
			
			ContentValues cv = new ContentValues();
			board.stats.F = 0;
			board.stats.G = 0;
			cv.put(BoardContentProvider.BOARD_BLOB, SerializableUtils.toByteArray(board));
			update(cv,
					BoardContentProvider.ACCOUNT_ID + "=? and "
							+ BoardContentProvider.BOARDID + "=? and "
							+ BoardContentProvider.TYPE + "=?",
					new String[] { String.valueOf(accountid),
							String.valueOf(boardid), String.valueOf(TYPE_BOARD) });
			
		}

		
	}
	
	

	// 排序
	public BoardSort getBoardSort(long accountid) {
		Cursor cursor = query(
				null,
				BoardContentProvider.ACCOUNT_ID + " =? and "
						+ BoardContentProvider.TYPE+"=?",
				new String[] { String.valueOf(accountid),
						String.valueOf(TYPE_BOARDSORT) }, null);
		if (cursor != null && cursor.getCount() > 0) {
			
			cursor.moveToFirst();
			return SerializableUtils.fromBytes(cursor.getBlob(cursor
					.getColumnIndexOrThrow(BoardContentProvider.BOARD_BLOB)));
		}
		return null;

	}

	public void insertBoardSort(long accountid, BoardSort sort) {
		if (constainBoardSort(accountid)) {
			updateBoardSort(accountid, sort);
		} else {
			ContentValues cv = new ContentValues();

			cv.put(BoardContentProvider.BOARD_BLOB,
					SerializableUtils.toByteArray(sort));
			cv.put(BoardContentProvider.ACCOUNT_ID, accountid);
			cv.put(BoardContentProvider.TYPE, String.valueOf(TYPE_BOARDSORT));
			insert(cv);
		}
	}

	public void updateBoardSort(long accountid, BoardSort sort) {
		ContentValues cv = new ContentValues();
		cv.put(BoardContentProvider.BOARD_BLOB,
				SerializableUtils.toByteArray(sort));
		update(cv,
				BoardContentProvider.ACCOUNT_ID + "=? and "
						+ BoardContentProvider.TYPE+"=?",
				new String[] { String.valueOf(accountid),
						String.valueOf(TYPE_BOARDSORT) });
	}

	public boolean constainBoardSort(long accountid) {
		Cursor cursor = query(
				new String[] { BoardContentProvider._ID },
				BoardContentProvider.ACCOUNT_ID + " =? and "
						+ BoardContentProvider.TYPE+"=?",
				new String[] { String.valueOf(accountid),
						String.valueOf(TYPE_BOARDSORT) }, null);
		if (cursor != null) {
			if(cursor.getCount() > 0){
				cursor.close();
				return true;
			}
			cursor.close();
		}
		return false;
	}

	public String getCacheKey() {
		return CACHEKEY + XiciApp.getAccountId();
	}
	
	public List<Board> getBoardsByAccountid(long accountid){
		
		List<Board> boards = new ArrayList<Board>();
		Cursor cursor = getList(
				null,
				BoardContentProvider.ACCOUNT_ID + "=? AND "
						+ BoardContentProvider.TYPE + "=?",
				new String[] { String.valueOf(accountid),
						 String.valueOf(TYPE_BOARD) },
				null);
		if (cursor != null) {
			int num = cursor.getCount();
			cursor.moveToFirst();
			for (int i = 0; i < num; i++) {
				Board board = fromCursor(cursor);
				boards.add(board);
				cursor.moveToNext();
			}
			cursor.close();
		}
		
		return boards;
	}
}
