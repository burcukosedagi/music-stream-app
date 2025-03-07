package com.example.app1;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app1.database.DatabaseManager;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    String source;
    String userId;
    TextView txtViewProfileName;
    DatabaseManager dbmanager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainSetting), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtViewProfileName = findViewById(R.id.txtViewProfileName);
        dbmanager = new DatabaseManager(this);
        userId = getIntent().getStringExtra("userId");


        Intent intent = getIntent();
        if (intent != null) {
            source = intent.getStringExtra("source");
        }


        setUserImage();
        getUserName(userId);
    }

    public void historyOnClick(View view){

        new AlertDialog.Builder(view.getContext())
                .setTitle("Dikkat!")
                .setMessage("Bu özellik henüz sunulmamış. Gelişmelerden haberdar olmak için yeniliklere göz atınız.")
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void backtoHome(View view) {

        Intent intent;
        if ("SearchActivity".equals(source)) {

            intent = new Intent(SettingsActivity.this, SearchActivity.class);
        } else {
            intent = new Intent(SettingsActivity.this, MainActivity.class);
        }

        intent.putExtra("userId",userId);

        startActivity(intent);
    }

    public void viewProfile(View view) {
        Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);

        intent.putExtra("userId",userId);

        startActivity(intent);
    }

    public void GoSettingPrivacyOnClick(View view) {
        Intent intent = new Intent(SettingsActivity.this, SettingPrivacyActivity.class);

        intent.putExtra("userId",userId);

        startActivity(intent);
    }

    public void GoInnovationsOnClick(View view) {
        Intent intent = new Intent(SettingsActivity.this, InnovationsActivity.class);

        intent.putExtra("userId",userId);

        startActivity(intent);
    }



    public void SignOutOnClick(View view){
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void setUserImage() {
        String userId = getIntent().getStringExtra("userId");

        String userImagePath = dbmanager.getUserImagePath(userId);

        ImageButton imgBtnProfilPhoto = findViewById(R.id.imgBtnProfilPhoto);

        if (userImagePath != null && !userImagePath.isEmpty()) {
            // Kullanıcı fotoğrafı dosya yolunu kontrol ediyoruz
            File imgFile = new File(userImagePath);
            if (imgFile.exists()) {
                // Fotoğrafı Bitmap olarak yükleyip ImageButton'a ayarlıyoruz
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgBtnProfilPhoto.setImageBitmap(bitmap);
            } else {
                imgBtnProfilPhoto.setImageResource(R.drawable.profileimage);
            }
        } else {
            imgBtnProfilPhoto.setImageResource(R.drawable.profileimage);
        }
    }
    private void getUserName(String userId) {

        String userName = dbmanager.getUserNameById(userId);
        if (userName != null) {
            txtViewProfileName.setText(userName);
        } else {
            txtViewProfileName.setText("Kullanıcı bulunamadı.");
        }

    }
}