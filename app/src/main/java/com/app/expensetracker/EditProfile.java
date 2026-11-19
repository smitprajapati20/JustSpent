
package com.app.expensetracker;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfile extends AppCompatActivity {

    SharedPreferences sp;
    SQLiteDatabase db;

    ImageView btnBack;
    EditText etName, etPhone, etEmail;
    TextView tvHeaderName, tvHeaderEmail,btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);

        tvHeaderName = findViewById(R.id.tvHeaderName);
        tvHeaderEmail = findViewById(R.id.tvHeaderEmail);

        btnSave = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBackProfile);

        db = openOrCreateDatabase(ConstantSP.DataB, MODE_PRIVATE, null);
        sp = getSharedPreferences(ConstantSP.DataB, MODE_PRIVATE);

        String userTable = "CREATE TABLE IF NOT EXISTS user(userid INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(50), email VARCHAR(30), contact VARCHAR(10), password VARCHAR(20))";
        db.execSQL(userTable);

        int userid = sp.getInt(ConstantSP.userid, 0);

        Cursor cursor = db.rawQuery(
                "SELECT name, email, contact FROM user WHERE userid = ?",
                new String[]{String.valueOf(userid)}
        );

        if(cursor.moveToFirst()) {
            etName.setText(cursor.getString(0));
            etEmail.setText(cursor.getString(1));
            etPhone.setText(cursor.getString(2));

            tvHeaderEmail.setText(cursor.getString(1));
        }

        cursor.close();

        updateHeaderName();

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateHeaderName();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if(name.isEmpty()) {
                etName.setError("Name is required");
            }
            else if(!phone.matches("[0-9]{10}")) {
                etPhone.setError("Enter a valid 10-digit number");
            }
            else {
                saveProfile(userid, name, phone);
            }
        });
    }

    private void updateHeaderName() {
        String name = etName.getText().toString().trim();

        tvHeaderName.setText(name.isEmpty() ? "Your name" : name);
    }

    private void saveProfile(int userid, String name, String phone) {

        db.execSQL(
                "UPDATE user SET name = ?, contact = ? WHERE userid = ?",
                new Object[]{name, phone, userid}
        );
        sp.edit()
                .putString(ConstantSP.name, name)
                .putString(ConstantSP.contact, phone)
                .apply();

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
