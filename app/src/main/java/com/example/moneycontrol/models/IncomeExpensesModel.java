package com.example.moneycontrol.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

@Entity(tableName = "income_expenses_table", indices = {@Index("mCategoryId")}, foreignKeys = {
        @ForeignKey(
                entity = CategoryModel.class,
                parentColumns = "mId",
                childColumns = "mCategoryId"
        )
})

public class IncomeExpensesModel implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long    mId;

    private double  mAmount;
    private Date    mDate;
    private String  mDescription;
    private boolean mIsDebit;
    private long    mCategoryId;

    public IncomeExpensesModel(){}

    protected IncomeExpensesModel(Parcel in) {
        mId             = in.readLong();
        mAmount         = in.readDouble();
        mDate           = new Date(in.readLong());
        mDescription    = in.readString();
        mIsDebit        = in.readByte() != 0;
        mCategoryId     = in.readLong();
    }

    public static final Creator<IncomeExpensesModel> CREATOR = new Creator<IncomeExpensesModel>() {
        @Override
        public IncomeExpensesModel createFromParcel(Parcel in) {
            return new IncomeExpensesModel(in);
        }

        @Override
        public IncomeExpensesModel[] newArray(int size) {
            return new IncomeExpensesModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeDouble(mAmount);
        dest.writeLong(mDate.getTime());
        dest.writeString(mDescription);
        dest.writeByte((byte) (mIsDebit ? 1 : 0));
        dest.writeLong(mCategoryId);
    }
}
