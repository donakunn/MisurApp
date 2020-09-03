package com.example.misurapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

/**
 * This class defines layout elements and properties that are in common to all activities
 */
public class MisurAppBaseActivity extends AppCompatActivity {
    /**
     * debug tag
     */
    private final String TAG = "MisurAppBaseActivity";
    /**
     * Application shared preferences object.
     */
    protected SharedPreferences prefs;
    /**
     * shared preferences editor object.
     */
    protected SharedPreferences.Editor editor;
    /**
     * language code in use
     */
    protected String currentLangCode;

    /**
     * onCreate() method. initialize shared preferences and load current language.
     *
     * @param savedInstanceState activity saved instance bundle
     */
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate()");

        prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        editor = prefs.edit();

        currentLangCode = prefs.getString("Language", "it");
        loadLocale();

    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     *
     * @param menu Menu to be inflated
     * @return true if the operation is successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Inflating menu");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected. It perform language
     * change based on the selected item.
     *
     * @param item MenuItem object
     * @return boolean that describe operations result
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "changing language");
        int id = item.getItemId();

        if (id == R.id.action_cambio_lingua) {
            String[] listItems = new String[]{getResources().getString(R.string.lingua_inglese), getResources().getString(R.string.lingua_spagnola), getResources().getString(R.string.lingua_italiana)};
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            mBuilder.setSingleChoiceItems(listItems, -1, (dialog, which) -> {
                Intent intent = getIntent();

                switch (which) {
                    case 0:
                        changeLang("en");
                        currentLangCode = "en";
                        finish();
                        startActivity(intent);
                        break;
                    case 1:
                        changeLang("es");
                        currentLangCode = "es";
                        finish();
                        startActivity(intent);
                        break;
                    case 2:
                        changeLang("it");
                        currentLangCode = "it";
                        finish();
                        startActivity(intent);
                        break;
                }

            });
            mBuilder.setNeutralButton(getResources().getString(R.string.dialog_annulla), (dialog, which) -> {

            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * onResume() method. It perform inherited onResume operation plus it check if the language
     * was changed by a different activity and change it too if this happen.
     */
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        if (!currentLangCode.equals(getResources().getConfiguration().locale.getLanguage())) {
            currentLangCode = getResources().getConfiguration().locale.getLanguage();
            recreate();
        }
    }

    /**
     * load saved locale code.
     */
    public void loadLocale() {
        Log.d(TAG, "loading locale");
        String langPref = "Language";
        String language = prefs.getString(langPref, "");
        changeLang(Objects.requireNonNull(language));
    }

    /**
     * change anctivity language based on code
     *
     * @param lang String code for the language to be set
     */
    public void changeLang(String lang) {
        Log.d(TAG, "changing language");
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources()
                .getDisplayMetrics());

    }

    /**
     * save locale code in shared preference
     *
     * @param lang locale code to be stored.
     */
    public void saveLocale(String lang) {
        Log.d(TAG, "saving locale");
        String langPref = "Language";
        editor.putString(langPref, lang);
        editor.apply();
    }

    protected DriveServiceHelper getGDriveServiceHelper() {
        return new DriveServiceHelper(googleAccountCredentialMaker
                (Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(this))));
    }
    private Drive googleAccountCredentialMaker(GoogleSignInAccount account) {
        Log.d(TAG, "Signed in as " + account.getEmail());

        // Use the authenticated account to sign in to the Drive service.
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        this, Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(account.getAccount());
        return new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName("Drive API Migration")
                .build();
    }
}