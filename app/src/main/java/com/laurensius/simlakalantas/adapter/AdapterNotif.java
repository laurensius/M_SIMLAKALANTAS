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
import com.laurensius.simlakalantas.model.Notif;

import java.util.List;

public class AdapterNotif extends RecyclerView.Adapter<AdapterNotif.HolderNotif> {
    List<Notif> listNotif;
    public AdapterNotif(List<Notif>listNotif){
        this.listNotif =listNotif;
    }

    @Override
    public HolderNotif onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_pemberitahuan,viewGroup,false);
        HolderNotif holderNotif = new HolderNotif(v);
        return holderNotif;
    }

    @Override
    public void onBindViewHolder(HolderNotif holderNotif,int i){
        holderNotif.tvContent.setText(listNotif.get(i).getContent());
        holderNotif.tvDatetime.setText(listNotif.get(i).getDatetime());
    }

    @Override
    public int getItemCount(){
        return listNotif.size();
    }

    public Notif getItem(int position){
        return listNotif.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class HolderNotif extends  RecyclerView.ViewHolder{
        CardView cvNotif;
        TextView tvContent;
        TextView tvDatetime;

        HolderNotif(View itemView){
            super(itemView);
            cvNotif = (CardView) itemView.findViewById(R.id.cv_pemberitahuan);
            tvContent = (TextView)itemView.findViewById(R.id.tv_datetime);
            tvDatetime = (TextView)itemView.findViewById(R.id.tv_content);
        }
    }
}