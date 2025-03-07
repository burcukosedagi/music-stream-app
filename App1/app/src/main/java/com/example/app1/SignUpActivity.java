package com.example.app1;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.app1.database.DatabaseManager;


public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        setupGenderCheckboxes();
        setupSaveButton();
        setupPrivacyPolicyText();

    }
    public void backToLogInOnClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    public void goToMainPonClick(View view) {

        String userName = ((EditText) findViewById(R.id.editTxtTxtUserName)).getText().toString();
        String email = ((EditText) findViewById(R.id.editTxtTxtEmailAddress)).getText().toString();
        String password = ((EditText) findViewById(R.id.editTxtTxtPassword)).getText().toString();
        String gender = getSelectedGender(); // Cinsiyetin seçildiği yerden alınan değer
        String birthDate = ((EditText) findViewById(R.id.editTxtDate)).getText().toString();
        DatabaseManager dbManager = new DatabaseManager(this);

        try {
            dbManager.addUser(userName, email, password, gender, birthDate);

            Toast.makeText(this, "Kayıt Başarılı!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

            Toast.makeText(this, "Bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "Kayıt Başarılı!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private void setupGenderCheckboxes() {

        CheckBox femaleCheckbox = findViewById(R.id.btncheckBoxGender1);
        CheckBox maleCheckbox = findViewById(R.id.btncheckBoxGender2);
        CheckBox otherCheckbox = findViewById(R.id.btncheckBoxGender3);

        // Checkbox'lar için tıklama dinleyicisi
        femaleCheckbox.setOnClickListener(v -> {
            if (femaleCheckbox.isChecked()) {
                maleCheckbox.setChecked(false);
                otherCheckbox.setChecked(false);
            }
        });

        maleCheckbox.setOnClickListener(v -> {
            if (maleCheckbox.isChecked()) {
                femaleCheckbox.setChecked(false);
                otherCheckbox.setChecked(false);
            }
        });

        otherCheckbox.setOnClickListener(v -> {
            if (otherCheckbox.isChecked()) {
                femaleCheckbox.setChecked(false);
                maleCheckbox.setChecked(false);
            }
        });
    }


    private String getSelectedGender() {
        CheckBox femaleCheckbox = findViewById(R.id.btncheckBoxGender1);
        CheckBox maleCheckbox = findViewById(R.id.btncheckBoxGender2);
        CheckBox otherCheckbox = findViewById(R.id.btncheckBoxGender3);

        if (femaleCheckbox.isChecked()) {
            return "Kadın";
        } else if (maleCheckbox.isChecked()) {
            return "Erkek";
        } else if (otherCheckbox.isChecked()) {
            return "Diğer";
        }
        return null;
    }


    private void setupSaveButton() {

        Button saveButton = findViewById(R.id.btnSignnn);


        saveButton.setOnClickListener(v -> {
            // Kullanıcıdan alınan bilgiler
            String userName = ((EditText) findViewById(R.id.editTxtTxtUserName)).getText().toString().trim();
            String email = ((EditText) findViewById(R.id.editTxtTxtEmailAddress)).getText().toString().trim();
            String password = ((EditText) findViewById(R.id.editTxtTxtPassword)).getText().toString().trim();
            String birthDate = ((EditText) findViewById(R.id.editTxtDate)).getText().toString().trim();
            String gender = getSelectedGender();
            boolean acceptedPrivacy = ((CheckBox) findViewById(R.id.btncheckBox)).isChecked();


            if (userName.isEmpty()) {
                Toast.makeText(this, "Kullanıcı adı boş bırakılamaz.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.isEmpty()) {
                Toast.makeText(this, "E-posta adresi boş bırakılamaz.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Geçerli bir e-posta adresi girin.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Şifre boş bırakılamaz.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Şifre en az 6 karakter olmalı.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (birthDate.isEmpty()) {
                Toast.makeText(this, "Doğum tarihi boş bırakılamaz.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (gender == null) {
                Toast.makeText(this, "Cinsiyet seçimi yapılmalıdır.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!acceptedPrivacy) {
                Toast.makeText(this, "Gizlilik sözleşmesini kabul etmelisiniz.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();

            DatabaseManager dbManager = new DatabaseManager(this);
            try {
                dbManager.addUser(userName, email, password, gender, birthDate);
                Toast.makeText(this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Kayıt sırasında hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }


    private void setupPrivacyPolicyText() {

        TextView privacyPolicyText = findViewById(R.id.txtPrivacyPolicy);
        String fullText = "gizlilik sözleşmesini okudum ve kabul ediyorum.";
        String clickablePart = "gizlilik sözleşmesini";

        SpannableString spannableString = new SpannableString(fullText);

        // Tıklanabilir alanı belirtiyoruz
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // AlertDialog ile gizlilik sözleşmesini göster
                showPrivacyPolicyDialog();
            }
        };

        int startIndex = fullText.indexOf(clickablePart);
        int endIndex = startIndex + clickablePart.length();
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        privacyPolicyText.setText(spannableString);
        privacyPolicyText.setMovementMethod(LinkMovementMethod.getInstance()); // Tıklanabilir yapmak için gerekli
    }

    // AlertDialog ile Gizlilik Sözleşmesi Gösterimi
    private void showPrivacyPolicyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("gizlilik sözleşmesi");
        builder.setMessage("1.Kullanıcılar, kişisel verilerinin yalnızca uygulamanın sağladığı hizmetlerin çalışması için kullanacağımızı kabul ederler.");

        CheckBox checkBox = findViewById(R.id.btncheckBox);

        builder.setPositiveButton("Kabul Et", (dialog, which) -> {

            checkBox.setChecked(true);
            dialog.dismiss();
        });
        builder.setNegativeButton("Vazgeç", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}