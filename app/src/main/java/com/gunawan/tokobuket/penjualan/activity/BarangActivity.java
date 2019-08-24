package com.gunawan.tokobuket.penjualan.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.gunawan.tokobuket.penjualan.R;
import com.gunawan.tokobuket.penjualan.adapter.BarangAdapter;
import com.gunawan.tokobuket.penjualan.api.ApiClient;
import com.gunawan.tokobuket.penjualan.api.ApiInterface;
import com.gunawan.tokobuket.penjualan.model.Barang;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarangActivity extends AppCompatActivity {
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private ProgressBar pbBarang;
    private RecyclerView rvBarang;
    private BarangAdapter adapter;
    private ArrayList<Barang> listBarang = new ArrayList<Barang>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        pbBarang = (ProgressBar) findViewById(R.id.pbBarang);
        rvBarang = (RecyclerView) findViewById(R.id.rvBarang);

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

        getBarang();
    }

    private void getBarang() {
        pbBarang.setVisibility(View.VISIBLE);
        rvBarang.setVisibility(View.GONE);
        ApiInterface api = new ApiClient().getClient().create(ApiInterface.class);
        SharedPreferences sp = this.getSharedPreferences("userLogin", Context.MODE_PRIVATE);
        Call<ArrayList<Barang>> call = api.getBarang(sp.getString("idKaryawan", ""));
        call.enqueue(new Callback<ArrayList<Barang>>() {
            @Override
            public void onResponse(Call<ArrayList<Barang>> call, Response<ArrayList<Barang>> response) {
                if(response.isSuccessful()) {
                    listBarang = response.body();
                    pbBarang.setVisibility(View.GONE);
                    rvBarang.setVisibility(View.VISIBLE);
                    adapter = new BarangAdapter(BarangActivity.this, listBarang);
                    rvBarang.setAdapter(adapter);
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

}
