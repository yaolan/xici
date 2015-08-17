package net.xici.newapp.ui.web;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import net.xici.newapp.R;
import net.xici.newapp.ui.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WebViewFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.webview)
    protected WebView mWebview;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;
    @InjectView(R.id.empty_image)
    ImageView mEmptyImage;
    @InjectView(R.id.empty_text)
    TextView mEmptyText;
    @InjectView(R.id.empty_view)
    LinearLayout mEmptyView;
    @InjectView(R.id.progress_layout)
    ProgressBar mProgressLayout;

    private boolean backenable = true;

    private String murl = "";
    private String title = "";

    private boolean startacitivity = false;


    public static WebViewFragment newInstance(Bundle b) {
        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        ButterKnife.inject(this, view);
        mSwipeContainer.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initview();
    }

    public void initview(){





        mWebview.setWebViewClient(new MyWebViewClient());

        mWebview.setWebChromeClient(new GapClient(getActivity()));

        WebSettings settings = mWebview.getSettings();
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        //settings.setDefaultFontSize(20);


        mWebview.addJavascriptInterface(new GetTopicdesc(), "gettopicdesc"); // 向webview注册一个本地接口
        mWebview.addJavascriptInterface(new GetDescription(), "getdescription"); // 向webview注册一个本地接口

        if (getArguments() != null) {
            murl = getArguments().getString("url");
            title = getArguments().getString("title");


            if (!TextUtils.isEmpty(murl)) {

                mProgressLayout.setVisibility(View.VISIBLE);
                setRefreshing(true);
                mWebview.loadUrl(murl);
            }

            boolean refreshenable = getArguments().getBoolean("refreshenable", true);

            setRefreshEnabled(refreshenable);
        }



    }

    @Override
    public void onRefresh() {


        mWebview.reload();
        mProgressLayout.setVisibility(View.VISIBLE);
        mWebview.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebview.stopLoading();
        mWebview.setWebChromeClient(null);
        mWebview.setWebViewClient(null);
    }

    public void loadurl(String u) {
        if (u.equals(murl)){
            return;
        }

        murl = u;
        if (!TextUtils.isEmpty(murl)) {

            mProgressLayout.setVisibility(View.VISIBLE);
            setRefreshing(true);
            mWebview.loadUrl(murl);
        }

    }

    public boolean goback() {

        if (backenable && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }
        return false;
    }

    public void setRefreshEnabled(boolean enabled) {
        mSwipeContainer.setEnabled(enabled);
    }

    public void setRefreshing(boolean refreshing) {
        mSwipeContainer.setRefreshing(refreshing);
    }

    protected class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {

            view.loadUrl("javascript:window.gettopicdesc.onGetTopicdesc("
                    + "document.getElementsByName('topicdesc')[0].content" + ");");


            view.loadUrl("javascript:window.getdescription.onGetDescription("
                    + "document.getElementsByName('description')[0].content" + ");");


            setRefreshing(false);
            mProgressLayout.setVisibility(View.GONE);

            super.onPageFinished(view, url);

        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mWebview.setVisibility(View.GONE);
            mEmptyText.setText("网络出错!");
            mEmptyView.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {




            view.loadUrl(url);
            return true;
        }
    }

    /**
     * Provides a hook for calling "alert" from javascript. Useful for debugging
     * your javascript.
     */
    public class GapClient extends WebChromeClient {

        private Context ctx;

        /**
         * Constructor.
         *
         * @param ctx
         */
        public GapClient(Context ctx) {
            this.ctx = ctx;
        }

        /**
         * Tell the client to display a javascript alert dialog.
         *
         * @param view
         * @param url
         * @param message
         * @param result
         */
        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 final JsResult result) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this.ctx);
            dlg.setMessage(message);
            dlg.setTitle("Alert");
            dlg.setCancelable(false);
            dlg.setPositiveButton(android.R.string.ok,
                    new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    });
            dlg.create();
            dlg.show();
            return true;
        }

        /**
         * Tell the client to display a confirm dialog to the user.
         *
         * @param view
         * @param url
         * @param message
         * @param result
         */
        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this.ctx);
            dlg.setMessage(message);
            dlg.setTitle("Confirm");
            dlg.setCancelable(false);
            dlg.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    });
            dlg.setNegativeButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    });
            dlg.create();
            dlg.show();
            return true;
        }

        /**
         * Tell the client to display a prompt dialog to the user. If the client
         * returns true, WebView will assume that the client will handle the
         * prompt dialog and call the appropriate JsPromptResult method.
         *
         * @param view
         * @param url
         * @param message
         * @param defaultValue
         * @param result
         */
        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, JsPromptResult result) {

            return true;
        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode== Activity.RESULT_OK){

        }
    }



    @Override
    public void onResume() {
        super.onResume();
        startacitivity = false;
    }

    private String topicdes = "";
    private String description = "";

    class GetTopicdesc {
        @JavascriptInterface
        public void onGetTopicdesc(String str) {

            Logger.d(str);
            if(!TextUtils.isEmpty(str)){

                topicdes = str;
            }
        }
    }

    class GetDescription {
        @JavascriptInterface
        public void onGetDescription(String str) {
            Logger.d(str);
            if(!TextUtils.isEmpty(str)){

                description = str;
            }

        }
    }




}
