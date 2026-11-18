package com.app.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.app.DatePickerDialog;
import androidx.appcompat.app.AppCompatActivity;



public class AddTransaction extends AppCompatActivity {

    EditText edtTitle,edtAmount;
    Spinner spnType,spnCategory,spnPayment;
    TextView txtDate;
    final Calendar calendar = Calendar.getInstance();
    boolean dateSelected = false;
    SharedPreferences sp;
    SQLiteDatabase db;
    String[] category = {
            "Food", "Shopping", "Transport", "Entertainment", "Healthcare", "Education",
            "Travel", "Groceries", "Rent", "Bills", "Personal Care", "Salary",
            "Business", "Freelance", "Investment", "Interest", "Rental Income", "Gift", "Other"
    };
    String[] payment_name = {
            "Cash", "UPI", "Credit Card", "Debit Card",
            "Net Banking", "Bank Transfer", "Wallet", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

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

        edtTitle = findViewById(R.id.edtTitle);
        edtAmount = findViewById(R.id.edtAmount);
        spnType = findViewById(R.id.spnType);
        spnCategory = findViewById(R.id.spnCategory);
        spnPayment = findViewById(R.id.spnPayment);
        txtDate = findViewById(R.id.txtDate);

        ImageView btnBack = findViewById(R.id.btnBack);
        Button btnSave = findViewById(R.id.btnSave);

        btnBack.setOnClickListener(v -> finish());

        setupSpinner(spnType, Arrays.asList("Income", "Expense"));
        setupSpinner(spnCategory,Arrays.asList(category));
        setupSpinner(spnPayment, Arrays.asList(payment_name));

        txtDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void setupSpinner(Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void showDatePicker() {
        new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    calendar.set(year, month, day);
                    SimpleDateFormat format =
                            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    txtDate.setText(format.format(calendar.getTime()));
                    dateSelected = true;
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void saveTransaction() {
        String title = edtTitle.getText().toString().trim();
        String amountText = edtAmount.getText().toString().trim();

        if (title.isEmpty()) {
            edtTitle.setError("Enter a title");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            edtAmount.setError("Enter a valid amount");
            return;
        }
        if (amount <= 0) {
            edtAmount.setError("Enter a valid amount");
            return;
        }

        if (!dateSelected) {
            Toast.makeText(this, "Select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        String type = spnType.getSelectedItem().toString();
        String categoryName = spnCategory.getSelectedItem().toString();
        String paymentName = spnPayment.getSelectedItem().toString();

        String date = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(calendar.getTime());

        // Get Category ID
        Cursor catCursor = db.rawQuery( "SELECT category_id FROM category_table WHERE category_name = ?",
                                        new String[]{categoryName} );
        int category_id = 0;

        if(catCursor.moveToFirst()){
            category_id = catCursor.getInt(0);
        }
        catCursor.close();

        // Get Payment ID
        Cursor payCursor = db.rawQuery("SELECT payment_method_id FROM payment_method_table WHERE payment_name = ?",
                                        new String[]{paymentName} );
        int payment_id = 0;

        if(payCursor.moveToFirst()){
            payment_id = payCursor.getInt(0);
        }
        payCursor.close();

        // Get User ID
        int userid = sp.getInt(ConstantSP.userid, 0);

        // Insert Transaction
        db.execSQL("INSERT INTO transaction_table " +
                        "(userid, category_id, payment_id, title, amount, transaction_type, transaction_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                        new Object[]{ userid, category_id, payment_id, title, amount, type, date }
        );

        Toast.makeText(this, "Transaction Added!", Toast.LENGTH_SHORT).show();
        finish();
    }

}