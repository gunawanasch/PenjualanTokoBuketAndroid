package com.gunawan.tokobuket.penjualan.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gunawan.tokobuket.penjualan.R;
import com.gunawan.tokobuket.penjualan.api.ApiClient;
import com.gunawan.tokobuket.penjualan.model.Barang;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PemesananAdapter extends RecyclerView.Adapter<PemesananAdapter.ViewHolder> {
    private ArrayList<Barang> barang;
    private Activity activity;
    private Context context;
    private OnCheckedChangeListener checkedListener;
    private OnEditTextChangeListener editTextListener;

    public PemesananAdapter(Activity activity, ArrayList<Barang> barang) {
        this.barang = barang;
        this.activity = activity;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChange(int position, int idBarang, int harga, String jumlah, boolean checkedBarang);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) { this.checkedListener = listener; }

    public interface OnEditTextChangeListener {
        void onEditTextChange(int position, int idBarang, int harga, String jumlah, boolean checkedBarang);
    }

    public void setOnEditTextChangeListener(OnEditTextChangeListener listener) { this.editTextListener = listener; }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.row_pemesanan, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PemesananAdapter.ViewHolder viewHolder, final int position) {
        Picasso.with(viewHolder.ivBarang.getContext()).load(ApiClient.IMAGE_URL+"assets/images_barang/"+barang.get(position).getFoto()).into(viewHolder.ivBarang);
        viewHolder.tvNama.setText(barang.get(position).getNama());
        Locale locale = new java.util.Locale("id");
        if(locale == null) {
            viewHolder.tvHarga.setText(NumberFormat.getNumberInstance(Locale.US).format(barang.get(position).getHarga()));
        }
        else {
            viewHolder.tvHarga.setText("Rp "+NumberFormat.getNumberInstance(locale).format(barang.get(position).getHarga()));
        }

        viewHolder.cbBarang.setChecked(false);
        viewHolder.etBarang.setVisibility(View.GONE);
        viewHolder.cbBarang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    barang.get(position).setCheckedBarang(isChecked);
                    viewHolder.cbBarang.setChecked(true);
                    viewHolder.etBarang.setVisibility(View.VISIBLE);
                    viewHolder.etBarang.setText("0");
                    checkedListener.onCheckedChange(position, barang.get(position).getIdBarang(), barang.get(position).getHarga(),
                            viewHolder.etBarang.getText().toString(), isChecked);
                }
                else {
                    barang.get(position).setCheckedBarang(isChecked);
                    viewHolder.cbBarang.setChecked(false);
                    viewHolder.etBarang.setVisibility(View.GONE);
                    checkedListener.onCheckedChange(position, barang.get(position).getIdBarang(), barang.get(position).getHarga(),
                            viewHolder.etBarang.getText().toString(), isChecked);
                }
            }
        });
        viewHolder.cbBarang.setChecked(barang.get(position).isCheckedBarang());

        viewHolder.etBarang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editTextListener.onEditTextChange(position, barang.get(position).getIdBarang(), barang.get(position).getHarga(),
                        viewHolder.etBarang.getText().toString(), true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != barang ? barang.size() : 0);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivBarang;
        private TextView tvNama, tvHarga;
        private CheckBox cbBarang;
        private EditText etBarang;
        private View cardView;

        public ViewHolder(View view) {
            super(view);
            ivBarang = (ImageView) view.findViewById(R.id.ivBarang);
            tvNama = (TextView) view.findViewById(R.id.tvNama);
            tvHarga = (TextView) view.findViewById(R.id.tvHarga);
            cbBarang = (CheckBox) view.findViewById(R.id.cbBarang);
            etBarang = (EditText) view.findViewById(R.id.etBarang);
            cardView = (CardView) view.findViewById(R.id.cardView);
        }
    }
}
