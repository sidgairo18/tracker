package team4.packagetrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Objects;



public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    /** Called when user taps submit button */
    public void submitRegistration(View view) {
        // need to ensure constraints on email ID, pw etc.

        EditText name = findViewById(R.id.nameField);
        EditText contact = findViewById(R.id.contactField);
        EditText email = findViewById(R.id.emailField);
        EditText username = findViewById(R.id.userField);
        EditText password = findViewById(R.id.passField);

        RadioButton seller = findViewById(R.id.sellerButton);
        RadioButton de = findViewById(R.id.deButton);

        String usertype;
        if(seller.isChecked()) {
            usertype = "seller";
        } else if(de.isChecked()) {
            usertype = "de";
        } else {
            usertype = "not set";
        }
/*
        Log.e("username", username.getText().toString());
        Log.e("password", password.getText().toString());
        Log.e("usertype", usertype);*/

        User newUser = new User(name.getText().toString(),
                                contact.getText().toString(), email.getText().toString(),
                                username.getText().toString(), password.getText().toString(),
                                usertype);

//        newUser.show();

        Gson gson = new Gson();
        String newUser_jsonString = gson.toJson(newUser);

        JSONObject newUser_json = null;
        try {
            newUser_json = new JSONObject(newUser_jsonString);
        }catch(org.json.JSONException e) {
            Log.e("JSON object creation", "failed");
        }

/*        try {
            Log.e("JSON string generated", newUser_json.toString());
        } catch(NullPointerException e){
            Log.e("JSON object creation", "failed");
        }*/

//        String URL= "http://10.0.2.2:8080/app/registration";

        SharedPreferences sharedPreferences= getSharedPreferences(MainActivity.HOST_Config, Context.MODE_PRIVATE);

        String hostIP= sharedPreferences.getString("HOST_IP",null);
        String hostPort= sharedPreferences.getString("HOST_PORT",null);

        String URL = "http://"+hostIP+":"+hostPort+"/app/registration";
        System.out.println(URL) ;
//        String URL= "http://10.3.0.147:8080/app/registration";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                newUser_json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject response_jsonObj;
                        String code = null, msg = null;
                        Log.e("Rest Response", response.toString());

                        try {
                            response_jsonObj = response.getJSONObject("Response");
                            code = response_jsonObj.getString("error_code");
                            msg = response_jsonObj.getString("message");
                        } catch(org.json.JSONException e) {
                            Log.e("json exception", "thrown");
                        }

                        if(Objects.equals(code, "000")) {
                            Toast.makeText(getApplicationContext(), msg,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "failed",
                                    Toast.LENGTH_LONG).show();
                        }

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest Error", error.toString());
                    }
                });

        requestQueue.add(objectRequest);
    }
}
