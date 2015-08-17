package net.xici.newapp.ui.web;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import net.xici.newapp.app.Constants;
import net.xici.newapp.data.pojo.Board;
import net.xici.newapp.data.pojo.Thread;
import net.xici.newapp.ui.base.BaseActivity;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_BROWSABLE;

public class UriLauncherActivity extends BaseActivity {

	public final static String HOST_DEFAULT = "3g.xici.net";

	public final static String PROTOCOL_HTTP = "http";

	static public void launchUri(Context context, Uri data) {
		Intent intent = getIntentForURI(data);
		if (intent != null) {
			context.startActivity(intent);
		} else {
			context.startActivity(new Intent(ACTION_VIEW, data)
					.addCategory(CATEGORY_BROWSABLE));
		}
	}
	
	static public Intent getIntent(Context context, Uri data) {
		Intent intent = getIntentForURI(data);
		if (intent != null) {
			
			return intent;
			
		} else {
			
			return new Intent(ACTION_VIEW, data).addCategory(CATEGORY_BROWSABLE);
		}
	}

	/**
	 * Convert global view intent one into one that can be possibly opened
	 * inside the current application.
	 *
	 * @param intent
	 * @return converted intent or null if non-application specific
	 */
	static public Intent convert(final Intent intent) {
		if (intent == null)
			return null;

		if (!ACTION_VIEW.equals(intent.getAction()))
			return null;

		Uri data = intent.getData();
		if (data == null)
			return null;

		if (TextUtils.isEmpty(data.getHost())
				|| TextUtils.isEmpty(data.getScheme())) {
			String host = data.getHost();
			if (TextUtils.isEmpty(host))
				host = HOST_DEFAULT;
			String scheme = data.getScheme();
			if (TextUtils.isEmpty(scheme))
				scheme = PROTOCOL_HTTP;
			String prefix = scheme + "://" + host;

			String path = data.getPath();
			if (!TextUtils.isEmpty(path))
				if (path.charAt(0) == '/')
					data = Uri.parse(prefix + path);
				else
					data = Uri.parse(prefix + '/' + path);
			else
				data = Uri.parse(prefix);
			intent.setData(data);
		}

		return getIntentForURI(data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();
		final Uri data = intent.getData();

		final Intent newIntent = getIntentForURI(data);
		if (newIntent != null) {
			startActivity(newIntent);
			finish();
			return;
		}

		if (!intent.hasCategory(CATEGORY_BROWSABLE)) {
			startActivity(new Intent(ACTION_VIEW, data)
					.addCategory(CATEGORY_BROWSABLE));
		}
		finish();
	}

	static private Intent getIntentForURI(Uri data) {

		if ("3g.xici.net".equals(data.getHost())
				|| "www,xici.net".equals(data.getHost())) {

			String url = data.toString();

			if (url.startsWith("http://www.xici.net/d")) {

			
				Thread thread = new Thread();
				try {
					thread.tid = Long.parseLong((String) url.subSequence(
							"http://www.xici.net/d".length(),
							url.indexOf(".htm")));

					Intent intent =  new Intent(Constants.THREAD_VIEW_ACTION);
					intent.putExtra("boardid", 0);
					intent.putExtra("thread", thread);
					return intent;
				
				} catch (Exception e) {

				}

			} else if (url.startsWith( "http://3g.xici.net/d")) {

			
				Thread thread = new Thread();
				try {
					
					thread.tid = Long.parseLong((String) url.subSequence(
							("http://3g.xici.net/d").length(), url.indexOf(".htm")));

					Intent intent =  new Intent(Constants.THREAD_VIEW_ACTION);
					intent.putExtra("boardid", 0);
					intent.putExtra("thread", thread);
					return intent;
					
				} catch (Exception e) {

				}

			} else if (url.contains("method=doc.view")) {

			
				Thread thread = new Thread();
				try {

					thread.tid = Long
							.parseLong((String) url.subSequence(
									url.indexOf("tid=") + "tid=".length(),
									url.length()));

					Intent intent =  new Intent(Constants.THREAD_VIEW_ACTION);
					intent.putExtra("boardid", 0);
					intent.putExtra("thread", thread);
					return intent;
				} catch (Exception e) {

				}

			} else if (url.contains("board.threads")) {

				Board board = new Board();
				board.boardName = "";
				try {

					board.boardUrl = Integer
							.parseInt((String) url.subSequence(
									url.indexOf("bid=") + "bid=".length(),
									url.length()));
					
					Intent intent =  new Intent(Constants.BOARD_VIEW_ACTION);
					intent.putExtra("board", board);
					return intent;
					
				} catch (Exception e) {

				}

			}else if(url.contains(".xici.net")){
				
			
				
			}

		}

		return null;
	}

}
