package net.xici.newapp.ui.mine.fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;

import net.xici.newapp.R;
import net.xici.newapp.data.contentprovider.MailContentProvider;
import net.xici.newapp.data.dao.MailDao;
import net.xici.newapp.data.pojo.AppMailItem;
import net.xici.newapp.data.pojo.Draft;
import net.xici.newapp.data.pojo.XiciResult;
import net.xici.newapp.support.request.ApiClient;
import net.xici.newapp.support.request.XiciException;
import net.xici.newapp.support.util.UIUtil;
import net.xici.newapp.support.util.XiciResponseHandler;
import net.xici.newapp.support.widget.emojicon.Emojicon;
import net.xici.newapp.support.widget.emojicon.EmojiconHandler;
import net.xici.newapp.ui.adapter.AppMailItemAdapter;
import net.xici.newapp.ui.base.BaseListViewFragment;
import net.xici.newapp.ui.picpicker.EmojiconGridFragment;
import net.xici.newapp.ui.picpicker.EmojiconGridFragment.OnEmojiconClickedListener;

import java.util.List;

@SuppressLint("NewApi")
public class AppMailItemFragment extends BaseListViewFragment implements
        View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>,
        View.OnFocusChangeListener, OnEmojiconClickedListener {

    private static String cachekey = "";

    private MailDao mMailDao;

    private String content;
    private long userid;
    private String username;
    private EditText et_content;
    private ImageButton btn_send;

    private EditText name;

    int page = 1;

    Fragment emojicongrid;

    private FrameLayout attachment_layout;

    private ImageButton btn_emojicon_add;

    private MaterialDialog dialog;

    private final static int GET_MAIL = 1;
    private final static int SEND_MAIL = 2;

    private boolean scrollList = false;

    public static AppMailItemFragment newinstance(Bundle bundle) {
        AppMailItemFragment fragment = new AppMailItemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View insideview = super.onCreateView(inflater, container,
                savedInstanceState);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        View view = inflater.inflate(R.layout.fragment_appmail_item, container,
                false);
        ((FrameLayout) view.findViewById(R.id.container)).addView(insideview,
                lp);

        et_content = (EditText) view.findViewById(R.id.et_content);

        attachment_layout = (FrameLayout) view
                .findViewById(R.id.attachment_layout);

        btn_emojicon_add = (ImageButton) view
                .findViewById(R.id.btn_emojicon_add);
        btn_emojicon_add.setOnClickListener(this);
        view.findViewById(R.id.btn_send).setOnClickListener(this);
        et_content.setOnFocusChangeListener(this);
        et_content.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMailDao = new MailDao(getActivity().getApplicationContext());
//        username = getArguments().getString("username");
//        userid = getArguments().getLong("userid");
        username = "bknight";
        userid = 30184106;

        getActionBar().setTitle(username);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getChildFragmentManager()
                    .beginTransaction();

            emojicongrid = getEmojiconGridFragment();

            if (!emojicongrid.isAdded()) {
                ft.add(R.id.attachment_layout, emojicongrid,
                        EmojiconGridFragment.class.getName());
                ft.hide(emojicongrid);
            }

            if (!ft.isEmpty()) {
                ft.commit();
                getChildFragmentManager().executePendingTransactions();
            }
        }

        ((AppMailItemAdapter) mAdapter).setUserId(userid);
        getLoaderManager().initLoader(1, null, this);

        cachekey = "appmail_" + userid + "_" + getAccountId();


        Reservoir.getAsync(cachekey, Draft.class, new ReservoirGetCallback<Draft>() {

            @Override
            public void onSuccess(Draft draft) {

                et_content.setText(draft.content);

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private EmojiconGridFragment getEmojiconGridFragment() {
        EmojiconGridFragment fragment = (EmojiconGridFragment) getChildFragmentManager()
                .findFragmentByTag(EmojiconGridFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new EmojiconGridFragment();
        }
        return fragment;
    }

    protected void buildListAdapter() {

        mAdapter = new AppMailItemAdapter(getActivity(), null);
    }

    ;

    @Override
    protected void listViewItemClick(AdapterView parent, View view,
                                     int position, long id) {

    }

    @Override
    protected void loadfirst() {
        doGetMail();
    }

    @Override
    protected void loadnext() {

    }

    private void showPanel(boolean emojicon, boolean keyboard) {

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.hide(emojicongrid);
        btn_emojicon_add.setImageResource(R.drawable.icon_image_add);

        if (emojicon) {
            attachment_layout.setVisibility(View.VISIBLE);
            ft.show(emojicongrid);
            btn_emojicon_add.setImageResource(R.drawable.icon_image_add_p);
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

    private void doGetMail() {

        ApiClient.user_appmails_dialogue(getActivity(), page, username,
                new XiciResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        try {
                            List<AppMailItem> result = AppMailItem.parse(arg0);
                            if (result != null) {

                                mMailDao.bulkInsertAppMailItem(result,
                                        getAccountId(), username, page == 1 ? 0
                                                : 1);

                                // 分页
                                if (result.size() > 0) {
                                    page++;
                                }

                                // 修改未读状态
                                mMailDao.updateAppMailUnRead(getAccountId(),
                                        username);

                            }
                            onFinishRequest(GET_MAIL);
                        } catch (XiciException e) {
                            OnApiException(e, GET_MAIL);
                        }
                    }

                    @Override
                    public void onFailure(Throwable arg0) {
                        super.onFailure(arg0);
                        onAPIFailure();
                        onFinishException(GET_MAIL);
                    }

                });

    }

    private void dosendmail() {
        ApiClient.user_sendmail(getActivity(), username, content,
                new XiciResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        dialog = new MaterialDialog.Builder(getActivity())
                                .content("")
                                .progress(true, 0)
                                .show();
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        try {
                            XiciResult result = XiciResult.sendMailparse(arg0);

                            // mailDao.insertOutbox(getAccountId(),
                            // receiver_name, content);

                            try {
                                Reservoir.delete(cachekey);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(getActivity(), "发送成功",
                                    Toast.LENGTH_SHORT).show();
                            onFinishRequest(SEND_MAIL);

                            mMailDao.insertAppMailItem(getAccountId(),
                                    username, content);
                            et_content.setText("");

                        } catch (XiciException e) {
                            OnApiException(e, SEND_MAIL);
                        }
                    }

                    @Override
                    public void onFailure(Throwable arg0) {
                        super.onFailure(arg0);
                        onAPIFailure();
                        onFinishException(SEND_MAIL);
                    }

                });

    }

//	@Override
//	protected void onFinishRequest(int action) {
//		super.onFinishRequest(action);
//		switch (action) {
//		case SEND_MAIL:
//			if (dialog != null) {
//				dialog.dismiss();
//			}
//			break;
//		case GET_MAIL:
//			mPullToRefreshLayout.setRefreshComplete();
//			break;
//		default:
//			break;
//		}
//
//	}

    @Override
    protected void onFinishException(int action) {
        super.onFinishException();
        onFinishRequest(action);
    }

//	@Override
//	protected void onRequestAgain(int action) {
//		super.onRequestAgain();
//		switch (action) {
//		case GET_MAIL:
//			doGetMail();
//			break;
//		case SEND_MAIL:
//			dosendmail();
//			break;
//		default:
//			break;
//		}
//
//	}


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_send:
                content = et_content.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    showPanel(false, false);
                    content = UIUtil.getContentString(content);
                    dosendmail();
                }
                break;

            case R.id.btn_emojicon_add:
                if ((attachment_layout.getVisibility() == View.VISIBLE)
                        && !emojicongrid.isHidden()) {
                    showPanel(false, false);
                } else {
                    showPanel(true, false);
                }
                break;
            case R.id.et_content:
                showPanel(false, true);
                break;

            default:
                break;
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        return mMailDao.getCursorLoaderAppmailDialogue(getAccountId(),
                username, MailContentProvider.MAIL_TYPE_APPMAIL_DIALOGUE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
        ((AppMailItemAdapter) mAdapter).swapCursor(data);
        if (first) {
            first = false;
            loadfirst();
        }

        if (data != null && data.getCount() > 0 && !scrollList) {
            scrollList = true;
            int last = mAdapter.getCount() - 1;
            getListView().setSelection(last >= 0 ? last : 0);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        ((AppMailItemAdapter) mAdapter).swapCursor(null);
    }

    public boolean onkeyback() {

        if (attachment_layout.getVisibility() == View.VISIBLE) {
            showPanel(false, false);
            return true;
        }

        saveCache();

        return false;
    }

    public void saveCache() {

        Draft draft = new Draft();

        draft.content = et_content.getText().toString();

        try {

            Reservoir.put(cachekey, draft);

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        et_content.append(EmojiconHandler.replaceFace(getActivity(),
                emojicon.emoji));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.et_content:
                    showPanel(false, true);
                    break;
                default:
                    break;
            }
        }
    }

}
