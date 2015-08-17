package net.xici.newapp.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.umeng.analytics.MobclickAgent;

import net.xici.newapp.R;
import net.xici.newapp.app.Constants;
import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.pojo.Account;
import net.xici.newapp.data.pojo.User;
import net.xici.newapp.support.request.ApiClient;
import net.xici.newapp.support.request.XiciException;
import net.xici.newapp.support.util.FileUtil;
import net.xici.newapp.support.util.UIUtil;
import net.xici.newapp.support.util.XiciResponseHandler;

import org.apache.http.HttpException;

import java.io.IOException;

/**
 * Created by bkmac on 15/1/23.
 */
public class BaseFragment extends Fragment {


    public final static int BASEACTION = 0;

    private LayoutInflater mLayoutInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public android.support.v7.app.ActionBar getActionBar(){
       return ((AppCompatActivity)getActivity()).getSupportActionBar();
    }


    protected void OnApiException(XiciException e) {
        OnApiException(e, true, BASEACTION);
    }

    protected void OnApiException(XiciException e, boolean login) {
        OnApiException(e, login, BASEACTION);
    }

    protected void OnApiException(XiciException e, int action) {
        OnApiException(e, true, action);
    }

    protected void OnApiException(XiciException e, boolean login, int action) {
        if (e.getCode() == XiciException.CODE_EXCEPTION_JSON) {
            //Toast.makeText(getActivity(), "解析错误", Toast.LENGTH_SHORT).show();
            onFinishException(action);
        }else if (e.getCode() == XiciException.CODE_EXCEPTION_UNKNOW) {
            Toast.makeText(getActivity(), "未知错误", Toast.LENGTH_SHORT).show();
            onFinishException(action);
        }else if (e.getCode() == XiciException.CODE_EXCEPTION_VALIDATE_TOKEN) {
            // 重新登录，成功之后重新调用上次调用接口，失败则退出该帐号，跳转到登录页面
            if (XiciApp.account != null
                    && !TextUtils.isEmpty(XiciApp.account.username)
                    && !TextUtils.isEmpty(XiciApp.account.password) && login) {
                dologin(action);
            } else {
                onFinishException(action);
            }
        } else {
            UIUtil.showToast(getActivity(), e.getMsg());
            onFinishException(action);
        }
    }

    protected void dologin(final int action) {

        ApiClient.login(getActivity(), XiciApp.account, new XiciResponseHandler() {

            @Override
            public void onSuccess(String arg0) {
                super.onSuccess(arg0);
                User.UserResult result = null;
                try {
                    result = User.parse(arg0);
                    XiciApp.getInstance().savetoken(result.accesstoken);
                    onRequestAgain(action);

                } catch (XiciException e) {

                    new MaterialDialog.Builder(getActivity())
                            .content("验证失败，请重新登录")
                            .cancelable(false)
                            .positiveText(R.string.ok)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {

                                    FileUtil.deleteFile(getActivity().getFilesDir()
                                            + "/" + Constants.ACCOUNT);
                                    XiciApp.account = null;
//                                    if (getActivity() instanceof HomeActivity) {
//                                        startActivity(new Intent(getActivity(),
//                                                SplashActivity.class));
//                                        getActivity().finish();
//                                    } else {
//
//                                        Intent intent = new Intent(getActivity(), HomeActivity.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        startActivity(intent);
//
//                                    }

                                }

                            })
                            .show();



                }

            }

            @Override
            public void onFailure(Throwable arg0) {
                super.onFailure(arg0);
                onAPIFailure();
                onFinishRequest(action);
            }

        });

    }

//    protected void startLoginActivity() {
//        startActivityForResult(new Intent(getActivity(), LoginActivity.class),
//                Constants.REQUEST_CODE_LOGIN);
//    }

    protected void onRequestAgain() {
    }
    /**
     * 重新登录成功则再次调用接口
     * @param action
     */
    protected void onRequestAgain(int action) {
        if (action == 0) {
            onRequestAgain();
        }
    }

    protected void onFinishException() {
    }

    protected void onFinishException(int action) {
        if (action == 0) {
            onFinishException();
        }

    }

    protected void onFinishRequest() {
    }

    protected void onFinishRequest(int action) {
        if (action == 0) {
            onFinishRequest();
        }
    }

    protected void onAPIFailure() {
        UIUtil.showToast(getActivity(), R.string.networkerror);
    }

    public long getAccountId() {
        Account account = getAccount();
        if (account == null) {
            return 0;
        } else {
            return account.userid;
        }

    }

    public String getToken() {
        Account account = getAccount();
        if (account != null && !TextUtils.isEmpty(account.accesstoken)) {
            return account.accesstoken;
        } else {
            return "";
        }

    }

    public Account getAccount() {
        return XiciApp.getAccount();
    }

    public void hideKeyboard(IBinder token) {
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(token, 0);
    }




    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    protected void OnException(final Exception e){
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (isAdded()) {
                    if (e instanceof HttpException) {
                        UIUtil.showToast(getActivity(), R.string.networkerror);
                    } else if (e instanceof IOException) {
                        UIUtil.showToast(getActivity(), R.string.networkerror);
                    } else if (e instanceof XiciException) {
                        XiciException exception = (XiciException) e;
                        UIUtil.showToast(getActivity(), exception.getMsg());
                    }
                }
            }
        });
    }





}
