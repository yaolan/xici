package net.xici.newapp.ui.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import u.aly.bo;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import de.greenrobot.event.EventBus;
import android.R.integer;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.xici.newapp.R;
import net.xici.newapp.R.id;
import net.xici.newapp.R.string;
import net.xici.newapp.app.Constants;
import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.dao.BoardDao;
import net.xici.newapp.data.pojo.Board;
import net.xici.newapp.data.pojo.TopPic;
import net.xici.newapp.data.pojo.XiciResult;
import net.xici.newapp.data.pojo.TopPic.TopPicSer;
import net.xici.newapp.support.request.ApiClient;
import net.xici.newapp.support.request.XiciException;
import net.xici.newapp.support.util.CacheUtil;
import net.xici.newapp.support.util.DateUtils;
import net.xici.newapp.support.util.ImageUtils;
import net.xici.newapp.support.util.XiciResponseHandler;

import net.xici.newapp.ui.adapter.BoardCursorAdapter;
import net.xici.newapp.ui.adapter.TopPicAdapter;
import net.xici.newapp.ui.base.BaseFragment;
import net.xici.newapp.ui.base.BaseListViewFragment;
import net.xici.newapp.ui.thread.ThreadListActivity;
import net.xici.newapp.ui.web.WebViewActivity;

/**
 * 收藏版块
 * 
 * @author bkmac
 * 
 */
public class BoardFragment extends BaseListViewFragment implements
		LoaderManager.LoaderCallbacks<Cursor>,
		AdapterView.OnItemLongClickListener, View.OnClickListener {

	private static int type = 0;
	private static String group = "";
	private static int stats = 1;

	private RelativeLayout toppicLayout;
	private ViewPager mtopViewPager;
	private TextView num;
	private BoardDao boadrdDao;
	private View header;
	private View footer;
	private LinearLayout footer_list_container;
	private long userid = -1;

	private TopPicSer topPics;
	private TopPicAdapter mTopPicAdapter;
	
	private Board currentBoard;
	
	public final static int ENTER_BOARD = 0;
	public final static int TOP_BOARD = 1;
	public final static int DELETE_BOARD = 2;

	public static BoardFragment newInstance() {
		BoardFragment fragment = new BoardFragment();
		return fragment;
	}

	@Override
	protected void loadfirst() {

		page = 1;
		doGetUserFavList();

	}

	@Override
	protected void loadnext() {


	}

	@Override
	protected void buildListAdapter() {
		mAdapter = new BoardCursorAdapter(getActivity(), null);
	}

	@Override
	protected void listViewItemClick(AdapterView parent, View view,
			int position, long id) {

		Board board = ((BoardCursorAdapter) mAdapter).getBoardItem(position);
		ThreadListActivity.start(getActivity(), board);
		boadrdDao.updateStateByAccountAndId(getAccountId(), board.boardUrl);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View insideview = super.onCreateView(inflater, container,
				savedInstanceState);
		header = inflater.inflate(R.layout.board_fragment_header,
				getListView(), false);
		footer = inflater.inflate(R.layout.board_fragment_footer,
				getListView(), false);
		footer_list_container = (LinearLayout) footer
				.findViewById(id.footer_list_container);
		getListView().setPadding(0, 0, 0, 0);

		toppicLayout = (RelativeLayout) header.findViewById(id.toppic_layout);
		mtopViewPager = (ViewPager) header.findViewById(id.viewpager_top);
//		mIndicator = (CirclePageIndicator) header.findViewById(id.indicator);

		num = (TextView) header.findViewById(id.num);
		getListView().addHeaderView(header);
		getListView().addFooterView(footer);
		getListView().setDivider(null);
		getListView().setSelector(R.drawable.transparent);
//		((JazzyListView) getListView())
//				.setTransitionEffect(JazzyHelper.SLIDE_IN);
//		((JazzyListView) getListView()).setShouldOnlyAnimateNewItems(true);
		getListView().setOnItemLongClickListener(this);
		return insideview;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		boadrdDao = new BoardDao(XiciApp.getContext());
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(getActivity());

		mTopPicAdapter = new TopPicAdapter(getActivity(), null);
		mTopPicAdapter
				.setOnItemClickListener(new TopPicAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(int postion) {
						TopPic topPic = mTopPicAdapter.getItem(postion);
						if (topPic != null) {
							WebViewActivity.start(getActivity(),
									topPic.picname, topPic.link);
						}

					}
				});

		mtopViewPager.setAdapter(mTopPicAdapter);
//		mIndicator.setViewPager(mtopViewPager);

		registerForContextMenu(getListView());

	}

	@Override
	public void onResume() {
		super.onResume();
		if (userid == -1) {
			userid = getAccountId();
			// initView();
			getLoaderManager().initLoader(1, null, this);
		} else {
			if (userid != getAccountId()) {
				userid = getAccountId();
				restart();
			}
		}
	}

	public void restart() {
		first = true;
		getLoaderManager().restartLoader(1, null, this);
	}

	/**
	 * 用户收藏版块
	 */
	private void doGetUserFavList() {
		ApiClient.user_fav_list(getActivity(), getAccountId(),
				String.valueOf(type), group, stats, new XiciResponseHandler() {
					@Override
					public void onStart() {
						super.onStart();
					}

					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						try {
							List<Board> result = Board.parse(arg0);
							if (result != null) {
								boadrdDao.bulkInsert(result, getAccountId(), 0);
								num.setText("" + result.size());
							} else {
								num.setText("0");
							}
							onFinishRequest();
						} catch (XiciException e) {
							OnApiException(e);
						}
					}

					@Override
					public void onFailure(Throwable arg0) {
						super.onFailure(arg0);
						onAPIFailure();
						onFinishRequest();
					}

					@Override
					public void onFinish() {
						super.onFinish();
					}

				});
	}

	/**
	 * 获取广告条
	 */
	private void getTopPic() {
		ApiClient.getTopPic(getActivity(), new XiciResponseHandler() {

			@Override
			public void onSuccess(String str) {
				super.onSuccess(str);
				try {
					if (!TextUtils.isEmpty(str)) {

						if ("{".equals(str.charAt(0))) {
							topPics = TopPic.parse(str);
						} else {
							topPics = TopPic.parse(str.substring(1,
									str.length()));
						}

						if (topPics != null) {
							CacheUtil.saveObject(getActivity(), topPics,
									Constants.CACHEKEY_TOPPIC);
						}

					}

				} catch (XiciException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (topPics == null) {
					Object object = CacheUtil.readObject(getActivity(),
							Constants.CACHEKEY_TOPPIC);
					if (object != null) {
						topPics = (TopPicSer) object;
					}
				}

				mTopPicAdapter.clear();
				mTopPicAdapter.notifyDataSetChanged();
				if (topPics != null) {

					for (TopPic topPic : topPics.list) {
						if (DateUtils.timeEanble(topPic.start, topPic.end)) {
							mTopPicAdapter.add(topPic);
						}
					}
					mTopPicAdapter.notifyDataSetChanged();
					if (mTopPicAdapter.getCount() > 0) {
						toppicLayout.setVisibility(ViewPager.VISIBLE);
//						mIndicator.setVisibility(mTopPicAdapter.getCount() == 1 ? View.GONE
//								: View.VISIBLE);
					}
				}

			}

		});
	}
	
	
	/**
	 * 预定，退订板块
	 */
	private void doChangeBoardSubscribe() {

		ApiClient.change_board_subscribe_state(true, getActivity(),
				currentBoard.boardUrl, "", new XiciResponseHandler() {

					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						try {
							XiciResult result = null;

							// 退订
							result = XiciResult.quitbdparse(arg0);
							if (result.success) {
								boadrdDao.deleteByAccountAndId(
										getAccountId(), currentBoard.boardUrl);
							}

						} catch (XiciException e) {
							OnApiException(e, false);
						}
					}

					@Override
					public void onFailure(Throwable arg0) {
						super.onFailure(arg0);
						onAPIFailure();
					}

					@Override
					public void onFinish() {
						super.onFinish();
					}

				});

	}

	@Override
	protected void onFinishRequest() {
		super.onFinishRequest();
	}

	@Override
	protected void onFinishException(int action) {
		super.onFinishException(action);
		onFinishRequest(action);
	}

	@Override
	protected void onRequestAgain(int action) {
		super.onRequestAgain(action);
		doGetUserFavList();
	}



	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return boadrdDao.getCursorLoader(getAccountId());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		((BoardCursorAdapter) mAdapter).swapCursor(data);
		if (data == null)
			return;
		if (XiciApp.getfirst(String.valueOf(getAccountId()))) {
			//XiciApp.loadboadfragment = true;
			XiciApp.setfirst(String.valueOf(getAccountId()));

		}
		handler.removeMessages(MESSAGE_ADDFOOT);
		handler.sendEmptyMessageDelayed(MESSAGE_ADDFOOT, 500);
		num.setText("" + data.getCount());
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		((BoardCursorAdapter) mAdapter).swapCursor(null);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (isPositionBetweenHeaderViewAndFooterView(position)) {
			
			currentBoard = null;
			
			currentBoard = ((BoardCursorAdapter) mAdapter).getBoardItem(position
					- getListView().getHeaderViewsCount());
			
			if(currentBoard!=null)
			{
				return false;
			}
		}

		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		if(currentBoard!=null){
			menu.setHeaderTitle(currentBoard.boardName);
			menu.clear();

			menu.add(0, ENTER_BOARD, 0, "进版");
			menu.add(0, TOP_BOARD, 0, "置顶");
			menu.add(0, DELETE_BOARD, 0, "退版");
			
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ENTER_BOARD:
//			ThreadListActivity.start(getActivity(), currentBoard);
			break;
		case DELETE_BOARD:

			MobclickAgent.onEvent(getActivity(), "Login_boardout");
			doChangeBoardSubscribe();
			break;
		case TOP_BOARD:

			MobclickAgent.onEvent(getActivity(), "Homelogin_up");
			boadrdDao.topByAccountAndId(getAccountId(), currentBoard.boardUrl);
			break;
		
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 
	 * 推荐版块
	 * 
	 * 用户收藏数小于5时，随机生成5个版块作为推荐版块
	 * 
	 * 
	 */
	private static final int MESSAGE_ADDFOOT = 1;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case MESSAGE_ADDFOOT:
				addBoardFoorerView();
				break;

			default:
				break;
			}

		};
	};

	private ImageView boardlogo1, boardlogo2, boardlogo3, boardlogo4,
			boardlogo5;
	private TextView boardname1, boardname2, boardname3, boardname4,
			boardname5;
	private List<Board> footBoards = null;

	private void addBoardFoorerView() {
		
		if(!isAdded()||getListView().getFooterViewsCount()<=0)
			return;
		
		if(mAdapter.getCount()>10){
			
			getListView().removeFooterView(footer);
			//footer.setVisibility(View.GONE);
			//footer_list_container.setVisibility(View.GONE);
			mAdapter.notifyDataSetChanged();
		}else {
			
			boardlogo1 = (ImageView) footer.findViewById(id.boardlogo1);
			boardlogo2 = (ImageView) footer.findViewById(id.boardlogo2);
			boardlogo3 = (ImageView) footer.findViewById(id.boardlogo3);
			boardlogo4 = (ImageView) footer.findViewById(id.boardlogo4);
			boardlogo5 = (ImageView) footer.findViewById(id.boardlogo5);

			boardname1 = (TextView) footer.findViewById(id.boardname1);
			boardname2 = (TextView) footer.findViewById(id.boardname2);
			boardname3 = (TextView) footer.findViewById(id.boardname3);
			boardname4 = (TextView) footer.findViewById(id.boardname4);
			boardname5 = (TextView) footer.findViewById(id.boardname5);
			
			footer.findViewById(id.board_1).setOnClickListener(this);
			footer.findViewById(id.board_2).setOnClickListener(this);
			footer.findViewById(id.board_3).setOnClickListener(this);
			footer.findViewById(id.board_4).setOnClickListener(this);
			footer.findViewById(id.board_5).setOnClickListener(this);
			footer.findViewById(id.change).setOnClickListener(this);

			footer.setVisibility(View.VISIBLE);
			footer_list_container.setVisibility(View.VISIBLE);
			footBoards = new ArrayList<Board>();

			List<Board> cache = new ArrayList<Board>();
			String[] boardname = getResources().getStringArray(
					R.array.default_board_name);
			String[] boardid = getResources().getStringArray(
					R.array.default_board_id);

			for (int i = 0; i < boardid.length; i++) {
				Board b = new Board();
				b.boardName = boardname[i];
				b.boardUrl = Integer.parseInt(boardid[i]);
				cache.add(b);
			}

			List<Board> myBoards = boadrdDao.getBoardsByAccountid(getAccountId());
			for (Board myb : myBoards) {

				for (Board b : cache) {

					if (b.boardUrl == myb.boardUrl) {
						cache.remove(b);
						break;
					}
				}
			}

			footBoards.clear();

			for (int i = 0; i < 5; i++) {

				int j = (int) (Math.random() * cache.size());
				if (j < cache.size()) {
					footBoards.add(cache.get(j));
					cache.remove(j);
				}

			}

			ImageUtils
					.displayboardlogoRound(footBoards.get(0).boardUrl, boardlogo1);
			boardname1.setText(footBoards.get(0).boardName);

			ImageUtils
					.displayboardlogoRound(footBoards.get(1).boardUrl, boardlogo2);
			boardname2.setText(footBoards.get(1).boardName);

			ImageUtils
					.displayboardlogoRound(footBoards.get(2).boardUrl, boardlogo3);
			boardname3.setText(footBoards.get(2).boardName);

			ImageUtils
					.displayboardlogoRound(footBoards.get(3).boardUrl, boardlogo4);
			boardname4.setText(footBoards.get(3).boardName);

			ImageUtils
					.displayboardlogoRound(footBoards.get(4).boardUrl, boardlogo5);
			boardname5.setText(footBoards.get(4).boardName);
			
		}

		

	}

	@Override
	public void onClick(View v) {

	    int index = 0;
		switch (v.getId()) {
		case id.change:
			MobclickAgent.onEvent(getActivity(), "Login_change");
			handler.removeMessages(MESSAGE_ADDFOOT);
			handler.sendEmptyMessageDelayed(MESSAGE_ADDFOOT, 500);
			return;
		case id.board_1:
			index = 0;
			break;
		case id.board_2:
			index = 1;
			break;
		case id.board_3:
			index = 2;
			break;
		case id.board_4:
			index = 3;
			break;
		case id.board_5:
			index = 4;
			break;

		default:
			break;
		}
		
		if(index<footBoards.size()){
//			ThreadListActivity.start(getActivity(), footBoards.get(index));
		}

	}
	
	@Override
	public void onStop() {
		super.onStop();

		unregisterForContextMenu(getListView());
	}
	


}
