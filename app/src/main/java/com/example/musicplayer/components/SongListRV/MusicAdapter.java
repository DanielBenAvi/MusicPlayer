package com.example.musicplayer.components.SongListRV;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private final List<SongListItem> songList;
    private final OnItemClickListener listener;

    private int selectedPosition = RecyclerView.NO_POSITION;

    public MusicAdapter(List<SongListItem> songList, OnItemClickListener listener) {
        this.songList = songList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusicAdapter.MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_item, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MusicViewHolder holder, int position) {
        SongListItem songListItem = songList.get(position);
        holder.songTitle.setText(songListItem.getSongName());


        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.selected_item));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.default_item));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(songListItem, holder.getAdapterPosition());
                notifyItemChanged(selectedPosition);
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(selectedPosition);
            } else {
                Toast.makeText(v.getContext(), "No listener set", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;

        public MusicViewHolder(@NonNull View ItemView) {
            super(ItemView);
            songTitle = ItemView.findViewById(R.id.textViewMusicName);
        }
    }

}
