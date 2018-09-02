package com.example.moneycontrol.customClasses;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;

import com.example.moneycontrol.R;

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {
    private Context             mContext;
    private TextInputLayout     mTextInputLayout;

    private int                 mInputTextLayoutResId;
    private String              mError;
    private String              mErrorValidation;

    private boolean             mErrorVisible;
    private boolean             mRequestFocusWhenShowError  = true;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){

        TypedArray attr         = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomEditText, 0, 0);

        mInputTextLayoutResId   = attr.getResourceId(R.styleable.CustomEditText_input_text_layout_id, 0);

        mError                  = attr.getString(R.styleable.CustomEditText_error_text);
        mErrorValidation        = attr.getString(R.styleable.CustomEditText_error_text_validation);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if(mInputTextLayoutResId != 0) {
            mTextInputLayout = getRootView().findViewById(mInputTextLayoutResId);
            mTextInputLayout.setErrorTextAppearance(R.style.error_appearance);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if(!getText().toString().isEmpty())
            hideError();
    }

    public void showError(){
        if(mTextInputLayout != null) {
            mErrorVisible = true;
            mTextInputLayout.setError(mError);
            if(mRequestFocusWhenShowError)
                requestFocus();
        }
    }

    public void hideError(){
        if(mTextInputLayout != null) {
            mErrorVisible = false;
            mTextInputLayout.setErrorEnabled(false);
        }
    }

    public void showErrorWithValidation(){
        if(mTextInputLayout != null){
            mErrorVisible = true;
            mTextInputLayout.setError(mErrorValidation);
        }
    }

    public TextInputLayout getmTextInputLayout() {
        return mTextInputLayout;
    }

    public void setmTextInputLayout(TextInputLayout mTextInputLayout) {
        this.mTextInputLayout = mTextInputLayout;
    }

    public String getmError() {
        return mError;
    }

    public void setmError(String mError) {
        this.mError = mError;
    }

    public String getmErrorValidation() {
        return mErrorValidation;
    }

    public void setmErrorValidation(String mErrorValidation) {
        this.mErrorValidation = mErrorValidation;
    }

    public boolean ismErrorVisible() {
        return mErrorVisible;
    }

    public void setmErrorVisible(boolean mErrorVisible) {
        this.mErrorVisible = mErrorVisible;
    }

    public boolean ismRequestFocusWhenShowError() {
        return mRequestFocusWhenShowError;
    }

    public void setmRequestFocusWhenShowError(boolean mRequestFocusWhenShowError) {
        this.mRequestFocusWhenShowError = mRequestFocusWhenShowError;
    }
}
