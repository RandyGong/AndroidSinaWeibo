package com.randy.model;



import android.graphics.drawable.Drawable;

public class UserInfo {
	public static final String ID="id";
	public static final String USERID="userid";
	public static final String TOKEN="token";
	public static final String TOKENSECRET="tokensecret";
	public static final String USERNAME="usernmae";
    public static final String USERICON="icon";
	private Integer id;
	private String userid;
	private String token;
	private String tokensecret;
	private String username;
	private Drawable icon;
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getTokensecret() {
		return tokensecret;
	}
	public void setTokensecret(String tokensecret) {
		this.tokensecret = tokensecret;
	}
     
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon2) {
		this.icon = icon2;
	}
	
}
