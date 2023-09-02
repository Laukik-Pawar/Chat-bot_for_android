package com.example.chatppt;


import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

	private TextView responseTV;
	private TextView questionTV;
	private TextInputEditText queryEdt;

	private String url = "https://api.openai.com/v1/completions";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		responseTV = findViewById(R.id.idTVResponse);
		questionTV = findViewById(R.id.idTVQuestion);
		queryEdt = findViewById(R.id.idEdtQuery);

		queryEdt.setOnEditorActionListener((v, actionId, event) -> {
			if (actionId == EditorInfo.IME_ACTION_SEND) {
				responseTV.setText("Please wait..");

				String query = queryEdt.getText().toString();
				if (query.length() > 0) {
					getResponse(query);
				} else {
					Toast.makeText(this, "Please enter your query..", Toast.LENGTH_SHORT).show();
				}
				return true;
			}
			return false;
		});
	}

	private void getResponse(String query) {
		questionTV.setText(query);
		queryEdt.setText("");

		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("model", "text-davinci-003");
			jsonObject.put("prompt", query);
			jsonObject.put("temperature", 0);
			jsonObject.put("max_tokens", 100);
			jsonObject.put("top_p", 1);
			jsonObject.put("frequency_penalty", 0.0);
			jsonObject.put("presence_penalty", 0.0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							String responseMsg = response.getJSONArray("choices").getJSONObject(0).getString("text");
							responseTV.setText(responseMsg);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("TAGAPI", "Error is : " + error.getMessage() + "\n" + error);
					}
				}) {
			@Override
			public java.util.Map<String, String> getHeaders() {
				java.util.Map<String, String> params = new java.util.HashMap<>();
				params.put("Content-Type", "application/json");
				params.put("Authorization", "Bearer Enter-Your-OpenAi-Api_key");
				return params;
			}
		};

		postRequest.setRetryPolicy(new RetryPolicy() {
			@Override
			public int getCurrentTimeout() {
				return 50000;
			}

			@Override
			public int getCurrentRetryCount() {
				return 50000;
			}

			@Override
			public void retry(VolleyError error) throws VolleyError {
			}
		});

		queue.add(postRequest);
	}
}
