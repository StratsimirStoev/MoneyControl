package com.example.moneycontrol.activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.moneycontrol.MoneyControlDatabase;
import com.example.moneycontrol.R;
import com.example.moneycontrol.TransactionsAdapter;
import com.example.moneycontrol.customClasses.Utils;
import com.example.moneycontrol.interfaces.IncomeExpensesDao;
import com.example.moneycontrol.models.IncomeExpensesModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher,
        NavigationView.OnNavigationItemSelectedListener, View.OnFocusChangeListener, AdapterView.OnItemClickListener {

    private ImageView                      mDrawerImg;
    private ImageView                      mSearchImg;
    private ImageView                      mCloseImg;
    private ImageView                      mFilterImg;
    private FloatingActionButton           mAddFAB;
    private ListView                       mTransactionsListView;
    private LinearLayout                   mSearchLayout;
    private LinearLayout                   mTitleLayout;
    private EditText                       mSearchEdt;
    private DrawerLayout                   mDrawerLayout;
    private NavigationView                 mNavigationView;
    private LinearLayout                   mFilterLayout;
    private EditText                       mFromAmountEdt;
    private EditText                       mToAmountEdt;
    private EditText                       mFromDateEdt;
    private EditText                       mToDateEdt;
    private AppCompatCheckBox              mDebitCheckbox;
    private AppCompatCheckBox              mCreditCheckBox;
    private Button                         mClearBtn;
    private Button                         mApplyBtn;

    private Date                           mDateFrom;
    private Date                           mDateTo;

    private double                         mDebit;
    private double                         mCredit;
    private double                         mTotal;

    private TransactionsAdapter            mAdapter;
    private ArrayList<IncomeExpensesModel> mTransactionsArrayList   = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initUI();
        setListeners();
        setAdapter();
        selectTransactions();
    }

    private void initUI(){
        mDrawerImg              = findViewById(R.id.left_img);
        mSearchImg              = findViewById(R.id.right_img);
        mCloseImg               = findViewById(R.id.close_img);
        mAddFAB                 = findViewById(R.id.add_fab);
        mTransactionsListView   = findViewById(R.id.transactions_listview);
        mSearchLayout           = findViewById(R.id.search_layout);
        mTitleLayout            = findViewById(R.id.title_layout);
        mSearchEdt              = findViewById(R.id.search_edt);
        mDrawerLayout           = findViewById(R.id.drawer_layout);
        mNavigationView         = findViewById(R.id.nav_view);
        mFilterImg              = findViewById(R.id.filter_img);
        mFilterLayout           = findViewById(R.id.filter_layout);
        mFromAmountEdt          = findViewById(R.id.start_amount_edt);
        mToAmountEdt            = findViewById(R.id.end_amount_edt);
        mFromDateEdt            = findViewById(R.id.start_date_edt);
        mToDateEdt              = findViewById(R.id.end_date_edt);
        mApplyBtn               = findViewById(R.id.apply_btn);
        mClearBtn               = findViewById(R.id.reset_btn);
        mDebitCheckbox          = findViewById(R.id.debit_checkbox);
        mCreditCheckBox         = findViewById(R.id.credit_checkbox);

        TextView mTitleTxt  = findViewById(R.id.title_txt);

        mTitleTxt.setText(R.string.app_name);
        mFilterImg.setVisibility(View.VISIBLE);
        mDrawerImg.setImageResource(R.drawable.ic_menu);
        mSearchImg.setImageResource(R.drawable.ic_search);

        mFilterLayout.post(new Runnable() {
            @Override
            public void run() {
                mFilterLayout.setY(-mFilterLayout.getHeight());
            }
        });
    }

    private void setListeners(){
        mDrawerImg.setOnClickListener(this);
        mSearchImg.setOnClickListener(this);
        mCloseImg.setOnClickListener(this);
        mAddFAB.setOnClickListener(this);
        mSearchEdt.addTextChangedListener(this);
        mNavigationView.setNavigationItemSelectedListener(this);
        mTransactionsListView.setOnItemClickListener(this);
        mFilterImg.setOnClickListener(this);
        mFromAmountEdt.setOnFocusChangeListener(this);
        mToAmountEdt.setOnFocusChangeListener(this);
        mApplyBtn.setOnClickListener(this);
        mClearBtn.setOnClickListener(this);
        mFromDateEdt.setOnClickListener(this);
        mToDateEdt.setOnClickListener(this);

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(buttonView.getId() == mDebitCheckbox.getId())
                        mCreditCheckBox.setChecked(false);
                    else
                        mDebitCheckbox.setChecked(false);
                }

            }
        };

        mDebitCheckbox.setOnCheckedChangeListener(listener);
        mCreditCheckBox.setOnCheckedChangeListener(listener);
    }

    private void setAdapter(){
        mAdapter = new TransactionsAdapter(this, mTransactionsArrayList);
        mTransactionsListView.setAdapter(mAdapter);
    }

    private void selectTransactions(){
        DatabaseQuery query = new DatabaseQuery();
        query.execute();
    }

    private void showSearch(final boolean show){
        final AlphaAnimation showAnim = new AlphaAnimation(0f, 1f);
        showAnim.setDuration(500);
        showAnim.setInterpolator(new LinearInterpolator());

        AlphaAnimation hideAnim = new AlphaAnimation(1f, 0f);
        hideAnim.setDuration(500);
        hideAnim.setInterpolator(new LinearInterpolator());

        if(show)
            mTitleLayout.startAnimation(hideAnim);
        else
            mSearchLayout.startAnimation(hideAnim);

        hideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(show) {
                    mTitleLayout.setVisibility(View.GONE);
                    mSearchLayout.startAnimation(showAnim);
                }
                else {
                    mSearchLayout.setVisibility(View.GONE);
                    mTitleLayout.startAnimation(showAnim);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(show) {
                    mSearchLayout.setVisibility(View.VISIBLE);

                    mSearchEdt.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    }
                }
                else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(mSearchEdt.getWindowToken(),0);
                    }

                    mTitleLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void showFilterLayout(final boolean in){
        if(in)
            mAddFAB.hide();
        else
            mAddFAB.show();

        ObjectAnimator transAnimation= ObjectAnimator.ofFloat(mFilterLayout, "translationY", in ? -mFilterLayout.getHeight() : 0, in ? 0 : -mFilterLayout.getHeight());
        transAnimation.setDuration(300);//set duration
        transAnimation.start();//
    }

    private void filterTransactions(){
        String fromAmount   = mFromAmountEdt.getText().toString();
        String toAmount     = mToAmountEdt.getText().toString();

        double fromAmountD  = 0d;
        double toAmountD    = Double.MAX_VALUE ;

        if(!fromAmount.isEmpty())
            fromAmountD  = Double.parseDouble(fromAmount);

        if(!toAmount.isEmpty())
            toAmountD    = Double.parseDouble(toAmount);

        if(mDateFrom == null)
            mDateFrom = new Date(Long.MIN_VALUE);

        if(mDateTo == null)
            mDateTo = new Date(Long.MAX_VALUE);


        DatabaseQuery query = new DatabaseQuery();
        query.setFilterByType(mDebitCheckbox.isChecked() || mCreditCheckBox.isChecked());
        query.setFilterTransactions(true);
        query.setFromAmount(fromAmountD);
        query.setToAmount(toAmountD);
        query.execute();
    }

    private void showDatePicker(final EditText editText){

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

                if(editText.getId() == mFromDateEdt.getId())
                    mDateFrom   = calendar.getTime();
                else
                    mDateTo     = calendar.getTime();

                editText.setText(sdf.format(calendar.getTime()));
            }
        };

        new DatePickerDialog(this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode == Utils.REQUEST_CODE_ADD_TRANSACTION || requestCode == Utils.REQUEST_CODE_EDIT_TRANSACTION) && resultCode == RESULT_OK){
            selectTransactions();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == mSearchImg.getId()){
            showSearch(true);
        }
        else if(view.getId() == mCloseImg.getId()) {
            mSearchEdt.setText("");
            showSearch(false);
        }
        else if(view.getId() == mDrawerImg.getId()){
            if(!mDrawerLayout.isDrawerOpen(GravityCompat.START))
                mDrawerLayout.openDrawer(GravityCompat.START);
        }
        else if(view.getId() == mAddFAB.getId()){
            Intent intent = new Intent(this, AddTransactionActivity.class);
            startActivityForResult(intent, Utils.REQUEST_CODE_ADD_TRANSACTION);
        }
        else if(view.getId() == mFilterImg.getId()){
            if(mFilterLayout.getY() < 0 )
                showFilterLayout(true);
            else
                showFilterLayout(false);
        }
        else if(view.getId() == mFromDateEdt.getId())
            showDatePicker(mFromDateEdt);
        else if(view.getId() == mToDateEdt.getId())
            showDatePicker(mToDateEdt);
        else if(view.getId() == mClearBtn.getId()){
            mFromDateEdt.setText("");
            mToDateEdt.setText("");
            mFromAmountEdt.setText("");
            mToDateEdt.setText("");
            mToAmountEdt.setText("");
            showFilterLayout(false);

            mDateTo = null;
            mDateFrom = null;
            mToAmountEdt.setText("");
            mFromAmountEdt.setText("");
            mToDateEdt.setText("");
            mFromDateEdt.setText("");

            selectTransactions();
        }
        else if(view.getId() == mApplyBtn.getId()){
            showFilterLayout(false);
            filterTransactions();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        selectTransactions();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_settings:
                if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
                    mDrawerLayout.closeDrawer(GravityCompat.START);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                    }
                }, 200);

                break;
        }

        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus)
            ((EditText) v).setText(Utils.formatAmount(((EditText) v).getText().toString()));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
            Intent intent = new Intent(this, AddTransactionActivity.class);
            intent.putExtra(Utils.INTENT_TRANSACTION, mTransactionsArrayList.get(position - 1));
            startActivityForResult(intent, Utils.REQUEST_CODE_EDIT_TRANSACTION);
        }
    }

    private class DatabaseQuery extends AsyncTask<Void, Void, Void> {

        private boolean filterTransactions;
        private boolean filterByType;
        private double  fromAmount;
        private double  toAmount;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mTransactionsArrayList.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if(filterByType || filterTransactions)
                filterTransactions();
            else
                selectTransactions();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Collections.sort(mTransactionsArrayList, new Comparator<IncomeExpensesModel>() {
                @Override
                public int compare(IncomeExpensesModel o1, IncomeExpensesModel o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });

            Collections.reverse(mTransactionsArrayList);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        private void selectTransactions(){
            mTransactionsArrayList.clear();
            MoneyControlDatabase database = MoneyControlDatabase.getDatabase(MainActivity.this);
            mTransactionsArrayList.addAll(database.incomeExpensesDao().getAllIncomeExpenses(mSearchEdt.getText().toString()));

            Calendar start = Calendar.getInstance();
            Calendar end   = Calendar.getInstance();

            Date minDate = new Date(Long.MIN_VALUE);

            start.set(Calendar.DAY_OF_MONTH, 1);

            IncomeExpensesDao dao = database.incomeExpensesDao();

            mDebit  = dao.getIncomeExpensesTotal(1, start.getTime(), end.getTime());
            mCredit = dao.getIncomeExpensesTotal(0, start.getTime(), end.getTime());
            mTotal  = dao.getIncomeExpensesTotal(0, minDate, end.getTime()) - dao.getIncomeExpensesTotal(1, minDate, end.getTime());;

            mAdapter.setmDebit(mDebit);
            mAdapter.setmCredit(mCredit);
            mAdapter.setmTotal(mTotal);
        }

        private void filterTransactions(){
            mTransactionsArrayList.clear();
            MoneyControlDatabase database = MoneyControlDatabase.getDatabase(MainActivity.this);

            if(filterByType){
                mTransactionsArrayList.addAll(database.incomeExpensesDao().filterTransactions(mDebitCheckbox.isChecked() ? 1 : 0, mDateFrom, mDateTo, fromAmount, toAmount));
            }
            else
                mTransactionsArrayList.addAll(database.incomeExpensesDao().filterTransactionsWithoutType(mDateFrom, mDateTo, fromAmount, toAmount));
        }

        public void setFilterTransactions(boolean filterTransactions) {
            this.filterTransactions = filterTransactions;
        }

        public void setFilterByType(boolean filterByType) {
            this.filterByType = filterByType;
        }

        public void setFromAmount(double fromAmount) {
            this.fromAmount = fromAmount;
        }

        public void setToAmount(double toAmount) {
            this.toAmount = toAmount;
        }
    }
}