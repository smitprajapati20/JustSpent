
package com.app.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    ArrayList<ExpenseModel> ExpenseList;
    String type;

    public ExpenseAdapter(ArrayList<ExpenseModel> expenseList, String type) {
        this.ExpenseList = expenseList;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ExpenseModel model = ExpenseList.get(position);

        holder.title.setText(model.title);
        holder.categoryName.setText(model.category);
        holder.Transactiontype.setText(model.payment);
        holder.date.setText(model.date);
        holder.icon.setImageResource(model.icon);

        String sign = type.equals("Expense") ? "- ₹" : "+ ₹";
        holder.amount.setText(sign + model.amount);
    }

    @Override
    public int getItemCount() {
        return ExpenseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title, categoryName, date, amount, Transactiontype;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.imgCategory);
            title = itemView.findViewById(R.id.txtTitle);
            categoryName = itemView.findViewById(R.id.txtCategoryName);
            Transactiontype = itemView.findViewById(R.id.txtTransactiontype);
            date = itemView.findViewById(R.id.txtTransactionDate);
            amount = itemView.findViewById(R.id.txtTransactionAmount);
        }
    }
}
