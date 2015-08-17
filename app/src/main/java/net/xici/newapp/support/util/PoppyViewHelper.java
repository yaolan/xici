package net.xici.newapp.support.util;


import com.nineoldandroids.view.ViewPropertyAnimator;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class PoppyViewHelper {
	
	public enum PoppyViewPosition {
		TOP, BOTTOM
	};

	private static final int SCROLL_TO_TOP = - 1;

	private static final int SCROLL_TO_BOTTOM = 1;

	private static final int SCROLL_DIRECTION_CHANGE_THRESHOLD = 5;

	private View mPoppyView;

	private int mScrollDirection = 0;

	private int mPoppyViewHeight = - 1;

	private PoppyViewPosition mPoppyViewPosition = PoppyViewPosition.BOTTOM;
	
	private void onScrollPositionChanged(int oldScrollPosition, int newScrollPosition) {
		int newScrollDirection;

		L.d(this, oldScrollPosition + " ->" + newScrollPosition);

		if(newScrollPosition < oldScrollPosition) {
			newScrollDirection = SCROLL_TO_TOP;
		} else {
			newScrollDirection = SCROLL_TO_BOTTOM;
		}

		if(newScrollDirection != mScrollDirection) {
			mScrollDirection = newScrollDirection;
			if(mPoppyView.getVisibility()==View.VISIBLE){
				translateYPoppyView();
			}
		}
	}

	private void translateYPoppyView() {
		mPoppyView.post(new Runnable() {

			@Override
			public void run() {
				if(mPoppyViewHeight <= 0) {
					mPoppyViewHeight = mPoppyView.getHeight();
				}

				int translationY = 0;
				switch (mPoppyViewPosition) {
				case BOTTOM:
					translationY = mScrollDirection == SCROLL_TO_TOP ? 0 : mPoppyViewHeight;
					break;
				case TOP:
					translationY = mScrollDirection == SCROLL_TO_TOP ? -mPoppyViewHeight : 0;
					break;
				}

				ViewPropertyAnimator.animate(mPoppyView).setDuration(300).translationY(translationY);
			}
		});
	}
	
	public void initPoppyViewOnListView(ListView listView,View popyView,final OnScrollListener onScrollListener){
		this.mPoppyView = popyView;
		listView.setOnScrollListener(new OnScrollListener() {
			
			int mScrollPosition;
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(onScrollListener != null) {
					onScrollListener.onScrollStateChanged(view, scrollState);
				}
			
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
				if(onScrollListener != null) {
					onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				}
				View topChild = view.getChildAt(0);

				int newScrollPosition = 0;
				if(topChild == null) {
					newScrollPosition = 0;
				} else {
					newScrollPosition = - topChild.getTop() + view.getFirstVisiblePosition() * topChild.getHeight();
				}

				if(Math.abs(newScrollPosition - mScrollPosition) >= SCROLL_DIRECTION_CHANGE_THRESHOLD) {
					onScrollPositionChanged(mScrollPosition, newScrollPosition);
				}

				mScrollPosition = newScrollPosition;
			}
		});
	}

}
