package com.example.misurapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.misurapp.db.DbManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class MainActivity extends MisurAppBaseActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton btnLogin;
    private Button btnLogout;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button boyscoutButton = findViewById(R.id.Boyscout);
        Button scoutMasterButton = findViewById(R.id.Caposcout);

        boyscoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (prefs.getBoolean("hasLogin", false)) {
                    v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_click));
                    Intent intent = new Intent(MainActivity.this, ListaStrumentiActivity.class);
                    startActivity(intent);

                } else {
                    toastMaker(getResources().getString(R.string.LoginRequest));
                }

            }
        });

        scoutMasterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (prefs.getBoolean("hasLogin", false)) {
                    v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_click));
                    Intent intent = new Intent(MainActivity.this, ScoutMasterDatabaseActivity.class);
                    startActivity(intent);
                } else {
                    toastMaker(getResources().getString(R.string.LoginRequest));
                }
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

        account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if (prefs.getBoolean("hasLogin", false))
            btnLogin.setVisibility(View.GONE);
        else
            btnLogout.setVisibility(View.GONE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }//fine onCreate();

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            if (account != null) {
                toastMaker(getResources().getString(R.string.LoginComplete));

                if (!Objects.equals(account.getEmail(), prefs.getString("email", ""))) {
                    new DbManager(this).dropTables();
                }
                saveLoginPropertiesInPreferences(account.getEmail(), true);
                setLogoutVisible();
            } else toastMaker(getResources().getString(R.string.loginNotSucceded));
        }
    }

    private void saveLoginPropertiesInPreferences(String email, boolean loginState) {
        editor.putString("email", email);
        editor.putBoolean("hasLogin", loginState);
        editor.apply();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
        } catch (ApiException e) {
            Log.w("Google Error ", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        revokeAccess();
                    }
                });
        saveLoginPropertiesInPreferences(null, false);
        toastMaker(getResources().getString(R.string.LogoutComplete));
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        setLoginVisible();
                    }
                });
    }

    private void toastMaker(String textToShow) {
        Toast toast = Toast.makeText(getApplicationContext(), textToShow, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.show();
    }

    private void setLoginVisible() {
        btnLogin.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.GONE);
    }

    private void setLogoutVisible() {
        btnLogin.setVisibility(View.GONE);
        btnLogout.setVisibility(View.VISIBLE);
    }
}