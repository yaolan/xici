package net.xici.newapp.support.util;

import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by bkmac on 15/7/8.
 */
public class XiciResponseHandler extends TextHttpResponseHandler {

    @Override
    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
        onFailure(throwable);
    }

    @Override
    public void onSuccess(int i, Header[] headers, String s) {
        onSuccess(s);
    }

    public void onFailure(Throwable arg0){

    }

    public void onSuccess(String arg0){

    }



}