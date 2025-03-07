package com.example.app1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;

import com.example.app1.database.DatabaseHelper;
import com.example.app1.database.DatabaseManager;
import com.example.app1.adapters.AlbumSongAdapter;
import com.example.app1.model.SongWithId;

import java.io.File;
import java.util.List;
import java.util.Random;

public class AlbumActivity extends AppCompatActivity {

    private ConstraintLayout ConstraintPropertiesCam, ConstraintRenameBack, ConstraintRenameFront;
    private ImageButton btnProperty, playButton;
    private Button btnRenameAlbum, btnRename, btnCancel, btnBack,btnDeleteAlbum;
    private TextView txtAlbumName, txtNewAlbumName;
    private RecyclerView recyclerViewSongs;
    private AlbumSongAdapter songAdapter;
    private String albumName, userId, albumPhotoPath;
    private ImageView imgAlbum;
    private int albumId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_album);

        btnBack = findViewById(R.id.btnBack);
        ConstraintPropertiesCam = findViewById(R.id.ConstraintPropertiesCam);
        ConstraintRenameBack = findViewById(R.id.ConstraintRenameBack);
        ConstraintRenameFront = findViewById(R.id.ConstraintRenameFront);
        btnProperty = findViewById(R.id.btnProperties);
        btnRenameAlbum = findViewById(R.id.btnRenameAlbum);
        btnRename = findViewById(R.id.btnRename);
        btnCancel = findViewById(R.id.btnCancel);
        txtAlbumName = findViewById(R.id.txtAlbumName);
        txtNewAlbumName = findViewById(R.id.txtNewAlbumName);
        btnDeleteAlbum = findViewById(R.id.btnDeleteAlbum);
        recyclerViewSongs = findViewById(R.id.recyclerViewSongs);
        imgAlbum = findViewById(R.id.imgAlbum);

        DatabaseManager dbmanager = new DatabaseManager(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainAlbum), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        albumName = getIntent().getStringExtra("albumName");
        userId = getIntent().getStringExtra("userId");

        albumId = dbmanager.getAlbumIdByTitle(albumName);

        if (userId == null) {
            int userIdInt = getIntent().getIntExtra("userId", -1);
            if (userIdInt != -1) {
                userId = String.valueOf(userIdInt);
            }
        }

        albumPhotoPath = dbmanager.getImagePathFromDatabase(albumName);

        if (albumPhotoPath != null && !albumPhotoPath.isEmpty()) {
            // Veritabanından alınan yol, fotoğraf dosyasının mevcut olup olmadığını kontrol ediyoruz
            File imgFile = new File(albumPhotoPath);

            // Dosya mevcutsa resmi yükle
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgAlbum.setImageBitmap(bitmap);
            } else {
                imgAlbum.setImageResource(R.drawable.default_image);
            }
        } else {
            imgAlbum.setImageResource(R.drawable.default_image);
        }

        List<String> songIds = dbmanager.getSongIdsByUserIdAndAlbumName(dbmanager, userId, albumName);
        List<SongWithId> AlbumSongs;
        if (songIds.isEmpty()) {
            Toast.makeText(this, "Eşleşen şarkı bulunamadı", Toast.LENGTH_SHORT).show();
        } else {

            AlbumSongs = dbmanager.getSongsBySongIds(songIds);

            songAdapter = new AlbumSongAdapter(AlbumSongs, song -> {
                // Şarkıya tıklanınca işlem
                Toast.makeText(this, "Tıklanan şarkı: " + song.getTitle(), Toast.LENGTH_SHORT).show();
            }, userId, albumName,String.valueOf(albumId));

            playButton = findViewById(R.id.btnPlay);
            // Play butonuna tıklanıldığında
            playButton.setOnClickListener(v -> {
                // RecyclerView'deki ilk şarkıyı al
                SongWithId firstSong = AlbumSongs.get(0);  // İlk şarkıyı al

                Intent playIntent = new Intent(AlbumActivity.this, PlaySongActivity.class);
                playIntent.putExtra("songName", firstSong.getTitle());
                playIntent.putExtra("artistName", firstSong.getArtist());
                playIntent.putExtra("filePath", firstSong.getFilePath());
                playIntent.putExtra("imgPath", firstSong.getImagePath());
                playIntent.putExtra("userId", userId);
                playIntent.putExtra("albumName", albumName);

                startActivity(playIntent);
            });


            ImageButton btnMix = findViewById(R.id.btnMix);
            btnMix.setOnClickListener(v -> {
                // Eğer şarkı listesi boş değilse
                if (!AlbumSongs.isEmpty()) {

                    int randomIndex = new Random().nextInt(AlbumSongs.size());
                    SongWithId randomSong = AlbumSongs.get(randomIndex);

                    // Seçilen şarkının bilgilerini almak
                    String songFilePath = randomSong.getFilePath();
                    String songTitle = randomSong.getTitle();
                    String songArtist = randomSong.getArtist();
                    String songImagePath = randomSong.getImagePath();

                    // PlaySongActivity'yi başlatıyoruz ve gerekli verileri iletiyoruz
                    Intent intent = new Intent(v.getContext(), PlaySongActivity.class);
                    intent.putExtra("songName", songTitle);
                    intent.putExtra("artistName", songArtist);
                    intent.putExtra("filePath", songFilePath);
                    intent.putExtra("imgPath", songImagePath);
                    intent.putExtra("userId", userId); // userId'yi ekliyoruz
                    intent.putExtra("albumName", albumName);
                    v.getContext().startActivity(intent);
                } else {
                    // Eğer şarkılar listesi boşsa, kullanıcıya bir uyarı göster
                    Toast.makeText(v.getContext(), "Uygun Şarkı Bulunamadı", Toast.LENGTH_SHORT).show();
                }
            });
        }

        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSongs.setAdapter(songAdapter);


        if (albumName != null) {
            // Gelen veriyle bir işlem yapın
            TextView albumNameTextView = findViewById(R.id.txtAlbumName);
            albumNameTextView.setText(albumName);
        }


        //Sarkı Scroll Bar'ın hareketinde kontrol sağlandı.
        recyclerViewSongs.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Yukarı kaydırırken
                if (dy > 0) {
                    btnBack.setVisibility(View.VISIBLE);
                }
                // Aşağı kaydırırken
                else if (dy < 0) {
                    btnBack.setVisibility(View.GONE);
                }
            }
        });


        // Back butonu davranışı için gelen intent kontrol ediliyor
        String source = getIntent().getStringExtra("source");
        btnBack.setOnClickListener(v -> {
            Intent intent;
            if ("ProfileActivity".equals(source)) {
                // ProfileActivity'den gelmişse ProfileActivity'ye dön
                intent = new Intent(AlbumActivity.this, ProfileActivity.class);
            } else {
                // Aksi halde MainActivity'ye dön
                intent = new Intent(AlbumActivity.this, MainActivity.class);
            }

            String UserId = getIntent().getStringExtra("userId");
            intent.putExtra("userId",UserId);
            startActivity(intent);

            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });


        btnRenameAlbum.setOnClickListener(v -> {

            btnRenameAlbum.setBackgroundColor(Color.GRAY);
            
            // 3 saniye sonra rengi eski haline döndür
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                btnRenameAlbum.setBackgroundColor(Color.TRANSPARENT); // Eski rengine döner
            }, 3000);

            ConstraintRenameBack.bringToFront();
            ConstraintRenameFront.bringToFront();

            ConstraintPropertiesCam.setVisibility(View.GONE);
            ConstraintRenameFront.setVisibility(View.VISIBLE);
            ConstraintRenameBack.setVisibility(View.VISIBLE);
        });

        btnRename.setOnClickListener(v -> {

            String txtNew = txtNewAlbumName.getText().toString().trim();
            if (!txtNew.isEmpty()) {

                closeKeyboard();

                txtAlbumName.setText(txtNew);

                // Eğer ilgili Button id'si varsa, onu bulup güncelle
                if (String.valueOf(albumId) != null) {

                    DatabaseHelper dbHelper = new DatabaseHelper(AlbumActivity.this);
                    dbHelper.updateAlbumName(albumId, txtNew);

                    Button albumButton = findViewById(getResources().getIdentifier(String.valueOf(albumId), "id", getPackageName()));
                    if (albumButton != null) {
                        albumButton.setText(txtNew);
                    }
                }
                ConstraintRenameFront.setVisibility(View.GONE);
                ConstraintRenameBack.setVisibility(View.GONE);
                txtNewAlbumName.setText("");

            } else {

                Toast.makeText(this, "Uyari! Album adi bos birakilamaz", Toast.LENGTH_SHORT).show();
            }
        });

        //Rename kısmında isim değişimini iptal etme
        btnCancel.setOnClickListener(v -> {

            closeKeyboard();

            String textClear = "";
            txtNewAlbumName.setText(textClear);
            ConstraintRenameFront.setVisibility(View.GONE);
            ConstraintRenameBack.setVisibility(View.GONE);
        });


        btnProperty.setOnClickListener(v -> {

            ConstraintPropertiesCam.bringToFront();

            if (ConstraintPropertiesCam.getVisibility() == View.VISIBLE) {

                ConstraintPropertiesCam.setVisibility(View.GONE);
            } else {

                ConstraintPropertiesCam.setVisibility(View.VISIBLE);
                ConstraintPropertiesCam.bringToFront();
            }
        });


        btnDeleteAlbum.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Albümü Sil")
                    .setMessage("Emin misiniz? Bu işlem geri alınamaz.")
                    .setPositiveButton("Evet", (dialog, which) -> {
                        finish(); // Ana aktiviteyi kapatır

                        DatabaseHelper dbHelper = new DatabaseHelper(AlbumActivity.this);
                        dbHelper.deleteAlbum(albumId);

                        if(albumId != -1){
                            Toast.makeText(AlbumActivity.this, "Albüm silindi", Toast.LENGTH_SHORT).show();

                            Intent intent3 = new Intent(this, ProfileActivity.class);
                            intent3.putExtra("userId",userId);
                            startActivity(intent3);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                        }else{
                            Toast.makeText(AlbumActivity.this, "Albüm silinemedi", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .setNegativeButton("Hayır", (dialog, which) -> {

                        ConstraintPropertiesCam.setVisibility(View.GONE);
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        });
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus(); // Şu an odakta olan View alınır
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
       }
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateSongsList();
    }
    private void updateSongsList() {
        DatabaseManager dbManager = new DatabaseManager(this);

        albumName = getIntent().getStringExtra("albumName");
        userId = getIntent().getStringExtra("userId");

        if (albumName != null && userId != null) {
            // Şarkıları yeniden yükleyin
            List<String> songIds = dbManager.getSongIdsByUserIdAndAlbumName(dbManager, userId, albumName);
            List<SongWithId> updatedAlbumSongs = dbManager.getSongsBySongIds(songIds);

            if (updatedAlbumSongs != null) {
                // Adaptöre yeni listeyi verin
                songAdapter.updateSongs(updatedAlbumSongs);
            }
        }
    }

    public void AddListOnClick(View view){
        Intent intent = new Intent(this, AddSongActivity.class);

        userId = getIntent().getStringExtra("userId");

        if (userId == null) {
            int userIdInt = getIntent().getIntExtra("userId", -1);
            if (userIdInt != -1) {
                userId = String.valueOf(userIdInt);
            }
        }
        intent.putExtra("userId",userId);

        albumName = getIntent().getStringExtra("albumName");
        intent.putExtra("albumName", albumName);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}