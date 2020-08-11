package com.example.misurapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    TextView lblInfo, lblHeader;
    SignInButton btnLogin;
    Button btnLogout;
    GoogleSignInOptions gso;
    GoogleSignInAccount account;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        editor = prefs.edit();

        Button client = findViewById(R.id.Boyscout);
        Button server = (Button) findViewById(R.id.Caposcout);


        client.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (prefs.getBoolean("hasLogin",false)) {
                    v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_click));
                    Intent intent = new Intent(MainActivity.this, ListaStrumentiActivity.class);
                    startActivity(intent);
                    //Intent intent = new Intent(MainActivity.this,ListaStrumentiActivity.class);
                    //startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.LoginRequest), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 50);
                    toast.show();
                }
            }
        });

        server.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (prefs.getBoolean("hasLogin", false)) {
                    v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_click));
                    Intent intent = new Intent(MainActivity.this, DatabaseCaposcout.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.LoginRequest), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 50);
                    toast.show();

                }
            }
        });

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        lblInfo = findViewById(R.id.lblInfo);
        lblHeader = findViewById(R.id.lblHeader);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

        account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if (account != null)
           updateUI(account);
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

    private void updateUI(GoogleSignInAccount account) {
      /*    try {

                + "\r\nEmail : " + account.getEmail() + "\r\nGiven name : " + account.getGivenName()
                    + "\r\nDisplay Name : " + account.getDisplayName() + "\r\nId : "
                    + account.getId();
//+ "\r\nImage URL : " + account.getPhotoUrl().toString();
//+ "\r\nAccount : " + account.getAccount().toString()
            lblInfo.setText(strData);

            lblHeader.setText("Sign In with Google Successful");
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);

        } catch (NullPointerException ex) {
            lblInfo.setText(lblInfo.getText().toString() + "\r\n" + "NullPointerException : " + ex.getMessage().toString());
        } catch (RuntimeException ex) {
            lblInfo.setText(lblInfo.getText().toString() + "\r\n" + "RuntimeException : " + ex.getMessage().toString());
        } catch (Exception ex) {
// lblInfo.setText(ex.getMessage().toString());
        } */
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
        //account.getEmail();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            editor.putString("email", account.getEmail());
            editor.putBoolean("hasLogin", true);
            editor.apply();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            updateUI(account);
        } catch (ApiException e) {
            Log.w("Google Error ", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
// ...
                        revokeAccess();
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
// ...
                  /*        lblInfo.setText("Please Login.");
                        lblHeader.setText("Android Login with Google");
                        btnLogin.setVisibility(View.VISIBLE);
                        btnLogout.setVisibility(View.GONE);
//imgProfilePic.setBackgroundResource(R.drawable.ic_lock); */
                    }
                });
    }
}