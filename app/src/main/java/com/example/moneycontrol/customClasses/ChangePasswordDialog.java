package com.example.moneycontrol.customClasses;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.moneycontrol.R;

public class ChangePasswordDialog  extends Dialog implements View.OnClickListener {

    private Context         mContext;
    private View            mLayout;

    private CustomEditText  mOldPasswordEdt;
    private CustomEditText  mNewPasswordEdt;
    private CustomEditText  mConfirmPasswordEdt;
    private Button          mSaveBtn;
    private Button          mCancelBtn;


    public ChangePasswordDialog(@NonNull Context context) {
        super(context);

        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        initUI();
    }

    private void initUI(){
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mLayout = layoutInflater.inflate(R.layout.dialog_change_password, null);
        this.setContentView(mLayout);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        mOldPasswordEdt      = findViewById(R.id.enter_old_passcode_edt);
        mNewPasswordEdt      = findViewById(R.id.enter_new_passcode_edt);
        mConfirmPasswordEdt  = findViewById(R.id.enter_confirm_passcode_edt);
        mSaveBtn             = findViewById(R.id.save_btn);
        mCancelBtn           = findViewById(R.id.cancel_btn);

        mSaveBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
    }

    private boolean validate(){
        if(mOldPasswordEdt.getText().toString().isEmpty()){
            mOldPasswordEdt.showError();
            return false;
        }

        if(mNewPasswordEdt.getText().toString().isEmpty()){
            mNewPasswordEdt.showError();
            return false;
        }

        if(mConfirmPasswordEdt.getText().toString().isEmpty()){
            mConfirmPasswordEdt.showError();
            return false;
        }

        if(!mOldPasswordEdt.getText().toString().equals(PreferenceManager.getDefaultSharedPreferences(mContext).getString(Utils.PREFERENCES_PASSWORD, ""))){
            Toast.makeText(mContext, R.string.wrong_password, Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!mNewPasswordEdt.getText().toString().equals(mConfirmPasswordEdt.getText().toString())){
            Toast.makeText(mContext, R.string.passwords_are_not_equal, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == mCancelBtn.getId())
            dismiss();
        else if(v.getId() == mSaveBtn.getId()){
            if(validate()){

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Utils.PREFERENCES_PASSWORD, mNewPasswordEdt.getText().toString());
                editor.apply();

                Toast.makeText(mContext, R.string.password_is_changed, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }
    }
}
