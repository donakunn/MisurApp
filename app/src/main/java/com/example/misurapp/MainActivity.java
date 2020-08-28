package com.example.misurapp;

import android.Manifest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    SignInButton btnLogin;
    Button btnLogout;
    GoogleSignInOptions gso;
    GoogleSignInAccount account;
    String[] listItems;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        editor = prefs.edit();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button boyscoutButton = findViewById(R.id.Boyscout);
        Button scoutMasterButton = (Button) findViewById(R.id.Caposcout);

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
                    Intent intent = new Intent(MainActivity.this, DatabaseCaposcout.class);
                    startActivity(intent);
                } else {
                    toastMaker(getResources().getString(R.string.LoginRequest));
                }
            }
        });

        ActivityCompat.requestPermissions(MainActivity.this, //da spostare
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        ActivityCompat.requestPermissions(MainActivity.this, //da spostare
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

        account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if (account != null)
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
                saveLoginPropertiesInPreferences(account.getEmail(), true);
                setLogoutVisible();
            }
            else toastMaker(getResources().getString(R.string.loginNotSucceded));
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


    private void setAppLocale(String localCode) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            conf.setLocale(new Locale(localCode.toLowerCase()));
        } else {
            conf.locale = new Locale(localCode.toLowerCase());
        }
        res.updateConfiguration(conf, dm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Gestisci i clic sugli elementi della barra delle azioni qui.
        La barra delle azioni gestirà automaticamente i clic sul pulsante Home / Up button,
        a condizione che specifichi un'attività genitore in AndroidManifest.xml.*/
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cambio_lingua) {
            listItems = new String[]{getResources().getString(R.string.lingua_inglese), getResources().getString(R.string.lingua_spagnola), getResources().getString(R.string.lingua_italiana)};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = getIntent();

                    switch (which) {

                        case 0:
                            setAppLocale("en");
                            finish();
                            startActivity(intent);
                            break;

                        case 1:
                            setAppLocale("es");
                            finish();
                            startActivity(intent);
                            break;

                        case 2:
                            setAppLocale("it");
                            finish();
                            startActivity(intent);
                            break;
                    }

                }
            });
            mBuilder.setNeutralButton(getResources().getString(R.string.dialog_annulla), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
            return true;
        }

        if (id == R.id.action_backup) {
            Intent intent = new Intent(MainActivity.this,ListaStrumentiActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
        if (prefs.getBoolean("flagMain", false)) {
            editor.putBoolean("flagMain", false);
            editor.apply();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
}