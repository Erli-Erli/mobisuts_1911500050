package com.example.utsmobis_1911500050.adapter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.utsmobis_1911500050.ClassGlobal;
import com.example.utsmobis_1911500050.MainActivity;
import com.example.utsmobis_1911500050.R;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PengumumanAdapter extends RecyclerView.Adapter<PengumumanAdapter.ViewHolder> {
    Context context;
    ArrayList<HashMap<String, String>>list_data;
    public PengumumanAdapter(MainActivity mainAct, ArrayList<HashMap<String, String>> list_data){
        this.context = mainAct;
        this.list_data = list_data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pengumuman_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PengumumanAdapter.ViewHolder holder, int position) {
        String cover_pengumuman =list_data.get(position).get("cover");
        if(cover_pengumuman.substring(0,16).equalsIgnoreCase("http://127.0.0.1")){
            //Ganti 127.0.0.1 dari database dengan 10.0.2.2 atau IP v4 server
            cover_pengumuman = cover_pengumuman.replace(
                    "http://127.0.0.1", ClassGlobal.global_ipaddress);
        }
        DrawableCrossFadeFactory factory =
                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        Glide.with(context)
                .load(cover_pengumuman)
                .transition(withCrossFade(factory))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_menu_camera) //loading gambar
                .error(R.drawable.ic_blokir) //jika ada kesalahan memuat gambar
                .into(holder.img);

        holder.tvJdlPengumuman.setText(list_data.get(holder.getAdapterPosition()).get("judul"));
        holder.tvTglPengumuman.setText(list_data.get(holder.getAdapterPosition()).get("tglPost"));
        holder.tvDeskripsi.setText(list_data.get(holder.getAdapterPosition()).get("desc"));
    }

    @Override
    public int getItemCount(){ return list_data.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvJdlPengumuman, tvTglPengumuman, tvDeskripsi;

        public ViewHolder(View itemView){
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.imgPengumuman);
            tvJdlPengumuman = (TextView) itemView.findViewById(R.id.titlePengumuman);
            tvTglPengumuman = (TextView) itemView.findViewById(R.id.tvTglPost);
            tvDeskripsi = (TextView) itemView.findViewById(R.id.tvDesc);
        }
    }
} // akhir class Promo Adapter