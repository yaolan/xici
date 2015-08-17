
package net.xici.newapp.ui.mine.fragment;

import java.util.ArrayList;
import java.util.List;


import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.xici.newapp.R;
import net.xici.newapp.data.pojo.AddMediaItem;
import net.xici.newapp.data.pojo.DocAddResult;
import net.xici.newapp.data.pojo.Draft;
import net.xici.newapp.data.pojo.FloorFollow;
import net.xici.newapp.data.pojo.UserPing;
import net.xici.newapp.support.request.ApiClient;
import net.xici.newapp.support.reservoir.Reservoir;
import net.xici.newapp.support.reservoir.ReservoirGetCallback;
import net.xici.newapp.support.set.SettingUtil;
import net.xici.newapp.support.util.CleanUtil;

import net.xici.newapp.support.widget.emojicon.Emojicon;
import net.xici.newapp.support.widget.emojicon.EmojiconHandler;
import net.xici.newapp.ui.adapter.FloorFollowAdapter.OnFloowFollowClickedListener;
import net.xici.newapp.ui.base.BaseFragment;
import net.xici.newapp.ui.picpicker.EmojiconGridFragment;
import net.xici.newapp.ui.picpicker.EmojiconGridFragment.OnEmojiconClickedListener;




/**
 * 消息页面，包含飞语 跟帖 @
 *
 * @author bkmac
 */
public class MessageFragment extends BaseFragment implements View.OnClickListener,
        View.OnFocusChangeListener, OnEmojiconClickedListener, OnFloowFollowClickedListener {

    ViewPager pager;

//    PagerSlidingTabStrip indicator;

    private FrameLayout attachment_layout;

    private LinearLayout reply_layout;

    private Fragment emojicongrid;

    private ImageButton btn_emojicon_add;

    private EditText et_content;

    private String content;

    private FloorFollow floorFollow;

    private SaveTask mSaveTask;

    private MaterialDialog dialog;

    private String cachekey = "";

    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        pager = (ViewPager) view.findViewById(R.id.viewpager);

//        indicator = (PagerSlidingTabStrip) view.findViewById(R.id.indicator);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);


        et_content = (EditText) view.findViewById(R.id.et_content);

        attachment_layout = (FrameLayout) view.findViewById(R.id.attachment_layout);
        reply_layout = (LinearLayout) view.findViewById(R.id.reply_layout);

        btn_emojicon_add = (ImageButton) view.findViewById(R.id.btn_emojicon_add);
        btn_emojicon_add.setOnClickListener(this);
        view.findViewById(R.id.btn_send).setOnClickListener(this);
        et_content.setOnFocusChangeListener(this);
        et_content.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);




//        EventBus.getDefault().register(getActivity());
        setMessageNum();

        if (savedInstanceState == null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();

            emojicongrid = getEmojiconGridFragment();

            if (!emojicongrid.isAdded()) {
                ft.add(R.id.attachment_layout, emojicongrid, EmojiconGridFragment.class.getName());
                ft.hide(emojicongrid);
            }

            if (!ft.isEmpty()) {
                ft.commit();
                getChildFragmentManager().executePendingTransactions();
            }
        }

        setupViewPager(pager);
        tabLayout.setupWithViewPager(pager);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void UserPingEventMainThread(UserPing event) {
        setMessageNum();
    }

    private void setMessageNum() {

        UserPing userPing = SettingUtil.getAccountMessageCount(getAccountId());
//        if (userPing != null && indicator != null) {
//
//            indicator.setNum(2, userPing.newmail);
//            indicator.setNum(0, userPing.follow);
//            indicator.setNum(1, userPing.atme);
//        }

    }

    private EmojiconGridFragment getEmojiconGridFragment() {
        EmojiconGridFragment fragment = (EmojiconGridFragment) getChildFragmentManager()
                .findFragmentByTag(EmojiconGridFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new EmojiconGridFragment();
        }
        return fragment;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_send:
                if (getAccount() != null) {
                    dosend();
                } else {
//                    startLoginActivity();
                }
                break;

            case R.id.btn_emojicon_add:
                if ((attachment_layout.getVisibility() == View.VISIBLE) && !emojicongrid.isHidden()) {
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
    public void onEmojiconClicked(Emojicon emojicon) {
        et_content.append(EmojiconHandler.replaceFace(getActivity(), emojicon.emoji));
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
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
                getActivity().INPUT_METHOD_SERVICE);
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
                    showPanel(false, true);
                    break;
                default:
                    break;
            }
        }
    }

    // 回复帖子
    public boolean onkeyback() {

        if (reply_layout.getVisibility() == View.VISIBLE) {
            if (attachment_layout.getVisibility() == View.VISIBLE) {
                showPanel(false, false);
                return true;
            }

            reply_layout.setVisibility(View.GONE);
            return true;
        }

        saveCache();

        return false;
    }

    public void saveCache() {

        if (TextUtils.isEmpty(cachekey))
            return;

        Draft draft = new Draft();
        draft.content = et_content.getText().toString();

        try {

            Reservoir.put(cachekey, draft);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onReplyClicked(FloorFollow floorFollow) {
        if (reply_layout.getVisibility() == View.GONE) {

            reply_layout.setVisibility(View.VISIBLE);

        }

        saveCache();

        this.floorFollow = floorFollow;
        String content = "@" + floorFollow.user_name + " ";
        et_content.setText(content);

        et_content.setSelection(content.length());

        cachekey = "floorfollow_" + floorFollow.bd_id + "_" + floorFollow.doc_id + "_"
                + floorFollow.user_id + "_" + getAccountId();
        ;

        Reservoir.getAsync(cachekey, Draft.class, new TypeToken<Draft>() {
        }.getType(), new ReservoirGetCallback<Draft>() {

            @Override
            public void onSuccess(Draft draft) {

                et_content.setText(draft.content);
                et_content.setSelection(draft.content.length());

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    protected void dosend() {

        if (floorFollow == null) {
            return;
        }
        content = et_content.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(getActivity(), R.string.contentempty, Toast.LENGTH_SHORT).show();
            return;
        }
        hideKeyboard(et_content.getWindowToken());

        MobclickAgent.onEvent(getActivity(), "news_reply");

        if (CleanUtil.isAsynctaskFinished(mSaveTask)) {
            mSaveTask = new SaveTask();
            mSaveTask.execute();
        }

    }


    private void setupViewPager(ViewPager viewPager) {
        MessageAdapter adapter = new MessageAdapter(getChildFragmentManager());
        adapter.addFragment(new AppMailFragment(), "飞语");
        adapter.addFragment(FloorFollowFragment.newinstance(FloorFollow.TYPE_FOLLOW), "跟帖");
        adapter.addFragment(FloorFollowFragment.newinstance(FloorFollow.TYPE_ATME), "@我");
        viewPager.setAdapter(adapter);
    }


    static class MessageAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public MessageAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }



    /**
     * 保存
     *
     * @author bkmac
     */
    protected class SaveTask extends AsyncTask<Void, Void, DocAddResult> {

        List<AddMediaItem> list = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            if (dialog == null) {
//                dialog = new DialogsProgressDialogIndeterminateFragment("正在发表……");
//            }
//            dialog.setCancelable(false);
//            dialog.show(getSupportActivity());
        }

        @Override
        protected DocAddResult doInBackground(Void... arg0) {

            try {
                // 回帖
                DocAddResult result = DocAddResult.parse(ApiClient.doc_reply(
                        (int) floorFollow.bd_id, content, (int) floorFollow.doc_id,
                        floorFollow.user_id));
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
                    cachekey = null;

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(getActivity(), "回复成功", Toast.LENGTH_SHORT).show();
                et_content.setText("");
                showPanel(false, false);
                reply_layout.setVisibility(View.GONE);
            }

//            if (dialog != null) {
//                dialog.dismissAllowingStateLoss();
//            }
        }

    }

}
