package com.example.app1;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.app1.database.DatabaseManager;
import com.example.app1.model.SongWithId;
import java.util.List;

public class PlaySongActivity extends AppCompatActivity {
    private TextView songNameTextView, artistNameTextView,albumNameTextView;
    private String songFilePath;
    private ImageView imgViewSongPhoto;
    DatabaseManager dbmanager = new DatabaseManager(this);


    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false; // Şarkı çalıyor mu durumu
    private ImageButton playPauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playsong);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        songNameTextView = findViewById(R.id.txtViewSongName);
        artistNameTextView = findViewById(R.id.txtViewArtistName);
        albumNameTextView = findViewById(R.id.txtViewAlbumName);
        imgViewSongPhoto = findViewById(R.id.imgViewSongPhoto);

        Intent intent = getIntent();
        String songName = intent.getStringExtra("songName");
        String artistName = intent.getStringExtra("artistName");
        String imgPath = intent.getStringExtra("imgPath");
        String userId = intent.getStringExtra("userId");
        String albumName = intent.getStringExtra("albumName");
        songFilePath = intent.getStringExtra("filePath");


        songNameTextView.setText(songName);
        artistNameTextView.setText(artistName);

        setUserImage(imgPath);

        playPauseButton = findViewById(R.id.imgBtnPlaySong);

        playPauseButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (isPlaying) {
                    pauseSong(); // Şarkıyı duraklat
                } else {
                    resumeSong(); // Şarkıyı devam ettir
                }
            }
        });

        playSong(songFilePath);


        ImageButton imgBtnPreviousSong = findViewById(R.id.imgBtnPreviousSong);
        ImageButton imgBtnNextSong = findViewById(R.id.imgBtnNextSong);
        if(albumName != null) {
            albumNameTextView.setText(albumName);

            List<String> songIds = dbmanager.getSongIdsByUserIdAndAlbumName(dbmanager, userId, albumName);
            List<SongWithId> AlbumSongs = dbmanager.getSongsBySongIds(songIds);


            // Önceki şarkıya geçiş
            imgBtnPreviousSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int currentSongIndex = getCurrentSongIndex(AlbumSongs, songFilePath);

                    if (currentSongIndex > 0) {
                        currentSongIndex--;


                        // Mevcut şarkıyı al
                        SongWithId currentSong = AlbumSongs.get(currentSongIndex);

                        // Yeni bir Intent ile aynı aktiviteyi başlat
                        Intent intent = new Intent(PlaySongActivity.this, PlaySongActivity.class);
                        intent.putExtra("songName", currentSong.getTitle());
                        intent.putExtra("userId", userId);
                        intent.putExtra("artistName", currentSong.getArtist());
                        intent.putExtra("albumName", albumName);
                        intent.putExtra("filePath", currentSong.getFilePath());
                        intent.putExtra("imgPath", currentSong.getImagePath());
                        intent.putExtra("currentIndex", currentSongIndex);

                        // Aktiviteyi yeniden başlat
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                        finish();

                    } else {
                        Toast.makeText(PlaySongActivity.this, "Bu ilk şarkı!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Sonraki şarkıya geçiş
            imgBtnNextSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int currentSongIndex = getCurrentSongIndex(AlbumSongs, songFilePath);

                    if (currentSongIndex < AlbumSongs.size() - 1) {
                        currentSongIndex++;


                        // Mevcut şarkıyı al
                        SongWithId currentSong = AlbumSongs.get(currentSongIndex);

                        // Yeni bir Intent ile aynı aktiviteyi başlat
                        Intent intent = new Intent(PlaySongActivity.this, PlaySongActivity.class);
                        intent.putExtra("songName", currentSong.getTitle());
                        intent.putExtra("artistName", currentSong.getArtist());
                        intent.putExtra("albumName", albumName);
                        intent.putExtra("userId", userId);
                        intent.putExtra("filePath", currentSong.getFilePath());
                        intent.putExtra("imgPath", currentSong.getImagePath());
                        intent.putExtra("currentIndex", currentSongIndex);

                        // Aktiviteyi yeniden başlat
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        finish();

                    } else {
                        Toast.makeText(PlaySongActivity.this, "Bu son şarkı!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            imgBtnPreviousSong.setVisibility(View.GONE);
            imgBtnNextSong.setVisibility(View.GONE);
        }
    }

    private int getCurrentSongIndex(List<SongWithId> AlbumSongs, String songFilePath) {
        for (int i = 0; i < AlbumSongs.size(); i++) {
            SongWithId song = AlbumSongs.get(i);
            if (songFilePath.equals(song.getFilePath())) {
                return i; // Eşleşen şarkının indeksini döndür
            }
        }
        return -1; // Hiçbir eşleşme bulunmazsa -1 döndür
    }


    public void BackAlbumOnClick(View view) {

        mediaPlayer.stop(); // Şarkıyı durdur
        mediaPlayer.release(); // Kaynağı serbest bırak
        mediaPlayer = null;

        Intent intent = getIntent();

        String userId = getIntent().getStringExtra("userId");
        String albumName = getIntent().getStringExtra("albumName");
        String categoryName = getIntent().getStringExtra("categoryName");

        if(categoryName != null){
            intent = new Intent(this, CategorysongActivity.class);
            intent.putExtra("categoryName",categoryName);
        } else {
            intent = new Intent(this, AlbumActivity.class);
            intent.putExtra("albumName",albumName);
        }
        intent.putExtra("userId",userId);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void playSong(String filePath) {
        // Dosya adını (örneğin "TakeFive.mp3") R.raw'daki kaynak adı ile eşleştiriyoruz
        String fileName = filePath.replace(".mp3", ""); // "TakeFive.mp3" -> "TakeFive"
        int resId = getResources().getIdentifier(fileName, "raw", getPackageName()); // R.raw'daki dosya id'sini al

        if (resId != 0) { // Kaynak id'si bulunduysa
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, resId);
                mediaPlayer.setOnCompletionListener(mp -> {
                    // Şarkı tamamlandığında durumu sıfırla
                    isPlaying = false;
                    playPauseButton.setImageResource(R.drawable.musicplayerbutton2); // İlk buton resmini geri yükle
                });
            }

            mediaPlayer.start();
            isPlaying = true;
            playPauseButton.setImageResource(R.drawable.musicplayerbutton1); // Çalma butonunu değiştir

        } else {
            Toast.makeText(this, "Şarkı bulunamadı: " + filePath, Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseSong() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            playPauseButton.setImageResource(R.drawable.musicplayerbutton2); // Duraklatma butonunu geri yükle
        }
    }

    private void resumeSong() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            playPauseButton.setImageResource(R.drawable.musicplayerbutton1); // Çalma butonunu değiştir
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private void setUserImage(String imagepath){

        // ImageButton'a profil fotoğrafını yerleştiriyoruz
        if (imagepath != null && !imagepath.isEmpty()) {
            int imageResource = getResources().getIdentifier(imagepath, "drawable", getPackageName());

            // Resim geçerli bir kaynaksa, arka plan olarak ayarlıyoruz
            if (imageResource != 0) {
                imgViewSongPhoto.setBackgroundResource(imageResource);
            } else {
                imgViewSongPhoto.setBackgroundResource(R.drawable.default_image);
            }
        } else {
            imgViewSongPhoto.setBackgroundResource(R.drawable.default_image);
        }
    }
}