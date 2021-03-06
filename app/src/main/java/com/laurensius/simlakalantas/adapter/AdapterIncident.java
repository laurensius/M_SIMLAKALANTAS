package com.laurensius.simlakalantas.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.laurensius.simlakalantas.R;
import com.laurensius.simlakalantas.model.Incident;

import java.util.List;

public class AdapterIncident extends RecyclerView.Adapter<AdapterIncident.HolderIncident> {
    List<Incident> listIncident;
    public AdapterIncident(List<Incident>listIncident){
        this.listIncident =listIncident;
    }

    @Override
    public HolderIncident onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_riwayat_laporan,viewGroup,false);
        HolderIncident holderIncident = new HolderIncident(v);
        return holderIncident;
    }

    @Override
    public void onBindViewHolder(HolderIncident holderIncident,int i){
        byte[] imageBytes = Base64.decode(listIncident.get(i).getImage(), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        String status_laporan = null;
        String deskripsi = null;
        if(listIncident.get(i).getLastStage() == 1){
            status_laporan = "Status laporan : menunggu respon";
        }else
        if(listIncident.get(i).getLastStage() == 2){
            status_laporan = "Status laporan : dalam proses";
        }else
        if(listIncident.get(i).getLastStage() == 3){
            status_laporan = "Status laporan : selesai";
        }

        if(listIncident.get(i).getDescription().length() > 60){
            for(int x=0;x<57;x++){
                deskripsi += listIncident.get(i).getDescription().charAt(x);
            }
            deskripsi += "...";
        }else{
            deskripsi = listIncident.get(i).getDescription();
        }
        holderIncident.ivImage.setImageBitmap(decodedImage);
        holderIncident.tvReceivedAt.setText(listIncident.get(i).getReceivedAt());
        holderIncident.tvDescription.setText(deskripsi);
        holderIncident.tvLastStage.setText(status_laporan);
    }

    @Override
    public int getItemCount(){
        return listIncident.size();
    }

    public Incident getItem(int position){
        return listIncident.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class HolderIncident extends  RecyclerView.ViewHolder{
        CardView cvRiwayatLaporan;
        ImageView ivImage;
        TextView tvReceivedAt;
        TextView tvDescription;
        TextView tvLastStage;

        HolderIncident(View itemView){
            super(itemView);
            cvRiwayatLaporan = (CardView) itemView.findViewById(R.id.cv_riwayat_laporan);
            ivImage = (ImageView)itemView.findViewById(R.id.iv_image);
            tvReceivedAt= (TextView)itemView.findViewById(R.id.tv_received_at);
            tvDescription = (TextView)itemView.findViewById(R.id.tv_description);
            tvLastStage = (TextView)itemView.findViewById(R.id.tv_last_stage);
        }
    }
}