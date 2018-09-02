
package com.example.moneycontrol.activities;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.moneycontrol.R;
import com.example.moneycontrol.customClasses.ChangePasswordDialog;
import com.example.moneycontrol.customClasses.FingerprintHandler;
import com.example.moneycontrol.customClasses.KeystoreHandler;
import com.example.moneycontrol.customClasses.LinkFingerprintDialog;
import com.example.moneycontrol.customClasses.Utils;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    public final static int     USE_FINGERPRINT             = 90;

    private LinearLayout        mChangePasswordLayout;
    private SwitchCompat        mFingerPrintSwitchCompat;
    private ImageView           mBackArrowImg;

    private FingerprintHandler  mFingerprintHandler;
    private KeystoreHandler     mKeystoreHandler;
    private SharedPreferences   mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initUI();
        setListeners();
    }

    private void initUI(){
        mChangePasswordLayout       = findViewById(R.id.change_password_layout);
        mFingerPrintSwitchCompat    = findViewById(R.id.fingerprint_checkbox);
        mBackArrowImg               = findViewById(R.id.left_img);

        mKeystoreHandler            = new KeystoreHandler();
        mFingerprintHandler         = new FingerprintHandler(this);
        mSharedPreferences          = PreferenceManager.getDefaultSharedPreferences(this);

        findViewById(R.id.right_img).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.title_txt)).setText(R.string.settings);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mFingerPrintSwitchCompat.setChecked(sharedPreferences.getBoolean(Utils.PREFERENCES_USE_FINGERPRINT_WHEN_LOG_IN, false));

        if(!mFingerprintHandler.isFingerScannerAvailableAndSet())
            findViewById(R.id.fingerprint_layout).setVisibility(View.GONE);
    }

    private void setListeners(){
        mBackArrowImg.setOnClickListener(this);
        mChangePasswordLayout.setOnClickListener(this);
        mFingerPrintSwitchCompat.setOnClickListener(this);
    }

    private void saveFingerprintSettings(boolean turnOn){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor    editor = preferences.edit();
        editor.putBoolean(Utils.PREFERENCES_USE_FINGERPRINT_WHEN_LOG_IN, turnOn);
        editor.apply();
    }

    private void showEnableFingerprintDialog(boolean isNewFingerprint){
        final LinkFingerprintDialog linkFingerprintDialog = new LinkFingerprintDialog(this, isNewFingerprint);
        linkFingerprintDialog.setEnableFingerPrintListener(new LinkFingerprintDialog.EnableFingerprintListener() {
            @Override
            public void onError() {
            }

            @Override
            public void onAthSuccess() {

                saveFingerprintSettings(true);

                mFingerPrintSwitchCompat.setChecked(true);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        linkFingerprintDialog.dismiss();
                    }
                }, 300);
            }
        });
        linkFingerprintDialog.showDialog();
    }

    private void changePasswordDialog(){
        ChangePasswordDialog dialog = new ChangePasswordDialog(this);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == mBackArrowImg.getId())
            onBackPressed();
        else if(v.getId() == mFingerPrintSwitchCompat.getId()){

            if(mFingerPrintSwitchCompat.isChecked()){
                mFingerPrintSwitchCompat.setChecked(!mFingerPrintSwitchCompat.isChecked());
                showEnableFingerprintDialog(false);
            }
            else {
                saveFingerprintSettings(false);
            }
        }
        else if(v.getId() == mChangePasswordLayout.getId())
            changePasswordDialog();
    }
}
