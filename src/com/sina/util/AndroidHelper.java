package com.sina.util;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class AndroidHelper {
	 public static void AutoBackground(Activity activity,View view,int _w,int _h){
	    	int orient=ScreenOrient(activity);
	    	if(orient==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
	    		view.setBackgroundResource(_w);
	    	}else{
	    		view.setBackgroundResource(_h);
	    	}
	    	
	    }
	 //获取屏幕方向
	    public static int ScreenOrient(Activity activity){
	    	int orient=activity.getRequestedOrientation();
	    	if(orient!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE&&orient!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
	    		//宽>高为横屏，反正为竖屏
	    		WindowManager windManager=activity.getWindowManager();
	    		Display display=windManager.getDefaultDisplay();
	    		int screenWidth=display.getWidth();
	    		int screenHeight=display.getHeight();
	    		orient=screenWidth<screenHeight?ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	    	}
	    	return orient;
	    }
}
