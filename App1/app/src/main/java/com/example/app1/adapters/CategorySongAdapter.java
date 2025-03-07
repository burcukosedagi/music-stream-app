package com.example.app1.adapters;

import com.example.app1.PlaySongActivity;
import com.example.app1.R;
import com.example.app1.model.Song;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategorySongAdapter extends RecyclerView.Adapter<CategorySongAdapter.SongViewHolder> {

    private List<Song> songList;
    private String userId;
    private String categoryName;

    public CategorySongAdapter(List<Song> songList, String userId, String categoryName)
    {
        this.songList = songList;
        this.userId = userId;
        this.categoryName = categoryName;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.txtSongName.setText(song.getTitle());
        holder.txtSingerName.setText(song.getArtist());


        if (song.getImagePath() != null && !song.getImagePath().isEmpty()) {
            int imageResource = holder.itemView.getContext()
                    .getResources()
                    .getIdentifier(song.getImagePath(), "drawable", holder.itemView.getContext().getPackageName());
            if (imageResource != 0) {
                holder.imgBtnSong.setImageResource(imageResource);
            } else {
                holder.imgBtnSong.setImageResource(R.drawable.default_image);
            }
        } else {
            holder.imgBtnSong.setImageResource(R.drawable.default_image);
        }

        holder.imgBtnSong.setOnClickListener(v -> {

            String songFilePath = song.getFilePath();
            Intent intent = new Intent(v.getContext(), PlaySongActivity.class);

            intent.putExtra("songName", song.getTitle());
            intent.putExtra("artistName", song.getArtist());
            intent.putExtra("filePath", songFilePath);
            intent.putExtra("imgPath", song.getImagePath());
            intent.putExtra("categoryName", categoryName);
            intent.putExtra("userId", userId);

            v.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    // Listeyi güncelleyen metot
    public void updateList(List<Song> filteredList) {
        songList.clear();
        songList.addAll(filteredList);
        notifyDataSetChanged(); // RecyclerView'i yenile
    }


    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView txtSongName, txtSingerName;
        ImageButton imgBtnSong;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSongName = itemView.findViewById(R.id.txtSongName);
            txtSingerName = itemView.findViewById(R.id.txtSingerName);
            imgBtnSong = itemView.findViewById(R.id.imgBtnSong);
        }
    }
}