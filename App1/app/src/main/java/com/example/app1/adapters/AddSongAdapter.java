package com.example.app1.adapters;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app1.AlbumActivity;
import com.example.app1.R;
import com.example.app1.database.DatabaseManager;
import com.example.app1.model.SongWithId;

import java.util.List;
//ınharıtance ornegı ust classın methodlarını mıras alır
public class AddSongAdapter extends RecyclerView.Adapter<AddSongAdapter.SongViewHolder> {

    private List<SongWithId> songList;
    private int userId;
    private String albumName, albumId;
    private OnAddToListClickListener onAddToListClickListener;

    // Constructor
    public AddSongAdapter(List<SongWithId> songList, OnAddToListClickListener listener, int userId, String albumName, String albumId) {
        this.songList = songList;
        this.onAddToListClickListener = listener;
        this.userId = userId;
        this.albumName = albumName;
        this.albumId = albumId;
    }
 //alttakı uc overrıde polymorphızm ornegıdır
    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_addplaylist, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        SongWithId song = songList.get(position);
        holder.singerText.setText(song.getArtist());
        holder.songText.setText(song.getTitle());

        // Resim yolu kontrolü
        if (song.getImagePath() != null && !song.getImagePath().isEmpty()) {
            int imageResource = holder.itemView.getContext()
                    .getResources()
                    .getIdentifier(song.getImagePath(), "drawable", holder.itemView.getContext().getPackageName());
            if (imageResource != 0) {
                holder.songImage.setImageResource(imageResource);
            } else {
                holder.songImage.setImageResource(R.drawable.default_image);
            }
        } else {
            holder.songImage.setImageResource(R.drawable.default_image);
        }

        holder.addToListButton.setOnClickListener(v -> {
            if (onAddToListClickListener != null) {
                // Listener tetikleniyor
                onAddToListClickListener.onAddToListClick(song);
            }

            DatabaseManager dbManager = new DatabaseManager(v.getContext());

            boolean isAdded = dbManager.addSongToAlbum(Integer.parseInt(albumId), song.getId(), userId);

            if(isAdded){
                Toast.makeText(v.getContext(), "Şarkı albüme eklendi!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(v.getContext(), AlbumActivity.class);
                intent.putExtra("albumId", albumId);

                intent.putExtra("userId", userId);

                if (userId == -1) {
                    String userIdString = intent.getStringExtra("userId");
                    userId = Integer.parseInt(userIdString);
                }


                intent.putExtra("albumName", albumName);

                v.getContext().startActivity(intent);

            }else{
                Toast.makeText(v.getContext(), "Şarkı zaten albüme eklenmiş!", Toast.LENGTH_SHORT).show();
            }
        });}

    @Override
    public int getItemCount() {
        return songList.size();
    }

    // Şarkı listesine yeni filtrelenmiş listeyi set eden metot
    public void updateSongList(List<SongWithId> newSongList) {
        this.songList = newSongList;
        notifyDataSetChanged();  // RecyclerView'i güncelliyoruz
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        public TextView singerText, songText;
        public ImageView songImage;
        public ImageButton addToListButton;

        public SongViewHolder(View view) {
            super(view);
            singerText = view.findViewById(R.id.txtSinger);
            songText = view.findViewById(R.id.txtSong);
            songImage = view.findViewById(R.id.imgSong);
            addToListButton = view.findViewById(R.id.btnAddToList);
        }
    }

    // OnAddToListClickListener interface'i, "Add to List" butonuna tıklanması için
    public interface OnAddToListClickListener {
        void onAddToListClick(SongWithId song);
    }
}
