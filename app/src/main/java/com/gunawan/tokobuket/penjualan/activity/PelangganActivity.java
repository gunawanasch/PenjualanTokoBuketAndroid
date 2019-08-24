package com.gunawan.tokobuket.penjualan.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gunawan.tokobuket.penjualan.R;
import com.gunawan.tokobuket.penjualan.adapter.PelangganAdapter;
import com.gunawan.tokobuket.penjualan.api.ApiClient;
import com.gunawan.tokobuket.penjualan.api.ApiInterface;
import com.gunawan.tokobuket.penjualan.model.Pelanggan;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PelangganActivity extends AppCompatActivity {
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private EditText etCari;
    private TextView tvCari;
    private ProgressBar pbPelanggan;
    private RecyclerView rvPelanggan;
    private FloatingActionButton fabAdd;
    private PelangganAdapter adapter;
    private ArrayList<Pelanggan> listPelanggan = new ArrayList<Pelanggan>();
    private boolean isFromPemesanan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggan);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        etCari = (EditText) findViewById(R.id.etCari);
        tvCari = (TextView) findViewById(R.id.tvCari);
        pbPelanggan = (ProgressBar) findViewById(R.id.pbPelanggan);
        rvPelanggan = (RecyclerView) findViewById(R.id.rvPelanggan);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rvPelanggan.setHasFixedSize(true);
        rvPelanggan.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        Intent i = getIntent();
        isFromPemesanan = i.getBooleanExtra("isFromPemesanan", false);
        if(isFromPemesanan) {
            fabAdd.hide();
        }
        else {
            fabAdd.show();
        }
        getPelanggan();

        tvCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPelangganByNama();
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PelangganActivity.this, FormPelangganActivity.class);
                startActivity(i);
            }
        });
    }

    private void getPelanggan() {
        pbPelanggan.setVisibility(View.VISIBLE);
        rvPelanggan.setVisibility(View.GONE);
        ApiInterface api = new ApiClient().getClient().create(ApiInterface.class);
        SharedPreferences sp = this.getSharedPreferences("userLogin", Context.MODE_PRIVATE);
        Call<ArrayList<Pelanggan>> call = api.getPelanggan(sp.getString("idKaryawan", ""));
        call.enqueue(new Callback<ArrayList<Pelanggan>>() {
            @Override
            public void onResponse(Call<ArrayList<Pelanggan>> call, Response<ArrayList<Pelanggan>> response) {
                if(response.isSuccessful()) {
                    listPelanggan = response.body();
                    pbPelanggan.setVisibility(View.GONE);
                    rvPelanggan.setVisibility(View.VISIBLE);
                    adapter = new PelangganAdapter(PelangganActivity.this, listPelanggan, isFromPemesanan);
                    rvPelanggan.setAdapter(adapter);
                    rvPelanggan.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            if(!isFromPemesanan) {
                                if (dy > 0 && fabAdd.getVisibility() == View.VISIBLE) {
                                    fabAdd.hide();
                                }
                                else if (dy < 0 && fabAdd.getVisibility() != View.VISIBLE) {
                                    fabAdd.show();
                                }
                            }
                        }
                    });
                }
                else {
                    pbPelanggan.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Gagal dalam mendapatkan respon", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Pelanggan>> call, Throwable t) {
                pbPelanggan.setVisibility(View.GONE);
                t.printStackTrace();
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Gagal dalam mengakses data", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private void getPelangganByNama() {
        pbPelanggan.setVisibility(View.VISIBLE);
        rvPelanggan.setVisibility(View.GONE);
        listPelanggan.clear();
        ApiInterface api = new ApiClient().getClient().create(ApiInterface.class);
        Call<ArrayList<Pelanggan>> call = api.getPelangganByNama(etCari.getText().toString());
        call.enqueue(new Callback<ArrayList<Pelanggan>>() {
            @Override
            public void onResponse(Call<ArrayList<Pelanggan>> call, Response<ArrayList<Pelanggan>> response) {
                if(response.isSuccessful()) {
                    listPelanggan = response.body();
                    pbPelanggan.setVisibility(View.GONE);
                    rvPelanggan.setVisibility(View.VISIBLE);
                    adapter = new PelangganAdapter(PelangganActivity.this, listPelanggan, isFromPemesanan);
                    rvPelanggan.setAdapter(adapter);
                }
                else {
                    pbPelanggan.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Gagal dalam mendapatkan respon", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Pelanggan>> call, Throwable t) {
                pbPelanggan.setVisibility(View.GONE);
                t.printStackTrace();
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Gagal dalam mengakses data", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listPelanggan.clear();
        getPelanggan();
    }
}
