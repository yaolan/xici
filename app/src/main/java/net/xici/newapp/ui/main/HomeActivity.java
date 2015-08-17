package net.xici.newapp.ui.main;


import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.xici.newapp.R;
import net.xici.newapp.app.Constants;
import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.dao.FloorDao;
import net.xici.newapp.data.dao.ThreadDao;
import net.xici.newapp.data.pojo.Board;
import net.xici.newapp.data.pojo.VersionInfo;
import net.xici.newapp.support.request.ApiClient;
import net.xici.newapp.support.request.XiciException;
import net.xici.newapp.support.service.LoadSplashPIcService;
import net.xici.newapp.support.set.SettingUtil;
import net.xici.newapp.support.util.UIUtil;
import net.xici.newapp.support.util.XiciResponseHandler;
import net.xici.newapp.ui.base.BaseActionBarActivity;
import net.xici.newapp.ui.base.BaseActivity;


import com.afollestad.materialdialogs.MaterialDialog;

import net.xici.newapp.ui.mine.fragment.MessageFragment;


public class HomeActivity extends BaseActionBarActivity {

    protected int theme = 0;
    private MessageFragment messageFragment;

    @Override
    protected int setLayoutResourceIdentifier() {
        return R.layout.activity_main;
    }

    @Override
    protected int getTitleToolBar() {
        return R.string.app_name;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // 广告图
//        startService(new Intent(this, LoadSplashPIcService.class));

        // 推送服务
//        PushAgent mPushAgent = PushAgent.getInstance(this);
//        if (mPushAgent.isEnabled() && !SettingUtil.getPushEnable()) {
//
//            mPushAgent.disable();
//
//        } else {
//
//            mPushAgent.enable();
//        }
//
//        if (startWelcomeActivity()) {
//            return;
//        }
//
//        if (startLandscapeActivity()) {
//            return;
//        }

//        SplashPicActivity.startSplashActivity(this);

        setContentView(R.layout.activity_main);
//        UmengUpdateAgent.setUpdateCheckConfig(false);
//
//
//        new cleanTask().execute();
//
//
//        startThreadActivity(getIntent());
//
//        dogetversioninfo();

        FragmentManager fragmentManager = getSupportFragmentManager();

        //加载消息页

//
//        messageFragment = new MessageFragment();
//        fragmentManager
//                .beginTransaction()
//                .replace(R.id.container,
//                        messageFragment).commit();


        //加载圈子页

        messageFragment = null;
        fragmentManager
                .beginTransaction()
                .replace(R.id.container,
                        new BoardFragment()).commit();


    }

    private boolean startThreadActivity(Intent intent) {
        if (Constants.BOARD_SHORTCUT_ACTION.equals(intent.getAction())) {
            Board board = new Board();
            board.boardName = intent.getStringExtra("boardname");
            board.boardUrl = intent.getIntExtra("boardid", 0);
//            ThreadListActivity.start(HomeActivity.this, board);
            return true;
        }

        if (Constants.HOME_EXIT.equals(intent.getAction())) {
            finish();
        }
        return false;
    }

    /**
     * 启动图
     *
     * @return
     */
    private boolean startLandscapeActivity() {
        if (!XiciApp.islogined()) {
//            startActivity(new Intent(this, SplashActivity.class));
            finish();
            return true;
        }
        return false;
    }

    /**
     * 欢迎页面
     *
     * @return
     */
    private boolean startWelcomeActivity() {
        if (!SettingUtil.getShowWelcome()) {
//            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!startThreadActivity(intent)) {
            startLandscapeActivity();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_USERUPDATE && resultCode == Activity.RESULT_OK) {
            logout();
        }
    }

    /**
     * 清理缓存
     *
     * @author bkmac
     */
    private class cleanTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            ThreadDao threaDao = new ThreadDao(XiciApp.getContext());
            FloorDao floorDao = new FloorDao(XiciApp.getContext());
            floorDao.deleteALl();
            threaDao.deleteALl();
            return true;
        }

    }


    private void logout() {
        XiciApp.accountDao.clearDefaultAccount();
        XiciApp.account = null;

//        startActivity(new Intent(this,
//                SplashActivity.class));
        finish();
    }

    /**
     * 服务器检查更新。判断是否要强制更新
     */
    private void dogetversioninfo() {
        final String versionname = UIUtil.getVersionName(HomeActivity.this);
        ApiClient.getAppVersionInfo(HomeActivity.this, versionname,
                new XiciResponseHandler() {

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (!SettingUtil.getForceUpdateByVersion(versionname)) {

                            UmengUpdateAgent.setUpdateOnlyWifi(false);
                            UmengUpdateAgent.update(getApplicationContext());

                        } else {

                            // 强制更新，用户选择"以后再说"时，关闭应用
                            UmengUpdateAgent.forceUpdate(HomeActivity.this);

                            UmengUpdateAgent
                                    .setDialogListener(new UmengDialogButtonListener() {

                                        @Override
                                        public void onClick(int status) {

                                            switch (status) {
                                                case UpdateStatus.Update:
                                                    break;
                                                case UpdateStatus.Ignore:
                                                    finishHome();
                                                    break;
                                                case UpdateStatus.NotNow:
                                                    finishHome();
                                                    break;
                                            }

                                        }
                                    });
                        }

                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        try {

                            VersionInfo info = VersionInfo.parse(arg0);
                            if (info != null && info.is_must_update) {
                                SettingUtil.setForceUpdateByVersion(
                                        versionname, true);
                            }

                        } catch (XiciException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                });
    }

    private void finishHome() {
        Intent intent = new Intent(Constants.HOME_EXIT);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && messageFragment != null && messageFragment.isAdded()) { // 按下的如果是BACK，同时没有重复
            // do something here
            boolean result = messageFragment.onkeyback();
            if (result) {
                return result;
            }
        }

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (onback()) {
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    //两次返回退出程序

    private static final int EXIT_LIFECYCLE = 3 * 1000;
    private boolean mInExitLifecycle = false;
    private static Handler sHandler = new Handler();
    private Runnable mExitTask = new Runnable() {
        @Override
        public void run() {
            mInExitLifecycle = false;
        }
    };

    private boolean onback() {

        if (mInExitLifecycle) {

            return false;

        } else {
            UIUtil.showToast(this, R.string.hit_again_to_exit);
            mInExitLifecycle = true;
            sHandler.postDelayed(mExitTask, EXIT_LIFECYCLE);
            return true;
        }

    }


}
