package net.xici.newapp.support.widget.emojicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.xici.newapp.support.util.DeviceUtil;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;

public class EmojiconHandler {
	
	public static int emojiSize;
	public static List<Emojicon> EMOJICONS = new ArrayList<Emojicon>();
	public static Pattern PATTERN_FACE = Pattern.compile("(\\[panda:\\w+\\])");
	public static Map<String, Emojicon> EMOJICON_MAP = new HashMap<String, Emojicon>();
	
	static {
		for(int i = 0; i < Panda.EMOJICON_IMAGE_IDS.length; i ++) {
			Emojicon emojicon = new Emojicon(Panda.EMOJICON_IMAGE_IDS[i], Panda.EMOJICON_TEXTS[i]);
			EMOJICONS.add(emojicon);
			EMOJICON_MAP.put(Panda.EMOJICON_TEXTS[i], emojicon);
			emojiSize = DeviceUtil.dp2px(32);
		}
	}
	
	public static SpannableString replaceFace(Context context, String text) {
		SpannableString spannableString;
		if(text == null) {
			text = "";
			spannableString = new SpannableString(text);
			return spannableString;
		}
		spannableString = new SpannableString(text);
		
		Matcher matcher = PATTERN_FACE.matcher(text);
		while(matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			Emojicon emojicon = EMOJICON_MAP.get(matcher.group());
			if(emojicon != null) {
				spannableString.setSpan(new EmojiconSpan(context, emojicon.icon, emojiSize), 
						start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		
		return spannableString;
	}
	
}
