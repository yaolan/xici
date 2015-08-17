package net.xici.newapp.support.util;

import android.os.AsyncTask;

public class CleanUtil {
	
	public static boolean cancelTask(AsyncTask<?, ?, ?> task) {
		if (!isAsynctaskFinished(task)) {
			return task.cancel(true);
		}
		return false;
	}

	public static boolean isAsynctaskFinished(AsyncTask<?, ?, ?> task) {
		if (task != null) {
			if (AsyncTask.Status.FINISHED != task.getStatus()) {
				return false;
			}
		}
		return true;
	}

	public static boolean cancelTask(MyAsyncTask<?, ?, ?> task) {
		if (!isAsynctaskFinished(task)) {
			return task.cancel(true);
		}
		return false;
	}

	public static boolean isAsynctaskFinished(MyAsyncTask<?, ?, ?> task) {
		if (task != null) {
			if (MyAsyncTask.Status.FINISHED != task.getStatus()) {
				return false;
			}
		}
		return true;
	}

}
