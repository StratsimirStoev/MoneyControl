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
import java.util.Date;
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
    private IncomeExpensesModel mTransactionModel;

    private boolean             mIsForEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        initUI();
        setListeners();

        if(getIntent() != null && getIntent().hasExtra(Utils.INTENT_TRANSACTION)) {
            mIsForEditing       = true;
            mTransactionModel   = getIntent().getParcelableExtra(Utils.INTENT_TRANSACTION);

            mAmountEdt.setText(Utils.formatAmount(String.valueOf(mTransactionModel.getAmount())));
            mDescriptionEdt.setText(mTransactionModel.getDescription());

            Date date = mTransactionModel.getDate();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", new Locale("bg"));

            mDateEdt.setText(simpleDateFormat.format(date));

            if(mTransactionModel.getIsDebit())
                mDebitCheckbox.setChecked(true);
            else
                mCreditCheckbox.setChecked(false);

            selectSubCategory(mTransactionModel.getCategoryId());
        }
        else
            setDate();
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

    }

    private void selectCategory(long categoryId){
        CrudDatabase crudDatabase = new CrudDatabase();
        crudDatabase.setmCategoryId(categoryId);
        crudDatabase.setSelectCategory(true);
        crudDatabase.execute();
    }

    private void selectSubCategory(long categoryId){
        CrudDatabase crudDatabase = new CrudDatabase();
        crudDatabase.setmCategoryId(categoryId);
        crudDatabase.setSelectSubCategory(true);
        crudDatabase.execute();
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
        saveInDatabase.setUpdateTransaction(mIsForEditing);
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

        private boolean selectCategory;
        private boolean selectSubCategory;
        private boolean updateTransaction;
        private long    mCategoryId;

        @Override
        protected Void doInBackground(Void... voids) {

            final MoneyControlDatabase database = MoneyControlDatabase.getDatabase(AddTransactionActivity.this);

            if(!selectCategory && !selectSubCategory) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", new Locale("bg"));

                IncomeExpensesModel incomeExpensesModel = new IncomeExpensesModel();

                if(updateTransaction)
                    incomeExpensesModel.setId(mTransactionModel.getId());

                try {
                    incomeExpensesModel.setDate(dateFormat.parse(mDateEdt.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                incomeExpensesModel.setCategoryId(mSelectedSubCategory.getId());
                incomeExpensesModel.setAmount(Double.parseDouble(mAmountEdt.getText().toString()));
                incomeExpensesModel.setDescription(mDescriptionEdt.getText().toString());
                incomeExpensesModel.setIsDebit(mDebitCheckbox.isChecked());

                if(!updateTransaction)
                    database.incomeExpensesDao().insert(incomeExpensesModel);
                else
                    database.incomeExpensesDao().update(incomeExpensesModel);
            }else if(selectCategory)
                mSelectedCategoryModel = database.categoryDao().getCategory(mCategoryId).get(0);
            else
                mSelectedSubCategory   = database.categoryDao().getCategory(mCategoryId).get(0);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if((!selectCategory && !selectSubCategory) || updateTransaction) {
                Toast.makeText(AddTransactionActivity.this, R.string.successful_transaction_added, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
            else if(selectCategory){
                mCategoryEdt.setText(mSelectedCategoryModel.getCategoryName());
            }
            else {
                mSubCategoryEdt.setText(mSelectedSubCategory.getCategoryName());
                selectCategory(mSelectedSubCategory.getCategoryId());
            }

        }

        public void setSelectCategory(boolean selectCategory) {
            this.selectCategory = selectCategory;
        }

        public void setSelectSubCategory(boolean selectSubCategory) {
            this.selectSubCategory = selectSubCategory;
        }

        public void setmCategoryId(long mCategoryId) {
            this.mCategoryId = mCategoryId;
        }

        public void setUpdateTransaction(boolean updateTransaction) {
            this.updateTransaction = updateTransaction;
        }
    }
}
