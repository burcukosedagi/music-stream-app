package com.example.app1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.model.Song;
import com.example.app1.adapters.CategorySongAdapter;
import com.example.app1.database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class CategorysongActivity extends AppCompatActivity {

    String categoryName;
    DatabaseManager databaseManager = new DatabaseManager(this);
    int categoryId;
    private List<Song> originalSongs;
    private List<Song> filteredSongs;
    CategorySongAdapter adapter;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categorysong);

        Intent intent = getIntent();
        // Intent'ten gönderilen "categoryName" verisini alıyoruz
        categoryName = intent.getStringExtra("categoryName");

        String userId = intent.getStringExtra("userId");

        categoryId = databaseManager.getCategoryIdByName(categoryName);
        // Kategori id'sini kullanarak şarkıları almak
        originalSongs = databaseManager.getSongsByCategoryId(categoryId); // Orijinal şarkı listesi
        filteredSongs = new ArrayList<>(originalSongs);  // Filtrelenmiş şarkı listesi

        adapter = new CategorySongAdapter(originalSongs, userId, categoryName);


        if (categoryName != null) {
            // categoryName null değilse işlemi yap
            TextView textCategoryName = findViewById(R.id.txtCategoryName);
            textCategoryName.setText(categoryName);
            // Diğer işlemler
        }


        // Eğer kategori adı null değilse, bunu bir TextView'de gösterebiliriz
        if (categoryName != null) {
            TextView textCategoryName = findViewById(R.id.txtCategoryName);
            textCategoryName.setText(categoryName); // TextView'e kategori adını set ediyoruz

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });


            // RecyclerView'i ayarlamak
            recyclerView = findViewById(R.id.recyclerViewSongs);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

            // Adapter'ı ayarlamak
            recyclerView.setAdapter(adapter);

            //Sarkı Scroll Bar'ın hareketinde kontrol sağlandı.
            RecyclerView RecyclerViewSongs = findViewById(R.id.recyclerViewSongs);
            RecyclerViewSongs.setOnTouchListener(new View.OnTouchListener() {
                private float lastY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastY = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float deltaY = lastY - event.getY(); // Kullanıcının hareket ettiği mesafe
                            RecyclerViewSongs.scrollBy(0, (int) -deltaY * 3); // Ters yönde kaydır
                            lastY = event.getY();
                            break;
                    }
                    return true; // Dokunma olayını tamamen ele aldığımız için true döndür
                }
            });


            // Arama işlevini başlatmak için EditText'i buluyoruz
            SearchView searchViewFindSongs = findViewById(R.id.searchViewFindSongs);
            searchViewFindSongs.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Arama submit edildiğinde yapılacak işlemler
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // Metin değiştiğinde filtrele
                    filterSongs(newText);
                    return true;
                }
            });
        }
    }

    // Arama fonksiyonu: Şarkı adını veya şarkıcıyı içeren şarkıları filtrele
    private void filterSongs(String query) {

        originalSongs = databaseManager.getSongsByCategoryId(categoryId); // Orijinal şarkı listesi
        filteredSongs.clear();  // Mevcut filtrelenmiş listeyi temizle


        // Eğer sorgu boş değilse, sadece filtreleme yapıyoruz
        if (query != null && !query.isEmpty()) {
            for (Song song : originalSongs) {
                // Şarkı adı veya şarkıcı adı içerisinde arama kelimesini içeren şarkıları ekle
                if (song.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        song.getArtist().toLowerCase().contains(query.toLowerCase())) {
                    filteredSongs.add(song);
                }
            }
        } else {
            // Eğer sorgu boşsa, tüm şarkıları göster
            filteredSongs.addAll(originalSongs);
        }

        // Adapter'a veri setini güncelle
        adapter.updateList(filteredSongs);
    }

    

    public void goSearchOnClick(View view) {
        Intent intent = new Intent(this, SearchActivity.class);

        String userId = getIntent().getStringExtra("userId");
        intent.putExtra("userId",userId);

        startActivity(intent);

        // geçiş animasyonu
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}