package com.app.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Expenses extends AppCompatActivity {

    ImageView back;
    RecyclerView ExpenseRV;
    SQLiteDatabase db;
    SharedPreferences sp;
    ArrayList<ExpenseModel> ExpenseList = new ArrayList<>();
    ExpenseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expenses);

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

        back = findViewById(R.id.btnMenu);
        ExpenseRV = findViewById(R.id.ExpenseRecyclerView);

        back.setOnClickListener(v->{
            startActivity(new Intent(Expenses.this,Dashboard.class));
            finish();
        });

        ExpenseRV.setLayoutManager(new LinearLayoutManager(this));
        getTransactions();

        adapter = new ExpenseAdapter(ExpenseList,"Expense");
        ExpenseRV.setAdapter(adapter);

    }

    private void getTransactions(){

        String query = "SELECT " +
                "transaction_table.transaction_id, " +
                "transaction_table.title, " +
                "transaction_table.amount, " +
                "transaction_table.transaction_date, " +
                "category_table.category_name, " +
                "category_table.category_icon, " +
                "payment_method_table.payment_name " +

                "FROM transaction_table " +

                "INNER JOIN category_table " +
                "ON transaction_table.category_id = category_table.category_id " +

                "INNER JOIN payment_method_table " +
                "ON transaction_table.payment_id = payment_method_table.payment_method_id " +

                "WHERE transaction_table.userid = ? AND transaction_table.transaction_type = 'Expense'  " +

                "ORDER BY transaction_table.transaction_id DESC";

        Cursor cursor = db.rawQuery(
                query,
                new String[]{String.valueOf(sp.getInt(ConstantSP.userid, 0))}
        );

        while(cursor.moveToNext()){

            String title = cursor.getString(1);
            double amount = cursor.getDouble(2);
            String date = cursor.getString(3);
            String categoryName = cursor.getString(4);
            int categoryIcon = cursor.getInt(5);
            String paymentName = cursor.getString(6);

            ExpenseList.add(new ExpenseModel(title, amount, date, categoryName, categoryIcon, paymentName));
        }

        cursor.close();
    }
}