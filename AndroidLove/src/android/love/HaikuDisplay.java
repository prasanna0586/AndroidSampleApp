package android.love;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import org.apache.commons.codec.binary.Base64;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HaikuDisplay extends ActionBarActivity {
	
	ProgressDialog progressDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_haiku_display);
		refreshData();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.haiku_display, menu);
		return true;
	}
	
	public void onRefresh (View view) {
		refreshData();
	}
	
	private void refreshData() {
		String url = "http://10.0.2.2:8080/sample/sample";
		InputHandlerTask handler = new InputHandlerTask();
		handler.execute(url);
	}
	
	@Override
	protected void onDestroy() {
		dismissProgressDialog();
		super.onDestroy();
	}
	
	private void showProgressDialog() {
		if(progressDialog == null) {
			progressDialog = new ProgressDialog(HaikuDisplay.this);
			progressDialog.setMessage("Please wait while data is loading...");
			progressDialog.setTitle("Loading...");
		}
		progressDialog.show();
	}
	
	private void dismissProgressDialog() {
		if(progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
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
	
	private class InputHandlerTask extends AsyncTask <String, Void, JSONObject> {
			
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressDialog();
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			byte[] byteArrayAsString = android.util.Base64.decode((String)result.get("image"), android.util.Base64.DEFAULT);
			Bitmap bitMap = BitmapFactory.decodeByteArray(byteArrayAsString, 0, byteArrayAsString.length);
			TextView textView = (TextView) findViewById(R.id.textView3);
			textView.setText((String) result.get("description"));
			ImageView imageView = (ImageView) findViewById(R.id.imageView1);
			imageView.setImageBitmap(bitMap);
			dismissProgressDialog();
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			InputStream inputStream = null;
			BufferedReader reader = null;
			StringBuilder out = null;
			JSONObject jsonObject = null;
			JSONParser jsonParser = new JSONParser();
			try {
				inputStream = new URL(params[0]).openStream();
				reader = new BufferedReader(new InputStreamReader(inputStream));
				out = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					out.append(line);
				}
				jsonObject = (JSONObject) jsonParser.parse(out.toString());
				Log.i("JSON String from server", jsonObject.toString());

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return jsonObject;
			
		}
	}
}
