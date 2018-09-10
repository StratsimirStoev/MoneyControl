package com.example.moneycontrol.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "categories_table")
public class CategoryModel implements Parcelable{

    @PrimaryKey(autoGenerate = true)
    private long    mId;

    private String  mCategoryName;
    private long    mCategoryId;

    public CategoryModel(String categoryName, long categoryId) {
        mCategoryName      = categoryName;
        mCategoryId        = categoryId;
    }

    protected CategoryModel(Parcel in) {
        mId             = in.readLong();
        mCategoryName   = in.readString();
        mCategoryId     = in.readLong();
    }

    public static final Creator<CategoryModel> CREATOR = new Creator<CategoryModel>() {
        @Override
        public CategoryModel createFromParcel(Parcel in) {
            return new CategoryModel(in);
        }

        @Override
        public CategoryModel[] newArray(int size) {
            return new CategoryModel[size];
        }
    };

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public void setCategoryName(String mCategoryName) {
        this.mCategoryName = mCategoryName;
    }

    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long mCategoryId) {
        this.mCategoryId = mCategoryId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mCategoryName);
        dest.writeLong(mCategoryId);
    }
}
