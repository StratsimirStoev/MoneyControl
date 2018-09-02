package com.example.moneycontrol.activities;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, NavigationView.OnNavigationItemSelectedListener{

    private ImageView                      mDrawerImg;
    private ImageView                      mSearchImg;
    private ImageView                      mCloseImg;
    private FloatingActionButton           mAddFAB;
    private ListView                       mTransactionsListView;
    private LinearLayout                   mSearchLayout;
    private LinearLayout                   mTitleLayout;
    private EditText                       mSearchEdt;
    private DrawerLayout                   mDrawerLayout;
    private NavigationView                 mNavigationView;

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

        TextView mTitleTxt  = findViewById(R.id.title_txt);

        mTitleTxt.setText(R.string.app_name);
        mDrawerImg.setImageResource(R.drawable.ic_menu);
        mSearchImg.setImageResource(R.drawable.ic_search);
    }

    private void setListeners(){
        mDrawerImg.setOnClickListener(this);
        mSearchImg.setOnClickListener(this);
        mCloseImg.setOnClickListener(this);
        mAddFAB.setOnClickListener(this);
        mSearchEdt.addTextChangedListener(this);
        mNavigationView.setNavigationItemSelectedListener(this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Utils.REQUEST_CODE_ADD_TRANSACTION && resultCode == RESULT_OK){
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

    private class DatabaseQuery extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mTransactionsArrayList.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {

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
    }
}