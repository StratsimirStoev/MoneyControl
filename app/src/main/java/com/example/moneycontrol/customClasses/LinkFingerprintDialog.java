package com.example.moneycontrol.customClasses;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneycontrol.R;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class LinkFingerprintDialog extends Dialog implements View.OnClickListener, FingerprintHandler.OnAuthenticationErrorListener, FingerprintHandler.OnAuthenticationSucceededListener {

    private Context                         mContext;
    private View                            mLayout;
    private ImageView                       mFingerprintImage;
    private TextView                        mTitleTextView;
    private TextView                        mDescriptionTextView;
    private TextView                        mInfoTextView;

    private FingerprintHandler              mFingerprintHandler;
    private SharedPreferences               mSharedPreferences;
    private KeyStore                        mKeyStore;
    private KeyGenerator                    mKeyGenerator;
    private FingerprintManager.CryptoObject mCryptoObject;
    private Cipher mCipher;

    private Button mCancelBtn;

    private EnableFingerprintListener       mListener;
    private boolean                         mIsNewFingerprint;

    public interface EnableFingerprintListener{
        void onError();
        void onAthSuccess();
    }


    public LinkFingerprintDialog(@NonNull Context context, boolean isNewFingerprint) {
        super(context);
        mContext            = context;
        mIsNewFingerprint   = isNewFingerprint;

        mSharedPreferences  = PreferenceManager.getDefaultSharedPreferences(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void setEnableFingerPrintListener(EnableFingerprintListener listener){
        mListener = listener;
    }

    public void showDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mLayout = layoutInflater.inflate(R.layout.dialog_enable_fingerprint, null);
        this.setContentView(mLayout);
        this.setCancelable(false);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        mCancelBtn              = findViewById(R.id.cancel_btn);
        mTitleTextView          = findViewById(R.id.title_text_view);
        mDescriptionTextView    = findViewById(R.id.title_description_text);
        mFingerprintImage       = findViewById(R.id.fingerprint_image_view);
        mInfoTextView           = findViewById(R.id.info_text_view);

        mCancelBtn.setOnClickListener(this);

        if(mIsNewFingerprint){
            mTitleTextView.setText(R.string.device_settings_changed);
            mDescriptionTextView.setText(R.string.confirm_identity_again);
        }

        try{
            prepareKey();

            if (initCipher(mCipher, Utils.TEMP_FINGERPRINT_KEY_NAME))
                mCryptoObject = new FingerprintManager.CryptoObject(mCipher);
            mFingerprintHandler = new FingerprintHandler(mContext);
            mFingerprintHandler.startListening(mCryptoObject);
            mFingerprintHandler.setOnAuthenticationSucceededListener(this);
            mFingerprintHandler.setOnAuthenticationFailedListener(this);

            show();
        }catch (Exception e){
            e.printStackTrace();
            setUseFingerprintWhenLogIn(false);
            if(mListener != null)
                mListener.onError();
        }
    }

    public void setFingerprintIncorrect(){
        mFingerprintImage.setImageResource(R.drawable.fingerprint_image_wrong);
        mInfoTextView.setText(R.string.fingerprint_not_recognized);
        mInfoTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_red_light));
    }

    public void setFingerprintCorrect() {
        mCancelBtn.setOnClickListener(null);
        mCancelBtn.setVisibility(View.INVISIBLE);

        mFingerprintImage.setImageResource(R.drawable.fingerprint_image_correct);
        mInfoTextView.setText(R.string.fingerprint_recognized);
        mInfoTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_green_light));
    }

    private void prepareKey() throws RuntimeException{

        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
        try {
            mKeyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }
        try {
            mCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }

        try {
            mKeyStore.load(null);

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(Utils.TEMP_FINGERPRINT_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void storeFinalKey() throws RuntimeException{
        try {
            mKeyStore.load(null);

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(Utils.FINGERPRINT_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            mKeyGenerator.init(builder.build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean initCipher(Cipher cipher, String keyName) throws RuntimeException{
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onAuthFailed() {
        setFingerprintIncorrect();
    }

    @Override
    public void onAuthSucceeded() {
        mFingerprintHandler.stopListening();
        setFingerprintCorrect();
        storeFinalKey();
        setUseFingerprintWhenLogIn(true);
        dismiss();
        if(mListener != null)
            mListener.onAthSuccess();
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        try {
            if (mFingerprintHandler != null)
                mFingerprintHandler.stopListening();
        }catch (Exception e){
            e.printStackTrace();
        }

        super.setOnDismissListener(listener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_btn:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Utils.PREFERENCES_FINGERPRINT, Utils.FINGERPRINT_NOT_ALLOWED);
                editor.apply();

                setUseFingerprintWhenLogIn(false);

                if(mListener != null)
                    mListener.onError();

                dismiss();
                break;
        }
    }

    private void setUseFingerprintWhenLogIn(boolean useFingerprint){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Utils.PREFERENCES_USE_FINGERPRINT_WHEN_LOG_IN, useFingerprint);
        editor.putBoolean(Utils.PREFERENCES_IS_FINGERPRINT_SET, true);
        editor.apply();
    }
}
