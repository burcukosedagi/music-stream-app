package com.example.app1.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.AlbumActivity;
import com.example.app1.R;
import com.example.app1.model.Album;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private List<Album> albumList;
    private String userId;
    private OnAlbumClickListener listener;

    public interface OnAlbumClickListener {
        void onAlbumClick(int albumId); // Tıklanan albümün ID'sini alacak metod
    }

    public AlbumAdapter(List<Album> albumList, String userId) {
        this.albumList = albumList;
        this.userId = userId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_albumsong, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.titleTextView.setText(album.getTitle());

        if (album.getImagePath() != null && !album.getImagePath().isEmpty()) {
            int imageResource = holder.itemView.getContext()
                    .getResources()
                    .getIdentifier(album.getImagePath(), "drawable", holder.itemView.getContext().getPackageName());
            if (imageResource != 0) {
                holder.albumImageView.setImageResource(imageResource);
            } else {
                holder.albumImageView.setImageResource(R.drawable.default_image);
            }
        } else {
            holder.albumImageView.setImageResource(R.drawable.default_image);
        }


        holder.itemView.setOnClickListener(v -> {
            // Albümün verilerini al
            Intent intent = new Intent(v.getContext(), AlbumActivity.class);

            intent.putExtra("albumName", album.getTitle());
            intent.putExtra("albumFoto", album.getImagePath());
            intent.putExtra("userId", userId);
            v.getContext().startActivity(intent);
            listener.onAlbumClick(album.getId());
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView albumImageView;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.txtSong);
            albumImageView = itemView.findViewById(R.id.imgSong);
        }
    }

}