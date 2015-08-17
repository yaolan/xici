package net.xici.newapp.data.dao;

import java.util.List;

import com.umeng.message.proguard.t;

import net.xici.newapp.data.contentprovider.FloorContentProvider;
import net.xici.newapp.data.contentprovider.ThreadContentProvider;
import net.xici.newapp.data.pojo.Floor;
import net.xici.newapp.data.pojo.FloorFollow;
import net.xici.newapp.support.util.SerializableUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

public class FloorDao extends BaseDataHelper {

	public FloorDao(Context context) {
		super(context);
	}

	@Override
	protected Uri getContentUri() {
		return FloorContentProvider.CONTENT_URI;
	}

	public ContentValues getContentValues(Floor floor, long accountid,
			int boardid, long threadid, int append) {
		ContentValues cv = new ContentValues();
		cv.put(FloorContentProvider.ACCOUNT_ID, accountid);
		cv.put(FloorContentProvider.BOARD_ID, boardid);
		cv.put(FloorContentProvider.THREAD_ID, threadid);
		cv.put(FloorContentProvider.FLOORNUM, floor.index);
		cv.put(FloorContentProvider.FLOOR_BLOB,
				SerializableUtils.toByteArray(floor));
		cv.put(FloorContentProvider.THREAD_ID, threadid);
		if (append != -1) {
			cv.put(ThreadContentProvider.APPEND, append);
		}
		return cv;
	}

	public Floor fromCursor(Cursor cursor) {
		return SerializableUtils.fromBytes(cursor.getBlob(cursor
				.getColumnIndexOrThrow(FloorContentProvider.FLOOR_BLOB)));
	}

	public FloorFollow getFloorFollowfromCursor(Cursor cursor) {
		return SerializableUtils.fromBytes(cursor.getBlob(cursor
				.getColumnIndexOrThrow(FloorContentProvider.FLOOR_BLOB)));
	}

	public int bulkInsert(List<Floor> floors, long accountid, int boardid,
			long threadid, int append) {
		if (floors == null || floors.size() == 0) {
			// 插入数据为空，判断是否情况数据
			if (append == 0) {
				// delete(accountid,boarid);
			}
			return 0;
		}
		ContentValues[] valueArray = new ContentValues[floors.size()];
		for (int i = 0; i < floors.size(); i++) {
			Floor floor = floors.get(i);
			ContentValues values = getContentValues(floor, accountid, boardid,
					threadid, append);
			valueArray[i] = values;
		}

		return bulkInsert(valueArray);
	}

	public int bulkInsertFloorFlow(List<FloorFollow> floors, long accountid,
			String type, int append) {
		if (floors == null || floors.size() == 0) {
			// 插入数据为空，判断是否情况数据
			if (append == 0) {
				// delete(accountid,boarid);
			}
			return 0;
		}
		ContentValues[] valueArray = new ContentValues[floors.size()];
		int threadid = 0;

		if (FloorFollow.TYPE_ATME.equals(type)) {
			threadid = 1;
		} else {
			threadid = 2;
		}
		for (int i = 0; i < floors.size(); i++) {
			FloorFollow floor = floors.get(i);
			ContentValues values = new ContentValues();
			
			values.put(FloorContentProvider.ACCOUNT_ID, accountid);
			values.put(FloorContentProvider.BOARD_ID, 0);
			values.put(FloorContentProvider.THREAD_ID, threadid);
			values.put(FloorContentProvider.FLOOR_BLOB,
					SerializableUtils.toByteArray(floor));
			if (append != -1) {
				values.put(ThreadContentProvider.APPEND, append);
			}
			valueArray[i] = values;
		}

		return bulkInsert(valueArray);
	}

	public Uri Insert(Floor floor, long accountid, int boardid, long threadid) {
		if (floor == null)
			return null;
		ContentValues values = getContentValues(floor, accountid, boardid,
				threadid, -1);
		return insert(values);
	}

	public CursorLoader getCursorLoader(long accountid, int boardid,
			long threadid, boolean reverse) {
		return new CursorLoader(
				getContext(),
				getContentUri(),
				null,
				FloorContentProvider.ACCOUNT_ID
						+ "=? and "
						+ FloorContentProvider.BOARD_ID
						+ "=? and "
						+ FloorContentProvider.THREAD_ID
						+ "=? and "
						+ "_id IN(SELECT min(_id) FROM floor GROUP BY account_id,board_id,thread_id,floornum )",
				new String[] { String.valueOf(accountid),
						String.valueOf(boardid), String.valueOf(threadid) },
				FloorContentProvider.FLOORNUM + (reverse ? " DESC" : " ASC"));

	}

	public void deleteByAccount(long accountid) {
		delete(FloorContentProvider.ACCOUNT_ID + "=?",
				new String[] { String.valueOf(accountid) });
	}

	public void deleteByFloor(long accountid, int boardid, long threadid,
			int floornum) {
		delete(FloorContentProvider.ACCOUNT_ID + "=? and "
				+ FloorContentProvider.BOARD_ID + "=? and "
				+ FloorContentProvider.THREAD_ID + "=? and "
				+ FloorContentProvider.FLOORNUM + "=?",
				new String[] { String.valueOf(accountid),
						String.valueOf(boardid), String.valueOf(threadid),
						String.valueOf(floornum) });
	}

	public void cleanByFloor(long accountid, int boardid, long threadid,
			Floor floor) {
		ContentValues values = getContentValues(floor, accountid, boardid,
				threadid, -1);

		update(values,
				FloorContentProvider.ACCOUNT_ID + "=? and "
						+ FloorContentProvider.BOARD_ID + "=? and "
						+ FloorContentProvider.THREAD_ID + "=? and "
						+ FloorContentProvider.FLOORNUM + "=?",
				new String[] { String.valueOf(accountid),
						String.valueOf(boardid), String.valueOf(threadid),
						String.valueOf(floor.index) });
	}
	
	public void changeUpstateByFloor(long accountid, int boardid, long threadid,
			Floor floor) {
		
		if(floor.up_status==0){
			floor.up_status = 1;
			floor.up_num ++;
		}else {
			floor.up_status = 0;
			floor.up_num --;
			if(floor.up_num<0){
				floor.up_num = 0;
			}
		}
		
		ContentValues values = getContentValues(floor, accountid, boardid,
				threadid, -1);

		update(values,
				FloorContentProvider.ACCOUNT_ID + "=? and "
						+ FloorContentProvider.BOARD_ID + "=? and "
						+ FloorContentProvider.THREAD_ID + "=? and "
						+ FloorContentProvider.FLOORNUM + "=?",
				new String[] { String.valueOf(accountid),
						String.valueOf(boardid), String.valueOf(threadid),
						String.valueOf(floor.index) });
	}

	public CursorLoader getFloorFollowCursorLoader(Context context,
			long accountid, String type) {

		int boardid = 0;
		int threadid = 0;

		if (FloorFollow.TYPE_ATME.equals(type)) {
			threadid = 1;
		} else {
			threadid = 2;
		}

		return new CursorLoader(getContext(), getContentUri(), null,
				FloorContentProvider.ACCOUNT_ID + "=? and "
						+ FloorContentProvider.BOARD_ID + "=? and "
						+ FloorContentProvider.THREAD_ID + "=?", new String[] {
						String.valueOf(accountid), String.valueOf(boardid),
						String.valueOf(threadid) }, null);

	}

	public void deleteALl() {
		delete(null, null);
	}

}
