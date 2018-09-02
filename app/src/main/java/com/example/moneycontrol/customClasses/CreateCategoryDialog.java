package com.example.moneycontrol.customClasses;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moneycontrol.R;

public class CreateCategoryDialog extends Dialog{

    private ButtonsClickedListener  mListener;
    private Context                 mContext;

    public interface ButtonsClickedListener {
        void onSaveClicked(String categoryName);
    }

    public CreateCategoryDialog(@NonNull Context context, ButtonsClickedListener listener) {
        super(context);

        mContext    = context;
        mListener   = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_category);

        if(getWindow() != null)
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        initUI();
    }

    private void initUI(){
        Button   cancelBtn      = findViewById(R.id.cancel_btn);
        Button   saveBtn        = findViewById(R.id.save_btn);
        final EditText categoryName   = findViewById(R.id.category_edt);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categoryName.getText().toString().isEmpty())
                    Toast.makeText(mContext, R.string.please_type_name, Toast.LENGTH_SHORT).show();
                else if(mListener != null) {
                    dismiss();
                    mListener.onSaveClicked(categoryName.getText().toString());
                }
            }
        });
    }
}
