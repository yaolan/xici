package net.xici.newapp.support.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.widget.Toast;

import net.xici.newapp.R;
import net.xici.newapp.data.pojo.AddMediaItem;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class UIUtil {

    public static SpannableString getThreadTagString(Context context, int top, int cool, String data) {
        return getThreadTagString(context, top, cool, data, null);
    }

	public static SpannableString getThreadTagString(Context context,int top,int cool,String data,List<String> imgs){

		if(TextUtils.isEmpty(data)) {
			return new SpannableString("");
		}
		SpannableString ss = null;
		String pre = "";
		String end = "";
		if(top>=1){
			pre+="  ";
		}else if(cool>0){
			pre+="  ";
		}

		ss = new SpannableString(pre+data+end);
		int index = 0;

		if(top>=1)
		{
			Drawable d = context.getResources().getDrawable(R.drawable.icon_top);
			d.setBounds(0, 0, d.getIntrinsicWidth(),
					d.getIntrinsicHeight());
			ImageSpan span = new ImageSpan(d,
					ImageSpan.ALIGN_BASELINE);

			ss.setSpan(span, index, index+1,
					Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			index+=2;
		}else if(cool>=1){

			Drawable d = context.getResources().getDrawable(R.drawable.icon_cool);
			d.setBounds(0, 0, d.getIntrinsicWidth(),
					d.getIntrinsicHeight());
			ImageSpan span = new ImageSpan(d,
					ImageSpan.ALIGN_BASELINE);

			ss.setSpan(span, index, index+1,
					Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			index+=2;
		}

		return ss;
	}


    public static String getContentString(String content) {
        return getContentString(content, "");
    }

    public static String getContentString(String content, String filename) {
        String newcontent = content.replace("\n", "\r\n");
        if (!TextUtils.isEmpty(filename)) {
            newcontent += "\r\n";
        }
        return newcontent;
    }

    public static String getContentString(Context context, String content, Map<String, AddMediaItem> imageviewlist) {
        StringBuffer stringBuffer = new StringBuffer(content);
        for (Map.Entry<String, AddMediaItem> entry : imageviewlist.entrySet()) {
            AddMediaItem addMediaItem = entry.getValue();
            if (addMediaItem.sended && !TextUtils.isEmpty(addMediaItem.url)) {
                stringBuffer.append("\r\n").append("<img src=\"http://pan.xici.com/").
                        append(addMediaItem.url).append("/800\" />");
            }
        }
        return stringBuffer.toString();
    }

    public static String getContentString(Context context, String content, List<AddMediaItem> list) {
        if (list == null) {
            return content;
        }
        StringBuffer stringBuffer = new StringBuffer(content);
        for (AddMediaItem addMediaItem : list) {
            if (addMediaItem.sended && !TextUtils.isEmpty(addMediaItem.url)) {
                stringBuffer.append("\r\n").append("<img src=\"http://pan.xici.com/").
                        append(addMediaItem.url).append("/800\" />");
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 全数字正则表示
     *
     * @param str
     * @return
     */
    public static boolean isNumeric1(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = pm.getPackageInfo(context
                    .getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
        }
        return "";

    }

    public static void showToast(Context context, int resId) {
        showToast(context, resId, false);
    }

    public static void showToast(Context context, int resId,
                                 boolean durationLong) {
        if (context == null)
            return;
        int duration;
        if (durationLong) {
            duration = Toast.LENGTH_LONG;
        } else {
            duration = Toast.LENGTH_SHORT;
        }

        Toast.makeText(context, resId, duration).show();
    }

    public static void showToast(Context context, String msg) {
        showToast(context, msg, false);
    }

    public static void showToast(Context context, String msg,
                                 boolean durationLong) {
        if (context == null)
            return;
        int duration;
        if (durationLong) {
            duration = Toast.LENGTH_LONG;
        } else {
            duration = Toast.LENGTH_SHORT;
        }

        Toast.makeText(context, msg, duration).show();
    }

    @SuppressLint("NewApi")
    public static void copyContent(android.app.Activity activity, String content) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) activity
                    .getSystemService(Activity.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", content);
            clipboard.setPrimaryClip(clip);
        } else {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) activity
                    .getSystemService(Activity.CLIPBOARD_SERVICE);
            clipboard.setText(content);
        }
    }


}
