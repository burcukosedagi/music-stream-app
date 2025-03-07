package com.example.app1;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.*;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.adapters.ProfileAlbumAdapter;
import com.example.app1.database.DatabaseManager;
import com.example.app1.model.Album;

import java.io.File;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtViewuserName;
    private DatabaseManager dbmanager;
    private String userId;
    private RecyclerView recyclerView;
    private ProfileAlbumAdapter albumAdapter;
    private List<Album> albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainProfile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtViewuserName = findViewById(R.id.txtViewuserName);
        dbmanager = new DatabaseManager(this);

        userId = getIntent().getStringExtra("userId");


        ImageButton copyUrlButton = findViewById(R.id.imgBtnshareProfile);
        copyUrlButton.setOnClickListener(v -> {
            // Kopyalanacak URL
            String urlToCopy = "https://www.pageHome.com/profile";

            // Panoya kopyalama işlemi
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("URL", urlToCopy);
            clipboard.setPrimaryClip(clip);


            Toast.makeText(ProfileActivity.this, "URL kopyalandı!", Toast.LENGTH_SHORT).show();

         });

        setUserImage();
        getUserName(userId);

        recyclerView = findViewById(R.id.recyclerViewAlbums);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        albumList = dbmanager.getAlbumsByUserId(Integer.parseInt(userId));


        albumAdapter = new ProfileAlbumAdapter(albumList, userId);
        recyclerView.setAdapter(albumAdapter);
    }

    public void GoSettingPrivacyOnClick(View view) {

        Intent intent = new Intent(ProfileActivity.this, SettingPrivacyActivity.class);

        intent.putExtra("userId",userId);
        intent.putExtra("source", "ProfileActivity");

        startActivity(intent);
    }

    public void shareProfileOnClick(View view) {
        Intent intent = new Intent(this, ShareProfileActivity.class);
        startActivity(intent);
    }


    public void goBackHomeOnClick(View v) {
        Intent intent3 = new Intent(this, MainActivity.class);

        intent3.putExtra("userId",userId);
        startActivity(intent3);

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void goBackProfilOnClick(View v) {
        Intent intent3 = new Intent(this, ProfileActivity.class);
        intent3.putExtra("userId",userId);
        startActivity(intent3);
    }
    public void goBackSearchOnClick(View v) {
        Intent intent3 = new Intent(this, SearchActivity.class);
        intent3.putExtra("userId",userId);
        startActivity(intent3);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    private void setUserImage() {
        String userImagePath = dbmanager.getUserImagePath(userId);
        ImageView imageViewPhoto = findViewById(R.id.imageViewPhoto);

        if (userImagePath != null && !userImagePath.isEmpty()) {
            // Kullanıcı fotoğrafı dosya yolunu kontrol ediyoruz
            File imgFile = new File(userImagePath);
            if (imgFile.exists()) {
                // Fotoğrafı Bitmap olarak yükleyip ImageView'e ayarlıyoruz
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageViewPhoto.setImageBitmap(bitmap);
            } else {
                imageViewPhoto.setImageResource(R.drawable.profileimage);
            }
        } else {
            imageViewPhoto.setImageResource(R.drawable.profileimage);
        }
    }


    private void getUserName(String userId) {

        String userName = dbmanager.getUserNameById(userId);
        if (userName != null) {
            txtViewuserName.setText(userName);
        } else {
            txtViewuserName.setText("Kullanıcı bulunamadı.");
        }
    }
}