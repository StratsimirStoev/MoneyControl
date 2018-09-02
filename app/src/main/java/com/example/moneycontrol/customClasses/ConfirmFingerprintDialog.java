package com.example.moneycontrol.customClasses;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneycontrol.R;

public class ConfirmFingerprintDialog extends Dialog implements View.OnClickListener{
    private Context         mContext;
    private View            mLayout;
    private ImageView       mFingerprintImage;
    private TextView        mInfoTextView;

	private Button          mUseLoginCodeBtn;
    private Button          mCancelBtn;

	private ConfirmFingerprintDialogListener mListener;

	public interface ConfirmFingerprintDialogListener{
		void onUseLoginCodePressed();
        void onBackPressed();
	}

    public ConfirmFingerprintDialog(Context context) {
        super(context);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void setConfirmFingerprintDialogListener(ConfirmFingerprintDialogListener listener){
	    mListener = listener;
    }

    public void showDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mLayout = layoutInflater.inflate(R.layout.confirm_fingerprint_dialog, null);
        this.setContentView(mLayout);
        this.setCancelable(false);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        mCancelBtn              = findViewById(R.id.cancel_btn);
	    mUseLoginCodeBtn        = findViewById(R.id.use_login_code_btn);
        mFingerprintImage       = findViewById(R.id.fingerprint_image_view);
        mInfoTextView           = findViewById(R.id.info_text_view);

	    mUseLoginCodeBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
	    try {
		    show();
	    }catch (Exception e){
		    e.printStackTrace();
	    }
    }

    public void setFingerprintIncorrect(){
        mFingerprintImage.setImageResource(R.drawable.fingerprint_image_wrong);
        mInfoTextView.setText(R.string.fingerprint_not_recognized);
        mInfoTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_red_light));
    }

    public void setFingerprintCorrect() {
	    mUseLoginCodeBtn.setOnClickListener(null);

	    mFingerprintImage.setImageResource(R.drawable.fingerprint_image_correct);
        mInfoTextView.setText(R.string.fingerprint_recognized);
        mInfoTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_green_light));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mUseLoginCodeBtn.getId() || v.getId() == mCancelBtn.getId()) {
            if(mListener != null)
                mListener.onUseLoginCodePressed();
            dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        mListener.onBackPressed();
    }
}