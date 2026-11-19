package com.app.expensetracker;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class Profile extends AppCompatActivity {

    LinearLayout changePass, editProfile;
    ImageView back;
    TextView Initial, Name, Email, Income,Expense, Net;
    SharedPreferences sp;
    Button logout;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        db = openOrCreateDatabase(ConstantSP.DataB, MODE_PRIVATE, null);
        sp = getSharedPreferences(ConstantSP.DataB, MODE_PRIVATE);

        String userTable = "CREATE TABLE IF NOT EXISTS user(userid INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(50), email VARCHAR(30), contact VARCHAR(10), password VARCHAR(20))";
        db.execSQL(userTable);

        changePass = findViewById(R.id.rowChangePassword);
        editProfile = findViewById(R.id.rowEditProfile);
        Initial = findViewById(R.id.txtInitial);
        Name = findViewById(R.id.txtName);
        logout = findViewById(R.id.btnLogout);
        Email = findViewById(R.id.txtEmail);
        Income = findViewById(R.id.txtIncome);
        Expense = findViewById(R.id.txtExpense);
        Net = findViewById(R.id.txtSavings);
        back = findViewById(R.id.btnMenu);

        back.setOnClickListener(v->{
            startActivity(new Intent(Profile.this,Dashboard.class));
            finish();
        });
        getTotalIncomeExpense();

        Name.setText(sp.getString(ConstantSP.name,""));
        Email.setText(sp.getString(ConstantSP.email,""));

        String value = sp.getString(ConstantSP.name,"");
        if(value != null && !value.trim().isEmpty()){
            String FirstLetter = value.trim().substring(0,1).toUpperCase();
            Initial.setText(FirstLetter);
        }
        else {
            Initial.setText("");
        }

        changePass.setOnClickListener(v->{
            startActivity(new Intent(Profile.this,ChangePassword.class));
        });

        logout.setOnClickListener(v->{
            Snackbar.make(v,"Sure for logout",Snackbar.LENGTH_LONG)
                    .setAction("yes",view ->{
                        sp.edit().clear().apply();
                        startActivity(new Intent(Profile.this,MainActivity.class));
                        finish();
                    }).show();
        });

        editProfile.setOnClickListener(v->{
            startActivity(new Intent(Profile.this,EditProfile.class));
        });

    }
    private void getTotalIncomeExpense(){

        int userId = sp.getInt(ConstantSP.userid, 0);
        double incomeAmount = 0;
        double expenseAmount = 0;


        Cursor incomeCursor = db.rawQuery(
                "SELECT SUM(amount) FROM transaction_table WHERE userid = ? AND transaction_type = ?",
                new String[]{String.valueOf(userId), "Income"}
        );

        if(incomeCursor.moveToFirst()) {
            incomeAmount = incomeCursor.getDouble(0);
        }
        incomeCursor.close();

        Cursor expenseCursor = db.rawQuery(
                "SELECT SUM(amount) FROM transaction_table WHERE userid = ? AND transaction_type = ?",
                new String[]{String.valueOf(userId), "Expense"}
        );

        if(expenseCursor.moveToFirst()){
            expenseAmount = expenseCursor.getDouble(0);
        }
        expenseCursor.close();

        double net = incomeAmount - expenseAmount ;

        Income.setText("₹ " + incomeAmount);
        Expense.setText("₹ " + expenseAmount);
        Net.setText("₹ " + net);

    }
}