package com.example.app1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.app1.model.Song;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import com.example.app1.model.User;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MusicLibrary.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_USER = "User";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "UserName";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_GENDER = "gender";
    public static final String COLUMN_USER_BIRTHDATE = "birthDate";

    public static final String COLUMN_USER_IMAGEPATH = "imagePath";
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        copyDatabase();

        db.execSQL("CREATE TABLE User (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "UserName TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "gender TEXT NOT NULL, " +
                "birthDate TEXT NOT NULL, " +
                "memberSince DATE DEFAULT CURRENT_DATE, " +
                "lastUpdated DATE DEFAULT CURRENT_DATE, " +
                "imagePath TEXT)");

        db.execSQL("CREATE TABLE Album (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "imagePath TEXT, " +
                "releaseDate DATE DEFAULT CURRENT_DATE, " +
                "userId INTEGER, " +
                "FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE Song (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "artist TEXT NOT NULL, " +
                "categoryId INTEGER, " +
                "duration INTEGER, " +   //BUNU SANİYE CİNSİNDEN ALIYORUZ ARDINDAN DAKİKASINI HESAPLAYABİLİYORUZ.
                "filePath TEXT, " +
                "imagePath TEXT, " +
                "updateTime DATE DEFAULT CURRENT_DATE, " +
                "addedTime DATE DEFAULT CURRENT_DATE, " +
                "FOREIGN KEY (categoryId) REFERENCES Category(id) ON DELETE SET NULL)");

        db.execSQL("CREATE TABLE Category (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL)");

        db.execSQL("CREATE TABLE AlbumDetail (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "albumId INTEGER, " +
                "songId INTEGER, " +
                "FOREIGN KEY (albumId) REFERENCES Album(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (songId) REFERENCES Song(id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS AlbumDetail");
        db.execSQL("DROP TABLE IF EXISTS Song");
        db.execSQL("DROP TABLE IF EXISTS Album");
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Category");
        onCreate(db);
    }

    public void deleteAlbum(int albumId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Album", "id = ?", new String[]{String.valueOf(albumId)});
        db.close();
    }

    public void updateAlbumName(int albumId, String newAlbumName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", newAlbumName);

        // Albüm ID'sine göre güncelleme işlemi yap
        db.update("Album", values, "id = ?", new String[]{String.valueOf(albumId)});
        db.close();
    }



    public void copyDatabase() {


        // Veritabanının hedef yolu (Android uygulamanızın özel veritabanı dizini)
        File databaseFile = context.getDatabasePath(DATABASE_NAME);

        // Eğer veritabanı dosyası mevcut ancak içeriği boşsa, kopyalamayı yap
        if (databaseFile.exists() && databaseFile.length() > 0) {
            Log.d("Database Copy", "Veritabanı zaten mevcut ve geçerli.");
            return;  // Eğer veritabanı zaten varsa ve içeriği doğruysa, kopyalama yapılmaz
        }



        // Veritabanı dosyasını assets'ten kopyalama işlemi
        try (InputStream inputStream = context.getAssets().open(DATABASE_NAME);
             OutputStream outputStream = new FileOutputStream(databaseFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (databaseFile.exists()) {
            Log.d("Database Copy", "Veritabanı başarıyla kopyalandı.");
        } else {
            Log.d("Database Copy", "Veritabanı kopyalanamadı.");
        }

    }

}