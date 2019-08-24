package com.gunawan.tokobuket.penjualan.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gunawan.tokobuket.penjualan.R;
import com.gunawan.tokobuket.penjualan.api.ApiClient;
import com.gunawan.tokobuket.penjualan.model.Barang;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.ViewHolder> {
    private ArrayList<Barang> barang;
    private Activity activity;
    private Context context;
    private PemesananAdapter.OnCheckedChangeListener checkedListener;
    private PemesananAdapter.OnEditTextChangeListener editTextListener;

    public BarangAdapter(Activity activity, ArrayList<Barang> barang) {
        this.barang = barang;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.row_barang, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BarangAdapter.ViewHolder viewHolder, final int position) {
        Picasso.with(viewHolder.ivBarang.getContext()).load(ApiClient.IMAGE_URL+"assets/images_barang/"+barang.get(position).getFoto()).into(viewHolder.ivBarang);
        viewHolder.tvNama.setText(barang.get(position).getNama());
        Locale locale = new java.util.Locale("id");
        if(locale == null) {
            viewHolder.tvHarga.setText(NumberFormat.getNumberInstance(Locale.US).format(barang.get(position).getHarga()));
        }
        else {
            viewHolder.tvHarga.setText("Rp "+NumberFormat.getNumberInstance(locale).format(barang.get(position).getHarga()));
        }
    }

    @Override
    public int getItemCount() {
        return (null != barang ? barang.size() : 0);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivBarang;
        private TextView tvNama, tvHarga;
        private View cardView;

        public ViewHolder(View view) {
            super(view);
            ivBarang = (ImageView) view.findViewById(R.id.ivBarang);
            tvNama = (TextView) view.findViewById(R.id.tvNama);
            tvHarga = (TextView) view.findViewById(R.id.tvHarga);
            cardView = (CardView) view.findViewById(R.id.cardView);
        }
    }
}
