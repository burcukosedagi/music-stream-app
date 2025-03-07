package com.example.app1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.adapters.AddSongAdapter;
import com.example.app1.database.DatabaseManager;
import com.example.app1.model.SongWithId;

import java.util.ArrayList;
import java.util.List;

public class AddSongActivity extends AppCompatActivity {

    private DatabaseManager dbmanager;
    private  int userId, albumId;
    private String albumName, userIdString;
    private RecyclerView recyclerView;
    private AddSongAdapter songAdapter;
    private List<SongWithId> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_song);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        dbmanager = new DatabaseManager(this);
        Intent intent = getIntent();


        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {

            userIdString = getIntent().getStringExtra("userId");
            userId = Integer.parseInt(userIdString);
        }

        albumName = intent.getStringExtra("albumName");
        albumId = dbmanager.getAlbumIdByTitle(albumName);

        recyclerView = findViewById(R.id.recyclerViewSongs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        songList = dbmanager.getAllSongsWithId();

        // Adapter'ı set ediyoruz ve gerekli listener'ı sağlıyoruz
        songAdapter = new AddSongAdapter(songList, new AddSongAdapter.OnAddToListClickListener() {
            @Override
            public void onAddToListClick(SongWithId song) {
            }
        }, userId, albumName, String.valueOf(albumId));

        recyclerView.setAdapter(songAdapter);

        SearchView searchViewFindSongs = findViewById(R.id.searchViewFindSongs);
        searchViewFindSongs.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
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

    private void filterSongs(String query) {
        // Kullanıcı tarafından girilen arama terimini küçük harfe çeviriyoruz
        String lowerCaseQuery = query.toLowerCase().trim();

        List<SongWithId> filteredSongs = new ArrayList<>();

        // Tüm şarkı listesi üzerinde dönüyoruz
        for (SongWithId song : songList) {
            // Şarkı adı ve sanatçıyı küçük harfe çeviriyoruz
            String songTitle = song.getTitle().toLowerCase();
            String songArtist = song.getArtist().toLowerCase();

            // Eğer şarkı adı veya sanatçı arama kelimesiyle uyuyorsa, listeye ekliyoruz
            if (songTitle.contains(lowerCaseQuery) || songArtist.contains(lowerCaseQuery)) {
                filteredSongs.add(song);
            }
        }

        // Filtrelenmiş listeyi adapter'a set ediyoruz
        songAdapter.updateSongList(filteredSongs);
    }

    public void BackAlbumOnClick(View v){
        Intent intent = new Intent(this, AlbumActivity.class);

        intent.putExtra("albumName", albumName);
        intent.putExtra("userId",userIdString);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}