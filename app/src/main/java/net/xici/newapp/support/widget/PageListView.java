package net.xici.newapp.support.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

import net.xici.newapp.app.Constants;


/**
 * Created by bkmac on 15/1/27.
 */
public class PageListView extends ListView implements AbsListView.OnScrollListener  {
    private LoadingFooter mLoadingFooter;

    private OnLoadNextListener mLoadNextListener;

    public PageListView(Context context) {
        super(context);
        init();
    }

    public PageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PageListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mLoadingFooter = new LoadingFooter(getContext());
        mLoadingFooter.setLoadingFooterOnClickListener(new LoadingFooter.LoadingFooterOnClickListener() {
            @Override
            public void onErrorClick() {
                mLoadingFooter.setState(LoadingFooter.State.Loading);
                mLoadNextListener.onLoadNext();
            }
        });
        addFooterView(mLoadingFooter.getView());
        setOnScrollListener(this);
    }

    public void setLoadNextListener(OnLoadNextListener listener) {
        mLoadNextListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mLoadingFooter.getState() == LoadingFooter.State.Loading
                || mLoadingFooter.getState() == LoadingFooter.State.TheEnd
                ||mLoadingFooter.getState() == LoadingFooter.State.Error) {
            return;
        }
        if (firstVisibleItem + visibleItemCount >= totalItemCount
                && totalItemCount != 0
                && totalItemCount != getHeaderViewsCount()
                + getFooterViewsCount() && mLoadNextListener != null
                && mLoadNextListener.getLoadNextAble()
                && ((totalItemCount-getHeaderViewsCount()-getFooterViewsCount())>= Constants.PAGESIZE)) {
            mLoadingFooter.setState(LoadingFooter.State.Loading);
            mLoadNextListener.onLoadNext();
        }
    }

    public void setState(LoadingFooter.State status) {
        mLoadingFooter.setState(status);
    }

    public void setState(LoadingFooter.State status, long delay) {
        mLoadingFooter.setState(status, delay);
    }

    public interface OnLoadNextListener {
        public void onLoadNext();
        public boolean getLoadNextAble();
    }


}
