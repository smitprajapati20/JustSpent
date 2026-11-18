package com.app.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ForgotPassword extends AppCompatActivity {

    Button reset;
    TextView back_login;
    EditText email_f, newPass_f, confirmNewPass_f;
    SharedPreferences sp;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        db = openOrCreateDatabase(ConstantSP.DataB, MODE_PRIVATE, null);
        sp = getSharedPreferences(ConstantSP.DataB, MODE_PRIVATE);

        String userTable = "CREATE TABLE IF NOT EXISTS user(userid INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(50), email VARCHAR(30), contact VARCHAR(10), password VARCHAR(20))";
        db.execSQL(userTable);

        reset = findViewById(R.id.btnReset);
        back_login = findViewById(R.id.txtBackToLogin);
        email_f = findViewById(R.id.edtEmail);
        newPass_f = findViewById(R.id.newPassword);
        confirmNewPass_f = findViewById(R.id.newConfirmPassword);

        back_login.setOnClickListener(v->{
            startActivity(new Intent(ForgotPassword.this,MainActivity.class ));
        });

        reset.setOnClickListener(view -> {
            String email = email_f.getText().toString().trim();
            String newPassword = newPass_f.getText().toString().trim();
            String confirmPassword = confirmNewPass_f.getText().toString().trim();

            if(email.isEmpty()){
                email_f.setError("Enter Email");
                email_f.requestFocus();
                return;
            }
            else if(newPassword.isEmpty()){
                newPass_f.setError("Enter New Password");
                newPass_f.requestFocus();
                return;
            }
            else if(newPassword.length() < 6){
                newPass_f.setError("Password must be of at least 6 characters");
                newPass_f.requestFocus();
                return;
            }
            else if(confirmPassword.isEmpty()){
                confirmNewPass_f.setError("Enter Confirm Password");
                confirmNewPass_f.requestFocus();
                return;
            }
            else if(!newPassword.equals(confirmPassword)){
                confirmNewPass_f.setError("Password do not match");
                confirmNewPass_f.requestFocus();
                return;
            }
            else {
                String checkUser = "SELECT * FROM user WHERE email = '"+email+"' ";
                Cursor cursor = db.rawQuery(checkUser, null);

                if (cursor.getCount() > 0){
                    String updatePassword = "UPDATE user SET password = '"+newPassword+"' WHERE email = '"+email+"'";
                    db.execSQL(updatePassword);
                    Toast.makeText(this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPassword.this,MainActivity.class));
                    finish();
                }
                else {
                    email_f.setError("User does not exist");
                    email_f.requestFocus();
                }
            }
        });

    }
}