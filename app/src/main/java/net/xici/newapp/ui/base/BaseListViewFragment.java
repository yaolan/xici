package net.xici.newapp.ui.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.xici.newapp.R;
import net.xici.newapp.support.widget.LoadingFooter;
import net.xici.newapp.support.widget.PageListView;

import java.lang.ref.WeakReference;

/**
 * 下拉刷新
 * Created by bkmac on 15/1/27.
 */
public abstract class BaseListViewFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    protected BaseAdapter mAdapter;

    protected SwipeRefreshLayout mSwipeLayout;

    protected PageListView mListView;

    protected View emptyview;

    protected TextView empty_text;
    protected ImageView empty_image;

    protected int page = 1;
    protected boolean first = true;

    private EmptyHandler mEmptyHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(getLayout(),
                container, false);

        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mListView = (PageListView) view.findViewById(R.id.list);

        emptyview = view.findViewById(R.id.empty_view);
        empty_text = (TextView) view.findViewById(R.id.empty_text);
        empty_image = (ImageView) view.findViewById(R.id.empty_image);





        return view;
    }

    protected int getLayout() {
        return R.layout.pagelist_layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeLayout.setOnRefreshListener(this);
        mListView.setLoadNextListener(new PageListView.OnLoadNextListener() {
            @Override
            public void onLoadNext() {
                loadnext();
            }

            @Override
            public boolean getLoadNextAble() {
                return !mSwipeLayout.isRefreshing();
            }
        });

        buildListAdapter();
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(listViewOnItemClickListener);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEmptyHandler = new EmptyHandler(this);

    }

    /**
     * LoadingFooter 设置
     *
     * @param isRefreshFromTop 是否初始加载
     * @param error            错误
     * @param hasemore         是否还有更多
     */
    protected void onFinishRequest(boolean isRefreshFromTop, boolean error, boolean hasemore) {

        if (isRefreshFromTop) {
            //刷新
            mSwipeLayout.setRefreshing(false);
            if (hasemore) {
                mListView.setState(LoadingFooter.State.Idle, 300);
            } else {
                mListView.setState(LoadingFooter.State.TheEnd, 300);
            }


        } else {

            if (error) {
                //加载更多错误
                mListView.setState(LoadingFooter.State.Error, 300);
            } else if (hasemore) {

                mListView.setState(LoadingFooter.State.Idle, 300);

            } else {
                mListView.setState(LoadingFooter.State.TheEnd, 300);
            }

        }
        mSwipeLayout.setRefreshing(false);

    }

    private AdapterView.OnItemClickListener listViewOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            int headerViewsCount = mListView.getHeaderViewsCount();
            if (isPositionBetweenHeaderViewAndFooterView(position)) {
                int indexInDataSource = position - headerViewsCount;
                listViewItemClick(parent, view, indexInDataSource, id);

            } else if (isLastItem(position)) {

            }
        }

    };

    protected boolean isPositionBetweenHeaderViewAndFooterView(int position) {
        return position - mListView.getHeaderViewsCount() < mAdapter
                .getCount()
                && position - mListView.getHeaderViewsCount() >= 0;
    }

    protected boolean isLastItem(int position) {
        return position - mListView.getHeaderViewsCount() >= mAdapter
                .getCount();
    }

    protected abstract void buildListAdapter();

    protected BaseAdapter getAdapter() {
        return mAdapter;
    }

    protected abstract void listViewItemClick(AdapterView parent, View view,
                                              int position, long id);

    protected abstract void loadfirst();

    protected abstract void loadnext();

    @Override
    public void onRefresh() {
        loadfirst();
    }

    //空数据提示


    //设置空数据状态
    protected void setEmptyText(String text) {
        empty_text.setText(text);
    }

    protected void setEmptyImage(int res) {
        empty_image.setBackgroundResource(res);
    }

    //显示空数据状态
    protected void showEmptyifNeed() {
        showEmptyifNeed(true);
    }

    protected void showEmptyifNeed(boolean show) {
        mEmptyHandler.removeMessages(EMPTY_VIEW_MESSAGE);
        emptyview.setVisibility(View.GONE);
        if (show && (getAdapter() == null || getAdapter().getCount() == 0)) {
            mEmptyHandler.sendEmptyMessageDelayed(EMPTY_VIEW_MESSAGE, 300);
        }
    }

    private final static int EMPTY_VIEW_MESSAGE = 100;

    static class EmptyHandler extends Handler {
        private WeakReference<BaseListViewFragment> mPageListFragment;

        EmptyHandler(BaseListViewFragment fragment) {
            mPageListFragment = new WeakReference<BaseListViewFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseListViewFragment fragment = mPageListFragment.get();
            if (fragment != null) {
                fragment.handleMessage(msg);
            }
        }
    }

    public void handleMessage(Message msg) {
        if (!isAdded()) {
            return;
        }
        switch (msg.what) {
            case EMPTY_VIEW_MESSAGE:

                if (getAdapter() == null || getAdapter().getCount() == 0) {
                    emptyview.setVisibility(View.VISIBLE);
                } else {
                    emptyview.setVisibility(View.INVISIBLE);
                }

                break;

            default:
                break;
        }
    }

    protected PageListView getListView() {
        return mListView;
    }

    @Override
    public void onStop() {
        super.onStop();
        mEmptyHandler.removeMessages(EMPTY_VIEW_MESSAGE);
    }


}
