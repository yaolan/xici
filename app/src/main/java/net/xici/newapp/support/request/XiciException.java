package net.xici.newapp.support.request;

public class XiciException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4450520191229558556L;
	
	public static int CODE_EXCEPTION_JSON = 1;
	
	public static int CODE_EXCEPTION_UNKNOW = 2;
	
	public static int CODE_EXCEPTION_XNBTOKEN = 3;
	
	public static int CODE_EXCEPTION_VALIDATE_TOKEN = 1001003;//validate token token 失效
	
	public static int CODE_EXCEPTION_USERNAMEORPSW_ERROR = 2002003;//用户名密码错误
	
	public static int CODE_EXCEPTION_UNBIND_ERROR = 200208; //未绑定第三方账号

	public static int CODE_EXCEPTION_DOCDELETED_ERROR = 2009008; //msg: "帖子已经被监控或删除
	
	private int code;
	private String msg;
	
	public XiciException(int code)
	{
		this(code, "");
	}
	
	public XiciException(int code,String msg)
	{
		super();
		this.code = code;
		this.msg = msg;
	}
	
	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}



}
