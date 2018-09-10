package com.example.moneycontrol.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneycontrol.MoneyControlDatabase;
import com.example.moneycontrol.R;
import com.example.moneycontrol.customClasses.CustomEditText;
import com.example.moneycontrol.customClasses.Utils;
import com.example.moneycontrol.models.CategoryModel;
import com.example.moneycontrol.models.IncomeExpensesModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView           mBackArrowImg;
    private CustomEditText      mDateEdt;
    private CustomEditText      mCategoryEdt;
    private CustomEditText      mSubCategoryEdt;
    private CustomEditText      mAmountEdt;
    private CustomEditText      mDescriptionEdt;
    private Button              mSaveBtn;
    private AppCompatCheckBox   mDebitCheckbox;
    private AppCompatCheckBox   mCreditCheckbox;

    private CategoryModel       mSelectedSubCategory;
    private CategoryModel       mSelectedCategoryModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        initUI();
        setListeners();
    }

    private void initUI(){

        mBackArrowImg   = findViewById(R.id.left_img);
        mDateEdt        = findViewById(R.id.date_edt);
        mCategoryEdt    = findViewById(R.id.category_edt);
        mSubCategoryEdt = findViewById(R.id.subcategory_edt);
        mAmountEdt      = findViewById(R.id.amount_edt);
        mDescriptionEdt = findViewById(R.id.description_edt);
        mSaveBtn        = findViewById(R.id.save_btn);
        mDebitCheckbox  = findViewById(R.id.debit_checkbox);
        mCreditCheckbox = findViewById(R.id.credit_checkbox);

        TextView titleTxt = findViewById(R.id.title_txt);
        titleTxt.setText(R.string.add_transaction);

        findViewById(R.id.right_img).setVisibility(View.GONE);

        setDate();
    }

    private void setDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", new Locale("bg"));
        String dateNow = simpleDateFormat.format(Calendar.getInstance().getTime());

        mDateEdt.setText(dateNow);
    }

    private void setListeners(){
        mBackArrowImg.setOnClickListener(this);
        mDateEdt.setOnClickListener(this);
        mCategoryEdt.setOnClickListener(this);
        mSubCategoryEdt.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);

        mAmountEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && !mAmountEdt.getText().toString().isEmpty())
                    mAmountEdt.setText(Utils.formatAmount(mAmountEdt.getText().toString()));
            }
        });

        mCreditCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    mDebitCheckbox.setChecked(false);
            }
        });

        mDebitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    mCreditCheckbox.setChecked(false);
            }
        });
    }

    private void showDatePicker(){

        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                String myFormat = "dd-MMM-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("bg"));

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                mDateEdt.setText(sdf.format(calendar.getTime()));
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        dialog.show();
    }

    private void startCategoryActivity(int requestCode) {
            Intent intent = new Intent(this, CategoriesActivity.class);
            if (requestCode == Utils.REQUEST_CODE_CHOOSE_SUBCATEGORY)
                intent.putExtra(Utils.INTENT_CATEGORY_ID, mSelectedCategoryModel.getId());
            startActivityForResult(intent, requestCode);
    }

    private void onSave(){
        if(mDateEdt.getText().toString().isEmpty()){
            mDateEdt.showError();
            return;
        }

        if(mCategoryEdt.getText().toString().isEmpty()){
            mCategoryEdt.showError();
            return;
        }

        if(mSubCategoryEdt.getText().toString().isEmpty()){
            mSubCategoryEdt.showError();
            return;
        }

        if(mAmountEdt.getText().toString().isEmpty()){
            mAmountEdt.showError();
            return;
        }

        if(mDescriptionEdt.getText().toString().isEmpty()){
            mDescriptionEdt.showError();
            return;
        }

        if(!mDebitCheckbox.isChecked() && !mCreditCheckbox.isChecked()){
            Toast.makeText(this, "Моля, изберете приход или разход!", Toast.LENGTH_SHORT).show();
            return;
        }

        CrudDatabase saveInDatabase = new CrudDatabase();
        saveInDatabase.execute();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == mBackArrowImg.getId())
            onBackPressed();
        else if(v.getId() == mDateEdt.getId())
            showDatePicker();
        else if(v.getId() == mCategoryEdt.getId())
            startCategoryActivity(Utils.REQUEST_CODE_CHOOSE_CATEGORY);
        else if(v.getId() == mSubCategoryEdt.getId()) {
            if (mSelectedCategoryModel != null)
                startCategoryActivity(Utils.REQUEST_CODE_CHOOSE_SUBCATEGORY);
        }
        else if(v.getId() == mSaveBtn.getId())
            onSave();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Utils.REQUEST_CODE_CHOOSE_CATEGORY && resultCode == RESULT_OK){
            mSelectedCategoryModel  = data.getParcelableExtra(Utils.INTENT_EXTRA_CATEGORY_NAME);

            mCategoryEdt.setText(mSelectedCategoryModel.getCategoryName());
        }
        else if (requestCode == Utils.REQUEST_CODE_CHOOSE_SUBCATEGORY && resultCode == RESULT_OK){
            mSelectedSubCategory    = data.getParcelableExtra(Utils.INTENT_EXTRA_CATEGORY_NAME);
            mSubCategoryEdt.setText(mSelectedSubCategory.getCategoryName());
        }
    }

    class CrudDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", new Locale("bg"));
            final MoneyControlDatabase mDatabase = MoneyControlDatabase.getDatabase(AddTransactionActivity.this);

            IncomeExpensesModel incomeExpensesModel = new IncomeExpensesModel();

            try {
                incomeExpensesModel.setDate(dateFormat.parse(mDateEdt.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            incomeExpensesModel.setCategoryId(mSelectedSubCategory.getId());
            incomeExpensesModel.setAmount(Double.parseDouble(mAmountEdt.getText().toString()));
            incomeExpensesModel.setDescription(mDescriptionEdt.getText().toString());
            incomeExpensesModel.setIsDebit(mDebitCheckbox.isChecked());

            mDatabase.incomeExpensesDao().insert(incomeExpensesModel);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(AddTransactionActivity.this, R.string.successful_transaction_added, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }
    }
}
