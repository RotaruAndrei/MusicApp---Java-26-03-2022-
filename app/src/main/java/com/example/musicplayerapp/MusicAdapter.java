package com.example.musicplayerapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<String> musicList ;

    public MusicAdapter(Context mContext) {
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_music,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String filePath = musicList.get(position);
        String songTitle = filePath.substring(filePath.lastIndexOf("/") + 1);
        holder.songTitle.setText(songTitle);

        holder.cardView.setOnClickListener(onClick ->{
            Intent intent = new Intent(mContext,MusicActivity.class);
                intent.putExtra("songTitle",songTitle);
                intent.putExtra("position",position);
                intent.putExtra("filePath",filePath);
                intent.putExtra("songsList",musicList);
                mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public void setMusicList(ArrayList<String> musicList) {
        this.musicList = musicList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView songTitle;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            songTitle = itemView.findViewById(R.id.card_title);
            cardView  = itemView.findViewById(R.id.card_view_id);
        }
    }
}
