package com.example.moneycontrol.interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.moneycontrol.models.IncomeExpensesModel;

import java.util.Date;
import java.util.List;

@Dao
public interface IncomeExpensesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(IncomeExpensesModel incomeExpensesModel);

    @Delete
    void delete(IncomeExpensesModel incomeExpensesModel);

    @Query("SELECT * FROM income_expenses_table WHERE mDescription LIKE :search || '%'")
    List<IncomeExpensesModel> getAllIncomeExpenses(String search);

    @Query("SELECT SUM(mAmount) FROM income_expenses_table WHERE mIsDebit = :isDebit AND mDate BETWEEN :from AND :to")
    double getIncomeExpensesTotal(int isDebit, Date from, Date to);

    @Query("SELECT * FROM income_expenses_table WHERE mIsDebit = :isDebit AND mDate BETWEEN :from AND :to AND mAmount >= :fromAmount AND mAmount <= :toAmount")
    List<IncomeExpensesModel> filterTransactions(int isDebit, Date from, Date to, double fromAmount, double toAmount);

    @Query("SELECT * FROM income_expenses_table WHERE mDate BETWEEN :from AND :to AND mAmount >= :fromAmount AND mAmount <= :toAmount")
    List<IncomeExpensesModel> filterTransactionsWithoutType(Date from, Date to, double fromAmount, double toAmount);

    @Update
    void update(IncomeExpensesModel incomeExpensesModel);
}