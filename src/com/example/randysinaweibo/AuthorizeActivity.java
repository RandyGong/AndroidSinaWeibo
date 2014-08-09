package com.example.randysinaweibo;


import com.randy.DataAccess.DataHelper;
import com.sina.model.UserInfo;
import com.sina.oauth.OAuth;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class AuthorizeActivity extends Activity {
	Dialog dialog;
	OAuth oauth;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.authorize);
        View diaView=View.inflate(this, R.layout.dialog, null);
        dialog=new Dialog(AuthorizeActivity.this,R.style.dialog);
        dialog.setContentView(diaView);
        dialog.show();
        ImageButton startBtn=(ImageButton)diaView.findViewById(R.id.btn_start);
        startBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "alert", Toast.LENGTH_LONG).show();
				oauth=new OAuth();
				oauth.RequestAccessToken(AuthorizeActivity.this, "myapp://AuthorizeActivity");
			}
        	
        });
	}
	@Override
	protected void onNewIntent(Intent intent){
		super.onNewIntent(intent);
	 //åœ¨è¿™é‡Œå¤„ç?†èŽ·å?–è¿”å›žçš„oauth_verifierå?‚æ•°
	   UserInfo user= oauth.getAccessToken(intent);
	   if(user!=null){
		   DataHelper helper=new DataHelper(this);
		   String uid=user.getUserid();
		   if(helper.HaveUserInfo(uid)){
			   helper.UpdateUserInfo(user);
			   Log.e("UserInfo","update");
		   }else{
			   helper.SaveUserInfo(user);
			   Log.e("UserInfo","add");
		   }
	   }
	}

}
