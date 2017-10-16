package com.viscino.viscino.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.viscino.viscino.Home.HomeActivity;
import com.viscino.viscino.R;
import com.viscino.viscino.Utils.BottomNavigationViewHelper;

/**
 * Created by j on 09-10-2017.
 */

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUM = 2;

    private Context mContext = ProfileActivity.this;

    private FirebaseAuth mAuth;

    private RelativeLayout mLogin;
    private RelativeLayout mLogout;

    private Button login;
    private Button logout;
    private ImageButton settings;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started.");
        mAuth = FirebaseAuth.getInstance();

        setupBottomNavigationView();
        init();
    }

    private void init(){

        mLogin = (RelativeLayout) findViewById(R.id.loginLayout);
        mLogout = (RelativeLayout) findViewById(R.id.logoutLayout);
        settings = (ImageButton) findViewById(R.id.settings);
        login = (Button) findViewById(R.id.login);
        logout = (Button) findViewById(R.id.logout);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, LoginActivity.class));

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(mContext, "Logout Successful.",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(mContext, HomeActivity.class));
            }
        });
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mLogin.setVisibility(View.GONE);
            mLogout.setVisibility(View.VISIBLE);
        } else {
            mLogin.setVisibility(View.VISIBLE);
            mLogout.setVisibility(View.GONE);
        }


    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
