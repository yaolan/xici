package net.xici.newapp.ui.mine.fragment;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;

import com.umeng.analytics.MobclickAgent;

import net.xici.newapp.app.Constants;
import net.xici.newapp.data.dao.FloorDao;
import net.xici.newapp.data.pojo.FloorFollow;
import net.xici.newapp.data.pojo.UserPing;
import net.xici.newapp.support.request.ApiClient;
import net.xici.newapp.support.request.XiciException;
import net.xici.newapp.support.set.SettingUtil;
import net.xici.newapp.support.util.TaskUtils;
import net.xici.newapp.support.util.XiciResponseHandler;
import net.xici.newapp.ui.adapter.FloorFollowAdapter;
import net.xici.newapp.ui.base.BaseListViewFragment;

public class FloorFollowFragment extends BaseListViewFragment implements LoaderManager.LoaderCallbacks<Cursor>{
	
	private int page = 1;

	private final static int GETFLOORFOLLOW = 1;
	
	private String type;
	
	private FloorDao mFloorDao;
	
	public static FloorFollowFragment newinstance(String type){
		FloorFollowFragment fragment = new FloorFollowFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
	}

	@Override
	protected void buildListAdapter() {
		mAdapter = new FloorFollowAdapter(getActivity(), null);
	}

	@Override
	protected void listViewItemClick(AdapterView parent, View view,
			int position, long id) {
		

		
	}

	@Override
	protected void loadfirst() {

		page = 1;
		doGetFloorFollow();

	}

	@Override
	protected void loadnext() {

		doGetFloorFollow();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		type = getArguments().getString("type");
		mFloorDao = new FloorDao(getActivity().getApplicationContext());

		int threadid;
		if (FloorFollow.TYPE_ATME.equals(type)) {

			MobclickAgent.onEvent(getActivity(), "news_and");
			
			setEmptyText("还没人@你呢！你需要更加活跃喔～");
			threadid = 1;
			((FloorFollowAdapter)mAdapter).setType("楼@我");
		} else {

			MobclickAgent.onEvent(getActivity(), "news_follow");
			
			setEmptyText("还没人回复你的帖子，需要加油发帖喔～");
			threadid = 2;
			((FloorFollowAdapter)mAdapter).setType("楼跟帖");
		}
		
		if(getParentFragment() instanceof FloorFollowAdapter.OnFloowFollowClickedListener){

			((FloorFollowAdapter)mAdapter).setOnFloowFollowClickedListener((FloorFollowAdapter.OnFloowFollowClickedListener)getParentFragment());
		}
		
		
		getLoaderManager().initLoader(threadid, null, this);
	}
	
	private void doGetFloorFollow() {
		ApiClient.userAtlist(getActivity(), type, page, new XiciResponseHandler() {

			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {

					final FloorFollow.FloorFollowResult result = FloorFollow.parse(arg0);

					if (result != null) {

						TaskUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
							@Override
							protected Object doInBackground(Object... params) {
								if (page <= 1) {

									mFloorDao.bulkInsertFloorFlow(result.floors, getAccountId(), type, 0);

									UserPing ping = SettingUtil.getAccountMessageCount(getAccountId());
									if (ping == null) {
										ping = new UserPing();
									}
									if (FloorFollow.TYPE_ATME.equals(type)) {
										ping.atme = 0;
									} else {
										ping.follow = 0;
									}
									UserPing.postevent(ping);

								} else {

									mFloorDao.bulkInsertFloorFlow(result.floors, getAccountId(), type, 1);

								}
								return null;
							}

							@Override
							protected void onPostExecute(Object o) {
								super.onPostExecute(o);

								onFinishRequest(page == 1, false, result.floors.size() >= Constants.PAGESIZE);
								if(result.floors.size()>=Constants.PAGESIZE){
									page++;
								}

							}
						});


					} else {
						onFinishRequest(page == 1, true, false);
					}


				} catch (XiciException e) {
					onFinishRequest(page == 1, true, false);
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
				super.onFailure(arg0);
				onAPIFailure();
				onFinishRequest(page == 1, true, false);
			}

		});
	}
	


	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return mFloorDao.getFloorFollowCursorLoader(getActivity(),getAccountId(),type);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
		((FloorFollowAdapter)mAdapter).swapCursor(data);		
		showEmptyifNeed();
		
		if(first){
			first = false;

			loadfirst();
		}
		showEmptyifNeed();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		 ((FloorFollowAdapter)mAdapter).swapCursor(null);
	}

}
