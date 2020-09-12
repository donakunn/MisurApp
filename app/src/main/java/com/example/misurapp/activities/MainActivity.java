package com.example.misurapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.misurapp.R;
import com.example.misurapp.db.DbManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;

import java.util.Objects;

/**
 * Main Activity of MisurApp.
 * It is responsible for showing buttons for the boyscout user type and the user type caposcout,
 * login and logout buttons;deals with launching the corresponding activities
 * and contains methods for managing the login
 */
public class MainActivity extends MisurAppBaseActivity {
    /**
     * Debug tag
     */
    private final String TAG = "MainActivity";
    /**
     * GoogleSignInClient object for interacting with the Google Sign In API.
     */
    private GoogleSignInClient mGoogleSignInClient;

    private static final int REQUEST_CODE_SIGN_IN = 1;

    /**
     * A user interface element the user can tap or click to perform login with Google account.
     */
    private SignInButton btnLogin;
    /**
     * A user interface element the user can tap or click to perform an action.
     */
    private Button btnLogout;
    /**
     * Object that holds the basic account information of the signed in Google user.
     */
    private GoogleSignInAccount account;

    /**
     * onCreate method of the activity
     * This method initializes the layout of the activity, button listeners,
     * and manages login operations
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button boyscoutButton = findViewById(R.id.Boyscout);
        Button scoutMasterButton = findViewById(R.id.Caposcout);

        boyscoutButton.setOnClickListener(v -> onScoutClickOperations
                (v, ListaStrumentiActivity.class));

        scoutMasterButton.setOnClickListener(v -> onScoutClickOperations
                (v, ScoutMasterDbActivity.class));

        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);

        googleAccountInit();

        buttonVisibilitySwitch();

        btnLogin.setOnClickListener(view -> signIn());
        btnLogout.setOnClickListener(view -> signOut());
    }

    /**
     * This method is responsible for checking whether the login has been done and if so
     * launch the next activity
     *
     * @param v            View to link the listener to
     * @param nextActivity next Activity class to be launched
     */
    private void onScoutClickOperations(View v, Class<?> nextActivity) {
        Log.d(TAG, "in Scout button listener");
        if (prefs.getBoolean("hasLogin", false)) {
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,
                    R.anim.button_click));
            Intent intent = new Intent(MainActivity.this,
                    nextActivity);
            startActivity(intent);
        } else {
            toastMaker(getResources().getString(R.string.LoginRequest));
        }
    }

    /**
     * Switch the visibility of login and logout buttons based on login status
     */
    private void buttonVisibilitySwitch() {
        Log.d(TAG, "in button visibility switch");
        if (prefs.getBoolean("hasLogin", false))
            setLogoutVisible();
        else
            setLoginVisible();
    }

    /**
     * Initialize google account info.
     */
    private void googleAccountInit() {
        Log.d(TAG, "google Account init");
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this,
                signInOptionsBuilder());

        account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);

    }

    /**
     * method that define options for the login request
     *
     * @return GoogleSignInOptions object, used to configure the GOOGLE_SIGN_IN_API.
     */
    private GoogleSignInOptions signInOptionsBuilder() {
        Log.d(TAG, "defining signIn options");
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();
    }

    /**
     * start signIn Activity to perform sign in.
     */
    private void signIn() {
        Log.d(TAG, "sign In");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            if (account != null) {
                if (!Objects.equals(account.getEmail(), prefs.getString("email", ""))) {
                    new DbManager(this).dropTables();
                    saveLoginPropertiesInPreferences(account.getEmail(),true);
                }
                setLogoutVisible();

                toastMaker(getResources().getString(R.string.LoginComplete));

            } else {
                saveLoginPropertiesInPreferences(null, false);
                toastMaker(getResources().getString(R.string.loginNotSucceded));
                setLoginVisible();
            }
        }
    }

    /**
     * Save login properties on Shared preferences.
     *
     * @param email      logged in user email.
     * @param loginState Boolean indicating whether the user was successfully logged in
     */
    private void saveLoginPropertiesInPreferences(String email, boolean loginState) {
        Log.d(TAG, "saving login properties");
        editor.putString("email", email);
        editor.putBoolean("hasLogin", loginState);
        editor.apply();
    }

    /**
     * This method handle Sign in result initializing GoogleSignInAccount with user info.
     *
     * @param completedTask result of the task GoogleSignInAccount
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
        } catch (ApiException e) {
            Log.w("Google Error ", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    /**
     * This method manage log out operations.
     */
    private void signOut() {
        Log.d(TAG, "log out");
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> revokeAccess());
        saveLoginPropertiesInPreferences(null, false);

        account = null;
        toastMaker(getResources().getString(R.string.LogoutComplete));
    }

    /**
     * Revokes access given to the current application.
     */
    private void revokeAccess() {
        Log.d(TAG, "revoke Access");
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, task -> setLoginVisible());
    }

    /**
     * Show Toast containing a message
     *
     * @param textToShow String to be printed into Toast object
     */
    private void toastMaker(String textToShow) {
        Log.d(TAG, "making Toast");
        Toast toast = Toast.makeText(getApplicationContext(), textToShow, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.show();
    }

    /**
     * set login button visible
     */
    private void setLoginVisible() {
        Log.d(TAG, "setting loginButton visible");
        btnLogin.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.GONE);
    }

    /**
     * set logout button visible
     */
    private void setLogoutVisible() {
        Log.d(TAG, "setting loginButton visible");
        btnLogin.setVisibility(View.GONE);
        btnLogout.setVisibility(View.VISIBLE);
    }
}