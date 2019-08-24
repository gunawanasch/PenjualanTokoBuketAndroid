package com.gunawan.tokobuket.penjualan.adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gunawan.tokobuket.penjualan.R;
import com.gunawan.tokobuket.penjualan.activity.DetailPelangganActivity;
import com.gunawan.tokobuket.penjualan.activity.PemesananActivity;
import com.gunawan.tokobuket.penjualan.model.Pelanggan;

import java.util.ArrayList;

public class PelangganAdapter extends RecyclerView.Adapter<PelangganAdapter.ViewHolder> {
    private ArrayList<Pelanggan> pelanggan;
    private Activity activity;
    private boolean isFromPemesanan;

    public PelangganAdapter(Activity activity, ArrayList<Pelanggan> pelanggan, boolean isFromPemesanan) {
        this.pelanggan = pelanggan;
        this.activity = activity;
        this.isFromPemesanan = isFromPemesanan;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.row_pelanggan, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PelangganAdapter.ViewHolder viewHolder, int position) {
        viewHolder.tvNama.setText(pelanggan.get(position).getNama());
        viewHolder.tvNoTelp.setText(pelanggan.get(position).getNoTelp());
        viewHolder.cardView.setOnClickListener(onClickListener(position));
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFromPemesanan) {
                    Intent i = new Intent(v.getContext(), PemesananActivity.class);
                    i.putExtra("idPelanggan", pelanggan.get(position).getIdPelanggan());
                    i.putExtra("namaPelanggan", pelanggan.get(position).getNama());
                    v.getContext().startActivity(i);
                }
                else {
                    Intent i = new Intent(v.getContext(), DetailPelangganActivity.class);
                    i.putExtra("idPelanggan", pelanggan.get(position).getIdPelanggan());
                    v.getContext().startActivity(i);
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return (null != pelanggan ? pelanggan.size() : 0);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNama, tvNoTelp;
        private View cardView;

        public ViewHolder(View view) {
            super(view);
            tvNama = (TextView) view.findViewById(R.id.tvNama);
            tvNoTelp = (TextView) view.findViewById(R.id.tvNoTelp);
            cardView = (CardView) view.findViewById(R.id.cardView);
        }
    }
}
