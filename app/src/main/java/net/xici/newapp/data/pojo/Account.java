package net.xici.newapp.data.pojo;

import java.io.Serializable;

public class Account implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -255080828476257444L;
	
	public String username;
	
	public String password;
	
	public String city;
	
	public long userid;
	
	public int userlevel;
	
	public String accesstoken;
	
	public String openid;
	
	public String partnerid;
	
	public int logintype = 1; //0用户id登录 1用户名登录 3 第三方登录
	
	public int isdefault = 1;
	
	public Account(){}
	
	public Account(String username,String password,String accesstoken,User user)
	{
	    this(username, password, accesstoken, 1, user);
	}
	
	public Account(String username,String password,String accesstoken,int logintype,User user)
	{
		this.username = user.userName;
		this.password = password;
		this.accesstoken = accesstoken;
		city = user.city;
		userid = user.userid;
		userlevel = user.userlevel;
		this.logintype = logintype;
	}
	
	public long getUserid()
	{
		return userid;
	}
	
	/**
	 * 是否真实网友
	 * @return
	 */
	public boolean isRealUser(){
		if(userlevel>0&&userlevel!=8&&userlevel!=30){
			return true;
		}else{
			return false;
		}
		
	}
	
}
