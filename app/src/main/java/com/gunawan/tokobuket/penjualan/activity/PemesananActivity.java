package com.gunawan.tokobuket.penjualan.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gunawan.tokobuket.penjualan.R;
import com.gunawan.tokobuket.penjualan.adapter.PemesananAdapter;
import com.gunawan.tokobuket.penjualan.api.ApiClient;
import com.gunawan.tokobuket.penjualan.api.ApiInterface;
import com.gunawan.tokobuket.penjualan.model.Barang;
import com.gunawan.tokobuket.penjualan.model.Pemesanan;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PemesananActivity extends AppCompatActivity {
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private ProgressBar pbBarang;
    private RecyclerView rvBarang;
    private LinearLayout llKirim;
    private TextView tvTotalHarga;
    private Button btnKirim;
    private PemesananAdapter adapter;
    private ArrayList<Barang> listBarang = new ArrayList<Barang>();
    private ArrayList<Pemesanan> listPemesanan = new ArrayList<Pemesanan>();
    private String idKaryawan, namaPelanggan;
    private int idPelanggan, totalHarga = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemesanan);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        pbBarang = (ProgressBar) findViewById(R.id.pbBarang);
        rvBarang = (RecyclerView) findViewById(R.id.rvBarang);
        llKirim = (LinearLayout) findViewById(R.id.llKirim);
        tvTotalHarga = (TextView) findViewById(R.id.tvTotalHarga);
        btnKirim = (Button) findViewById(R.id.btnKirim);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rvBarang.setHasFixedSize(true);
        rvBarang.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        pbBarang.setVisibility(View.VISIBLE);
        pbBarang.setVisibility(View.GONE);
        llKirim.setVisibility(View.GONE);

        Intent i = getIntent();
        idPelanggan = i.getIntExtra("idPelanggan", 0);
        namaPelanggan = i.getStringExtra("namaPelanggan");
        SharedPreferences sp = this.getSharedPreferences("userLogin", Context.MODE_PRIVATE);
        idKaryawan = sp.getString("idKaryawan", "");
        getBarang();
    }

    private void getBarang() {
        ApiInterface api = new ApiClient().getClient().create(ApiInterface.class);
        Call<ArrayList<Barang>> call = api.getBarang(idKaryawan);
        call.enqueue(new Callback<ArrayList<Barang>>() {
            @Override
            public void onResponse(Call<ArrayList<Barang>> call, Response<ArrayList<Barang>> response) {
                Log.d("respons", response.toString());
                if(response.isSuccessful()) {
                    listBarang.clear();
                    listPemesanan.clear();
                    listBarang = response.body();
                    pbBarang.setVisibility(View.GONE);
                    rvBarang.setVisibility(View.VISIBLE);
                    llKirim.setVisibility(View.VISIBLE);

                    adapter = new PemesananAdapter(PemesananActivity.this, listBarang);
                    rvBarang.setAdapter(adapter);
                    for(int i=0; i<listBarang.size(); i++) {
                        listPemesanan.add(new Pemesanan(listBarang.get(i).getIdBarang(), listBarang.get(i).getHarga(), 0, false));
                        countTotalHarga();
                    }
                    adapter.setOnCheckedChangeListener(new PemesananAdapter.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChange(int position, int idBarang, int harga, String jumlah, boolean checkedBarang) {
                            listPemesanan.set(position, new Pemesanan(idBarang, harga, jumlah.isEmpty() ? 0 : Integer.parseInt(jumlah), checkedBarang));
                            countTotalHarga();
                        }
                    });
                    adapter.setOnEditTextChangeListener(new PemesananAdapter.OnEditTextChangeListener() {
                        @Override
                        public void onEditTextChange(int position, int idBarang, int harga, String jumlah, boolean checkedBarang) {
                            listPemesanan.set(position, new Pemesanan(idBarang, harga, jumlah.isEmpty() ? 0 : Integer.parseInt(jumlah), checkedBarang));
                            countTotalHarga();
                        }
                    });

                    btnKirim.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(listPemesanan.size() > 0 && totalHarga > 0) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(PemesananActivity.this);
                                alert.setMessage("Data pemesanan untuk pelanggan "+namaPelanggan+ " dengan total harga "+tvTotalHarga.getText()+" akan dikirim, apakah Anda yakin?");
                                alert.setCancelable(false)
                                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ArrayList<Integer> listIdBarang     = new ArrayList<>();
                                                ArrayList<Integer> listJmlBarang    = new ArrayList<>();
                                                ArrayList<Integer> listTotalHarga   = new ArrayList<>();
                                                for(int i=0; i<listPemesanan.size(); i++) {
                                                    if(listPemesanan.get(i).isCheckedBarang()) {
                                                        listIdBarang.add(listPemesanan.get(i).getIdBarang());
                                                        listJmlBarang.add(listPemesanan.get(i).getJumlah());
                                                        listTotalHarga.add(listPemesanan.get(i).getJumlah() * listPemesanan.get(i).getHarga());
                                                    }
                                                }
                                                addPemesanan(listIdBarang, listJmlBarang, listTotalHarga);
                                            }
                                        })
                                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
                                        });
                                AlertDialog alertDialog = alert.create();
                                alertDialog.show();
                            }
                        }
                    });
                }
                else {
                    pbBarang.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Gagal dalam mendapatkan respon", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Barang>> call, Throwable t) {
                pbBarang.setVisibility(View.GONE);
                t.printStackTrace();
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Gagal dalam mengakses data", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private void addPemesanan(ArrayList<Integer> listIdBarang, ArrayList<Integer> listIJmlBarang, ArrayList<Integer> listTotalHarga) {
        final ProgressDialog progress = ProgressDialog.show(PemesananActivity.this, "", "Loading...", false, false);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.addPemesanan(idKaryawan, idPelanggan, listIdBarang, listIJmlBarang, listTotalHarga).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress.dismiss();
                try {
                    String strResponse = response.body().string();
                    if(response.isSuccessful()) {
                        JSONObject obj = new JSONObject(strResponse);
                        int status = obj.getInt("status");
                        if(status == 1) {
                            Toast.makeText(PemesananActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            Intent i = new Intent(PemesananActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                        }
                        else {
                            AlertDialog.Builder ab = new AlertDialog.Builder(
                                    PemesananActivity.this);
                            ab.setMessage(obj.getString("message"));
                            ab.setNeutralButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dlg, int sumthin) {}}).show();
                        }
                    }
                    else {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Gagal dalam mendapatkan respon", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress.dismiss();
                t.printStackTrace();
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Gagal dalam mengakses data", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private void countTotalHarga() {
        totalHarga = 0;
        for(int i=0; i<listPemesanan.size(); i++) {
            totalHarga = totalHarga + listPemesanan.get(i).getHarga() * listPemesanan.get(i).getJumlah();
        }
        Locale locale = new java.util.Locale("id");
        if(locale == null) {
            tvTotalHarga.setText(NumberFormat.getNumberInstance(Locale.US).format(totalHarga));
        }
        else {
            tvTotalHarga.setText("Rp "+NumberFormat.getNumberInstance(locale).format(totalHarga));
        }
    }

}
