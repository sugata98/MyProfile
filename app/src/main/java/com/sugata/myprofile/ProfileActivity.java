package com.sugata.myprofile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import static com.sugata.myprofile.MainActivity.flag;

public class ProfileActivity extends AppCompatActivity {

    private static final String host = "api.linkedin.com";
    private static final String url = "https://" +host+"/v1/people/~:" + "(headline,email-address,formatted-name,phone-numbers,picture-url)";

    private TextView username,useremail,liHeadline;
    private ProfilePictureView profilePicture;
    private ImageView liProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(flag==2) {
            //now get id from xml file

            username = (TextView) findViewById(R.id.name_surname);
            useremail = (TextView) findViewById(R.id.text_email);
            liHeadline = (TextView) findViewById(R.id.text_headline);
            liProfileImage = (ImageView)findViewById(R.id.linkedin_profile_img);
            liProfileImage.setVisibility(View.VISIBLE);

            //get data from intent
            Bundle bundle = getIntent().getExtras();
            String getokn = bundle.getString("value");

            //token.setText(getokn);

            linkedinHelperApi();
        }
        else {

            Intent intent = getIntent();
            String userID = intent.getStringExtra("id");
            String name = intent.getStringExtra("name");
            String surname = intent.getStringExtra("lastname");
            String email = intent.getStringExtra("email");
            //String birthday = intent.getStringExtra("birthday");
            String gender = intent.getStringExtra("gender");

            TextView nameView = (TextView) findViewById(R.id.name_surname);
            TextView emailView = (TextView) findViewById(R.id.text_email);
            //TextView birthdayView = (TextView) findViewById(R.id.text_birthday);
            TextView genderView = (TextView) findViewById(R.id.text_gender);

            nameView.setText(" " + name + " " + surname);
            emailView.setText(email);
            //birthdayView.setText(birthday);
            genderView.setText(gender);


            profilePicture = (ProfilePictureView) findViewById(R.id.profilePicture);
            profilePicture.setProfileId(userID);
            profilePicture.setVisibility(View.VISIBLE);

        }
        Button logout = (Button)findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

    }

    public void linkedinHelperApi()
    {
        APIHelper apiHelper =APIHelper.getInstance(getApplicationContext());

        apiHelper.getRequest(ProfileActivity.this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                //use try and catch block to handle exception

                try {
                    finalResult(apiResponse.getResponseDataAsJson());

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onApiError(LIApiError liApiError) {
                liApiError.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error: " + liApiError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Now Retrieving User's Details

    public void finalResult(JSONObject jsonObject)
    {
        try{
            //set data

            username.setText("Full Name:\n"+jsonObject.get("formattedName").toString());
            useremail.setText("Email Address:\n"+jsonObject.get("emailAddress").toString());
            liHeadline.setText("Headline:\n"+jsonObject.getString("headline"));
            Picasso.with(getApplicationContext()).load(jsonObject.getString("pictureUrl")).into(liProfileImage);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void logout() {
        LoginManager.getInstance().logOut();
        Intent login = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(login);
        finish();
    }

}
