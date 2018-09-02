package com.example.moneycontrol;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.moneycontrol.customClasses.Utils;
import com.example.moneycontrol.models.IncomeExpensesModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TransactionsAdapter extends ArrayAdapter<IncomeExpensesModel> {

    private Context                         mContext;
    private ArrayList<IncomeExpensesModel>  mTransactionsArrayList  = new ArrayList<>();

    private double                          mDebit      = 0d;
    private double                          mCredit     = 0d;
    private double                          mTotal      = 0d;

    public TransactionsAdapter(@NonNull Context context, @NonNull ArrayList<IncomeExpensesModel> objects) {
        super(context, R.layout.item_transaction, objects);

        mContext                = context;
        mTransactionsArrayList  = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null){

            if(position != 0)
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_transaction, null, false);
            else
                convertView = LayoutInflater.from(mContext).inflate(R.layout.transactions_header, null, false);

            holder      = new ViewHolder(convertView, position);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();

        if(position == 0){
            holder.mDebitTxt.setText(Utils.formatAmount(String.valueOf(mDebit))     + " лв.");
            holder.mCreditTxt.setText(Utils.formatAmount(String.valueOf(mCredit))   + " лв.");
            holder.mTotalTxt.setText(Utils.formatAmount(String.valueOf(mTotal))     + " лв.");

            Calendar now = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", new Locale("bg"));
            holder.mMonthTxt.setText(simpleDateFormat.format(now.getTime()) + ":");

            holder.mTotalTxt.setTextColor(ContextCompat.getColor(mContext, mTotal < 0 ? android.R.color.holo_red_dark : android.R.color.holo_green_dark));
        }
        else {
            IncomeExpensesModel incomeExpensesModel = mTransactionsArrayList.get(position - 1);

            SimpleDateFormat secondFormat = new SimpleDateFormat("dd.MM.yyyy", new Locale("bg"));

            Calendar now = Calendar.getInstance();
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(incomeExpensesModel.getDate());

            if (now.get(Calendar.DATE) == dateCalendar.get(Calendar.DATE))
                holder.mDateTxt.setText(R.string.today);
            else if (now.get(Calendar.DATE) - dateCalendar.get(Calendar.DATE) == 1)
                holder.mDateTxt.setText(R.string.yesterday);
            else
                holder.mDateTxt.setText(secondFormat.format(incomeExpensesModel.getDate()));

            if (position != 1 && mTransactionsArrayList.get(position - 2).getDate().compareTo(incomeExpensesModel.getDate()) == 0)
                holder.mDateTxt.setVisibility(View.GONE);
            else
                holder.mDateTxt.setVisibility(View.VISIBLE);

            String amount = (incomeExpensesModel.getIsDebit() ? "-" : "+") + Utils.formatAmount(String.valueOf(incomeExpensesModel.getAmount())) + " лв.";
            holder.mAmountTxt.setText(amount);

            holder.mAmountTxt.setTextColor(ContextCompat.getColor(mContext, incomeExpensesModel.getIsDebit() ? android.R.color.holo_red_dark : android.R.color.holo_green_dark));

            holder.mDescrTxt.setText(incomeExpensesModel.getDescription());
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mTransactionsArrayList.size() + 1;
    }

    public void setmDebit(double mDebit) {
        this.mDebit = mDebit;
    }

    public void setmCredit(double mCredit) {
        this.mCredit = mCredit;
    }

    public void setmTotal(double mTotal) {
        this.mTotal = mTotal;
    }

    class ViewHolder {

        TextView mDescrTxt;
        TextView mAmountTxt;
        TextView mDateTxt;
        TextView mTotalTxt;
        TextView mDebitTxt;
        TextView mCreditTxt;
        TextView mMonthTxt;

        private ViewHolder(View itemView, int position) {
            if (position != 0) {
                mDescrTxt   = itemView.findViewById(R.id.descr_txt);
                mAmountTxt  = itemView.findViewById(R.id.amount_txt);
                mDateTxt    = itemView.findViewById(R.id.date_txt);
            } else {
                mTotalTxt   = itemView.findViewById(R.id.total_txt);
                mCreditTxt  = itemView.findViewById(R.id.montly_credit_txt);
                mDebitTxt   = itemView.findViewById(R.id.montly_debit_txt);
                mMonthTxt   = itemView.findViewById(R.id.month_txt);
            }
        }
    }
}
