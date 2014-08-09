package com.sina.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.SortedSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;



import com.sina.model.UserInfo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class OAuth {
	private CommonsHttpOAuthConsumer httpOauthConsumer;
	private OAuthProvider httpOauthprovider;
	public static String CONSUMER_KEY = "258645839";
	public static String CONSUMER_SECRET = "e883a27673d5f8171a65330e648cfbb4";

	public OAuth() {
            
	}

	public Boolean RequestAccessToken(Activity activity, String callBackUrl) {
		Boolean ret = false;
		try {
			httpOauthConsumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY,
					CONSUMER_SECRET);
			httpOauthprovider = new DefaultOAuthProvider(
					"http://api.t.sina.com.cn/oauth/request_token",
					"http://api.t.sina.com.cn/oauth/access_token",
					"http://api.t.sina.com.cn/oauth/authorize");
			String authUrl = httpOauthprovider.retrieveRequestToken(
					httpOauthConsumer, callBackUrl);
			activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse(authUrl)));
			ret = true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return ret;
	}

	public UserInfo getAccessToken(Intent intent) {
		UserInfo user = null;
		Uri uri = intent.getData();
		String verifier = uri
				.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
		try {
			httpOauthprovider.setOAuth10a(true);
			httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);

		} catch (OAuthMessageSignerException ex) {
			ex.printStackTrace();
		} catch (OAuthNotAuthorizedException ex) {
			ex.printStackTrace();
		} catch (OAuthExpectationFailedException ex) {
			ex.printStackTrace();
		} catch (OAuthCommunicationException ex) {
			ex.printStackTrace();
		}
		SortedSet<String> user_id = httpOauthprovider.getResponseParameters()
				.get("user_id");
		String userId = user_id.first();
		String userKey = httpOauthConsumer.getToken();
		String userSecret = httpOauthConsumer.getTokenSecret();
		user = new UserInfo();
		user.setUserid(userId);
		user.setToken(userKey);
		user.setTokensecret(userSecret);
		return user;
	}

	public HttpResponse SignRequest(String token, String tokenSecret,
			String url, List params) {
		HttpPost post = new HttpPost(url);
		try {
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// å…³é—­Expect:100-Continueæ�¡æ‰‹
		// 100-Continueæ�¡æ‰‹éœ€è°¨æ…Žä½¿ç”¨ï¼Œå› ä¸ºé�‡åˆ°ä¸�æ”¯æŒ�HTTP/1.1å��è®®çš„æœ�åŠ¡å™¨æˆ–è€…ä»£ç�†æ—¶ä¼šå¼•èµ·é—®é¢˜
		post.getParams().setBooleanParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
		return SignRequest(token, tokenSecret, post);
	}

	public HttpResponse SignRequest(String token, String tokenSecret,
			HttpPost post) {
		httpOauthConsumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY,
				CONSUMER_SECRET);
		httpOauthConsumer.setTokenWithSecret(token, tokenSecret);
		HttpResponse response = null;
		try {
			httpOauthConsumer.sign(post);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		// å�–å¾—HTTP response
        try {
        	response=new DefaultHttpClient().execute(post);
		} catch (ClientProtocolException  e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
}
