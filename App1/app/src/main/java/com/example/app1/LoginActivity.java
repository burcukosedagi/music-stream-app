package com.example.app1;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app1.database.DatabaseHelper;
import com.example.app1.database.DatabaseManager;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;

    DatabaseManager dbManager = new DatabaseManager(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.copyDatabase();


        editTextEmail = findViewById(R.id.editTxtTxtEmail);
        editTextPassword = findViewById(R.id.editTxtTxtPassword2);
        Button btnLogIn = findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                logInAppOnClick();
            }
        });
    }

    public void GoSignUpOnClick(View view){
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void logInAppOnClick () {


        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();


        // Kullanıcı bilgilerini doğruluyoruz
        if (dbManager.checkUserCredentials(email, password)) {

            Intent intent = new Intent(this, MainActivity.class);

            String currentUserId = dbManager.getUserIdByEmailAndPassword(dbManager, email, password);

            intent.putExtra("userId", currentUserId);
            startActivity(intent);
            Toast.makeText(this, "Giriş Başarılı", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, "Geçersiz e-posta veya şifre", Toast.LENGTH_SHORT).show();
        }
    }
}