package com.example.randysinaweibo;

import java.util.List;

import com.randy.DataAccess.DataHelper;
import com.sina.image.AsyncImageLoader;
import com.sina.image.AsyncImageLoader.ImageCallback;
import com.sina.model.UserInfo;
import com.sina.model.WeiBoHolder;
import com.sina.model.WeiBoInfo;
import com.sina.util.AndroidHelper;
import com.sina.util.ConfigHelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LoginActivity extends Activity {
	Dialog dialog;
	DataHelper dbHelper;
	List<UserInfo> userList;
	private EditText iconSelect;
	private ImageView icon;
    private static String Select_Name="name";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		iconSelect = (EditText) findViewById(R.id.iconSelect);
		icon = (ImageView) findViewById(R.id.icon);
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
		// 背景自动适应
	    AndroidHelper.AutoBackground(this, layout, R.drawable.bg_w,	R.drawable.bg_h);
		
		ImageButton iconSelectBtn = (ImageButton) findViewById(R.id.iconSelectBtn);
		iconSelectBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				View diaView = View.inflate(LoginActivity.this,
						R.layout.dialog2, null);
				dialog = new Dialog(LoginActivity.this, R.style.dialog2);
				dialog.setContentView(diaView);
				dialog.show();

				UserAdapater adapater = new UserAdapater();
				ListView listview = (ListView) diaView.findViewById(R.id.list);
				listview.setVerticalScrollBarEnabled(false);// ListView去掉下拉条
				listview.setAdapter(adapater);
				listview.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int arg2, long arg3) {
						TextView tv = (TextView) view
								.findViewById(R.id.showName);
						iconSelect.setText(tv.getText());
						ImageView iv = (ImageView) view
								.findViewById(R.id.iconImg);
						icon.setImageDrawable(iv.getDrawable());
						dialog.dismiss();
					}

				});
			}

		});

		ImageButton login = (ImageButton) findViewById(R.id.login);
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GoHome();
			}

		});
		initUser();
	}

	// 进入用户首页
	private void GoHome() {
		if (userList != null) {
			String name = iconSelect.getText().toString();
			UserInfo u = GetUserByName(name);
			if (u != null) {
				ConfigHelper.nowUser = u;// 获取当前选择的用户并且保存
			}
		}
		if (ConfigHelper.nowUser != null) {
			// 进入用户首页
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, HomeActivity.class);
			startActivity(intent);
		}
	}
	@Override
    protected void onStop() {
        //获得SharedPreferences对象
        SharedPreferences MyPreferences = getSharedPreferences(Select_Name, Activity.MODE_PRIVATE);
        //获得SharedPreferences.Editor对象
        SharedPreferences.Editor editor = MyPreferences.edit();
        //保存组件中的值
        editor.putString("name", iconSelect.getText().toString());
        editor.commit();
        super.onStop();
    }
	public UserInfo GetUserByName(String name) {
		for (UserInfo u : userList) {
			if (u.getUsername().equals(name)) {
				return u;
			}
		}
		return null;
	}

	// 获取账号列表
	private void initUser() {
		// 获取账号列表
		dbHelper = new DataHelper(this);
		userList = dbHelper.GetUserList(false);
		if (userList.isEmpty()) {
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, AuthorizeActivity.class);
			startActivity(intent);
		} else {
			
			  SharedPreferences preferences = getSharedPreferences(Select_Name, Activity.MODE_PRIVATE); 
			  String str= preferences.getString("name", "");
			  UserInfo user=null; if(str!="") { 
				  user=GetUserByName(str);
			}
			  if(user==null) { user=userList.get(0); }
			 
			icon.setImageDrawable(user.getIcon());
			iconSelect.setText(user.getUsername());
		}
	}

	
	public class UserAdapater extends BaseAdapter {
		

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return userList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return userList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			convertView = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.item_user, null);

			ImageView iv = (ImageView) convertView.findViewById(R.id.iconImg);
			TextView tv = (TextView) convertView.findViewById(R.id.showName);
			UserInfo user = userList.get(position);
			try {
				// 设置图片显示
				iv.setImageDrawable(user.getIcon());
				// 设置信息
				tv.setText(user.getUsername());

			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;

		}

		

	}
}
