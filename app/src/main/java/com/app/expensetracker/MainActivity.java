package com.app.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sp;
    SQLiteDatabase db;
    EditText email,password;
    Button Login;
    TextView SignUp,ForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase(ConstantSP.DataB, MODE_PRIVATE, null);
        sp = getSharedPreferences(ConstantSP.DataB, MODE_PRIVATE);

        String userTable = "CREATE TABLE IF NOT EXISTS user(userid INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(50), email VARCHAR(30), contact VARCHAR(10), password VARCHAR(20))";
        db.execSQL(userTable);

        email = findViewById(R.id.edtEmail);
        password = findViewById(R.id.edtPassword);
        ForgotPassword = findViewById(R.id.txtForgotPassword);
        Login = findViewById(R.id.btnLogin);
        SignUp = findViewById(R.id.txtGoSignup);

        Login.setOnClickListener(view -> {
            String email_text = email.getText().toString();
            String password_text = password.getText().toString();

            if (email_text.isEmpty()){
                email.setError("Enter Email");
                email.requestFocus();
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email_text).matches()){
                email.setError("Enter Valid Email");
                email.requestFocus();
                return;
            }
            if (password_text.isEmpty()){
                password.setError("Enter Password");
                password.requestFocus();
                return;
            }
            if (password_text.length() < 6){
                password.setError("Password must be 6 characters long");
                password.requestFocus();
                return;
            }
            else {
                String checkUser = "SELECT * FROM user WHERE email = '"+email_text+"' AND password = '"+password_text+"' ";
                Cursor cursor = db.rawQuery(checkUser, null);
                if (cursor.moveToFirst()){
                    sp.edit()
                            .putInt(ConstantSP.userid, cursor.getInt(0))
                            .putString(ConstantSP.name, cursor.getString(1))
                            .putString(ConstantSP.email, cursor.getString(2))
                            .putString(ConstantSP.contact, cursor.getString(3))
                            .putString(ConstantSP.password, cursor.getString(4))
                            .apply();
                    startActivity(new Intent(MainActivity.this, Dashboard.class));
                    finish();
                }
                else {
                    email.setError("User does not exist");
                    email.requestFocus();
                    Toast.makeText(this,"User does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ForgotPassword.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, ForgotPassword.class));
        });

        SignUp.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, SignUp.class));
        });
    }
}