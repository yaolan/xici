package net.xici.newapp.ui.thread;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.xici.newapp.R;
import net.xici.newapp.app.Constants;
import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.dao.FloorDao;
import net.xici.newapp.data.dao.ThreadDao;
import net.xici.newapp.data.pojo.AddMediaItem;
import net.xici.newapp.data.pojo.DocAddResult;
import net.xici.newapp.data.pojo.Draft;
import net.xici.newapp.data.pojo.Floor;
import net.xici.newapp.data.pojo.Right;
import net.xici.newapp.data.pojo.Thread;
import net.xici.newapp.data.pojo.ThumbnailSize;
import net.xici.newapp.data.pojo.Topic;
import net.xici.newapp.data.pojo.Floor.FloorResult;
import net.xici.newapp.data.pojo.XiciResult;
import net.xici.newapp.support.request.ApiClient;
import net.xici.newapp.support.request.XiciException;
import net.xici.newapp.support.reservoir.Reservoir;
import net.xici.newapp.support.reservoir.ReservoirGetCallback;
import net.xici.newapp.support.util.BitmapUtil;
import net.xici.newapp.support.util.CleanUtil;
import net.xici.newapp.support.util.StringUtil;
import net.xici.newapp.support.util.UIUtil;
import net.xici.newapp.support.util.XiciResponseHandler;
import net.xici.newapp.support.widget.emojicon.Emojicon;
import net.xici.newapp.support.widget.emojicon.EmojiconHandler;


import net.xici.newapp.ui.adapter.FloorCursorNewAdapter;
import net.xici.newapp.ui.adapter.FloorCursorNewAdapter.OnFloorClickListener;
import net.xici.newapp.ui.base.BaseListViewFragment;

import net.xici.newapp.ui.picpicker.EmojiconGridFragment;
import net.xici.newapp.ui.picpicker.EmojiconGridFragment.OnEmojiconClickedListener;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;


/**
 * 帖子详情，回复列表
 *
 * @author bkmac
 */
@SuppressLint("NewApi")
public class FloorListFragment extends BaseListViewFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener,
        AdapterView.OnItemLongClickListener, View.OnFocusChangeListener,
        OnEmojiconClickedListener, OnFloorClickListener {

    //private static String cachekey = "doc_view";
    private final static int DOC_REPLAY_ACTION = 1;

    private final static int DOC_COLLECT_ACTION = 2;

    private final static int DOC_REPLY_DELETE_ACTION = 3;

    private final static int DOC_REPLY_COPY_ACTION = 4;

    private final static int DOC_PRAISE = 5;

    private final static int NAVINDEX_ALL = 0;
    private final static int NAVINDEX_REVERSE = 1;
    private final static int NAVINDEX_ONLYAUTHOR = 2;

    private int boardid;
    private Thread thread;
    private Topic topic;
    private ThreadDao threadDao;
    private FloorDao floorDao;

    private int page = 1; // 当前页数
    private int totlepage = 0; // 总页数
    private long doc_reply_userid = 0;
    private String doc_reply_pre = "";

    protected String content;
    private String mMediaCapturePath = "";
    protected String mfilename = "";

    private FrameLayout attachment_layout;

    Fragment emojicongrid;
//    Fragment picpicker;

    private ImageButton btn_pic_add;
    private ImageButton btn_emojicon_add;

    private LinearLayout zan_layout;
    private LinearLayout edit_layout;
    private ImageView btn_zan;
    private TextView btn_reply;

    // 删除照片tag
    // protected String deleteviewtag;
    // 照片列表
    protected Map<String, AddMediaItem> imageviewlist = new LinkedHashMap<String, AddMediaItem>();

    protected String xnbtoken;

    private int navindex = NAVINDEX_ALL;
    private int count = 0;
    private long author;
    private Right right = null;
    private Floor topFloor;
    private Floor currentFloor;
    private int currentFloorNum;

    private EditText et_content;
    private LinearLayout toolbar;

    private TextView title;
    private LinearLayout floor_more_layout;
    private View floor_more_line;

    private SaveTask mSaveTask;
    protected MaterialDialog dialog;
//	private SelectPictureDialog mSelectPictureDialog;

    private String cachekey = "";

    //锚点功能，可向前翻页
    private int prepage = 1; // 上一页
    private boolean loadpre = false;  //是否向上加载
    private boolean loadfromnetworkfirst = true;
    private boolean hasepre = false;
    private int floorfollowindex = -1;

    //handler
    private final static int MSG_NEW = 1;
    private final static int MSG_SHOWITEM = 2;
    private final static int MSG_ADDMEDIA_ERROR = 3;
    private final static int MSG_PRE = 4;
    private final static int MSG_FLOORFOLLOWINDEX = 5;


    public static FloorListFragment newInstance(Bundle b) {
        FloorListFragment fragment = new FloorListFragment();
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    protected void loadfirst() {

        page = 1;

    }

    @Override
    protected void loadnext() {


    }

    @Override
    protected void buildListAdapter() {
        mAdapter = new FloorCursorNewAdapter(getActivity(), null);
    }

    @Override
    protected void listViewItemClick(AdapterView parent, View view,
                                     int position, long id) {

//		if (getAccount() == null) {
//			return;
//		}
//
//		Floor floor = ((FloorCursorNewAdapter) mAdapter).getFloorItem(position);
//		setReplayUser(floor);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View insideview = super.onCreateView(inflater, container,
                savedInstanceState);
        View header = inflater.inflate(R.layout.floor_fragment_header,
                getListView(), false);
        title = (TextView) header.findViewById(R.id.title);
        //显示前页内容
        floor_more_layout = (LinearLayout) header.findViewById(R.id.floor_more_layout);
        floor_more_line = header.findViewById(R.id.floor_more_line);
        header.findViewById(R.id.floor_more_all).setOnClickListener(this);
        header.findViewById(R.id.floor_more_pre).setOnClickListener(this);

        getListView().addHeaderView(header);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        View view = inflater.inflate(R.layout.floor_list_fragment, container,
                false);
        ((FrameLayout) view.findViewById(R.id.container)).addView(insideview,
                lp);
        et_content = (EditText) view.findViewById(R.id.et_content);

        attachment_layout = (FrameLayout) view
                .findViewById(R.id.attachment_layout);

        btn_pic_add = (ImageButton) view.findViewById(R.id.btn_pic_add);
        btn_emojicon_add = (ImageButton) view
                .findViewById(R.id.btn_emojicon_add);
        btn_pic_add.setOnClickListener(this);
        btn_emojicon_add.setOnClickListener(this);


        btn_zan = (ImageView) view.findViewById(R.id.btn_zan);
        btn_reply = (TextView) view
                .findViewById(R.id.btn_reply);
        btn_zan.setOnClickListener(this);
        btn_reply.setOnClickListener(this);

        zan_layout = (LinearLayout) view.findViewById(R.id.zan_layout);
        edit_layout = (LinearLayout) view.findViewById(R.id.edit_layout);

        view.findViewById(R.id.btn_send).setOnClickListener(this);


//        ((JazzyListView) getListView()).setTransitionEffect(JazzyHelper.SLIDE_IN);
//        ((JazzyListView) getListView()).setShouldOnlyAnimateNewItems(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = savedInstanceState;
        if (bundle == null) {
            bundle = getArguments();
        }
        boardid = bundle.getInt("boardid");
        thread = (Thread) bundle.getSerializable("thread");

        //定位楼层
        floorfollowindex = bundle.getInt("floorfollowindex", -1);
        if (floorfollowindex > 0) {

            prepage = Floor.getpage(floorfollowindex);
            if (prepage >= 1) {

                page = prepage;
                hasepre = true;

            }

        }


        threadDao = new ThreadDao(XiciApp.getContext());
        floorDao = new FloorDao(XiciApp.getContext());
//        ((JazzyListView) getListView()).setOnItemLongClickListener(this);
        buildActionBarNav();
        registerForContextMenu(getListView());

        title.setText(UIUtil.getThreadTagString(getActivity(), thread.top, thread.cool, StringUtil.fromhtml(thread.title)));

        getActivity().supportInvalidateOptionsMenu();
        if (savedInstanceState == null) {
            FragmentTransaction ft = getChildFragmentManager()
                    .beginTransaction();

            emojicongrid = getEmojiconGridFragment();
//            picpicker = getPicPickerFragment();

            if (!emojicongrid.isAdded()) {
                ft.add(R.id.attachment_layout, emojicongrid,
                        EmojiconGridFragment.class.getName());
                ft.hide(emojicongrid);
            }

//            if (!picpicker.isAdded()) {
//                ft.add(R.id.attachment_layout, picpicker,
//                        PicPickerFragment.class.getName());
//                ft.hide(picpicker);
//            }

            if (!ft.isEmpty()) {
                ft.commit();
                getChildFragmentManager().executePendingTransactions();
            }
        }
        et_content.setOnFocusChangeListener(this);
        et_content.setOnClickListener(this);
        ((FloorCursorNewAdapter) mAdapter).setOnFloorClickListener(this);


        getLoaderManager().initLoader((int) thread.tid, null, this);


        //cache  草稿

        cachekey = "docview_" + boardid + "_" + thread.tid + "_" + getAccountId();

        Reservoir.getAsync(cachekey, Draft.class, new TypeToken<Draft>() {
        }.getType(), new ReservoirGetCallback<Draft>() {

            @Override
            public void onSuccess(Draft draft) {

                et_content.setText(draft.content);
                et_content.setSelection(draft.content.length());

                if (draft.listMediaItems != null && draft.listMediaItems.size() > 0) {
                    List<AddMediaItem> list = new ArrayList<AddMediaItem>();
                    for (AddMediaItem.SimpleMedia media : draft.listMediaItems) {

                        list.add(new AddMediaItem(media));
                    }
//                    if (picpicker != null) {
//                        ((PicPickerFragment) picpicker).setAddMediaItems(list);
//                    }
                }


            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        if (XiciApp.islogined()) {

            MobclickAgent.onEvent(getActivity(), "Login_d");

        } else {

            MobclickAgent.onEvent(getActivity(), "visitor_d");
        }

    }

    /**
     * 导航，全部，只看楼主，倒序
     */
    private void buildActionBarNav() {
//        mActionBar.setDisplayShowHomeEnabled(true);
//        mActionBar.setDisplayShowTitleEnabled(false);
//        mActionBar.setDisplayHomeAsUpEnabled(true);
//        mActionBar.setDisplayUseLogoEnabled(true);
//        mActionBar.setLogo(R.drawable.xici_logo_small);
//
//        mActionBar.setTitle("");
//        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//        SpinnerAdapter navadapter = ArrayAdapter.createFromResource(
//                getSupportActivity(), R.array.floor_list_nav_type,
//                R.layout.spinner_dropdown_item_black);
//
//        mActionBar.setListNavigationCallbacks(navadapter,
//                new OnNavigationListener() {
//
//                    @Override
//                    public boolean onNavigationItemSelected(int itemPosition,
//                                                            long itemId) {
//                        if (navindex != itemPosition) {
//                            navindex = itemPosition;
//                            reSetList();
//                            setRefreshComplete();
//                            mhandler.removeMessages(MSG_NEW);
//                            mhandler.sendEmptyMessageDelayed(MSG_NEW, 200);
//                        }
//                        return true;
//                    }
//
//                });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        int headerViewsCount = getListView().getHeaderViewsCount();
        if (isPositionBetweenHeaderViewAndFooterView(position)) {
            currentFloorNum = position - headerViewsCount;
            if (currentFloorNum == 0 && mAdapter.getCount() == 0) {
                return true;
            }

            currentFloor = ((FloorCursorNewAdapter) mAdapter)
                    .getFloorItem(currentFloorNum);

            return false;
            // if (right != null && (right.isAdminDeleted || right.isDelDocs)) {
            // return false;
            // }
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("管理帖子");
        menu.clear();
        if (right != null && currentFloor != null && currentFloor.index > 1) {
            if (right.isAdminDeleted || right.isDelDocs) {
                menu.add(0, DOC_REPLY_DELETE_ACTION, 0, "删除此楼");
            }
        }

        menu.add(0, DOC_REPLY_COPY_ACTION, 0, "复制到剪切板");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DOC_REPLY_DELETE_ACTION:

                MobclickAgent.onEvent(getActivity(), "host_clear2");
                doReplyDelete(String.valueOf(currentFloorNum));
                break;
            case DOC_REPLY_COPY_ACTION:
                if (currentFloor != null) {
                    UIUtil.copyContent(getActivity(), currentFloor.getContent());
                    UIUtil.showToast(getActivity(), "该楼内容已经复制到剪切板");
                }
                break;

            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("boardid", boardid);
        outState.putSerializable("thread", thread);
    }

    /**
     * 获取楼层列表
     */
    private void doGetDocView() {
        ApiClient.doc_view(
                getActivity(),
                boardid,
                loadpre ? prepage : page,
                thread.tid,
                NAVINDEX_ONLYAUTHOR == navindex ? String
                        .valueOf(thread.author.userid) : "",
                getAccount() != null ? true : false, new XiciResponseHandler() {

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        try {
                            if (!isAdded()) {
                                return;
                            }
                            FloorResult result = Floor.parse(arg0);
                            if (result != null) {

                                if (result.topic != null) {

                                    thread.author.userid = result.topic.author_id;

                                }

                                if (navindex == NAVINDEX_REVERSE) {     //是否倒序看帖


                                    //隐藏加载上一页
                                    floor_more_layout.setVisibility(View.GONE);
                                    floor_more_line.setVisibility(View.GONE);

                                    if (page == totlepage) {
                                        if (isAdded()) {
                                            getLoaderManager().restartLoader(
                                                    (int) thread.tid, null,
                                                    FloorListFragment.this);
                                        }

                                        floorDao.bulkInsert(result.floors,
                                                getAccountId(), boardid,
                                                thread.tid, 0);
                                        if (totlepage >= 2
                                                && result.floors.size() < Constants.PAGESIZE) {
                                            //最后一页条数太少，再加载倒数第二页
                                            page--;
                                            doGetDocView();
                                            return;
                                        }

                                    } else {
                                        floorDao.bulkInsert(result.floors,
                                                getAccountId(), boardid,
                                                thread.tid, 1);
                                    }

                                    if (page == 1 && result.floors.size() > 0) {
                                        topFloor = result.floors.get(0);
                                        setTopFloorZan();
                                    }

                                    page--;
                                    if (page > 0) {
                                    } else {
                                    }


                                } else {
                                    // 初始 获取页数，判断是否加载锚点页（定位）
                                    if (!loadpre) {

                                        //隐藏加载上一页

//										floor_more_layout.setVisibility(View.GONE);
//										floor_more_line.setVisibility(View.GONE);

                                        if (page <= 1) {

                                            floor_more_layout.setVisibility(View.GONE);
                                            floor_more_line.setVisibility(View.GONE);

                                            if (navindex == NAVINDEX_ALL) {
                                                loadfromnetworkfirst = false;
                                                count = result.count;
                                                ((FloorCursorNewAdapter) mAdapter)
                                                        .setFloorTotalCount(count);
                                                totlepage = count
                                                        / Constants.PAGESIZE
                                                        + (count
                                                        % Constants.PAGESIZE > 0 ? 1
                                                        : 0);
                                                // 版号
                                                boardid = result.topic.bd_id;
                                                topic = result.topic;
                                                right = result.right;

//                                                title.setText(UIUtil.getThreadTagString(getActivity(), thread.top,
//                                                        thread.cool, StringUtil.fromhtml(topic.doc_title)));
//
//                                                getSupportActivity()
//                                                .supportInvalidateOptionsMenu();
                                            }
                                            if (result.floors.size() > 0) {
                                                topFloor = result.floors.get(0);
                                                setTopFloorZan();

                                            }
                                            if (isAdded()) {
                                                getLoaderManager().restartLoader(
                                                        (int) thread.tid, null,
                                                        FloorListFragment.this);
                                            }

                                            floorDao.bulkInsert(result.floors,
                                                    getAccountId(), boardid,
                                                    thread.tid, 0);


                                        } else {
                                            floorDao.bulkInsert(result.floors,
                                                    getAccountId(), boardid,
                                                    thread.tid, 1);
                                        }
                                        page++;
                                        if (page <= totlepage) {
                                        } else {
                                        }

                                    } else {

                                        //加载上一页
                                        if (loadfromnetworkfirst) {
                                            loadfromnetworkfirst = false;
                                            count = result.count;
                                            ((FloorCursorNewAdapter) mAdapter)
                                                    .setFloorTotalCount(count);
                                            totlepage = count
                                                    / Constants.PAGESIZE
                                                    + (count
                                                    % Constants.PAGESIZE > 0 ? 1
                                                    : 0);
                                            // 版号
                                            boardid = result.topic.bd_id;
                                            topic = result.topic;
                                            right = result.right;

//                                            title.setText(UIUtil.getThreadTagString(getActivity(), thread.top,
//                                                    thread.cool, StringUtil.fromhtml(topic.doc_title)));

                                            getActivity()
                                                    .supportInvalidateOptionsMenu();

                                        }

                                        //主楼
                                        if (prepage == 1 && result.floors.size() > 0) {
                                            topFloor = result.floors.get(0);
                                            setTopFloorZan();
                                        }


                                        floorDao.bulkInsert(result.floors,
                                                getAccountId(), boardid,


                                                thread.tid, page == prepage ? 0 : 1);


                                        if (page == prepage) {

                                            page++;
                                            if (page <= totlepage) {
                                            } else {
                                            }

                                            if (isAdded()) {
                                                getLoaderManager().restartLoader(
                                                        (int) thread.tid, null,
                                                        FloorListFragment.this);

//                                                mhandler.sendEmptyMessageDelayed(MSG_FLOORFOLLOWINDEX, 500);
                                            }

                                        }

                                        prepage--;

                                        if (prepage < 1) {

                                            floor_more_layout.setVisibility(View.GONE);
                                            floor_more_line.setVisibility(View.GONE);

                                        } else {

                                            floor_more_layout.setVisibility(View.VISIBLE);
                                            floor_more_line.setVisibility(View.VISIBLE);

                                        }


                                    }


                                }

                            } else {

                            }

                        } catch (XiciException e) {
                            OnApiException(e);
                            if (e.getCode() == XiciException.CODE_EXCEPTION_DOCDELETED_ERROR) {
                                getActivity().finish();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Throwable arg0) {
                        super.onFailure(arg0);
                        onAPIFailure();
                        onFinishException(BASEACTION);
                    }
                });
    }

    private void doReplyDelete(final String floors) {
        if (TextUtils.isEmpty(floors))
            return;
        ApiClient.doc_reply_delete(getActivity(), boardid, thread.tid, floors,
                new XiciResponseHandler() {

                    public void onStart() {
//                        dialog = new DialogsProgressDialogIndeterminateFragment(
//                                "");
//                        dialog.setCancelable(false);
//                        dialog.showAllowingStateLoss(
//                                getChildFragmentManager(),
//                                DialogsProgressDialogIndeterminateFragment.class
//                                        .getSimpleName());
                    }

                    ;

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        try {
                            XiciResult result = XiciResult.simpleparse(arg0);
                            if (result != null && result.success) {

                                if (currentFloor != null) {
                                    currentFloor.textList.clear();
                                    currentFloor.textList.add("此楼内容已被删除");
                                    currentFloor.text = "此楼内容已被删除";
                                    currentFloor.thumbnail.clear();
                                    floorDao.cleanByFloor(getAccountId(),
                                            boardid, thread.tid, currentFloor);
                                    mAdapter.notifyDataSetChanged();
                                }
                                Toast.makeText(getActivity(), "回帖已删除",
                                        Toast.LENGTH_SHORT).show();
                            }
                            onFinishRequest(DOC_REPLY_DELETE_ACTION);
                        } catch (XiciException e) {
                            OnApiException(e, DOC_REPLY_DELETE_ACTION);
                        }

                    }

                    @Override
                    public void onFailure(Throwable arg0) {
                        super.onFailure(arg0);
                        onAPIFailure();
                        onFinishException(DOC_REPLY_DELETE_ACTION);
                    }
                });
    }

    @Override
    protected void onFinishRequest(int action) {
        super.onFinishRequest(action);
        switch (action) {
            case DOC_REPLAY_ACTION:
            case DOC_REPLY_DELETE_ACTION:
//                if (dialog != null) {
//                    dialog.dismissAllowingStateLoss();
//                }
                break;

            default:
                break;
        }

    }

    @Override
    protected void onFinishException(int action) {
        super.onFinishException(action);
        switch (action) {
            case BASEACTION:

                if ((page > 1 && navindex != NAVINDEX_REVERSE)
                        || (page < totlepage && navindex == NAVINDEX_REVERSE)) {
//                    showErrorFooterView();
                }
                break;

            default:
                break;
        }
        onFinishRequest(action);

    }


    @Override
    protected void onRequestAgain(int action) {
        super.onRequestAgain();
        if (action == BASEACTION) {
            doGetDocView();
        } else if (action == DOC_REPLAY_ACTION) {
            dosend();
        } else if (action == DOC_COLLECT_ACTION) {
            dofav();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_floorlist, menu);
//        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
//        MenuItem favItem = menu.findItem(R.id.action_fav);
//        if (topic == null || getAccount() == null) {
//            favItem.setVisible(false);
//        } else {
//            favItem.setVisible(true);
//            if (Topic.COLLECTED.equals(topic.iscollect)) {
//                favItem.setIcon(R.drawable.ic_fav_en);
//                favItem.setTitle(R.string.cancelfav);
//            } else {
//                favItem.setIcon(R.drawable.ic_fav_un);
//                favItem.setTitle(R.string.fav);
//            }
//
//        }
    }

    public Intent createShareIntent() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setAction(Intent.ACTION_SEND)
                .putExtra(
                        Intent.EXTRA_TEXT,
                        "我正在使用\"西祠胡同\"Android客户端,发现了文章【" + thread.title
                                + "】,想与你分享，查看原文" + ":http://3g.xici.net/d"
                                + thread.tid
                                + ".htm?from=NewAndroidCoreThread【来自西祠胡同】")
                .setType("text/plain");
        return share;
    }

    //重置list
    private void reSetList() {
        ApiClient.cancel(getActivity(), true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_refresh:
//                reSetList();
//                mhandler.removeMessages(MSG_NEW);
//                mhandler.sendEmptyMessageDelayed(MSG_NEW, 200);
//                break;
//            case R.id.action_top:
//                mListView.setSelection(0);
//                break;
//            case R.id.action_copy_threadurl:
//                // int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//                Floor floor = ((FloorCursorNewAdapter) mAdapter).getFloorItem(0);
//                if (floor != null) {
//                    String threadurl = thread.title + "\n" + floor.getContent()
//                            + "\n" + "http://3g.xici.net/d"
//                            + thread.tid + ".htm";
//                    UIUtil.copyContent(getActivity(), threadurl);
//                    Toast.makeText(getSupportActivity(), "帖子地址已经复制到剪切板",
//                            Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case R.id.action_share:
//
//                final UMSocialService mController = UMServiceFactory
//                        .getUMSocialService("com.umeng.share");
//
//                UMengUtil.allSsoHandle(getActivity(), mController);
//
//                UMengUtil.allSsoHandle(getActivity(), mController);
//                UMengUtil.setSharecontent(getActivity(), mController, "热贴推荐："
//                        + thread.title, "我正在使用\"西祠胡同\"Android客户端,发现了文章【"
//                        + thread.title + "】,想与你分享，", "http://3g.xici.net/d"
//                        + thread.tid + ".htm");
//
//                // 设置分享内容
//                // mController.setShareContent("我正在使用\"西祠胡同\"Android客户端,发现了文章【"+thread.title+"】,想与你分享，查看原文"+":http://3g.xici.net/d"
//                // + thread.tid+".htm");
//
//                mController.openShare(getActivity(), false);
//
//                break;
//            case R.id.action_fav:
//                dofav();
//                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    /**
     * 收藏
     */
    private void dofav() {
        if (topic.isCollect()) {

            MobclickAgent.onEvent(getActivity(), "unfav");
            ApiClient.doc_collectdel(getActivity(), (int) thread.tid,
                    favHandler);
        } else {

            MobclickAgent.onEvent(getActivity(), "fav");
            ApiClient.doc_collect(getActivity(), topic == null ? thread.title : topic.doc_title,
                    (int) thread.tid, favHandler);
        }
    }

    private AsyncHttpResponseHandler favHandler = new XiciResponseHandler() {

        public void onSuccess(String arg0) {

            XiciResult xiciResult = null;

            try {
                if (topic.isCollect()) {
                    xiciResult = XiciResult.CollectDelparse(arg0);
                    topic.iscollect = Topic.COLLECTDEL;
                    UIUtil.showToast(getActivity(), "已取消收藏");
                } else {
                    xiciResult = XiciResult.Collectparse(arg0);
                    topic.iscollect = Topic.COLLECTED;
                    UIUtil.showToast(getActivity(), "已收藏");
                }
                getActivity().supportInvalidateOptionsMenu();
                onFinishRequest(DOC_COLLECT_ACTION);
            } catch (XiciException e) {
                OnApiException(e, DOC_COLLECT_ACTION);
            }
        }

        ;

        public void onFailure(Throwable arg0) {
            onAPIFailure();
            onFinishRequest(DOC_COLLECT_ACTION);
        }

        ;
    };

    //点赞
    private void doDocPaise(final Floor floor) {

        MobclickAgent.onEvent(getActivity(), floor.up_status == 0 ? "d_paise" : "d_unpaise");

        ApiClient.doc_praise(getActivity(), thread.tid, floor.user.userid,
                floor.index, floor.up_status == 0 ? 1 : 0,
                new XiciResponseHandler() {

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        try {

                            XiciResult result = XiciResult.docpraiseParse(arg0);

                            if (topFloor != null && topFloor.index == floor.index) {
                                topFloor = floor;

                            }

                            floorDao.changeUpstateByFloor(getAccountId(),
                                    boardid, thread.tid, floor);

                            if (topFloor != null && topFloor.index == floor.index) {
                                setTopFloorZan();
                            }


                        } catch (XiciException e) {
                            OnApiException(e, DOC_PRAISE);
                        }

                    }

                    public void onFailure(Throwable arg0) {
                        onAPIFailure();
                    }

                    ;

                });

    }

    // 回复帖子
    public boolean onkeyback() {
        if (attachment_layout.getVisibility() == View.VISIBLE) {
            showPanel(false, false, false);
            return true;
        }
        if (edit_layout.getVisibility() == View.VISIBLE) {
            edit_layout.setVisibility(View.GONE);
            zan_layout.setVisibility(View.VISIBLE);
            return true;
        }
        saveCache();
        return false;
    }

    private EmojiconGridFragment getEmojiconGridFragment() {
        EmojiconGridFragment fragment = (EmojiconGridFragment) getChildFragmentManager()
                .findFragmentByTag(EmojiconGridFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new EmojiconGridFragment();
        }
        return fragment;
    }

//    private PicPickerFragment getPicPickerFragment() {
//        PicPickerFragment fragment = (PicPickerFragment) getChildFragmentManager()
//                .findFragmentByTag(PicPickerFragment.class.getSimpleName());
//        if (fragment == null) {
//            fragment = new PicPickerFragment();
//        }
//        return fragment;
//    }

    /*
     * 切换选择表情，图片，输入框
     */
    private void showPanel(boolean pic, boolean emojicon, boolean keyboard) {

        if (!isAdded()) {
            return;
        }

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
//        ft.hide(picpicker);
        ft.hide(emojicongrid);
        btn_pic_add.setImageResource(R.drawable.icon_camera_add);
        btn_emojicon_add.setImageResource(R.drawable.icon_image_add);
        if (pic || emojicon) {
            attachment_layout.setVisibility(View.VISIBLE);
            if (pic) {
//                ft.show(picpicker);
                btn_pic_add.setImageResource(R.drawable.icon_camera_add_p);
            }
            if (emojicon) {
                ft.show(emojicongrid);
                btn_emojicon_add.setImageResource(R.drawable.icon_image_add_p);
            }
        } else {
            attachment_layout.setVisibility(View.GONE);
        }

        ft.commit();

        if (keyboard) {
            attachment_layout.setVisibility(View.GONE);
        } else {
            hideSoftInput();
        }
    }

    private void hideSoftInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(getActivity().INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View v = getActivity().getCurrentFocus();
            if (v == null) {
                return;
            }

            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.et_content:
                    showPanel(false, false, true);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        et_content.append(EmojiconHandler.replaceFace(getActivity(),
                emojicon.emoji));
    }

    //发送
    protected void dosend() {

        content = et_content.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(getActivity(), R.string.contentempty,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        hideKeyboard(et_content.getWindowToken());
//        if (picpicker != null) {
//            ((PicPickerFragment) picpicker).cancelUploadTask();
//        }
        if (CleanUtil.isAsynctaskFinished(mSaveTask)) {

            MobclickAgent.onEvent(getActivity(), "all_reply");
            mSaveTask = new SaveTask();
            mSaveTask.execute();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (XiciApp.islogined()) {
                    dosend();
                } else {
//                    startLoginActivity();
                }
                break;
            case R.id.btn_pic_add:
//                if ((attachment_layout.getVisibility() == View.VISIBLE)
//                        && !picpicker.isHidden()) {
//                    showPanel(false, false, false);
//                } else {
//                    showPanel(true, false, false);
//                }
                break;
            case R.id.btn_emojicon_add:
                if ((attachment_layout.getVisibility() == View.VISIBLE)
                        && !emojicongrid.isHidden()) {
                    showPanel(false, false, false);
                } else {
                    showPanel(false, true, false);
                }
                break;
            case R.id.et_content:
                showPanel(false, false, true);
                break;
            case R.id.btn_zan:
                if (!XiciApp.islogined()) {
//                    startLoginActivity();
                } else if (topFloor == null) {
                    //UIUtil.showToast(getActivity(), R.string.feature_not_available);
                } else {
                    doDocPaise(topFloor);
                }

                break;
            case R.id.btn_reply:
                if (XiciApp.islogined()) {

                    edit_layout.setVisibility(View.VISIBLE);
                    zan_layout.setVisibility(View.GONE);

                } else {
                    //startLoginActivity();
                    UIUtil.showToast(getActivity(), R.string.not_login);
                }
                break;
            case R.id.floor_more_all:

                //加载全部
                reSetList();
//                setRefreshComplete();
//                mhandler.removeMessages(MSG_NEW);
//                mhandler.sendEmptyMessageDelayed(MSG_NEW, 200);

                break;
            case R.id.floor_more_pre:
                //加载上一页
//                if (shouldloadOldMsg()) {
//
//                    reSetList();
//                    setRefreshComplete();
//                    mhandler.removeMessages(MSG_PRE);
//                    mhandler.sendEmptyMessageDelayed(MSG_PRE, 200);
//
//                }

                break;
            default:
                break;
        }

    }

    /**
     * 数据加载
     */
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        return floorDao.getCursorLoader(getAccountId(), boardid, thread.tid,
                navindex == NAVINDEX_REVERSE ? true : false);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {

        if (first) {

            first = false;
            if (hasepre) {

//                mhandler.removeMessages(MSG_PRE);
//                mhandler.sendEmptyMessageDelayed(MSG_PRE, 200);

            } else {
//                loadNewMsg();
            }


        } else {
            ((FloorCursorNewAdapter) mAdapter).swapCursor(cursor);
//            showMoreFooterViewifNeed(Constants.PAGESIZE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        ((FloorCursorNewAdapter) mAdapter).swapCursor(null);
    }

    @Override
    public void onDestroy() {
        unregisterForContextMenu(getListView());
        ApiClient.cancel(getActivity(), true);
        if (thread != null) {
            getLoaderManager().destroyLoader((int) thread.tid);
        }
        super.onDestroy();
    }


    /**
     * 回帖
     *
     * @author bkmac
     */
    protected class SaveTask extends AsyncTask<Void, Void, DocAddResult> {

        List<AddMediaItem> list = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            if (dialog == null) {
//                dialog = new DialogsProgressDialogIndeterminateFragment(
//                        "正在发表……");
//            }
//            dialog.setCancelable(false);
//            dialog.show(getSupportActivity());
        }

        @Override
        protected DocAddResult doInBackground(Void... arg0) {

            try {
//                if (picpicker != null) {
//                    ((PicPickerFragment) picpicker).processPic();
//                    list = ((PicPickerFragment) picpicker).getAddMediaItems();
//                }
                // 回帖
                DocAddResult result = DocAddResult.parse(ApiClient.doc_reply(
                        boardid,
                        UIUtil.getContentString(getActivity(), doc_reply_pre
                                + content, list), (int) thread.tid,
                        doc_reply_userid));
                return result;

            } catch (Exception e) {
                OnException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(DocAddResult result) {
            super.onPostExecute(result);

            if (result != null) {

                try {
                    Reservoir.delete(cachekey);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Toast.makeText(getActivity(), "回帖成功", Toast.LENGTH_SHORT)
                        .show();
                Floor floor = result.floor;
                if (floor != null) {
                    floor.text = doc_reply_pre + content;
                    floor.textList = new ArrayList<String>();
                    floor.textList.add(floor.text);

                    if (list != null) {
                        for (int i = 0; i < list.size(); i++) {
                            AddMediaItem addMediaItem = list.get(i);
                            floor.thumbnail.add("file://" + addMediaItem.path);
                            floor.textList.add("[img]attachment" + (i + 1)
                                    + "[/img]");

                            ThumbnailSize size = BitmapUtil.getSize(addMediaItem.path);

                            floor.thumbnail_size.add(size);
                        }
                    }

                    floorDao.Insert(floor, getAccountId(), boardid, thread.tid);
                }

                et_content.setText("");
                doc_reply_userid = 0;
                et_content.setHint(R.string.doc_reply_author);
//                if (picpicker != null) {
//                    ((PicPickerFragment) picpicker).clean();
//                }

                // 回帖数加1
                thread.replycount += 1;
                threadDao.updateThreadReplyCount(getAccountId(), boardid,
                        thread);

                //显示赞按钮
                showPanel(false, false, false);
                edit_layout.setVisibility(View.GONE);
                zan_layout.setVisibility(View.VISIBLE);

            }

//            if (dialog != null) {
//                dialog.dismissAllowingStateLoss();
//            }
        }

    }

    @Override
    public void onReplyClick(Floor floor) {
        if (XiciApp.islogined()) {

            if (edit_layout.getVisibility() == View.GONE) {
                edit_layout.setVisibility(View.VISIBLE);
                zan_layout.setVisibility(View.GONE);
            }
            setReplayUser(floor);
        }

    }

    @Override
    public void onZanClick(Floor floor) {
        if (XiciApp.islogined()) {
            doDocPaise(floor);
        } else {
            //UIUtil.showToast(getActivity(), R.string.not_login);
//            startLoginActivity();
        }

    }

    private void setReplayUser(Floor floor) {

        if (floor.user != null && floor.isDel == 0) {

            if (floor.user.userid == thread.author.userid) {

                doc_reply_userid = 0;
                doc_reply_pre = "";
                et_content.setHint(R.string.doc_reply_author);

                et_content.setText("");

            } else {

                doc_reply_userid = floor.user.userid;

                //et_content.setText("@"+floor.user.userName+" ");
                doc_reply_pre = getString(R.string.doc_reply_pre, floor.index,
                        floor.user.userName);

                et_content.setHint(doc_reply_pre);
            }

        }
    }

    private void setTopFloorZan() {
        if (topFloor != null) {
            btn_zan.setBackgroundResource(topFloor.up_status == 0 ? R.drawable.praise_b_off : R.drawable.praise_b_on);
        }
    }

    //草稿
    protected void saveCache() {

        Draft draft = new Draft();
        draft.bid = boardid;
        draft.tid = (int) thread.tid;

        draft.content = et_content.getText().toString();

//        if (picpicker != null) {
//
//            ((PicPickerFragment) picpicker).cancelUploadTask();
//            List<AddMediaItem> list = ((PicPickerFragment) picpicker).getAddMediaItems();
//            if (list != null && list.size() > 0) {
//                draft.listMediaItems = new ArrayList<AddMediaItem.SimpleMedia>();
//
//                for (AddMediaItem item : list) {
//                    draft.listMediaItems.add(item.getsimpleMedia());
//                }
//            }
//
//        }


        try {

            Reservoir.put(cachekey, draft);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
