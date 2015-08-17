package net.xici.newapp.ui.thread;

import java.util.HashMap;
import java.util.Map;


import com.afollestad.materialdialogs.MaterialDialog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMSocialService;

import net.xici.newapp.R;
import net.xici.newapp.app.Constants;
import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.dao.BoardDao;
import net.xici.newapp.data.dao.ThreadDao;
import net.xici.newapp.data.dao.UnreadDao;
import net.xici.newapp.data.pojo.Board;
import net.xici.newapp.data.pojo.BoardInfo;
import net.xici.newapp.data.pojo.Right;
import net.xici.newapp.data.pojo.Thread;
import net.xici.newapp.data.pojo.Thread.ThreadsResult;
import net.xici.newapp.data.pojo.XiciResult;
import net.xici.newapp.support.request.ApiClient;
import net.xici.newapp.support.request.XiciException;
import net.xici.newapp.support.util.ImageUtils;
import net.xici.newapp.support.util.PoppyViewHelper;
import net.xici.newapp.support.util.StringUtil;
import net.xici.newapp.support.util.UIUtil;
import net.xici.newapp.support.util.XiciResponseHandler;

import net.xici.newapp.ui.adapter.ArrayAdapter;
import net.xici.newapp.ui.adapter.ThreadCursorAdapter;
import net.xici.newapp.ui.base.BaseListViewFragment;
import android.app.Activity;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ThreadListFragment extends BaseListViewFragment implements
		LoaderManager.LoaderCallbacks<Cursor>,
		AdapterView.OnItemLongClickListener {

	private int sort = 1; //0最新发布 1最近更新

	private final static int GET_BOARD_THREAD = 1; // 列表
	private final static int CHANGE_BOARD_SUBSCRIBE = 2; // 预订，退订
	private final static int DELETE_THREAD = 3; // 删除
	private final static int RECOMM_THREAD = 4; // 推荐置顶
	private final static int TOP_THREAD = 5; // 普通置顶
	private final static int GET_BOARDINFO = 6; // 获取版块详情
	private final static int COOL_THREAD = 7; // 加酷

	private static String cachekey = "board_threads";

	private Board mainboard; // 主板
	private Board board; // 当前版块
	private BoardInfo boardInfo; // 版块详细

	private int page = 1;

	private ThreadDao threadDao;
	private BoardDao boardDao;
	private UnreadDao unreadDao;

	private boolean boardissubscribe = false;
	private boolean menufirstPrepare = true;

	private LinearLayout subscribe_board_layout;
	private Right right = null;
	private Thread currentThread;
	private int navindex = 0 ;//导航
	private View header;
	private ImageView board_logo;
	private TextView board_name;
	private TextView board_users_count;
	private TextView board_post_count;

	private final static int MSG_NEW = 1;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_NEW:
				//刷新
				break;

			default:
				break;
			}

		}
	};


	@Override
	protected void loadfirst() {

		page = 1;
		doGetBoardThreads();

	}

	@Override
	protected void loadnext() {


	}

	// private ActionMode mActionMode;
	private MaterialDialog dialog;

	public static ThreadListFragment newInstance(Bundle b) {
		ThreadListFragment fragment = new ThreadListFragment();
		fragment.setArguments(b);
		return fragment;
	}

	@Override
	protected void buildListAdapter() {
		mAdapter = new ThreadCursorAdapter(getActivity(), null);
	}

	@Override
	protected void listViewItemClick(AdapterView parent, View view,
			int position, long id) {
		Thread thread = ((ThreadCursorAdapter) mAdapter)
				.getThreadItem(position);
		unreadDao.insertThread(thread.tid, getAccountId());
		mAdapter.notifyDataSetChanged();
		FloorListActivity.start(getActivity(), mainboard.boardUrl, thread);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View insideview = super.onCreateView(inflater, container,
				savedInstanceState);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		View view = inflater.inflate(R.layout.thread_list_fragment, container,
				false);
		((FrameLayout) view.findViewById(R.id.container)).addView(insideview,
				lp);
		subscribe_board_layout = (LinearLayout) view
				.findViewById(R.id.subscribe_board_layout);
		subscribe_board_layout.setOnClickListener(new View.OnClickListener() {

			//收藏该版
			@Override
			public void onClick(View v) {
				if (XiciApp.islogined()) {
					doChangeBoardSubscribe();
				} else {
					//UIUtil.showToast(getActivity(), R.string.not_login);
//					startLoginActivity();
				}

			}
		});
		
		header = inflater.inflate(R.layout.thread_fragment_header,
				getListView(), false);
		board_logo = (ImageView) header.findViewById(R.id.board_logo);
		board_name = (TextView) header.findViewById(R.id.board_name);
		board_post_count = (TextView) header.findViewById(R.id.board_post_count);
		board_users_count = (TextView) header.findViewById(R.id.board_users_count);
		
		getListView().addHeaderView(header);
		

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		board = (Board) getArguments().getSerializable("board");
		mainboard = board;

		
		board_name.setText(mainboard.boardName);
		ImageUtils.displayboardlogo(mainboard.boardUrl, board_logo);


		new PoppyViewHelper().initPoppyViewOnListView(getListView(),
				subscribe_board_layout, null);

		threadDao = new ThreadDao(XiciApp.getContext());
		boardDao = new BoardDao(XiciApp.getContext());
		unreadDao = new UnreadDao(XiciApp.getContext());

		getLoaderManager().initLoader(mainboard.boardUrl, null, this);
		handler.postDelayed(issubscribeRunnable, 200);

		registerForContextMenu(getListView());

		doGetBoardinfo();
		
		if (XiciApp.islogined()) {

			MobclickAgent.onEvent(getActivity(), "Login_b");

		} else {

			MobclickAgent.onEvent(getActivity(), "visitor_b");
		}
		
		if(XiciApp.islogined()&&!XiciApp.relogin){
			XiciApp.relogin = true;
			dologin(BASEACTION);
		}
	}

	//导航，子版
	private void buildActionBarNav() {
		
		if(!isAdded()||boardInfo==null)
			return;
		mainboard.boardName = boardInfo.bd_name;
//		mActionBar.setTitle(mainboard.boardName);
		board_name.setText(StringUtil.fromhtml(mainboard.boardName));
		
		board_users_count.setText(boardInfo.users+"");
		
		if(boardInfo.sub_boards==null||boardInfo.sub_boards.size()==0)
			return;
		
//		mActionBar.setDisplayShowHomeEnabled(true);
//		mActionBar.setDisplayShowTitleEnabled(false);
//		mActionBar.setDisplayHomeAsUpEnabled(true);
//		mActionBar.setDisplayUseLogoEnabled(true);
//		mActionBar.setDisplayUseLogoEnabled(true);
//		mActionBar.setLogo(R.drawable.xici_logo_small);
//
//		mActionBar.setTitle("");
//		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		String[] strings = new String[boardInfo.sub_boards.size()+1];
		strings[0] = StringUtil.fromhtml(mainboard.boardName);
		for (int i = 0; i < boardInfo.sub_boards.size(); i++) {
			strings[1+i] =StringUtil.fromhtml(boardInfo.sub_boards.get(i).boardName) ;
		}
//		SpinnerAdapter navadapter =  new ArrayAdapter<CharSequence>(getActivity(),
//				R.layout.spinner_dropdown_item_black, strings);
//				ArrayAdapter.createFromResource(
//				getSupportActivity(), R.array.floor_list_nav_type,
//				R.layout.spinner_dropdown_item_black);



		//
//		mActionBar.setListNavigationCallbacks(navadapter,
//				new OnNavigationListener() {
//
//					@Override
//					public boolean onNavigationItemSelected(int itemPosition,
//							long itemId) {
//						if(navindex!=itemPosition){
//							reSetList();
//							navindex = itemPosition;
//							if(navindex==0){
//								board = mainboard;
//							}else if(boardInfo!=null&&boardInfo.sub_boards!=null&&navindex<=boardInfo.sub_boards.size()){
//								board = boardInfo.sub_boards.get(navindex-1);
//							}
//							if (isAdded()) {
//								getLoaderManager().restartLoader(
//										mainboard.boardUrl, null,
//										ThreadListFragment.this);
//							}
//							handler.removeMessages(MSG_NEW);
//							handler.sendEmptyMessageDelayed(MSG_NEW, 200);
//						}
//						return true;
//					}
//
//				});
	}
	
	private void reSetList() {
		ApiClient.cancel(getActivity(), true);
//		setRefreshComplete();
	}

	// 长按处理
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		int headerViewsCount = getListView().getHeaderViewsCount();
		if (isPositionBetweenHeaderViewAndFooterView(position)) {
			int indexInDataSource = position - headerViewsCount;
			currentThread = ((ThreadCursorAdapter) mAdapter)
					.getThreadItem(indexInDataSource);
			if (right != null
					&& (right.isAdminDeleted || right.isDelDocs || right.isAdminRecomm)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("管理帖子");
		menu.clear();
		if (right != null && currentThread != null) {
			if (right.isAdminDeleted || right.isDelDocs) {
				menu.add(0, DELETE_THREAD, 0, "删除帖子");
			}
			if (right.isAdminRecomm && currentThread.top == 0) {

				menu.add(0, RECOMM_THREAD, 0, "推荐置顶");
				menu.add(0, TOP_THREAD, 0, "普通置顶");

			} else if (right.isAdminRecomm && currentThread.top > 0) {

				menu.add(0, TOP_THREAD, 0, "取消置顶");

			}
			
			if (right.isAdminCool && currentThread.cool == 0) {

				menu.add(0, COOL_THREAD, 0, "加酷");

			} else if (right.isAdminCool && currentThread.cool > 0) {

				menu.add(0, COOL_THREAD, 0, "取消加酷");

			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_THREAD:

			MobclickAgent.onEvent(getActivity(), "host_clear");
			doDocDelete(String.valueOf(currentThread.tid));
			break;
		case RECOMM_THREAD:
			MobclickAgent.onEvent(getActivity(), currentThread.top == 0?"host_up":"host_down");
			doDocRecomm(1, String.valueOf(currentThread.tid));
			break;
		case TOP_THREAD:
			MobclickAgent.onEvent(getActivity(), currentThread.top == 0?"host_up":"host_down");
			doDocRecomm(currentThread.top == 0 ? 2 : 0,
					String.valueOf(currentThread.tid));
			break;
		case COOL_THREAD:
			MobclickAgent.onEvent(getActivity(), currentThread.cool == 0?"host_jiaku":"host_quxiaojiaku");
			doDocCool(currentThread.cool == 0 ? 1 : 0,
					String.valueOf(currentThread.tid));
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 获取列表
	 */
	private void doGetBoardThreads() {
		ApiClient.board_threads(getActivity(), board.boardUrl, page, "", "",
				sort, XiciApp.account != null ? true : false,
				new XiciResponseHandler() {

					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						try {
							ThreadsResult result = Thread.parse(arg0);
							if (result != null) {
								//权限
								right = result.right;
								if (page <= 1) {
									board_post_count.setText(String.valueOf(result.count));
									threadDao.bulkInsert(result.threads,
											getAccountId(), sort,
											board.boardUrl, 0);
									if (isAdded()) {
										getLoaderManager().restartLoader(
												mainboard.boardUrl, null,
												ThreadListFragment.this);
									}
								} else {
									threadDao.bulkInsert(result.threads,
											getAccountId(), sort,
											board.boardUrl, 1);
								}
								page++;
								if (result.threads.size() < Constants.PAGESIZE) {
								} else {
								}
								
							} else {

							}
							
						} catch (XiciException e) {
							OnApiException(e, GET_BOARD_THREAD);
						}

					}

					@Override
					public void onFailure(Throwable arg0) {
						super.onFailure(arg0);
						onAPIFailure();
						onFinishException(GET_BOARD_THREAD);
					}

				});

	}

	/**
	 * 预定，退订板块
	 */
	private void doChangeBoardSubscribe() {
		
		MobclickAgent.onEvent(getActivity(), boardissubscribe?"Login_yudingout":"Login_yuding");

		ApiClient.change_board_subscribe_state(boardissubscribe, getActivity(),
				mainboard.boardUrl, "", new XiciResponseHandler() {

					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						try {
							XiciResult result = null;

							if (boardissubscribe) {
								// 退订
								result = XiciResult.quitbdparse(arg0);
								if (result.success) {
									boardDao.deleteByAccountAndId(
											getAccountId(), mainboard.boardUrl);
								}
							} else {
								// 预定
								result = XiciResult.bookbdparse(arg0);
								if (result.success) {
									boardDao.insert(mainboard, getAccountId());
								}
							}
							if (result.success) {
								handler.postDelayed(issubscribeRunnable, 200);
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

	/**
	 * 删帖
	 * 
	 * @param tids
	 */
	private void doDocDelete(final String tids) {
		if (TextUtils.isEmpty(tids))
			return;
		ApiClient.doc_belete(getActivity(), board.boardUrl, tids,
				new XiciResponseHandler() {

					public void onStart() {
//						dialog = new DialogsProgressDialogIndeterminateFragment(
//								"");
//						dialog.setCancelable(false);
//						dialog.show(getSupportActivity());
					};

					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						try {
							XiciResult result = XiciResult.docDeleteparse(arg0);
							if (result != null && result.success) {
								threadDao.deleteByID(getAccountId(),
										board.boardUrl, Long.parseLong(tids));
								Toast.makeText(getActivity(), "帖子已删除",
										Toast.LENGTH_SHORT).show();
							}
							onFinishException(DELETE_THREAD);
						} catch (XiciException e) {
							OnApiException(e, DELETE_THREAD);
						}

					}

					@Override
					public void onFailure(Throwable arg0) {
						super.onFailure(arg0);
						onAPIFailure();
						onFinishException(DELETE_THREAD);
					}
				});
	}

	/**
	 * 置顶
	 * 
	 * @param type
	 * @param tids
	 */
	private void doDocRecomm(final int type, final String tids) {
		if (TextUtils.isEmpty(tids))
			return;
		ApiClient.doc_recomm(getActivity(), type, board.boardUrl, tids,
				new XiciResponseHandler() {

					public void onStart() {
//						dialog = new DialogsProgressDialogIndeterminateFragment(
//								"");
//						dialog.setCancelable(false);
//						dialog.show(getSupportActivity());
					};

					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						try {
							XiciResult result = XiciResult.DocRecommparse(arg0);
							if (result != null && result.success) {
								if (type == 0) {
									Toast.makeText(getActivity(),
											"帖子已取消置顶", Toast.LENGTH_SHORT)
											.show();
								} else {
									Toast.makeText(getActivity(),
											"帖子已置顶", Toast.LENGTH_SHORT).show();
								}

//								setRefreshing(true);
//								loadNewMsg();
							}
							onFinishException(RECOMM_THREAD);
						} catch (XiciException e) {
							OnApiException(e, RECOMM_THREAD);
						}

					}

					@Override
					public void onFailure(Throwable arg0) {
						super.onFailure(arg0);
						onAPIFailure();
						onFinishException(RECOMM_THREAD);
					}
				});
	}
	
	/**
	 * 置顶
	 * 
	 * @param type
	 * @param tids
	 */
	private void doDocCool(final int type, final String tids) {
		if (TextUtils.isEmpty(tids))
			return;
		ApiClient.doc_cool(getActivity(), type, board.boardUrl, tids,
				new XiciResponseHandler() {

					public void onStart() {
//						dialog = new DialogsProgressDialogIndeterminateFragment(
//								"");
//						dialog.setCancelable(false);
//						dialog.show(getSupportActivity());
					};

					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						try {
							XiciResult result = XiciResult.DocCoolparse(arg0);
							if (result != null && result.success) {
								if (type == 0) {
									Toast.makeText(getActivity(),
											"帖子已取消加酷", Toast.LENGTH_SHORT)
											.show();
								} else {
									Toast.makeText(getActivity(),
											"帖子已加酷", Toast.LENGTH_SHORT).show();
								}


							}
							onFinishException(COOL_THREAD);
						} catch (XiciException e) {
							OnApiException(e, COOL_THREAD);
						}

					}

					@Override
					public void onFailure(Throwable arg0) {
						super.onFailure(arg0);
						onAPIFailure();
						onFinishException(COOL_THREAD);
					}
				});
	}

	/**
	 * 版块详细
	 */
	private void doGetBoardinfo() {
		ApiClient.board_info(getActivity(), mainboard.boardUrl,
				new XiciResponseHandler() {

					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						try {
							boardInfo = BoardInfo.parse(arg0);
							buildActionBarNav();
							onFinishRequest(GET_BOARDINFO);

						} catch (XiciException e) {
							OnApiException(e, false, GET_BOARDINFO);
						}
					}

					@Override
					public void onFailure(Throwable arg0) {
						super.onFailure(arg0);
						onAPIFailure();
						onFinishException(GET_BOARDINFO);
					}

				});
	}

	@Override
	protected void onFinishException(int action) {
		super.onFinishException();
		switch (action) {
		case GET_BOARD_THREAD:
			if (page > 1) {
//				showErrorFooterView();
			} else {
//				setRefreshComplete();
			}
			break;
		case DELETE_THREAD:
		case RECOMM_THREAD:
		case COOL_THREAD:
			if (dialog != null) {
//				dialog.dismissAllowingStateLoss();
			}
			break;

		default:
			break;
		}

	}

	@Override
	protected void onRequestAgain(int action) {
		super.onRequestAgain();
		switch (action) {
		case GET_BOARD_THREAD:
			doGetBoardThreads();
			break;
		case DELETE_THREAD:
			doDocDelete(((ThreadCursorAdapter) mAdapter).getSelectedItemID());
			break;
		default:
			break;
		}

	}



	// 加载数据
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return threadDao.getCursorLoaderBysort(getAccountId(), board.boardUrl,
				sort);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {

		if (first) {
			first = false;
			// mPullToRefreshListView.setRefreshing();
		} else {
			((ThreadCursorAdapter) mAdapter).swapCursor(cursor);
//			showMoreFooterViewifNeed(Constants.PAGESIZE);
		}
		//showEmptyifNeed();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		((ThreadCursorAdapter) mAdapter).swapCursor(null);
	}

	// 菜单
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
//		if (!menufirstPrepare) {
//			menu.clear();
//			MenuInflater inflater = getMenuInflater();
//			if (!boardissubscribe || getAccount() == null) {
//				inflater.inflate(R.menu.menu_board_subscribe, menu);
//				if (2488 != board.boardUrl) {
//					subscribe_board_layout.setVisibility(View.VISIBLE);
//				}
//			} else {
//				if (getAccount() != null) {
//					inflater.inflate(R.menu.menu_board_unsubscribe, menu);
//					subscribe_board_layout.setVisibility(View.GONE);
//				}
//			}
//			MenuItem sortItem = menu.findItem(R.id.action_sort);
//			if (sortItem != null) {
//				sortItem.setTitle(sort == 0 ? "最近更新" : "最新发布");
//			}
//		}
	}

	public Intent createShareIntent() {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setAction(Intent.ACTION_SEND)
				.putExtra(
						Intent.EXTRA_TEXT,
						"我正在使用\"西祠胡同\"Android客户端,有热版推荐:"+mainboard.boardName+",http://3g.xici.net/b"
								+ mainboard.boardUrl
								+ "?from=NewAndroidCoreBoard【来自西祠胡同】")
				.setType("text/plain");
		return share;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
//		case R.id.action_add:
//			if (!XiciApp.islogined()) {
//
//				//UIUtil.showToast(getActivity(), R.string.not_login);
////				startLoginActivity();
//
//				return true;
//			} else if(!XiciApp.account.isRealUser()){
//
//				Toast.makeText(getActivity(), "您还不是真实网友哦，请升级后再尝试。", Toast.LENGTH_SHORT).show();
//				return true;
//
//			}else{
//				//DocPutActivity.start(getActivity(), board.boardUrl);
//
//				MobclickAgent.onEvent(getActivity(),"all_set");
//
//				startActivityForResult(DocPutActivity.getIntent(getActivity(), board.boardUrl), REQUEST_DOCADD);
//			}
//
//			break;
//		case R.id.action_refresh:
//			ApiClient.cancel(getActivity(), true);
//			setRefreshComplete();
//			dismissFooterView();
//
//			handler.removeMessages(MSG_NEW);
//			handler.sendEmptyMessageDelayed(MSG_NEW, 200);
//			// handler.s
//			break;
//		case R.id.action_top:
//			mListView.setSelection(0);
//			break;
//		case R.id.action_share:
//
//			// 首先在您的Activity中添加如下成员变量
//			UMSocialService mController = ((ThreadListActivity)getActivity()).getUMSocialService();
//					// UMServiceFactory.getUMSocialService("com.umeng.share");
//
//			// 设置分享内容
//			UMengUtil.allSsoHandle(getActivity(), mController);
//			UMengUtil.setSharecontent(getActivity(), mController,
//					 "热版推荐:"+mainboard.boardName,
//					 "我正在使用\"西祠胡同\"Android客户端,有热版推荐",
//					"http://3g.xici.net/b"+ mainboard.boardUrl);
//
//			mController.openShare(getActivity(), false);
//
//			break;
//		case R.id.action_desktop:
//
//			MobclickAgent.onEvent(getActivity(), "b_table");
//			addShortcut();
//			break;
//		case R.id.action_unsubscribe:
//			doChangeBoardSubscribe();
//			break;
//		case R.id.action_sort:
//
//
//			MobclickAgent.onEvent(getActivity(), sort == 0?"b_update":"b_newset");
//
//
//			ApiClient.cancel(getActivity(), true);
//			setRefreshComplete();
//			sort = (sort == 0 ? 1 : 0);
//			getSupportActivity().supportInvalidateOptionsMenu();
//			handler.removeMessages(MSG_NEW);
//			handler.sendEmptyMessageDelayed(MSG_NEW, 200);
//			break;
//		case R.id.action_info:
//			BoardInfoActivity.start(getActivity(), mainboard, boardInfo);
//			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private Runnable issubscribeRunnable = new Runnable() {

		@Override
		public void run() {
			if (getAccount() != null) {
				if (boardDao.issubscribe(getAccountId(), mainboard.boardUrl)) {
					boardissubscribe = true;
				} else {
					boardissubscribe = false;
				}
			}
			menufirstPrepare = false;
//			getSupportActivity().supportInvalidateOptionsMenu();
		}
	};

	/**
	 * 为程序创建桌面快捷方式 带参数
	 */
	private void addShortcut() {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, mainboard.boardName);
		shortcut.putExtra("duplicate", false); // 不允许重复创建

		// 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
		// 注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序
		// ComponentName comp = new
		// ComponentName(getActivity().getPackageName(),
		// "." + getActivity().getLocalClassName());
		Intent intent = new Intent("net.xici.newapp.board.shortcut");
		// .setComponent(comp);
		// intent.addCategory(Intent.CATEGORY_LAUNCHER);
		// Intent intent = new Intent(Intent.ACTION_MAIN).setComponent(comp);
		intent.putExtra("boardname", mainboard.boardName);
		intent.putExtra("boardid", mainboard.boardUrl);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

		// 快捷方式的图标
		ShortcutIconResource iconRes = ShortcutIconResource.fromContext(
				getActivity(), R.drawable.logo);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		getActivity().sendBroadcast(shortcut);
		Toast.makeText(getActivity(),
				"已添加快捷方式\"" + mainboard.boardName + "\"到桌面", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mainboard != null) {
			getLoaderManager().destroyLoader(mainboard.boardUrl);
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		unregisterForContextMenu(getListView());
	}
	
	private final static int REQUEST_DOCADD = 1;
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_DOCADD&&resultCode==Activity.RESULT_OK){
			reSetList();

			handler.removeMessages(MSG_NEW);
			handler.sendEmptyMessageDelayed(MSG_NEW, 200);
		}
	}
	
}
