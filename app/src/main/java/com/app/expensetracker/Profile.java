package com.app.expensetracker;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
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
    TextView Initial, Name, Email;
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

    }
}