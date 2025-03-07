package com.example.app1.database;

import static com.example.app1.database.DatabaseHelper.COLUMN_USER_ID;
import static com.example.app1.database.DatabaseHelper.COLUMN_USER_NAME;
import static com.example.app1.database.DatabaseHelper.TABLE_USER;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.app1.model.Album;
import com.example.app1.model.Song;
import com.example.app1.model.SongWithId;
import com.example.app1.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    public DatabaseHelper dbHelper;

    private static final String TABLE_SONG = "Song";
    private static final String COLUMN_SONG_ID = "id";
    private static final String COLUMN_SONG_TITLE = "title";
    private static final String COLUMN_SONG_ARTIST = "artist";
    private static final String COLUMN_SONG_CATEGORY_ID = "categoryId";
    private static final String COLUMN_SONG_DURATION = "duration";
    private static final String COLUMN_SONG_FILEPATH = "filePath";
    private static final String COLUMN_SONG_IMAGEPATH = "imagePath";

    public DatabaseManager(Context context) {

        dbHelper = new DatabaseHelper(context);
    }

    public void addUser(String userName, String email, String password, String gender, String birthDate) throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("UserName", userName);
        values.put("email", email);
        values.put("password", password);
        values.put("gender", gender);
        values.put("birthDate", birthDate);

        long result = db.insert("User", null, values);
        db.close();

        if (result == -1) {
            throw new Exception("Kullanıcı kaydı yapılamadı. Email adresi zaten mevcut olabilir.");
        }
    }

    // Albüm ekleme
    public int addAlbum(String title, String imagePath, int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("imagePath", imagePath);
        values.put("userId", userId);

        long result = db.insert("Album", null, values);
        return (int) result; // long -> int dönüşümü
    }

    public boolean addSongToAlbum(int albumId, int songId, int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Albümde bu şarkı zaten var mı kontrol et
        String query = "SELECT * FROM AlbumDetail WHERE albumId = ? AND songId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(albumId), String.valueOf(songId)});

        boolean isAlreadyExists = cursor.moveToFirst();
        cursor.close();

        if (isAlreadyExists) {
            db.close();
            return false;
        }

        // Şarkı albümde yoksa ekle
        ContentValues values = new ContentValues();
        values.put("albumId", albumId);
        values.put("songId", songId);

        long result = db.insert("AlbumDetail",null, values);
        db.close();
        return result != -1;
    }


    // Sarkı ekleme yaparken kullanıldı
    public void addSong(String title, String artist, int categoryId, int duration, String filePath, String imagePath) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("artist", artist);
        values.put("categoryId", categoryId);
        values.put("duration", duration); // Saniye cinsinden
        values.put("filePath", filePath);
        values.put("imagePath", imagePath);
        long result = db.insert("Song", null, values);

        if (result == -1) {
            System.out.println("Şarkı eklenirken bir hata oluştu!");
        } else {
            System.out.println("Şarkı başarıyla eklendi: " + title);
        }

        db.close();
    }


    // Kullanıcıları listele
    public String getAllUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM User", null);
        StringBuilder sb = new StringBuilder("Kullanıcılar:\n");
        while (cursor.moveToNext()) {
            sb.append(cursor.getString(1)).append(" ").append(cursor.getString(2)).append(" - ").append(cursor.getString(3)).append("\n");
        }
        cursor.close();
        db.close();
        return sb.toString();
    }


    // Tüm şarkıları alma
    public List<Song> getAllSongs() {
        List<Song> songList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_SONG;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Song song = new Song(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_ARTIST)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SONG_CATEGORY_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SONG_DURATION)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_FILEPATH)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_IMAGEPATH))
                    );
                    songList.add(song);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return songList;
    }

    // Tüm şarkıları ID ile alma
    public List<SongWithId> getAllSongsWithId() {
        List<SongWithId> songList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_SONG;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    SongWithId song = new SongWithId(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SONG_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_ARTIST)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SONG_DURATION)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_FILEPATH)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_IMAGEPATH))
                    );
                    songList.add(song);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return songList;
    }

    // Belirli bir kategoriId'ye sahip şarkıları döndüren method
    private List<Song> songList;
    public List<Song> getSongsByCategoryId(int categoryId) {

        List<Song> filteredSongs = new ArrayList<>();
        songList = getAllSongs();

        for (Song song : songList) {
            if (song.getCategoryId() == categoryId) {
                filteredSongs.add(song);
            }
        }
        return filteredSongs;
    }

    public List<Album> getAlbumsByUserId(int userId) {
        List<Album> albumList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Album WHERE userId = ?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Album album = new Album();
                album.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                album.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                album.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow("imagePath")));
                album.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow("releaseDate")));
                album.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
                albumList.add(album);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return albumList;
    }

    public int getCategoryIdByName(String categoryName) {
        int categoryId = -1;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM Category WHERE name = ?",
                new String[]{categoryName}); // Kategori adıyla sorgula

        if (cursor.moveToFirst()) {
            categoryId = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return categoryId;
    }

    public boolean checkUserCredentials(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER +
                        " WHERE " + DatabaseHelper.COLUMN_USER_EMAIL + " = ? AND " + DatabaseHelper.COLUMN_USER_PASSWORD + " = ?",
                new String[]{email, password});

        boolean userExists = cursor != null && cursor.moveToFirst();
        cursor.close();
        db.close();
        return userExists;
    }

    public String getUserIdByEmailAndPassword(DatabaseManager databaseManager, String email, String password) {
        SQLiteDatabase db = databaseManager.dbHelper.getReadableDatabase();
        String userId = null;

        // SQL sorgusu
        String query = "SELECT id FROM User WHERE Email = ? AND Password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        if (cursor.moveToFirst()) {
            userId = cursor.getString(cursor.getColumnIndexOrThrow("id"));
        }
        cursor.close();
        db.close();

        return userId;
    }

    public List<String> getSongIdsByUserIdAndAlbumName(DatabaseManager databaseManager, String userId, String albumName) {
        SQLiteDatabase db = databaseManager.dbHelper.getReadableDatabase();
        List<String> songIds = new ArrayList<>();

        // SQL sorgusu: Kullanıcı ve albüm adıyla eşleşen şarkıların ID'lerini al
        String query = "SELECT s.id FROM Song s " +
                "JOIN AlbumDetail ad ON s.id = ad.songId " +
                "JOIN Album a ON ad.albumId = a.id " +
                "WHERE a.userId = ? AND a.title = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userId, albumName});

        // Cursor ile verileri al
        while (cursor.moveToNext()) {
            String songId = cursor.getString(cursor.getColumnIndexOrThrow("id")); // Song tablosundaki id sütunu
            songIds.add(songId);
        }
        // Kaynakları kapat
        cursor.close();
        db.close();

        return songIds;
    }
    public List<SongWithId> getSongsBySongIds(List<String> songIds) {

        List<SongWithId> filteredSongs = new ArrayList<>();

        List<SongWithId> allSongsWithId = getAllSongsWithId();

        for (SongWithId song : allSongsWithId) {

            String songId = String.valueOf(song.getId());

            if (songIds.contains(songId)) {
                filteredSongs.add(song);
            }
        }
        return filteredSongs;
    }


    @SuppressLint("Range")
    public String getImagePathFromDatabase(String albumName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT imagePath FROM Album WHERE title = ?";
        Cursor cursor = db.rawQuery(query, new String[]{albumName});
        String imagePath = null;

        if (cursor != null && cursor.moveToFirst()) {
            imagePath = cursor.getString(cursor.getColumnIndex("imagePath"));
            cursor.close();
        }
        return imagePath;
    }


    public String getUserImagePath(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("User", new String[]{"imagePath"}, "Id=?", new String[]{userId}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("imagePath"));
            cursor.close();
            return imagePath;
        }
        return null;
    }


    public String getUserNameById(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT " + COLUMN_USER_NAME + " FROM " + TABLE_USER + " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        String userName = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                userName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME));
            }
            cursor.close();
        }
        db.close();
        return userName;
    }

    public void deleteSongFromPlaylist(int songId, String AlbumId) {
        int albumId = Integer.parseInt(AlbumId);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete("albumDetail", "songId = ? AND albumId = ?",
                new String[]{String.valueOf(songId), String.valueOf(albumId)
                });
        db.close();
    }

    public void updatePlaylistAfterDeletion(int albumId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String countQuery = "SELECT COUNT(*) FROM AlbumDetail WHERE albumId = ?";
        Cursor cursor = db.rawQuery(countQuery, new String[]{String.valueOf(albumId)});

        int songCount = 0;
        if (cursor.moveToFirst()) {
            songCount = cursor.getInt(0);
        }
        cursor.close();

        // Albümdeki şarkı sayısı sıfırsa, albümü silebiliriz veya başka bir işlem yapabiliriz
        if (songCount == 0) {
            // Albümde hiç şarkı kalmadığında albümü silebilirsiniz
            db.delete("Album", "id = ?", new String[]{String.valueOf(albumId)});
        }

        db.close();
    }

    public int getAlbumIdByTitle(String albumTitle) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int albumId = -1;

        Cursor cursor = null;
        try {
            String query = "SELECT id FROM Album WHERE title = ?";
            cursor = db.rawQuery(query, new String[]{albumTitle});

            if (cursor != null && cursor.moveToFirst()) {
                albumId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return albumId;
    }

    public User getUserById(String userId) {
        User user = null;
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM User WHERE id = ?", new String[]{userId});
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("UserName")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password")),
                    cursor.getString(cursor.getColumnIndexOrThrow("gender")),
                    cursor.getString(cursor.getColumnIndexOrThrow("birthDate")),
                    cursor.getString(cursor.getColumnIndexOrThrow("imagePath"))
            );
        }
        cursor.close();
        return user;
    }


    public boolean updateUser(int userId, String userName, String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userName", userName);
        contentValues.put("email", email);
        contentValues.put("password", password);

        int rowsUpdated = db.update("user", contentValues, "id = ?", new String[]{String.valueOf(userId)});
        return rowsUpdated > 0;
    }

    public boolean updateUserPhoto(int userId, String photoPath) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("imagePath", photoPath);

        int rowsUpdated = db.update("User", contentValues, "id = ?", new String[]{String.valueOf(userId)});
        return rowsUpdated > 0;
    }
}