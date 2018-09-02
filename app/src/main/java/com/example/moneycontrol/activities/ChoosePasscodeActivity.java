package com.example.moneycontrol.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.moneycontrol.R;
import com.example.moneycontrol.customClasses.CustomEditText;
import com.example.moneycontrol.customClasses.FingerprintHandler;
import com.example.moneycontrol.customClasses.KeystoreHandler;
import com.example.moneycontrol.customClasses.LinkFingerprintDialog;
import com.example.moneycontrol.customClasses.Utils;

public class ChoosePasscodeActivity extends AppCompatActivity implements View.OnClickListener {

    public final static int     USE_FINGERPRINT             = 90;

    private CustomEditText      mChoosePasswordEdt;
    private CustomEditText      mConfirmPasswordEdt;
    private Button              mSaveBtn;

    private FingerprintHandler  mFingerprintHandler;
    private KeystoreHandler     mKeystoreHandler;
    private SharedPreferences   mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_passcode);

        initUI();
        setListeners();
    }

    private void initUI(){
        mChoosePasswordEdt  = findViewById(R.id.enter_passcode_edt);
        mConfirmPasswordEdt = findViewById(R.id.confirm_passcode_edt);
        mSaveBtn            = findViewById(R.id.save_btn);

        mKeystoreHandler    = new KeystoreHandler();
        mFingerprintHandler = new FingerprintHandler(this);
        mSharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void setListeners(){
        mSaveBtn.setOnClickListener(this);
    }

    private boolean validate(){
        if(mChoosePasswordEdt.getText().toString().isEmpty()) {
            mChoosePasswordEdt.showError();
            return false;
        }

        if(mConfirmPasswordEdt.getText().toString().isEmpty()) {
            mConfirmPasswordEdt.showError();
            return false;
        }

        if(!mChoosePasswordEdt.getText().toString().equals(mConfirmPasswordEdt.getText().toString())) {
            Toast.makeText(this, R.string.passwords_are_not_equal, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void onSave(){
        SharedPreferences           sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor    editor            = sharedPreferences.edit();

        editor.putString(Utils.PREFERENCES_PASSWORD, mChoosePasswordEdt.getText().toString());
        editor.apply();

        boolean isFingerPrintSet = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Utils.PREFERENCES_USE_FINGERPRINT_WHEN_LOG_IN, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_FINGERPRINT}, USE_FINGERPRINT);
                return;
            }
        }
        if(mFingerprintHandler.isFingerScannerAvailableAndSet() && !isFingerPrintSet) {
            showEnableFingerprintDialog(false);
        }
        else {
            startMainActivity();
        }
    }

    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void showEnableFingerprintDialog(boolean isNewFingerprint){
        LinkFingerprintDialog linkFingerprintDialog = new LinkFingerprintDialog(this, isNewFingerprint);
        linkFingerprintDialog.setEnableFingerPrintListener(new LinkFingerprintDialog.EnableFingerprintListener() {
            @Override
            public void onError() {
                startMainActivity();
            }

            @Override
            public void onAthSuccess() {

                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean(Utils.PREFERENCES_USE_FINGERPRINT_WHEN_LOG_IN     , true);
                editor.apply();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startMainActivity();
                    }
                }, 300);
            }
        });
        linkFingerprintDialog.showDialog();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == mSaveBtn.getId()){
            if(validate())
                onSave();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == USE_FINGERPRINT && grantResults.length > 0){
            onSave();
        }
        else
            startMainActivity();
    }
}
