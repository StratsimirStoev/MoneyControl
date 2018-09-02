package com.example.moneycontrol.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "income_expenses_table", indices = {@Index("mCategoryId")}, foreignKeys = {
        @ForeignKey(
                entity = CategoryModel.class,
                parentColumns = "mId",
                childColumns = "mCategoryId"
        )
})

public class IncomeExpensesModel {

    @PrimaryKey(autoGenerate = true)
    private long    mId;

    private double  mAmount;
    private Date    mDate;
    private String  mDescription;
    private boolean mIsDebit;
    private long    mCategoryId;

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public double getAmount() {
        return mAmount;
    }

    public void setAmount(double mAmount) {
        this.mAmount = mAmount;
    }

    public boolean getIsDebit() {
        return mIsDebit;
    }

    public void setIsDebit(boolean mIsDebit) {
        this.mIsDebit = mIsDebit;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long mCategoryId) {
        this.mCategoryId = mCategoryId;
    }
}
