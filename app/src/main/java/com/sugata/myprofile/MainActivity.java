package com.sugata.myprofile;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;

public class MainActivity extends AppCompatActivity {

    static int flag=0;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);

        flag = 1;
        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = loginResult.getAccessToken();
                        Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();
                        get_profile(accessToken);
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException error) {
                    }
                });
    }

    public void login(View view)
    {
        //login activity
        flag = 2;

        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                //Do something if successful for example get token id...
                //now get user's details if success in next page

                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                intent.putExtra("value",LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString());
                //passing access token id to home page
                //and now launch new activity

                startActivity(intent);
            }

            @Override
            public void onAuthError(LIAuthError liAuthError) {
                //do something if error appears
            }
        },true);
    }

     //set the permisiion first to retrieve basic information of user's linkedin account

    private static Scope buildScope()
    {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this,requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void get_profile(AccessToken accessToken){
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                        try {
                            String userID = object.getString("id");
                            profileIntent.putExtra("id", userID);
                            String firstname = "";
                            String lastname = "";
                            String email = "";
                            //String birthday = "";
                            String gender = "";

                            if (object.has("first_name")) {
                                firstname = object.getString("first_name");
                                profileIntent.putExtra("name", firstname);
                            }
                            if (object.has("last_name")) {
                                lastname = object.getString("last_name");
                                profileIntent.putExtra("lastname", lastname);
                            }
                            if (object.has("email")){
                                email = object.getString("email");
                                profileIntent.putExtra("email", email);
                            }
                            /*if (object.has("birthday")) {
                                birthday = object.getString("birthday");
                                profileIntent.putExtra("birthday", birthday);
                            }*/
                            if (object.has("gender")) {
                                gender = object.getString("gender");
                                profileIntent.putExtra("gender", gender);
                            }
                            startActivity(profileIntent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, gender");
        request.setParameters(parameters);
        request.executeAsync();

    }
}
