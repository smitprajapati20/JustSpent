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

public class ChangePassword extends AppCompatActivity {

    Button Change;
    TextView backToProfile;
    EditText currentPass, newPass, email;
    SharedPreferences sp;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        db = openOrCreateDatabase(ConstantSP.DataB, MODE_PRIVATE, null);
        sp = getSharedPreferences(ConstantSP.DataB, MODE_PRIVATE);

        String userTable = "CREATE TABLE IF NOT EXISTS user(userid INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(50), email VARCHAR(30), contact VARCHAR(10), password VARCHAR(20))";
        db.execSQL(userTable);

        Change = findViewById(R.id.btnChange);
        backToProfile = findViewById(R.id.txtBackToProfile);
        currentPass = findViewById(R.id.CurrentPassword);
        newPass = findViewById(R.id.newPassword);
        email = findViewById(R.id.edtEmail);

        email.setText(sp.getString(ConstantSP.email,""));

        backToProfile.setOnClickListener(v->{
            startActivity(new Intent(ChangePassword.this,Profile.class));
        });

        Change.setOnClickListener(view -> {
            String CurrentPassword = currentPass.getText().toString().trim();
            String NewPassword = newPass.getText().toString().trim();

            if(CurrentPassword.isEmpty()){
                currentPass.setError("Enter Current Password");
                currentPass.requestFocus();
                return;
            }
            else if(!CurrentPassword.equals(sp.getString(ConstantSP.password, ""))){
                currentPass.setError("Password is not matching");
                currentPass.requestFocus();
                return;
            }
            else if(NewPassword.isEmpty()){
                newPass.setError("Enter Confirm Password");
                newPass.requestFocus();
                return;
            }
            else if(NewPassword.length() < 6){
                newPass.setError("Password must be less than 6 characters");
                newPass.requestFocus();
                return;
            }
            else {
                String checkUser = "SELECT * FROM user WHERE email = '"+sp.getString(ConstantSP.email,"")+"' ";
                Cursor cursor = db.rawQuery(checkUser, null);

                if (cursor.getCount() > 0){
                    String updatePassword = "UPDATE user SET password = '"+NewPassword+"' WHERE email = '"+sp.getString(ConstantSP.email,"")+"'";
                    db.execSQL(updatePassword);
                    Toast.makeText(this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ChangePassword.this,Profile.class));
                    finish();
                }

            }
        });


    }
}