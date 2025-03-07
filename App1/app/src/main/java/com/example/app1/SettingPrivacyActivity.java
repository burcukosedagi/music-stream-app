package com.example.app1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app1.database.DatabaseManager;
import com.example.app1.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingPrivacyActivity extends AppCompatActivity {

    private DatabaseManager dbManager;
    private EditText editTxtUserName, editTxtEmail, editTxtPassword;
    private Button btnSave;
    private String userId, source;
    private ImageButton imgBtnUserphoto;
    private String selectedPhotoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_privacy);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.privacyPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbManager = new DatabaseManager(this);
        editTxtUserName = findViewById(R.id.editTxtUserName);
        editTxtEmail = findViewById(R.id.editTxtTxtEmail);
        editTxtPassword = findViewById(R.id.editTxtTxtPassword);
        imgBtnUserphoto = findViewById(R.id.imgBtnPhoto);
        btnSave = findViewById(R.id.btnSave);

        userId = getIntent().getStringExtra("userId");
        loadUserData();

        Intent intent = getIntent();
        if (intent != null) {
            source = intent.getStringExtra("source");
        }

        btnSave.setOnClickListener(this::saveUserProfile);
        imgBtnUserphoto.setOnClickListener(this::openGallery);
    }

    private void loadUserData() {
        if (userId != null) {
            User userData = dbManager.getUserById(userId);
            if (userData != null) {
                editTxtUserName.setText(userData.getUserName());
                editTxtEmail.setText(userData.getEmail());
                editTxtPassword.setText(userData.getPassword());
                loadImage(userData.getImagePath());
            }
        }
    }

    private void loadImage(String photoPath) {
        if (photoPath != null && !photoPath.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            if (bitmap != null) {
                imgBtnUserphoto.setImageBitmap(bitmap);
            } else {
                imgBtnUserphoto.setImageResource(R.drawable.profileimage);
            }
        } else {
            imgBtnUserphoto.setImageResource(R.drawable.profileimage);
        }
    }

    private void saveUserProfile(View view) {
        String newUserName = editTxtUserName.getText().toString().trim();
        String newEmail = editTxtEmail.getText().toString().trim();
        String newPassword = editTxtPassword.getText().toString().trim();

        if (newUserName.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Tüm alanları doldurmanız gerekiyor!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateInputs(newEmail, newPassword)) {
            return;
        }

        boolean isUpdated = dbManager.updateUser(
                Integer.parseInt(userId),
                newUserName,
                newEmail,
                newPassword
        );

        if (isUpdated) {
            if (selectedPhotoPath != null) {
                updateUserPhoto(selectedPhotoPath);
            }

            Toast.makeText(this, "Profil başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Bir hata oluştu, lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String email, String password) {
        if (password.length() < 6) {
            Toast.makeText(this, "Şifre en az 6 karakter olmalı.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Geçersiz e-posta formatı.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void backtToSetOnClick(View view) {
        Intent intent;
        if ("ProfileActivity".equals(source)) {
            intent = new Intent(SettingPrivacyActivity.this, ProfileActivity.class);
        } else {
            intent = new Intent(SettingPrivacyActivity.this, SettingsActivity.class);
        }
        intent.putExtra("userId", userId);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void openGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                selectedPhotoPath = saveImageToAlbumFolder(selectedImageUri);
                imgBtnUserphoto.setImageURI(selectedImageUri);
            }
        }
    }

    private String saveImageToAlbumFolder(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            File albumFolder = new File(getFilesDir(), "AlbumFotolari");
            if (!albumFolder.exists()) {
                albumFolder.mkdirs();
            }

            String fileName = "album_" + System.currentTimeMillis() + ".png";
            File file = new File(albumFolder, fileName);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateUserPhoto(String photoPath) {
        boolean isUpdated = dbManager.updateUserPhoto(Integer.parseInt(userId), photoPath);
        if (isUpdated) {
            Toast.makeText(this, "Fotoğraf başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Fotoğraf güncellenemedi.", Toast.LENGTH_SHORT).show();
        }
    }
}