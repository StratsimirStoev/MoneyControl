package com.example.moneycontrol.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.moneycontrol.MoneyControlDatabase;
import com.example.moneycontrol.R;
import com.example.moneycontrol.customClasses.CreateCategoryDialog;
import com.example.moneycontrol.customClasses.Utils;
import com.example.moneycontrol.interfaces.CategoryDao;
import com.example.moneycontrol.models.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView                   mBackArrowImg;
    private ListView                    mCategoriesListView;
    private FloatingActionButton        mAddBtn;
    private TextView                    mNoCategoriesTxt;

    private ArrayList<String>           mCategoriesTitlesArrayList  = new ArrayList<>();
    private ArrayList<CategoryModel>    mCategoriesArrayList        = new ArrayList<>();
    private ArrayAdapter<String>        mCategoriesAdapter;

    private boolean                     mIsSubcategory              = false;

    private long                        mCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        mCategoryId     = getIntent().getLongExtra(Utils.INTENT_CATEGORY_ID, -1);
        mIsSubcategory = getIntent().getBooleanExtra(Utils.INTENT_EXTRA_SUBCATEGORY, false);

        initUI();
        setListeners();
        setAdapter();
        selectData();
    }

    private void initUI(){
        mBackArrowImg           = findViewById(R.id.left_img);
        mCategoriesListView     = findViewById(R.id.categories_listview);
        mAddBtn                 = findViewById(R.id.add_btn);
        mNoCategoriesTxt        = findViewById(R.id.no_categories_txt);

        ((TextView) findViewById(R.id.title_txt)).setText(mCategoryId == 1 ? R.string.choose_category : R.string.select_subcategory);
    }

    private void setListeners(){
        mBackArrowImg.setOnClickListener(this);
        mCategoriesListView.setOnItemClickListener(this);
        mAddBtn.setOnClickListener(this);
    }

    private void selectData(){
        CrudDatabase crudDatabase = new CrudDatabase(true, "");
        crudDatabase.execute();
    }

    private void setAdapter(){
        mCategoriesAdapter = new ArrayAdapter<String>(this, R.layout.item_category, R.id.category_name_txt, mCategoriesTitlesArrayList);
        mCategoriesListView.setAdapter(mCategoriesAdapter);
    }

    private void showCreateCategoryDialog(){
        final CreateCategoryDialog dialog = new CreateCategoryDialog(this, new CreateCategoryDialog.ButtonsClickedListener() {
            @Override
            public void onSaveClicked(String categoryName) {
                CrudDatabase crudDatabase = new CrudDatabase(false, categoryName);
                crudDatabase.execute();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == mBackArrowImg.getId())
            onBackPressed();
        else if(view.getId() == mAddBtn.getId())
            showCreateCategoryDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra(Utils.INTENT_EXTRA_CATEGORY_NAME, mCategoriesArrayList.get(position));

        setResult(RESULT_OK, intent);
        finish();
    }

    class CrudDatabase extends AsyncTask<Void, Void, Void> {

        private boolean isSelectCategories       = true;
        private String  mCategoryName            = "";
        final MoneyControlDatabase mDatabase     = MoneyControlDatabase.getDatabase(CategoriesActivity.this);

        public CrudDatabase(boolean isSelectCategories, String mCategoryName) {
            this.isSelectCategories = isSelectCategories;
            this.mCategoryName      = mCategoryName;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if(isSelectCategories)
                selectCategories();
            else
                insertCategory(mCategoryName);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(isSelectCategories) {
                mNoCategoriesTxt.setVisibility(mCategoriesTitlesArrayList.isEmpty() ? View.VISIBLE : View.GONE);
                mCategoriesAdapter.notifyDataSetChanged();
            }
            else
                selectData();
        }

        private void selectCategories() {

            try {
                mCategoriesTitlesArrayList.clear();
                mCategoriesArrayList.clear();
                CategoryDao categoryDao = mDatabase.categoryDao();
                mCategoriesArrayList = (ArrayList<CategoryModel>) categoryDao.getAllCategories(mCategoryId);

                for (CategoryModel categoryModel : mCategoriesArrayList)
                    mCategoriesTitlesArrayList.add(categoryModel.getCategoryName());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void insertCategory(String categoryName) {
            try {
                CategoryDao categoryDao = mDatabase.categoryDao();
                categoryDao.insert(new CategoryModel(categoryName, mCategoryId));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
