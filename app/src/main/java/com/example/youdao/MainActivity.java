package com.example.youdao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	EditText et;
	TextView tx;
	StringBuffer buffer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.button1).setOnClickListener(this);
		et = (EditText) findViewById(R.id.editText1);
		tx = (TextView) findViewById(R.id.textView1);
	}

	@Override
	public void onClick(View v) {
		if(et.getText().length() == 0){
			et.setError("编辑框不能为空");
		}else{
			tx.setText("");
			//建立进度条对话框、
			final ProgressDialog progressDialog = ProgressDialog.show(this, "正在搜索", "搜索网络中，请稍等...");
			new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... params) {
					try {
						// 创建超链接对象
						URL url = new URL(params[0]);
						// 建立连接。
						HttpURLConnection con = (HttpURLConnection) url
								.openConnection();

						InputStream is = con.getInputStream();
						InputStreamReader isr = new InputStreamReader(is, "utf-8");
						BufferedReader br = new BufferedReader(isr);
						String line = null;
						buffer = new StringBuffer();
						while ((line = br.readLine()) != null) {
							buffer.append(line);
							Log.d("Tag", buffer.toString());
						}
						return buffer.toString();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}

				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					// 在你获取的string这个JSON对象中，提取你所需要的信息。
					// {
					// "translation":["小时"],
					// "basic":{"us-phonetic":"'a??","phonetic":"'a??","uk-phonetic":"'a??","
					// explains":["n. 小时；钟头；课时；\u2026点钟","n. (Hour)人名；(法)乌尔；(柬)胡"]},
					// "query":"hour",
					// "errorCode":0,
					// "web":[
					// {"value":["小时","小时","钟头"],"key":"Hour"},
					// {"value":["欢乐时光","欢乐时光","快乐时光"],"key":"Happy hour"},
					// {"value":["人工工时","工时","人工小时"],"key":"labor hour"}
					// ]
					// }

					//结束对话框！！！
					progressDialog.dismiss();
					try {
						JSONObject obj = new JSONObject(result);
						// 取得翻译
						JSONArray array = obj.getJSONArray("translation");
						for (int i = 0; i < array.length(); i++) {
							tx.append("translation：" + array.get(i).toString()
									+ "\n");
						}
						tx.append("\n");
						JSONObject basicobj = obj.getJSONObject("basic");

						JSONArray array1 = basicobj.getJSONArray("explains");
						for (int i = 0; i < array1.length(); i++) {
							tx.append(array1.getString(i) + "\n");
						}
						tx.append("\n");

						String query = obj.getString("query");
						tx.append("query:" + query);
						tx.append("\n");

						//注意此项不能执行，一旦执行内容将截断！！!谨记---

//					String errorcode = obj.getString("errorcode");
//					tx.append("errorcode:" + errorcode);

						JSONArray array2 = obj.getJSONArray("web");
						for (int i = 0; i < array2.length(); i++) {
							JSONArray v = array2.getJSONObject(i).getJSONArray("value");
							String key= array2.getJSONObject(i).getString("key");
							tx.append("("+(i+1)+")"+key+"\n");
							for (int j = 0; j < v.length(); j++) {
								String ts=v.getString(j);

								tx.append(ts+"\n");
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					// tx.setText(result);
					// Log.d("Tag", tx.getText().toString());
				};

			}.execute("http://fanyi.youdao.com/openapi.do?keyfrom=dicFarsight&key=305582204&type=data&doctype=json&version=1.1&q="
					+ et.getText().toString());
		}


	}

}
