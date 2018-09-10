package com.example.moneycontrol.interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.moneycontrol.models.CategoryModel;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CategoryModel categoryModel);

    @Delete
    void delete(CategoryModel categoryModel);

    @Query("SELECT * FROM categories_table WHERE mCategoryId = :categoryId")
    List<CategoryModel> getAllCategories(long categoryId);

    @Query("SELECT * FROM categories_table WHERE mId = :id")
    List<CategoryModel> getCategory(long id);
}