package net.xici.newapp.ui.mine.fragment;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.umeng.analytics.MobclickAgent;

import net.xici.newapp.R;
import net.xici.newapp.app.Constants;
import net.xici.newapp.app.XiciApp;
import net.xici.newapp.data.contentprovider.MailContentProvider;
import net.xici.newapp.data.dao.MailDao;
import net.xici.newapp.data.pojo.AppMail;
import net.xici.newapp.data.pojo.XiciResult;
import net.xici.newapp.support.request.ApiClient;
import net.xici.newapp.support.request.XiciException;
import net.xici.newapp.support.util.TaskUtils;
import net.xici.newapp.support.util.UIUtil;
import net.xici.newapp.support.util.XiciResponseHandler;
import net.xici.newapp.ui.adapter.AppMailCursorAdapter;
import net.xici.newapp.ui.base.BaseListViewFragment;
import net.xici.newapp.ui.mine.AppMailItemActivity;

import java.util.List;

public class AppMailFragment extends BaseListViewFragment implements
		LoaderManager.LoaderCallbacks<Cursor>,
		AdapterView.OnItemLongClickListener {

	public final static int DEL_MAIL = 0;
	public final static int View_MAIL = 1;
	private final static int GET_MAILS = 1;

	private int page = 1;
	private MailDao mailDao;

	private AppMail mAppMail;

	int mailtype = MailContentProvider.MAIL_TYPE_APPMAIL;

	@Override
	protected void buildListAdapter() {
		mAdapter = new AppMailCursorAdapter(getActivity(), null);
	}

	@Override
	protected void listViewItemClick(AdapterView parent, View view,
			int position, long id) {

		AppMail mail = ((AppMailCursorAdapter) mAdapter).getMailItem(position);
		AppMailItemActivity.start(getActivity(), mail.uid, mail.mUserName);
	}

	@Override
	protected void loadfirst() {

		doGetMails();
	}

	@Override
	protected void loadnext() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// mActionBar.setDisplayShowHomeEnabled(false);
		// mActionBar.setDisplayShowTitleEnabled(true);
		// mActionBar.setDisplayHomeAsUpEnabled(true);
		// mActionBar.setTitle(R.string.minemail);
		setEmptyText("哎呀！还没人给你发飞语。赶紧的发个给朋友呗~");

		getListView().setOnItemLongClickListener(this);

		mailDao = new MailDao(XiciApp.getContext());

		
		registerForContextMenu(getListView());

		getLoaderManager().initLoader(0, null, this);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_mailwrite, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_new:

			MobclickAgent.onEvent(getActivity(), "news_set");
			//startActivity(new Intent(MailWriteActivity.ACTION_NEW));
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	private void doGetMails() {
		ApiClient.user_appmails(getActivity(), page, new XiciResponseHandler() {

			@Override
			public void onSuccess(String arg0) {
				super.onSuccess(arg0);
				try {

					final List<AppMail> result = AppMail.parse(arg0);

					if (result != null) {

						TaskUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
							@Override
							protected Object doInBackground(Object... params) {
								if (page <= 1) {
									mailDao.bulkInsertAppMails(result, getAccountId(),
											0);

								} else {
									mailDao.bulkInsertAppMails(result, getAccountId(),
											1);
								}
								return null;
							}

							@Override
							protected void onPostExecute(Object o) {
								super.onPostExecute(o);

								onFinishRequest(page == 1, false, result.size() >= Constants.PAGESIZE);
								if (result.size() >= Constants.PAGESIZE) {
									page++;
								}

							}
						});



					} else {
						onFinishRequest(page == 1, true, false);
					}


				} catch (XiciException e) {
					OnApiException(e, GET_MAILS);
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
		return mailDao.getCursorLoader(getAccountId(), mailtype);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		((AppMailCursorAdapter) mAdapter).swapCursor(cursor);

		showEmptyifNeed();
		if (first) {
			first = false;
			loadfirst();
		}

		showEmptyifNeed();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		((AppMailCursorAdapter) mAdapter).swapCursor(null);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		if (isPositionBetweenHeaderViewAndFooterView(position)) {

			mAppMail = null;

			mAppMail = ((AppMailCursorAdapter) mAdapter).getMailItem(position
					- getListView().getHeaderViewsCount());

			if (mAppMail != null) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (mAppMail != null) {
			menu.setHeaderTitle(mAppMail.mUserName);
			menu.clear();

			menu.add(0, DEL_MAIL, 0, "删除");
			menu.add(0, View_MAIL, 0, "查看");

		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DEL_MAIL:
			new MaterialDialog.Builder(getActivity())
					.content(R.string.mail_delete)
					.positiveText(R.string.ok)
					.negativeText(R.string.cancel)
					.callback(new MaterialDialog.ButtonCallback() {
						@Override
						public void onPositive(MaterialDialog dialog) {
							domaildelete();
						}

						@Override
						public void onNegative(MaterialDialog dialog) {
						}
					})
					.show();
			break;
		case View_MAIL:
			//AppMailItemActivity.start(getActivity(), mAppMail.uid, mAppMail.mUserName);
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 删飞鱼
	 */
	private void domaildelete() {

		ApiClient.user_mail_del(getActivity(), mAppMail.mUserName,
				new XiciResponseHandler() {

					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						try {
							XiciResult result = null;

							// 退订
							result = XiciResult.deleteMailparse(arg0);
							if (result.success) {
								
								mailDao.deleteByAccountUsername(getAccountId(), mAppMail.mUserName);
								
								UIUtil.showToast(getActivity(), "飞语已删除");
							}

						} catch (XiciException e) {
							OnApiException(e, false);
						}
					}

					@Override
					public void onFailure(Throwable arg0) {
						super.onFailure(arg0);
						onAPIFailure();
					}

				});

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		unregisterForContextMenu(getListView());
	}


}
