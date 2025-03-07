package com.example.app1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.adapters.MainAlbumAdapter;
import com.example.app1.database.DatabaseManager;
import com.example.app1.model.Album;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private DatabaseManager dbmanager = new DatabaseManager(this);
    private String source, userId;
    private TextView txtViewUsername, txtViewAlbumCount;
    private List<Album> albumList;
    private String selectedImagePath = null;
    private ImageView selectedImageView;  // Fotoğrafın ön izlemesi için ImageView
    private static final String IMAGE_FOLDER = "AlbumFotoları";  // Fotoğrafın kaydedileceği klasör


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getStringExtra("userId");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        albumList = dbmanager.getAlbumsByUserId(Integer.parseInt(userId));

        List<Album> first6Albums = getFirst8Albums(albumList);

        MainAlbumAdapter adapter = new MainAlbumAdapter(first6Albums, userId);
        recyclerView.setAdapter(adapter);



        ImageButton imgBtnProfile = findViewById(R.id.imgBtnSettings);
        imgBtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, SettingsActivity.class);

                String userId = getIntent().getStringExtra("userId");
                intent2.putExtra("userId",userId);

                startActivity(intent2);
            }
        });

        setUserImage();
        getUserName(userId);



        // Albüm listesinin son 5 öğesini al
        int albumCount = albumList.size();
        int startIndex = Math.max(0, albumCount - 5); // Son 5 albümü al

        setAlbumImages(albumList, startIndex);
    }

    private List<Album> getFirst8Albums(List<Album> albumList) {

        if (albumList.size() > 6) {
            return new ArrayList<>(albumList.subList(0, 6));
        } else {
            return albumList;
        }
    }

    public void setAlbumImages(List<Album> albumList, int startIndex) {

        txtViewAlbumCount = findViewById(R.id.txtViewAlbumCount);

        // ImageButton'lar ve TextView'ları tanımla
        ImageButton[] imgButtons = {
                findViewById(R.id.imgBtnFotoAlbum1),
                findViewById(R.id.imgBtnFotoAlbum2),
                findViewById(R.id.imgBtnFotoAlbum3),
                findViewById(R.id.imgBtnFotoAlbum4),
                findViewById(R.id.imgBtnFotoAlbum5)
        };

        TextView[] textViews = {
                findViewById(R.id.txtViewFotoAlbumName1),
                findViewById(R.id.txtViewFotoAlbumName2),
                findViewById(R.id.txtViewFotoAlbumName3),
                findViewById(R.id.txtViewFotoAlbumName4),
                findViewById(R.id.txtViewFotoAlbumName5)
        };

        int count = 0;
        for (int i = 0; i < imgButtons.length; i++) {
            int albumIndex = startIndex + i; // Mevcut albüm indeksi
            if (albumIndex < albumList.size()) { // Albüm listesini aşmamak için kontrol
                Album album = albumList.get(albumIndex); // Albümü al

                // Albüm fotoğraf yolunu veritabanından al
                String imagePath = dbmanager.getImagePathFromDatabase(album.getTitle());

                if (imagePath != null && !imagePath.isEmpty()) {
                    // Fotoğraf yolundan dosya yolu al ve var mı diye kontrol et
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        imgButtons[i].setImageBitmap(bitmap); // Fotoğrafı ImageButton'a yerleştir
                        imgButtons[i].setContentDescription(album.getTitle());
                        count++;
                    } else {
                        imgButtons[i].setImageResource(R.drawable.default_image);
                    }
                } else {
                    imgButtons[i].setImageResource(R.drawable.default_image);
                }

                textViews[i].setText(album.getTitle());

                imgButtons[i].setVisibility(View.VISIBLE);
                textViews[i].setVisibility(View.VISIBLE);

            } else {
                // Albüm kalmadıysa, imgButton ve textView'leri gizle
                imgButtons[i].setVisibility(View.GONE);
                textViews[i].setVisibility(View.GONE);
            }

            if (count == 0){
                txtViewAlbumCount.setVisibility(View.VISIBLE);
            }
        }
    }

    public void imggoAlbumOnClick(View v) {

        ImageButton clickedButton = (ImageButton) v;
        String albumName = clickedButton.getContentDescription().toString();

        Intent intent3 = new Intent(MainActivity.this, AlbumActivity.class);
        intent3.putExtra("albumName", albumName);
        intent3.putExtra("userId", userId);

        startActivity(intent3);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goBackHomeOnClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
    }
    public void goBackProfilOnClick(View v) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
        // geçiş animasyonu
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void goBackSearchOnClick(View v) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("userId",userId);

        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setUserImage(){

        String userImagePath = dbmanager.getUserImagePath(userId);

        // ImageButton'a profil fotoğrafını yerleştiriyoruz
        ImageButton imgBtnSettings = findViewById(R.id.imgBtnSettings);

        if (userImagePath != null && !userImagePath.isEmpty()) {
            // Kullanıcı fotoğrafı dosya yolunu kontrol ediyoruz
            File imgFile = new File(userImagePath);
            if (imgFile.exists()) {
                // Fotoğrafı Bitmap olarak yükleyip ImageButton'a ayarlıyoruz
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgBtnSettings.setImageBitmap(bitmap);
            } else {
                imgBtnSettings.setImageResource(R.drawable.profileimage);
            }
        } else {
            imgBtnSettings.setImageResource(R.drawable.profileimage);
        }

    }

    private void getUserName(String userId) {

        txtViewUsername = findViewById(R.id.txtViewUsername2);

        String userName = dbmanager.getUserNameById(userId);
        if (userName != null) {
            txtViewUsername.setText("Hoşgeldin  " + userName + ",");  // TextView'e kullanıcı adını yazdırıyoruz
        } else {
            txtViewUsername.setText("Kullanıcı bulunamadı.");
        }

    }



    public void AddAlbumOnClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Albüm Ekle");


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 40);

        final EditText albumNameInput = new EditText(this);
        albumNameInput.setHint("Albüm adı giriniz");
        layout.addView(albumNameInput);

        selectedImageView = new ImageView(this);
        selectedImageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200)); // Boyutlandırma (isteğe bağlı)
        layout.addView(selectedImageView);

        final Button selectImageButton = new Button(this);
        selectImageButton.setText("Fotoğraf Seç");
        layout.addView(selectImageButton);

        builder.setView(layout);

        builder.setPositiveButton("Tamam", (dialog, which) -> {
            String albumName = albumNameInput.getText().toString().trim();
            String imagePath = selectedImagePath != null ? selectedImagePath : "defaultimg.png";

            if (albumName.isEmpty()) {
                Toast.makeText(this, "Albüm adı boş olamaz!", Toast.LENGTH_SHORT).show();
            } else {
                int userId = Integer.parseInt(getIntent().getStringExtra("userId"));



                // Albümü veritabanına ekle
                int albumId = dbmanager.addAlbum(albumName, imagePath, userId);
                if (albumId > 0) {
                    Toast.makeText(this, "Albüm başarıyla oluşturuldu.", Toast.LENGTH_SHORT).show();

                    // AddSongActivity'ye yönlendirme
                    Intent intent = new Intent(this, AddSongActivity.class);

                    intent.putExtra("userId", userId);
                    intent.putExtra("albumName", albumName);
                    intent.putExtra("albumId", albumId);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Albüm oluşturulurken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("İptal", (dialog, which) -> dialog.cancel());

        builder.show();

        // Fotoğraf seçme butonunun tıklama olayını ayarlama
        selectImageButton.setOnClickListener(v -> {
            ChoosePhoto(); // Fotoğraf seçme metodunu çağır
        });
    }

    public void ChoosePhoto() {
        Intent intent1 = new Intent(Intent.ACTION_PICK);
        intent1.setType("image/*");
        startActivityForResult(intent1, 1);
    }

    // onActivityResult metodunu ekleyin
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            selectedImagePath = saveImageToAlbumFolder(imageUri); // Fotoğrafı kaydedip yolunu al
            // Seçilen fotoğrafın ön izlemesini göster
            selectedImageView.setImageURI(Uri.parse(selectedImagePath));
        }
    }

    private String saveImageToAlbumFolder(Uri imageUri) {
        try {
            // Bitmap'i al
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            // AlbumFotoları klasörünü oluştur
            File albumFolder = new File(getFilesDir(), IMAGE_FOLDER);
            if (!albumFolder.exists()) {
                albumFolder.mkdirs();
            }

            // Fotoğrafı kaydetmek için dosya adı oluştur
            String fileName = "album_" + System.currentTimeMillis() + ".png";
            File file = new File(albumFolder, fileName);

            // Bitmap'i dosyaya kaydet
            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }

            // Kaydedilen fotoğrafın tam yolunu döndür
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}