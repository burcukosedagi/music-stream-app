package com.example.app1.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.AlbumActivity;
import com.example.app1.PlaySongActivity;
import com.example.app1.R;
import com.example.app1.model.Album;

import java.io.File;
import java.util.List;
public class MainAlbumAdapter extends RecyclerView.Adapter<MainAlbumAdapter.SongViewHolder> {

    private List<Album> albumList;
    private String userId;

    // Constructor to pass the data
    public MainAlbumAdapter(List<Album> albumList, String userId) {
        this.albumList = albumList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the row layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mainalbum, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {

        Album album = albumList.get(position);
        holder.txtSong.setText(album.getTitle());


        if (album.getImagePath() != null && !album.getImagePath().isEmpty()) {
            // Dosya yolunu alıp resmi yüklemek
            File imgFile = new  File(album.getImagePath());
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.imgSong.setImageBitmap(bitmap); // Fotoğrafı ImageView'e ayarla
            } else {
                holder.imgSong.setImageResource(R.drawable.default_image);
            }
        } else {
            holder.imgSong.setImageResource(R.drawable.default_image);
        }


        // Tıklama olayını ekliyoruz
        holder.itemView.setOnClickListener(v -> {
            // Albümün verilerini al
            Intent intent = new Intent(v.getContext(), AlbumActivity.class);

            intent.putExtra("albumName", album.getTitle());
            intent.putExtra("albumFoto", album.getImagePath());
            intent.putExtra("userId", userId);
            v.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


    // ViewHolder class
    public class SongViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgSong;
        private TextView txtSong;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            imgSong = itemView.findViewById(R.id.imgSong);
            txtSong = itemView.findViewById(R.id.txtSong);
        }
    }
}