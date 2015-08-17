package net.xici.newapp.support.util;

import android.text.Html;
import android.text.TextUtils;

public class StringUtil {

	public static boolean checkPsw(String str) {
		
		boolean isLetterOrDigit = false;

		boolean isDigit = false;// 定义一个boolean值，用来表示是否包含数字
		boolean isLetter = false;// 定义一个boolean值，用来表示是否包含字母

	
		int lettercount = 0;
		int digitcount = 0;
		
		if(str.length()<6||str.length()>32){
			return false;
		}
		
		for (int i = 0; i < str.length(); i++) { // 循环遍历字符串

			if (Character.isDigit(str.charAt(i))) { // 用char包装类中的判断字母的方法判断每一个字符
				isDigit = true;
				digitcount++;
			}
		}

		for (int i = 0; i < str.length(); i++) { // 循环遍历字符串

			if (Character.isLetter(str.charAt(i))) { // 用char包装类中的判断字母的方法判断每一个字符
				isLetter = true;
				lettercount++;
			}
		}
		
		//不能含其他字符
		if((lettercount+digitcount)<str.length()){
			return false;
		}
		
	    if(isDigit&&isLetter){
	    	//包含字母，数字
	    	return true;
	    	
	    }
	    
//	    else if((lettercount+digitcount)<str.length()){
//	    	//字母或者数字不存在，但是包含其他字符
//	    	return true;
//		}

		return false;
	}

	/**
	 * 判断是否包含数字和字母
	 * 
	 * @return
	 */
	public static boolean containDigitLetter(String str) {

		boolean isLetterOrDigit = false;

		for (int i = 0; i < str.length(); i++) { // 循环遍历字符串
			if (Character.isLetterOrDigit(str.charAt(i))) { // 用char包装类中的判断数字的方法判断每一个字符
				isLetterOrDigit = true;
			}
		}

		return isLetterOrDigit;

	}

	public static boolean containDigit(String str) {

		boolean isDigit = false;// 定义一个boolean值，用来表示是否包含数字

		for (int i = 0; i < str.length(); i++) { // 循环遍历字符串
			if (Character.isDigit(str.charAt(i))) { // 用char包装类中的判断数字的方法判断每一个字符
				isDigit = true;
			}

		}

		return isDigit;
	}

	public static boolean containLetter(String str) {

		boolean isLetter = false;// 定义一个boolean值，用来表示是否包含字母

		for (int i = 0; i < str.length(); i++) { // 循环遍历字符串

			if (Character.isLetter(str.charAt(i))) { // 用char包装类中的判断字母的方法判断每一个字符
				isLetter = true;
			}
		}

		return isLetter;
	}
	
	public static String fromhtml(String str){
		if(!TextUtils.isEmpty(str)){
			return Html.fromHtml(str).toString();
		}
		return str;
	}

}
