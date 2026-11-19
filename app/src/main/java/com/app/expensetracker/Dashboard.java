package com.app.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    SharedPreferences sp;
    SQLiteDatabase db;
    RecyclerView transactionRV;
    Button AddTransaction, income, expense, states;
    TextView totalIncome, totalExpense;
    ImageView profile;
    ArrayList<TransactionModel> transactionList = new ArrayList<>();
    TransactionAdapter adapter;

    String[] category = {
            "Food", "Shopping", "Transport", "Entertainment", "Healthcare", "Education",
            "Travel", "Groceries", "Rent", "Bills", "Personal Care", "Salary",
            "Business", "Freelance", "Investment", "Interest", "Rental Income", "Gift", "Other"
    };
    int[] icons = {
            R.drawable.ic_food, R.drawable.ic_shopping, R.drawable.ic_transport,
            R.drawable.ic_entertainment, R.drawable.ic_healthcare, R.drawable.ic_education,
            R.drawable.ic_travel, R.drawable.ic_groceries, R.drawable.ic_rent,
            R.drawable.ic_bills, R.drawable.ic_personal_care, R.drawable.ic_salary,
            R.drawable.ic_business, R.drawable.ic_freelance, R.drawable.ic_investment,
            R.drawable.ic_interest, R.drawable.ic_rent, R.drawable.ic_gift, R.drawable.ic_category
    };
    String[] payment_name = {
            "Cash", "UPI", "Credit Card", "Debit Card",
            "Net Banking", "Bank Transfer", "Wallet", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        db = openOrCreateDatabase(ConstantSP.DataB, MODE_PRIVATE, null);
        sp = getSharedPreferences(ConstantSP.DataB, MODE_PRIVATE);

        String userTable = "CREATE TABLE IF NOT EXISTS user(userid INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(50), email VARCHAR(30), contact VARCHAR(10), password VARCHAR(20))";
        db.execSQL(userTable);

        String TransactionTable = "CREATE TABLE IF NOT EXISTS transaction_table(transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,userid INTEGER, category_id INTEGER, payment_id INTEGER, title VARCHAR(20), amount REAL, transaction_type VARCHAR(10), transaction_date TEXT DEFAULT CURRENT_DATE )";
        db.execSQL(TransactionTable);

        String CategoryTable = "CREATE TABLE IF NOT EXISTS category_table(category_id INTEGER PRIMARY KEY AUTOINCREMENT, category_name VARCHAR(50), category_icon INTEGER)";
        db.execSQL(CategoryTable);

        String PaymentMethodTable = "CREATE TABLE IF NOT EXISTS payment_method_table(payment_method_id INTEGER PRIMARY KEY AUTOINCREMENT, payment_name VARCHAR(20))";
        db.execSQL(PaymentMethodTable);

        transactionRV = findViewById(R.id.recyclerTransactions);
        income = findViewById(R.id.income);
        expense = findViewById(R.id.Expense);
        states = findViewById(R.id.States);
        totalExpense = findViewById(R.id.txtTotalExpense);
        totalIncome = findViewById(R.id.txtTotalIncome);
        profile = findViewById(R.id.btnProfile);
        AddTransaction = findViewById(R.id.AddTransaction);

        income.setOnClickListener(v->{
            startActivity(new Intent(Dashboard.this, Incomes.class));
        });

        expense.setOnClickListener(v->{
            startActivity(new Intent(Dashboard.this, Expenses.class));
        });

        states.setOnClickListener(v->{
            startActivity(new Intent(Dashboard.this, Statistics.class));
        });

        profile.setOnClickListener(v->{
            startActivity(new Intent(Dashboard.this, Profile.class));
        });

        AddTransaction.setOnClickListener(v->{
            startActivity(new Intent(Dashboard.this, AddTransaction.class));
        });

        for(int i=0; i< category.length;i++){
            String checkCategory = "SELECT * FROM category_table WHERE category_name = '"+category[i]+"' ";
            Cursor cursor = db.rawQuery(checkCategory,null);

            if(cursor.getCount()==0){
                String insertCategory = "INSERT INTO category_table VALUES(NULL, '"+category[i]+"', '"+icons[i]+"')";
                db.execSQL(insertCategory);
            }
        }

        for(int i=0; i< payment_name.length;i++){
            String checkPayment = "SELECT * FROM payment_method_table WHERE payment_name = '"+payment_name[i]+"' ";
            Cursor paycursor = db.rawQuery(checkPayment,null);

            if(paycursor.getCount()==0){
                String insertPayment = "INSERT INTO payment_method_table VALUES(NULL, '"+payment_name[i]+"')";
                db.execSQL(insertPayment);
            }
        }

        transactionRV.setLayoutManager(new LinearLayoutManager(this));
        getTransactions();
        getTotalIncomeExpense();

        adapter = new TransactionAdapter(transactionList);
        transactionRV.setAdapter(adapter);

    }

    private void getTransactions(){

        String query = "SELECT " +
                "transaction_table.transaction_id, " +
                "transaction_table.title, " +
                "transaction_table.amount, " +
                "transaction_table.transaction_type, " +
                "transaction_table.transaction_date, " +
                "category_table.category_name, " +
                "category_table.category_icon, " +
                "payment_method_table.payment_name " +

                "FROM transaction_table " +

                "INNER JOIN category_table " +
                "ON transaction_table.category_id = category_table.category_id " +

                "INNER JOIN payment_method_table " +
                "ON transaction_table.payment_id = payment_method_table.payment_method_id " +

                "WHERE transaction_table.userid = ? " +

                "ORDER BY transaction_table.transaction_id DESC";

        Cursor cursor = db.rawQuery(
                query,
                new String[]{String.valueOf(sp.getInt(ConstantSP.userid, 0))}
        );

        while(cursor.moveToNext()){

            String title = cursor.getString(1);
            double amount = cursor.getDouble(2);
            String type = cursor.getString(3);
            String date = cursor.getString(4);
            String categoryName = cursor.getString(5);
            int categoryIcon = cursor.getInt(6);
            String paymentName = cursor.getString(7);

            transactionList.add(new TransactionModel(title, amount, type, date, categoryName, categoryIcon, paymentName));
        }

        cursor.close();
    }

    private void getTotalIncomeExpense(){

        int userId = sp.getInt(ConstantSP.userid, 0);

        Cursor incomeCursor = db.rawQuery(
                "SELECT SUM(amount) FROM transaction_table WHERE userid = ? AND transaction_type = ?",
                new String[]{String.valueOf(userId), "Income"}
        );

        if(incomeCursor.moveToFirst()) {
            double incomeAmount = incomeCursor.getDouble(0);
            totalIncome.setText("₹ " + incomeAmount);
        }
        incomeCursor.close();

        Cursor expenseCursor = db.rawQuery(
                "SELECT SUM(amount) FROM transaction_table WHERE userid = ? AND transaction_type = ?",
                new String[]{String.valueOf(userId), "Expense"}
        );

        if(expenseCursor.moveToFirst()){
            double expenseAmount = expenseCursor.getDouble(0);
            totalExpense.setText("₹ " + expenseAmount);
        }

        expenseCursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        transactionList.clear();

        if(adapter != null){
            getTransactions();
            adapter.notifyDataSetChanged();
        }
        getTotalIncomeExpense();
    }
}