package com.example.app1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.app1.database.DatabaseManager;

import java.io.File;

public class SearchActivity extends AppCompatActivity {


    private Button btnCategory1, btnCategory2, btnCategory3, btnCategory4, btnCategory5, btnCategory6;
    private SearchView searchViewFindSongs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainSearch), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btnCategory1 = findViewById(R.id.btnCategory1);
        btnCategory2 = findViewById(R.id.btnCategory2);
        btnCategory3 = findViewById(R.id.btnCategory3);
        btnCategory4 = findViewById(R.id.btnCategory4);
        btnCategory5 = findViewById(R.id.btnCategory5);
        btnCategory6 = findViewById(R.id.btnCategory6);
        searchViewFindSongs = findViewById(R.id.searchViewFindSongs);


        searchViewFindSongs.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCategories(newText);
                return true;
            }
        });

        setUserImage();
    }

    // Kategorileri filtrele
    private void filterCategories(String query) {
        // Arama metni boşsa, tüm butonları göster
        if (query.isEmpty()) {
            showAllCategories();
        } else {

            if (btnCategory1.getText().toString().toLowerCase().contains(query.toLowerCase())) {
                btnCategory1.setVisibility(View.VISIBLE);
            } else {
                btnCategory1.setVisibility(View.GONE);
            }

            if (btnCategory2.getText().toString().toLowerCase().contains(query.toLowerCase())) {
                btnCategory2.setVisibility(View.VISIBLE);
            } else {
                btnCategory2.setVisibility(View.GONE);
            }

            if (btnCategory3.getText().toString().toLowerCase().contains(query.toLowerCase())) {
                btnCategory3.setVisibility(View.VISIBLE);
            } else {
                btnCategory3.setVisibility(View.GONE);
            }

            if (btnCategory4.getText().toString().toLowerCase().contains(query.toLowerCase())) {
                btnCategory4.setVisibility(View.VISIBLE);
            } else {
                btnCategory4.setVisibility(View.GONE);
            }

            if (btnCategory5.getText().toString().toLowerCase().contains(query.toLowerCase())) {
                btnCategory5.setVisibility(View.VISIBLE);
            } else {
                btnCategory5.setVisibility(View.GONE);
            }

            if (btnCategory6.getText().toString().toLowerCase().contains(query.toLowerCase())) {
                btnCategory6.setVisibility(View.VISIBLE);
            } else {
                btnCategory6.setVisibility(View.GONE);
            }
        }
    }

    private void showAllCategories() {
        btnCategory1.setVisibility(View.VISIBLE);
        btnCategory2.setVisibility(View.VISIBLE);
        btnCategory3.setVisibility(View.VISIBLE);
        btnCategory4.setVisibility(View.VISIBLE);
        btnCategory5.setVisibility(View.VISIBLE);
        btnCategory6.setVisibility(View.VISIBLE);
    }


    public void backtoSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("source", "SearchActivity"); // Profil sayfasından geldiğinizi belirtiyoruz

        String userId = getIntent().getStringExtra("userId");
        intent.putExtra("userId",userId);

        startActivity(intent);
    }


    public void goCategoryOnClick(View v) {

        Button clickedButton = (Button) v;
        String buttonText = clickedButton.getText().toString(); // Butonun metnini alıyoruz

        Intent intent = new Intent(this, CategorysongActivity.class);

        String userId = getIntent().getStringExtra("userId");
        intent.putExtra("userId",userId);

        intent.putExtra("categoryName", buttonText);

        // CategoryActivity'ye yönlendiriyoruz
        startActivity(intent);

    }
    public void goBackHomeOnClick(View v) {
        Intent intent3 = new Intent(this, MainActivity.class);


        String userId = getIntent().getStringExtra("userId");
        intent3.putExtra("userId",userId);

        startActivity(intent3);

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void goBackProfilOnClick(View v) {
        Intent intent3 = new Intent(this, ProfileActivity.class);


        String userId = getIntent().getStringExtra("userId");
        intent3.putExtra("userId",userId);

        startActivity(intent3);

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void goBackSearchOnClick(View v) {
        Intent intent3 = new Intent(this, SearchActivity.class);

        String userId = getIntent().getStringExtra("userId");
        intent3.putExtra("userId",userId);

        startActivity(intent3);
    }


    DatabaseManager dbmanager = new DatabaseManager(this);
    private void setUserImage() {
        String userId = getIntent().getStringExtra("userId");

        String userImagePath = dbmanager.getUserImagePath(userId);

        ImageButton imgBtnProfile = findViewById(R.id.imgBtnProfile2);

        if (userImagePath != null && !userImagePath.isEmpty()) {
            // Kullanıcı fotoğrafı dosya yolunu kontrol ediyoruz
            File imgFile = new File(userImagePath);
            if (imgFile.exists()) {
                // Fotoğrafı Bitmap olarak yükleyip ImageButton'a ayarlıyoruz
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgBtnProfile.setImageBitmap(bitmap);
            } else {
                imgBtnProfile.setImageResource(R.drawable.profileimage);
            }
        } else {
            imgBtnProfile.setImageResource(R.drawable.profileimage);
        }
    }
}