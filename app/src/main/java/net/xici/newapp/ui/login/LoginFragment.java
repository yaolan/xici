package net.xici.newapp.ui.login;

import java.util.Map;
import java.util.Set;


import org.json.JSONException;
import org.json.JSONObject;


import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import de.greenrobot.event.EventBus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.xici.newapp.R;
import net.xici.newapp.app.Constants;
import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.pojo.Account;
import net.xici.newapp.data.pojo.ThirdBind;
import net.xici.newapp.data.pojo.User;
import net.xici.newapp.data.pojo.UserPing;
import net.xici.newapp.data.pojo.User.UserResult;
import net.xici.newapp.support.request.ApiClient;
import net.xici.newapp.support.request.XiciException;
import net.xici.newapp.support.util.DeviceUtil;
import net.xici.newapp.support.util.L;
import net.xici.newapp.support.util.SecurityCodeUtil;
import net.xici.newapp.support.util.UIUtil;
import net.xici.newapp.support.util.XiciResponseHandler;
import net.xici.newapp.support.widget.materialdesign.views.ButtonRectangle;
import net.xici.newapp.ui.base.BaseFragment;

import com.afollestad.materialdialogs.MaterialDialog;

import net.xici.newapp.ui.main.HomeActivity;
import net.xici.newapp.ui.web.WebViewActivity;

public class LoginFragment extends BaseFragment implements View.OnClickListener {


    private EditText username;
    private EditText password;
    private EditText captcha;
    private ImageButton username_clean;
    private ImageButton password_clean;
    private ButtonRectangle submit;

    private RelativeLayout captcha_layout;
    private ImageView captcha_imageview;


    private MaterialDialog progressDialog;

    private boolean showpassword = false;

    private QQAuth mQQAuth;
    private Tencent mTencent;


    private String openid;
    private String opentoken;
    private String screen_name;
    private String partnerid = Constants.PARTNERID_QQ;

    ThirdBind thirdBind;

    UMSocialService mController;

    private int error_count = 0;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container, false);
        username = (EditText) view.findViewById(R.id.username);
        password = (EditText) view.findViewById(R.id.password);
        username_clean = (ImageButton) view.findViewById(R.id.username_clean);
        password_clean = (ImageButton) view.findViewById(R.id.password_clean);

        view.findViewById(R.id.submit).setOnClickListener(this);
        view.findViewById(R.id.signup).setOnClickListener(this);
        view.findViewById(R.id.password_eye).setOnClickListener(this);
        view.findViewById(R.id.oauth_qq).setOnClickListener(this);
        view.findViewById(R.id.oauth_sina).setOnClickListener(this);
        view.findViewById(R.id.oauth_weixin).setOnClickListener(this);
        view.findViewById(R.id.forget).setOnClickListener(this);
        view.findViewById(R.id.captcha_change).setOnClickListener(this);
        username_clean.setOnClickListener(this);
        password_clean.setOnClickListener(this);

        submit = (ButtonRectangle) view.findViewById(R.id.submit);
        submit.setRippleSpeed(50);

        captcha_imageview = (ImageView) view.findViewById(R.id.captcha_imageview);
        captcha_layout = (RelativeLayout) view.findViewById(R.id.captcha_layout);
        captcha = (EditText) view.findViewById(R.id.captcha);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        username.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (TextUtils.isEmpty(username.getText().toString())) {
                    username_clean.setVisibility(View.INVISIBLE);
                } else {
                    username_clean.setVisibility(View.VISIBLE);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (TextUtils.isEmpty(password.getText().toString())) {
                    password_clean.setVisibility(View.INVISIBLE);
                } else {
                    password_clean.setVisibility(View.VISIBLE);
                }
            }
        });

        mController = UMServiceFactory.getUMSocialService("com.umeng.login");

        SecurityCodeUtil.getInstance().setSize(DeviceUtil.dp2px(80), DeviceUtil.dp2px(40)).setfontSize(DeviceUtil.dp2px(20)).setBasePaddingTop(DeviceUtil.dp2px(18));


        EventBus.getDefault().register(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.submit:
                //登录
                MobclickAgent.onEvent(getActivity(), "Inlogin_xici");
                dologin();
                break;
            case R.id.signup:
                //注册
                MobclickAgent.onEvent(getActivity(), "signup_login");
//			startActivity(new Intent(getActivity(),
//					RegisterRealUserActivity.class));

                break;
            case R.id.password_eye:
                if (showpassword) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                Editable etable = password.getText();
                Selection.setSelection(etable, etable.length());
                showpassword = !showpassword;
                break;
            case R.id.oauth_qq:
                MobclickAgent.onEvent(getActivity(), "Inlogin_QQ");
                oauth_qq();
                break;
            case R.id.oauth_sina:
                MobclickAgent.onEvent(getActivity(), "Inlogin_weibo");
                oauth_sina();
                break;
            case R.id.oauth_weixin:
                MobclickAgent.onEvent(getActivity(), "Inlogin_weixin");
                oauth_weixin();
                break;
            case R.id.username_clean:
                username.setText("");
                break;
            case R.id.password_clean:
                password.setText("");
                break;
            case R.id.forget:
                MobclickAgent.onEvent(getActivity(), "Inlogin_forget");
                WebViewActivity.start(getActivity(), "忘记密码", Constants.URL_USER_FORGET_3G);
                break;
            case R.id.captcha_change:
                captcha_imageview.setImageBitmap(SecurityCodeUtil.getInstance().createCodeBitmap());
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//		if(mTencent!=null){
//			mTencent.onActivityResult(requestCode, resultCode, data);
//		}
//		if (mSsoHandler != null) {
//            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
//
//		UMSsoHandler umssoHandler = mController.getConfig().getSsoHandler(
//				requestCode);
//		if (umssoHandler != null) {
//			umssoHandler.authorizeCallBack(requestCode, resultCode, data);
//		}
    }

    //qq  授权
    public void oauth_qq() {
        // qq
        partnerid = Constants.PARTNERID_QQ;
        mTencent = Tencent.createInstance(Constants.QQ_APP_ID,
                XiciApp.getContext());
        mQQAuth = QQAuth.createInstance(Constants.QQ_APP_ID,
                XiciApp.getContext());
        mTencent.login(getActivity(), "get_user_info", new LoginUiListener());

    }

    //sina  授权
    public void oauth_sina() {


        // 创建微博实例
//		mWeiboAuth = new WeiboAuth(getActivity(), Constants.WEIBO_APP_KEY,
//						Constants.WEIBO_REDIRECT_URL, Constants.WEIBO_SCOPE);
//
//
//		partnerid = Constants.PARTNERID_WEIBO;
//		mSsoHandler = new SsoHandler(getActivity(), mWeiboAuth);
//		mSsoHandler.authorize(new AuthListener());
    }

    private void dologin() {

        if (captcha_layout.getVisibility() == View.VISIBLE) {
            String c = captcha.getText().toString();
            if (!SecurityCodeUtil.getInstance().getCode().toLowerCase().equals(c.toLowerCase())) {
                UIUtil.showToast(getActivity(), "验证码不正确");
                return;
            }
        }

        final String name = username.getText().toString();
        final String psw = password.getText().toString();
        int logintypetemp = 1;
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(psw)) {
            UIUtil.showToast(getActivity(), "请确认您的帐号信息");
            return;
        }
        try {
            Long.parseLong(name);
            logintypetemp = 0;
        } catch (Exception e) {

        }

        final int logintype = logintypetemp;

        ApiClient.login(getActivity(), name, psw, logintype,
                new XiciResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();


                        progressDialog = new MaterialDialog.Builder(getActivity())
                                .title("")
                                .content(R.string.login_wait)
                                .progress(true, 0)
                                .progressIndeterminateStyle(false)
                                .show();


                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        UserResult result = null;
                        try {
                            result = User.parse(arg0);
                            XiciApp.account = new Account(
                                    name, psw, result.accesstoken, logintype,
                                    result.user);
                            XiciApp.getInstance().saveaccount();
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();


                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } catch (XiciException e) {
                            showCaptchaLayout();
                            Toast.makeText(getActivity(), e.getMsg(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Throwable arg0) {
                        super.onFailure(arg0);
                        onAPIFailure();
                    }

                });

    }

    // weibo


    // qq
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            UIUtil.showToast(getActivity(), e.errorMessage);
        }

        @Override
        public void onCancel() {
        }
    }

    /**
     * qq登录
     *
     * @author gmz
     */
    private class LoginUiListener extends BaseUiListener {

        protected void doComplete(JSONObject values) {
            openid = mQQAuth.getQQToken().getOpenId();
            opentoken = mQQAuth.getQQToken().getAccessToken();
            partnerid = Constants.PARTNERID_QQ;

            doOpenLogin();
        }
    }


    private void doOpenLogin() {


        ApiClient.login(getActivity(), opentoken, partnerid, openid,
                new XiciResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();

                        progressDialog = new MaterialDialog.Builder(getActivity())
                                .title("")
                                .content(R.string.login_wait)
                                .progress(true, 0)
                                .progressIndeterminateStyle(false)
                                .show();


                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        progressDialog.dismiss();
                        UserResult result = null;
                        try {
                            result = User.parse(arg0);

                            XiciApp.account = new Account(
                                    result.user.userName, partnerid,
                                    result.accesstoken, 3, result.user);
                            XiciApp.account.partnerid = partnerid;
                            XiciApp.account.openid = openid;

                            XiciApp.getInstance().saveaccount();
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();

                        } catch (XiciException e) {

                            if (e.getCode() == XiciException.CODE_EXCEPTION_UNBIND_ERROR) {

                                thirdBind = new ThirdBind();

                                thirdBind.open_id = openid;
                                thirdBind.platform = partnerid;
                                if (Constants.PARTNERID_QQ.equals(partnerid)) {

                                    updateQQUserInfo();

                                } else if (Constants.PARTNERID_WEIBO.equals(partnerid)) {

                                    updateWeiboUserInfo();

                                } else {

                                    thirdBind.screen_name = screen_name;
//									ThirdBindSelectActivity.start(getActivity(),
//											thirdBind);

                                }

                            } else {
                                UIUtil.showToast(getActivity(), e.getMsg());
                            }

                        }
                    }

                    @Override
                    public void onFailure(Throwable arg0) {
                        super.onFailure(arg0);
                        progressDialog.dismiss();
                        UIUtil.showToast(getActivity(), R.string.networkerror);
                    }

                });

    }

    //qq 用户信息，得到用户名
    private void updateQQUserInfo() {

    }

    //sina 用户信息，得到用户名
    private void updateWeiboUserInfo() {


    }

    private void showCaptchaLayout() {
        error_count++;
        if (error_count >= 5) {
            captcha_layout.setVisibility(View.VISIBLE);
            captcha.setText("");
            captcha_imageview.setImageBitmap(SecurityCodeUtil.getInstance().createCodeBitmap());
        }
    }

    // 微信
    public void oauth_weixin() {

        // 添加微信平台
        final UMWXHandler wxHandler = new UMWXHandler(getActivity(), Constants.WEIXIN_APPID,
                Constants.WEIXIN_APPSECRET);
        wxHandler.addToSocialSDK();
        wxHandler.setRefreshTokenAvailable(false);

        if (OauthHelper.isAuthenticated(getActivity(), SHARE_MEDIA.WEIXIN)) {

            mController.deleteOauth(getActivity(), SHARE_MEDIA.WEIXIN,
                    new SocializeClientListener() {
                        @Override
                        public void onStart() {
                        }


                        @Override
                        public void onComplete(int arg0, SocializeEntity arg1) {


                            wxHandler.deleteAuthorization(arg1, SHARE_MEDIA.WEIXIN, new SocializeClientListener() {

                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onComplete(int arg0,
                                                       SocializeEntity arg1) {


                                    do_weixin_Oauth();

                                }
                            });


                        }
                    });

        } else {

            do_weixin_Oauth();

        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public void onEvent(Account event) {
        getActivity().finish();
    }

    private void do_weixin_Oauth() {

        mController.doOauthVerify(getActivity(), SHARE_MEDIA.WEIXIN,
                new UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                        // Toast.makeText(mContext, "授权开始", Toast.LENGTH_SHORT)
                        // .show();
                    }

                    @Override
                    public void onError(SocializeException e,
                                        SHARE_MEDIA platform) {

                        UIUtil.showToast(getActivity(), "授权错误");
                    }

                    @Override
                    public void onComplete(Bundle value, SHARE_MEDIA platform) {
                        // Toast.makeText(mContext, "授权完成", Toast.LENGTH_SHORT)
                        // .show();
                        // 获取相关授权信息
                        mController.getPlatformInfo(getActivity(),
                                SHARE_MEDIA.WEIXIN, new UMDataListener() {
                                    @Override
                                    public void onStart() {
                                        // Toast.makeText(MainActivity.this,
                                        // "获取平台数据开始...",
                                        // Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onComplete(int status,
                                                           Map<String, Object> info) {
                                        if (status == 200 && info != null) {
                                            openid = (String) info.get("unionid");
                                            opentoken = "";
                                            screen_name = (String) info.get("nickname");
                                            partnerid = Constants.PARTNERID_WEIXIN;
                                            doOpenLogin();

                                        } else {

                                            UIUtil.showToast(getActivity(), "授权取消");
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA platform) {

                        UIUtil.showToast(getActivity(), "授权取消");
                    }
                });

    }


}
