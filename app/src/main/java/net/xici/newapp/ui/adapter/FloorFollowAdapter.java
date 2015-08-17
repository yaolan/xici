package net.xici.newapp.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.xici.newapp.R;
import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.dao.FloorDao;
import net.xici.newapp.data.pojo.Board;
import net.xici.newapp.data.pojo.FloorFollow;
import net.xici.newapp.data.pojo.Thread;
import net.xici.newapp.support.util.ImageUtils;
import net.xici.newapp.support.util.StringUtil;
import net.xici.newapp.support.widget.BabushkaText;
import net.xici.newapp.ui.thread.FloorListActivity;

public class FloorFollowAdapter extends CursorAdapter implements
		View.OnClickListener {

	private LayoutInflater mInflater;
	private FloorDao floorDao = null;
	protected Context context;
	private String type = "";

	public FloorFollowAdapter(Context context, Cursor c) {
		super(context, c);
		mInflater = LayoutInflater.from(context);
		floorDao = new FloorDao(XiciApp.getContext());
		this.context = context;
	}

	public void setType(String type) {
		this.type = type;
	}

	public FloorFollow getFloorItem(int position) {
		Cursor cursor = (Cursor) getItem(position);
		FloorFollow floor = floorDao.getFloorFollowfromCursor(cursor);
		return floor;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();

		FloorFollow floor = floorDao.getFloorFollowfromCursor(cursor);

		ImageUtils.displayAvatarCircle(floor.user_id, holder.avatar);

		holder.username.setText(StringUtil.fromhtml(floor.user_name));

		// holder.title.setText(floor.doc_title);
		holder.title.reset();
		holder.title.addPiece(new BabushkaText.Piece.Builder("在 ").textColor(
				Color.parseColor("#ff999999"))
				.build());
		holder.title.addPiece(new BabushkaText.Piece.Builder("《"+StringUtil.fromhtml(floor.doc_title)+"》")
				.textColor(Color.parseColor("#ff444444")).build());
		holder.title.addPiece(new BabushkaText.Piece.Builder(" 第"+floor.floor_id+type).textColor(
				Color.parseColor("#ff999999"))
				.build());
		holder.title.display();

		holder.content.setText(StringUtil.fromhtml(floor.doccontent));

		holder.from_board.setText("["+StringUtil.fromhtml(floor.bd_name)+"]");

		holder.createtime.setText(floor.time);

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		View view = mInflater.inflate(R.layout.floor_list_item_follow, arg2,
				false);
		ViewHolder holder = new ViewHolder();

		holder.avatar = (ImageView) view.findViewById(R.id.avatar);
		holder.username = (TextView) view.findViewById(R.id.username);
		holder.title = (BabushkaText) view.findViewById(R.id.title);
		holder.content = (TextView) view.findViewById(R.id.content);
		holder.from_board = (TextView) view.findViewById(R.id.from_board);
		holder.createtime = (TextView) view.findViewById(R.id.createtime);
		holder.reply = (TextView) view.findViewById(R.id.reply);
		
		holder.avatar.setOnClickListener(this);
		holder.username.setOnClickListener(this);
		
		holder.title.setOnClickListener(this);
		holder.content.setOnClickListener(this);
		holder.from_board.setOnClickListener(this);
		holder.reply.setOnClickListener(this);
		view.setTag(holder);
		return view;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		ViewHolder  holder = (ViewHolder) view.getTag();
		holder.reply.setTag(position);
		holder.username.setTag(position);
		holder.avatar.setTag(position);
		holder.from_board.setTag(position);
		holder.title.setTag(position);
		holder.content.setTag(position);
		
		return view;
	}

	class ViewHolder {
		TextView reply;
		ImageView avatar;
		TextView username;
		BabushkaText title;
		TextView content;
		TextView from_board;
		TextView createtime;
	}

	@Override
	public void onClick(View v) {
		int index = (Integer) v.getTag();
		FloorFollow floorFollow = getFloorItem(index);
		switch(v.getId()){
		case R.id.reply:
			if(onFloowFollowClickedListener!=null){
				onFloowFollowClickedListener.onReplyClicked(floorFollow);
			}
			break;
		case R.id.avatar:
		case R.id.username:
//			UserProfileActivity.start(context, floorFollow.user_name, floorFollow.user_id);
			break;
		case R.id.from_board:
			Board board = new Board();
			board.boardUrl = (int) floorFollow.bd_id;
			board.boardName = floorFollow.bd_name;
//			ThreadListActivity.start(context, board);
			break;
		case R.id.title:
		case R.id.content:
			Thread thread = new Thread();
			thread.tid = floorFollow.doc_id;
			thread.title = floorFollow.doc_title;
			context.startActivity(FloorListActivity.getIntent(context, 0, thread, floorFollow.floor_id));
			break;

		}

	}

	private OnFloowFollowClickedListener onFloowFollowClickedListener;

	
	public void setOnFloowFollowClickedListener(
			OnFloowFollowClickedListener onFloowFollowClickedListener) {
		this.onFloowFollowClickedListener = onFloowFollowClickedListener;
	}

	public interface OnFloowFollowClickedListener {
		void onReplyClicked(FloorFollow floorFollow);
	}

}
