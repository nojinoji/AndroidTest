package com.example.androidsendreceivetest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

	private EditText id;
	private EditText pwd;
	private Button btnSend;
	private Button btnJoin;
	private TextView tvRecvData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		id = (EditText) findViewById(R.id.id);
		pwd = (EditText) findViewById(R.id.pwd);
		btnSend = (Button) findViewById(R.id.btn_sendData);
		btnJoin = (Button) findViewById(R.id.btn_join);
		tvRecvData = (TextView)	findViewById(R.id.tv_recvData);

		/*	Send 버튼을 눌렀을 때 서버에 데이터를 보내고 받는다	*/
		btnSend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String idMessage = id.getText().toString();  // 아이디를 받아옴
				String pwdMessage = pwd.getText().toString();  // 패스워드를 받아옴
				String result = SendByHttp(idMessage, pwdMessage); // 메시지를 서버에 보냄
				String[] parsedData = jsonParserList(result); // 받은 메시지를 json 파싱
				if(parsedData[1].equals("1")){
				tvRecvData.setText(parsedData[0]+"님이 로그인하셨습니다.");	// 받은 메시지를 화면에 보여주기
				}
				else if(parsedData[1].equals("0")){
					tvRecvData.setText("비밀번호가 틀렸습니다.");	// 받은 메시지를 화면에 보여주기
				}else{
					tvRecvData.setText("아이디가 없습니다.");
				}
			}
		});
	}

	/**
	 *서버에 데이터를 보내는 메소드
	 * @param
	 * @return
	 */
	private String SendByHttp(String idmsg, String pwdmsg) {
		if(idmsg == null)
			idmsg = "";
		if(pwdmsg == null)
			pwdmsg = "";

		String URL = "http://210.123.254.135:8080/testAndroid/LoginServer.jsp";

		DefaultHttpClient client = new DefaultHttpClient();
		try {
			/* 체크할 id와 pwd값 서버로 전송 */
			HttpPost post = new HttpPost(URL+"?idmsg="+idmsg+"&pwdmsg="+pwdmsg);

			/* 지연시간 최대 3초 */
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);

			/* 데이터 보낸 뒤 서버에서 데이터를 받아오는 과정 */

			HttpResponse response = client.execute(post);
			BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

			String line = null;
			String result = "";

			while ((line = bufreader.readLine()) != null) {
				result += line;
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			client.getConnectionManager().shutdown();	// ���� ���� ����
			return "";
		}

	}

	/**
	 * 받은 JSON 객체를 파싱하는 메소드
	 * @param
	 * @return
	 */
	private String[] jsonParserList(String pRecvServerPage) {

		Log.i("서버에서 받은 전체 내용 : ", pRecvServerPage);


		try {
			JSONObject json = new JSONObject(pRecvServerPage);
			JSONArray jArr = json.getJSONArray("List");


			// 받아온 pRecvServerPage를 분석하는 부분
			String[] jsonName = {"msg1", "msg2"};
			String[] parseredData = new String[jsonName.length];
			json = jArr.getJSONObject(0);
			for (int i = 0; i < jsonName.length; i++) {
					parseredData[i] = json.getString(jsonName[i]);
			}


			// 분해 된 데이터를 확인하기 위한 부분
			for(int i =0 ; i<parseredData.length;i++) {
				Log.i("JSON을 분석한 데이터  : ", parseredData[i]);
			}



			return parseredData;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

}
