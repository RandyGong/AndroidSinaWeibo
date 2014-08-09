package com.example.randysinaweibo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;








import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.randy.DataAccess.DataHelper;
import com.sina.model.UserInfo;
import com.sina.oauth.OAuth;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends Activity {
	private DataHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        
     // 获取账号列表
     		dbHelper = new DataHelper(this);
     		List<UserInfo> userList = dbHelper.GetUserList(true);

     		if (userList.isEmpty()) {
     			// 如果为空说明第一次使用跳到AuthorizeActivity页面进行OAuth认证
     			Intent intent = new Intent();
     			intent.setClass(MainActivity.this, AuthorizeActivity.class);
     			startActivity(intent);
     		} else {
     			// 然后根据这3个值调用新浪的api接口获取这些记录对应的用户昵称和用户头像图标等信息。
     			UpdateUserInfo(this, userList);
     			Intent intent = new Intent();
     			intent.setClass(MainActivity.this, LoginActivity.class);
     			startActivity(intent);
     		}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    public void UpdateUserInfo(Context context, List<UserInfo> userList) {
		DataHelper dbHelper = new DataHelper(context);
		OAuth auth = new OAuth();
		String url = "http://api.t.sina.com.cn/users/show.json";
		Log.e("userCount", userList.size() + "");
		for (UserInfo user : userList) {
			if (user != null) {
				List params = new ArrayList();
				params.add(new BasicNameValuePair("source", auth.CONSUMER_KEY));
				params.add(new BasicNameValuePair("user_id", user.getUserid()));
				HttpResponse response = auth.SignRequest(user.getToken(), user
						.getTokensecret(), url, params);
				if (200 == response.getStatusLine().getStatusCode()) {
					try {
						InputStream is = response.getEntity().getContent();
						Reader reader = new BufferedReader(
								new InputStreamReader(is), 4000);
						StringBuilder buffer = new StringBuilder((int) response
								.getEntity().getContentLength());
						try {
							char[] tmp = new char[1024];
							int l;
							while ((l = reader.read(tmp)) != -1) {
								buffer.append(tmp, 0, l);
							}
						} finally {
							reader.close();
						}
						String string = buffer.toString();
						response.getEntity().consumeContent();
						JSONObject data = new JSONObject(string);
						String ImgPath = data.getString("profile_image_url");
						Bitmap userIcon = DownloadImg(ImgPath);

						String userName = data.getString("screen_name");
						dbHelper.UpdateUserInfo(userName, userIcon, user
								.getUserid());
						Log.e("ImgPath", ImgPath);

					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		dbHelper.Close();
	}
    public Bitmap DownloadImg(String url) {
		URL uri;
		Bitmap bm = null;
		try {
			uri = new URL(url);
			// 获取图片流数据
			InputStream is = uri.openStream();
			// 生成图片
			bm = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bm;
	}

}
