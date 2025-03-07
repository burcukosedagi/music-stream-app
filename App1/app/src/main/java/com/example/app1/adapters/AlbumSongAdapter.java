package com.example.app1.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.PlaySongActivity;
import com.example.app1.R;
import com.example.app1.database.DatabaseManager;
import com.example.app1.model.SongWithId;
import java.util.List;
public class AlbumSongAdapter extends RecyclerView.Adapter<AlbumSongAdapter.SongViewHolder> {

    private List<SongWithId> songList;
    private OnItemClickListener onItemClickListener;
    private String userId, albumName, albumId;

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(SongWithId song);
    }


    public AlbumSongAdapter(List<SongWithId> songList, OnItemClickListener listener, String userId, String albumName, String albumId) {
        this.songList = songList;
        this.onItemClickListener = listener;
        this.userId = userId;
        this.albumName = albumName;
        this.albumId = albumId;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_albumsong, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {

        SongWithId song = songList.get(position);
        holder.txtSong.setText(song.getTitle());
        holder.txtSinger.setText(song.getArtist());

        // Resim yolu kontrolü
        if (song.getImagePath() != null && !song.getImagePath().isEmpty()) {
            int imageResource = holder.itemView.getContext()
                    .getResources()
                    .getIdentifier(song.getImagePath(), "drawable", holder.itemView.getContext().getPackageName());
            if (imageResource != 0) {
                holder.imgSong.setImageResource(imageResource);
            } else {
                // Varsayılan resim
                holder.imgSong.setImageResource(R.drawable.default_image);
            }
        } else {
            // Varsayılan resim
            holder.imgSong.setImageResource(R.drawable.default_image);
        }


        // Song item'ına tıklama olayını ekliyoruz
        holder.itemView.setOnClickListener(v -> {

            String songFilePath = song.getFilePath();
            Intent intent = new Intent(v.getContext(), PlaySongActivity.class);

            intent.putExtra("songName", song.getTitle());
            intent.putExtra("artistName", song.getArtist());
            intent.putExtra("filePath", songFilePath);
            intent.putExtra("imgPath", song.getImagePath());
            intent.putExtra("userId", userId);
            intent.putExtra("albumName", albumName);
            intent.putExtra("albumId", albumId);

            v.getContext().startActivity(intent);
        });


        holder.btnSongProperty.setOnClickListener(v -> {
            // AlertDialog oluşturuluyor
            new android.app.AlertDialog.Builder(v.getContext())
                    .setTitle(song.getTitle() +", silmek istediğinden emin misin?")
                    .setPositiveButton("Evet", (dialog, which) -> {


                        // Veritabanından şarkıyı sil
                        DatabaseManager dbManager = new DatabaseManager(v.getContext());
                        dbManager.deleteSongFromPlaylist(song.getId(), albumId);

                        songList.remove(holder.getAdapterPosition());

                        // RecyclerView'e bu değişikliği bildir
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(), songList.size());

                        dbManager.updatePlaylistAfterDeletion(Integer.parseInt(albumId));
                    })
                    .setNegativeButton("Hayır", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }


    // ViewHolder class
    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong;
        TextView txtSinger;
        TextView txtSong;
        ImageButton btnSongProperty;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            imgSong = itemView.findViewById(R.id.imgSong);
            txtSinger = itemView.findViewById(R.id.txtSinger);
            txtSong = itemView.findViewById(R.id.txtSong);
            btnSongProperty = itemView.findViewById(R.id.btnSongProperty);
        }
    }

    public void updateSongs(List<SongWithId> newSongs) {
        this.songList.clear();
        this.songList.addAll(newSongs);
        notifyDataSetChanged();
    }
}