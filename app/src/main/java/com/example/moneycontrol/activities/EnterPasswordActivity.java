package com.example.moneycontrol.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.moneycontrol.R;
import com.example.moneycontrol.customClasses.ConfirmFingerprintDialog;
import com.example.moneycontrol.customClasses.CustomEditText;
import com.example.moneycontrol.customClasses.FingerprintHandler;
import com.example.moneycontrol.customClasses.KeystoreHandler;
import com.example.moneycontrol.customClasses.Utils;

public class EnterPasswordActivity extends AppCompatActivity implements View.OnClickListener, FingerprintHandler.OnAuthenticationErrorListener, FingerprintHandler.OnAuthenticationSucceededListener {

    public final static int             USE_FINGERPRINT             = 90;

    private Button                      mLoginBtn;
    private CustomEditText              mPasswordEdt;

    private KeystoreHandler             mKeystoreHandler;
    private SharedPreferences           mSharedPreferences;
    private FingerprintHandler          mFingerprintHandler;
    private ConfirmFingerprintDialog    mConfirmFingerprintDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(PreferenceManager.getDefaultSharedPreferences(this).getString(Utils.PREFERENCES_PASSWORD, "").isEmpty()){
            Intent intent = new Intent(this, ChoosePasscodeActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_enter_password);

        initUI();
        setListeners();
        setFingerPrintScanner();
    }

    private void initUI(){
        mPasswordEdt    = findViewById(R.id.enter_passcode_edt);
        mLoginBtn       = findViewById(R.id.login_btn);

        mKeystoreHandler    = new KeystoreHandler();
        mSharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void setListeners(){
        mLoginBtn.setOnClickListener(this);
    }

    private void onLoginClicked(){
        String password = PreferenceManager.getDefaultSharedPreferences(this).getString(Utils.PREFERENCES_PASSWORD, "");

        if(password.equals(mPasswordEdt.getText().toString()))
            startMainActivity();
        else
            Toast.makeText(this, R.string.wrong_password, Toast.LENGTH_SHORT).show();
    }

    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setUseFingerprintWhenLogIn(boolean useFingerprint){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Utils.PREFERENCES_USE_FINGERPRINT_WHEN_LOG_IN, useFingerprint);
        editor.apply();
    }

    private void setFingerPrintScanner(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_FINGERPRINT}, USE_FINGERPRINT);
                return;
            }
        }

        mFingerprintHandler = new FingerprintHandler(this);

        if(mSharedPreferences.getBoolean(Utils.PREFERENCES_USE_FINGERPRINT_WHEN_LOG_IN, true) &&
                mFingerprintHandler.isFingerScannerAvailableAndSet()) {

            mFingerprintHandler.setOnAuthenticationSucceededListener(this);
            mFingerprintHandler.setOnAuthenticationFailedListener(this);

            if (mFingerprintHandler.isFingerScannerAvailableAndSet()) {
                if (mSharedPreferences.getBoolean(Utils.PREFERENCES_USE_FINGERPRINT_WHEN_LOG_IN, true)) {
                    mKeystoreHandler = new KeystoreHandler();
                    mKeystoreHandler.prepareKey();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == mLoginBtn.getId())
            onLoginClicked();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void startFingerprintListening(){
        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(mKeystoreHandler.getCipher());
        mFingerprintHandler.startListening(cryptoObject);

        if(mConfirmFingerprintDialog == null) {
            mConfirmFingerprintDialog = new ConfirmFingerprintDialog(this);
            mConfirmFingerprintDialog.setConfirmFingerprintDialogListener(new ConfirmFingerprintDialog.ConfirmFingerprintDialogListener() {
                @Override
                public void onUseLoginCodePressed() {
                    mFingerprintHandler.stopListening();
                }

                @Override
                public void onBackPressed() {
                    mConfirmFingerprintDialog.dismiss();
                    finish();
                }
            });
            mConfirmFingerprintDialog.showDialog();
        }
    }

    @Override
    public void onAuthSucceeded() {
        mFingerprintHandler.stopListening();
        mConfirmFingerprintDialog.setFingerprintCorrect();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
                mConfirmFingerprintDialog.dismiss();
            }
        }, 200);
    }

    @Override
    public void onAuthFailed() {
        mConfirmFingerprintDialog.setFingerprintIncorrect();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mSharedPreferences.getBoolean(Utils.PREFERENCES_USE_FINGERPRINT_WHEN_LOG_IN, true)
            && mFingerprintHandler.isFingerScannerAvailableAndSet()) {
            try {
                if (mKeystoreHandler.initCipher(Utils.FINGERPRINT_KEY_NAME)) {
                    startFingerprintListening();
                }
            } catch (Exception e){
                try {
                    boolean canUseFingerprint = mKeystoreHandler.createFingerPrintKeyForOldClient();
                    setUseFingerprintWhenLogIn(canUseFingerprint);
                    onResume();
                } catch (Exception ex) {
                    e.printStackTrace();
                }
            }
        }
    }
}
