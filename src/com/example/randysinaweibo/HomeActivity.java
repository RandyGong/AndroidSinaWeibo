package com.example.randysinaweibo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sina.image.AsyncImageLoader;
import com.sina.image.AsyncImageLoader.ImageCallback;
import com.sina.model.UserInfo;
import com.sina.model.WeiBoHolder;
import com.sina.model.WeiBoInfo;
import com.sina.oauth.OAuth;
import com.sina.util.ConfigHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HomeActivity extends Activity {
	private UserInfo user = null;
	private List<WeiBoInfo> wbList;
	private LinearLayout loadingLayout=null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		loadList();
	}

	private void loadList() {
		loadingLayout=(LinearLayout)findViewById(R.id.loadingLayout);
		if (ConfigHelper.nowUser == null) {

		} else {
			user = ConfigHelper.nowUser;
			// 显示当前用户名称
			TextView showName = (TextView) findViewById(R.id.showName);
			showName.setText(user.getUsername());

			OAuth auth = new OAuth();
			String url = "http://api.t.sina.com.cn/statuses/friends_timeline.json";
			List params = new ArrayList();
			params.add(new BasicNameValuePair("source", auth.CONSUMER_KEY));
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
					// Log.e("json", "rs:" + string);
					response.getEntity().consumeContent();
					JSONArray data = new JSONArray(string);
					for (int i = 0; i < data.length(); i++) {
						JSONObject d = data.getJSONObject(i);
						// Log.e("json", "rs:" + d.getString("created_at"));
						if (d != null) {
							JSONObject u = d.getJSONObject("user");
							if (d.has("retweeted_status")) {
								JSONObject r = d.getJSONObject("retweeted_status");
							}

							// 微博id
							String id = d.getString("id");
							String userId = u.getString("id");
							String userName = u.getString("screen_name");
							String userIcon = u.getString("profile_image_url");
							Log.e("userIcon", userIcon);
							String time = d.getString("created_at");
							String text = d.getString("text");
							Boolean haveImg = false;
							if (d.has("thumbnail_pic")) {
								haveImg = true;
								// String
								// thumbnail_pic=d.getString("thumbnail_pic");
								// Log.e("thumbnail_pic", thumbnail_pic);
							}

							Date date = new Date(time);
							time = ConvertTime(date);
							if (wbList == null) {
								wbList = new ArrayList<WeiBoInfo>();
							}
							WeiBoInfo w = new WeiBoInfo();
							w.setId(id);
							w.setUserId(userId);
							w.setUserName(userName);
							w.setTime(time);
							w.setText(text);

							w.setHaveImage(haveImg);
							w.setUserIcon(userIcon);
							wbList.add(w);
						}
					}

				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			if (wbList != null) {
				WeiBoAdapater adapater = new WeiBoAdapater();
				ListView Msglist = (ListView) findViewById(R.id.Msglist);
				Msglist.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int arg2, long arg3) {
						Object obj = view.getTag();
						if (obj != null) {
							String id = obj.toString();
							Intent intent = new Intent(HomeActivity.this,
									ViewActivity.class);
							Bundle b = new Bundle();
							b.putString("key", id);
							intent.putExtras(b);
							startActivity(intent);
						}

					}

				});
				Msglist.setAdapter(adapater);
			}
		}
		 loadingLayout.setVisibility(View.GONE);
	}

	private String ConvertTime(Date olddate) {
		Date nowDate = new Date();

		Long timeSub = (nowDate.getTime() - olddate.getTime()) / 1000;
		int day = (int) (timeSub / (3600 * 24));
		if(day > 1) {
			return (day + "天前");
		}else {
			int hours = (int) (timeSub % (3600 * 24));
			int hour = hours / (3600);
			if (hour > 1)
				return (hour + "小时前");
			int mins = (int) (hours % 3600);
			int min = mins / (60);
			if (min > 1) {
				return (min + "分种前");
			} else {
				int sec = (int) (mins % 60);
				return (sec + "秒前");
			}

		}

	}

	// textHighlight(wh.wbtext, new char[] { '#' }, new char[] {
				// '#' });
	private void textHighlight(TextView textView,char[] chars,char[] ss,String s){
		
	}
	private void textHighlight2(TextView textView, String start, String end) {
		Spannable sp = (Spannable) textView.getText();
		String text = textView.getText().toString();
		int n = 0;
		int s = -1;
		int e = -1;
		while (n < text.length()) {
			s = text.indexOf(start, n);
			if (s != -1) {
				e = text.indexOf(end, s + start.length());
				if (e != -1) {
					e = e + end.length();
				} else {
					e = text.length();
				}
				n = e;
				sp.setSpan(new ForegroundColorSpan(Color.BLUE), s, e,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				s = e = -1;
			} else {
				n = text.length();
			}
		}
	}

	public class WeiBoAdapater extends BaseAdapter {

		private AsyncImageLoader asyncImageLoader;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return wbList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return wbList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			asyncImageLoader = new AsyncImageLoader();
			if (convertView == null) {
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.weibo, null);
			}

			WeiBoHolder wh = new WeiBoHolder();
			wh.wbicon = (ImageView) convertView.findViewById(R.id.wbicon);
			wh.wbtext = (TextView) convertView.findViewById(R.id.wbtext);
			wh.wbtime = (TextView) convertView.findViewById(R.id.wbtime);
			wh.wbuser = (TextView) convertView.findViewById(R.id.wbuser);
			wh.wbimage = (ImageView) convertView.findViewById(R.id.wbimage);
			WeiBoInfo wb = wbList.get(position);
			if (wb != null) {
				convertView.setTag(wb.getId());
				wh.wbuser.setText(wb.getUserName());
				wh.wbtime.setText(wb.getTime());
				wh.wbtext.setText(wb.getText(), TextView.BufferType.SPANNABLE);
				// textHighlight(wh.wbtext, new char[] { '#' }, new char[] {
				// '#' });
			//	textHighlight(wh.wbtext, new char[] { '@' }, new char[] {
				// ':',' ' });
				textHighlight2(wh.wbtext, "http://", " ");

				if (wb.getHaveImage()) {
				   wh.wbimage.setImageResource(R.drawable.images);
				}
				Drawable cachedImage = asyncImageLoader.loadDrawable(wb
						.getUserIcon(), wh.wbicon, new ImageCallback() {

					@Override
					public void imageLoaded(Drawable imageDrawable,
							ImageView imageView, String imageUrl) {
						imageView.setImageDrawable(imageDrawable);
					}

				});
				if (cachedImage == null) {
					// wh.wbicon.setImageResource(R.drawable.usericon);
				} else {
					wh.wbicon.setImageDrawable(cachedImage);
				}
			}

			return convertView;
		}

	}
}
