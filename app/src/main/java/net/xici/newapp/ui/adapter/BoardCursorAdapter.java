package net.xici.newapp.ui.adapter;

import net.xici.newapp.R;
import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.dao.BoardDao;
import net.xici.newapp.data.pojo.Board;
import net.xici.newapp.support.util.ImageUtils;
import net.xici.newapp.support.util.StringUtil;



import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BoardCursorAdapter extends CursorAdapter implements
		View.OnClickListener {

	private LayoutInflater mInflater;
	private BoardDao boardDao = null;
	protected Context context;
	private int currentid = -1;

	public BoardCursorAdapter(Context context, Cursor c) {
		super(context, c);
		mInflater = LayoutInflater.from(context);
		boardDao = new BoardDao(XiciApp.getContext());
		this.context = context;
	}
	
	public void setCurrentIndex(int position){
		Board board = getBoardItem(position);
		if(board!=null){
			currentid = board.boardUrl;
			notifyDataSetChanged();
		}
	}
	
	public int getCurrentId(){
		return currentid;
	}
	
	public void clearSelectedItem(){
		currentid = -1;
		notifyDataSetChanged();
	}
	
	public Board getBoardItem(int position)
	{
		Cursor cursor = (Cursor) getItem(position);
		Board board = boardDao.fromCursor(cursor);
		return board;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		Board board = boardDao.fromCursor(cursor);
		holder.boardname.setText(StringUtil.fromhtml(board.boardName));
		ImageUtils.displayboardlogoRound(board.boardUrl, holder.bg_logo);
		if (board.stats != null) {
			
			if(board.stats.F>0||board.stats.G>0){
				
				int newpost = (board.stats.F+board.stats.G);
				if(newpost>=999){

					holder.boardstate.setText(999+"+");
					
				}else {
					holder.boardstate.setText((board.stats.F+board.stats.G)+"");
				}
				
				holder.boardstate.setVisibility(View.VISIBLE);
				
			}else {
				holder.boardstate.setVisibility(View.GONE);
			}
			
		}else {
			holder.boardstate.setVisibility(View.GONE);
		} 
		//else {
			//holder.F.setText("0");
			//holder.G.setText("0");
		//}
		if(currentid==board.boardUrl){
			holder.board_info.setPressed(true);
		}else {
			holder.board_info.setPressed(false);
		}

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		View view = mInflater.inflate(R.layout.board_list_item2, arg2, false);
		ViewHolder holder = new ViewHolder();
		holder.boardname = (TextView) view.findViewById(R.id.boardname);
		holder.F = (TextView) view.findViewById(R.id.boardstate_f);
		holder.G = (TextView) view.findViewById(R.id.boardstate_g);
		holder.bg_logo = (ImageView)view.findViewById(R.id.bg_logo);
		holder.btn_new = (ImageView) view.findViewById(R.id.btn_new);
		holder.boardstate = (TextView)view.findViewById(R.id.boardstate);
		
		holder.board_info =  view.findViewById(R.id.board_info);
		holder.btn_new.setOnClickListener(this);
		view.setTag(holder);
		return view;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		ViewHolder  holder = (ViewHolder) view.getTag();
		holder.btn_new.setTag(position);
		return view;
	}

	class ViewHolder {
		TextView boardname;
		ImageView bg_logo;
		ImageView btn_new;
		View board_info;
		TextView F;
		TextView G;
		TextView boardstate;
	}

	@Override
	public void onClick(View v) {
        if(v.getId()==R.id.btn_new)
        {
//       	 int position = (Integer)v.getTag();
//       	 Board board = getBoardItem(position);
//       	 DocPutActivity.start(context, board.boardUrl);
        }
	}
	
}
