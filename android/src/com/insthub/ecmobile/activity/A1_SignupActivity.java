package com.insthub.ecmobile.activity;

//
//                       __
//                      /\ \   _
//    ____    ____   ___\ \ \_/ \           _____    ___     ___
//   / _  \  / __ \ / __ \ \    <     __   /\__  \  / __ \  / __ \
//  /\ \_\ \/\  __//\  __/\ \ \\ \   /\_\  \/_/  / /\ \_\ \/\ \_\ \
//  \ \____ \ \____\ \____\\ \_\\_\  \/_/   /\____\\ \____/\ \____/
//   \/____\ \/____/\/____/ \/_//_/         \/____/ \/___/  \/___/
//     /\____/
//     \/___/
//
//  Powered by BeeFramework
//
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;
import com.insthub.BeeFramework.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.external.activeandroid.util.ReflectionUtils;
import com.external.androidquery.callback.AjaxStatus;
import com.insthub.ecmobile.R;
import com.insthub.BeeFramework.model.BusinessResponse;
import com.insthub.BeeFramework.view.ToastView;
import com.insthub.ecmobile.model.ProtocolConst;
import com.insthub.ecmobile.model.RegisterModel;

public class A1_SignupActivity extends BaseActivity implements OnClickListener, BusinessResponse {
	
	private ImageView back;
	private Button register;
	
	private EditText userName;
	private EditText email;
	private EditText password1;
	private EditText password2;
	
	private LinearLayout body;
	
	private String name;
	private String mail;
	private String psd1;
	private String psd2;
	
	private RegisterModel registerModel;
	
	private ArrayList<String> items = new ArrayList<String>();
	
	public static Map<Integer, EditText> edit;  
	private JSONArray jsonArray = new JSONArray();
	
	private boolean flag = true;
	
	private ProgressDialog pd = null;

    Resources resource ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a1_signup);
		
		resource = (Resources) getBaseContext().getResources();
		
		back = (ImageView) findViewById(R.id.register_back);
		register = (Button) findViewById(R.id.register_register);
		userName = (EditText) findViewById(R.id.register_name);
		email = (EditText) findViewById(R.id.register_email);
		password1 = (EditText) findViewById(R.id.register_password1);
		password2 = (EditText) findViewById(R.id.register_password2);
		
		body = (LinearLayout) findViewById(R.id.register_body);
		
		back.setOnClickListener(this);
		register.setOnClickListener(this);
		
		registerModel = new RegisterModel(this);
		registerModel.addResponseListener(this);
		registerModel.signupFields();
		
		pd = new ProgressDialog(A1_SignupActivity.this);
        String holdon=resource.getString(R.string.hold_on);
		pd.setMessage(holdon);
		pd.show();
		
	}

	//动态添加输入框
	public void signupFields() {
		edit = new HashMap<Integer, EditText>();
		
		if(registerModel.signupfiledslist.size()>0) {
			body.setVisibility(View.VISIBLE);
			for(int i=0;i<registerModel.signupfiledslist.size();i++) {
				View view = LayoutInflater.from(this).inflate(R.layout.a1_register_item, null);
				EditText goods_name = (EditText) view.findViewById(R.id.register_item_edit);
                String nonull=resource.getString(R.string.not_null);
				
				if(registerModel.signupfiledslist.get(i).need.equals("1")) { //判断是否是必填
					goods_name.setHint(registerModel.signupfiledslist.get(i).name+nonull);
				} else {
					goods_name.setHint(registerModel.signupfiledslist.get(i).name);
				}
				
				View line = view.findViewById(R.id.register_item_line);
				if(i == registerModel.signupfiledslist.size()-1) {
					line.setVisibility(View.GONE);
				}
				edit.put(i, goods_name);
				body.addView(view);
			}
		} else {
			body.setVisibility(View.GONE);
		}				
		
	}

	@Override
	public void onClick(View v) {		
		switch(v.getId()) {
		case R.id.register_back:
			finish();
			break;
		case R.id.register_register:
			name = userName.getText().toString();
			mail = email.getText().toString();
			psd1 = password1.getText().toString();
			psd2 = password2.getText().toString();

            String user=resource.getString(R.string.user_name_cannot_be_empty);
			String email=resource.getString(R.string.email_cannot_be_empty);
            String pass=resource.getString(R.string.password_cannot_be_empty);
            String fault=resource.getString(R.string.fault);
            String passw=resource.getString(R.string.password_not_match);
            String req=resource.getString(R.string.required_cannot_be_empty);            

            if("".equals(name)) {				
				ToastView toast = new ToastView(this, user);
		        toast.setGravity(Gravity.CENTER, 0, 0);
		        toast.show();
			} else if("".equals(mail)) {				
				ToastView toast = new ToastView(this, email);
		        toast.setGravity(Gravity.CENTER, 0, 0);
		        toast.show();
			} else if("".equals(psd1)) {				
				ToastView toast = new ToastView(this, pass);
		        toast.setGravity(Gravity.CENTER, 0, 0);
		        toast.show();
			} else if(!ReflectionUtils.isEmail(mail)) {				
				ToastView toast = new ToastView(this, fault);
		        toast.setGravity(Gravity.CENTER, 0, 0);
		        toast.show();
			} else if(!psd1.equals(psd2)) {				
				ToastView toast = new ToastView(this, passw);
		        toast.setGravity(Gravity.CENTER, 0, 0);
		        toast.show();
			} else {
				StringBuffer sbf = new StringBuffer();
				for (int i = 0; i < registerModel.signupfiledslist.size(); ++i) {
					if(registerModel.signupfiledslist.get(i).need.equals("1") && edit.get(i).getText().toString().equals("")) {						
						ToastView toast = new ToastView(this, req);
				        toast.setGravity(Gravity.CENTER, 0, 0);
				        toast.show();
						flag = false;
						break;
					}
					items.add(edit.get(i).getText().toString());
					sbf.append(edit.get(i).getText().toString()+"/");
					
					JSONObject jsonItem = new JSONObject();
					try {
						jsonItem.put("id", registerModel.signupfiledslist.get(i).id);
						jsonItem.put("value",edit.get(i).getText().toString());
					} catch (JSONException e) {						
						e.printStackTrace();
					}
					jsonArray.put(jsonItem);
				}
				
				signup();
				
			}
			break;
		}
		
	}

	public void signup() {
		
		if(flag) {
			CloseKeyBoard(); //关闭键盘
			registerModel.signup(name, psd1, mail, jsonArray);
			pd = new ProgressDialog(A1_SignupActivity.this);
            Resources resource = (Resources) getBaseContext().getResources();
            String holdon=resource.getString(R.string.hold_on);
			pd.setMessage(holdon);
			pd.show();
		}
		
	}

	@Override
	public void OnMessageResponse(String url, JSONObject jo, AjaxStatus status)
			throws JSONException {		
		if(pd.isShowing()) {
			pd.dismiss();
		}
		
		if(registerModel.responseStatus.succeed == 1) {
			if(url.endsWith(ProtocolConst.SIGNUPFIELDS)) {
				signupFields();
			} else if(url.endsWith(ProtocolConst.SIGNUP)) {
				Intent intent = new Intent();
				intent.putExtra("login", true);
				setResult(Activity.RESULT_OK, intent);  
	            finish(); 
	            String wel=resource.getString(R.string.welcome);
	            ToastView toast = new ToastView(this,wel);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
			}
		}
		
	}
	
	// 关闭键盘
	public void CloseKeyBoard() {
		userName.clearFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(userName.getWindowToken(), 0);
	}
}
