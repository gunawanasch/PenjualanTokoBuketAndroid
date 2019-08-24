package com.gunawan.tokobuket.penjualan.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.PopupMenu;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gunawan.tokobuket.penjualan.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivOptionMenu;
    private TextView tvWelcome;
    private CardView cvBarang, cvPelanggan, cvPemesanan, cvRiwayatPenjualan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivOptionMenu = (ImageView) findViewById(R.id.ivOptionMenu);
        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        cvBarang = (CardView) findViewById(R.id.cvBarang);
        cvPelanggan = (CardView) findViewById(R.id.cvPelanggan);
        cvPemesanan = (CardView) findViewById(R.id.cvPemesanan);
        cvRiwayatPenjualan = (CardView) findViewById(R.id.cvRiwayatPenjualan);

        SharedPreferences sp = MainActivity.this.getSharedPreferences("userLogin", Context.MODE_PRIVATE);
        tvWelcome.setText("Selamat datang, "+sp.getString("nama", ""));

        ivOptionMenu.setOnClickListener(this);
        cvBarang.setOnClickListener(this);
        cvPelanggan.setOnClickListener(this);
        cvPemesanan.setOnClickListener(this);
        cvRiwayatPenjualan.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivOptionMenu:
                PopupMenu popup = new PopupMenu(this, ivOptionMenu);
                popup.inflate(R.menu.menu_main);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_logout:
                                SharedPreferences sp = MainActivity.this.getSharedPreferences("userLogin", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.clear();
                                editor.commit();
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
                break;
            case R.id.cvBarang:
                Intent intentBarang = new Intent(this, BarangActivity.class);
                startActivity(intentBarang);
                break;
            case R.id.cvPelanggan:
                Intent intentPelanggan = new Intent(this, PelangganActivity.class);
                intentPelanggan.putExtra("isFromPemesanan", false);
                startActivity(intentPelanggan);
                break;
            case R.id.cvPemesanan:
                Intent intentPemesanan = new Intent(this, PelangganActivity.class);
                intentPemesanan.putExtra("isFromPemesanan", true);
                startActivity(intentPemesanan);
                break;
            case R.id.cvRiwayatPenjualan:
                Intent intentRiwayatPenjualan = new Intent(this, RiwayatPenjualanActivity.class);
                startActivity(intentRiwayatPenjualan);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_logout) {
            SharedPreferences sp = this.getSharedPreferences("userLogin", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.commit();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setMessage("Apakah anda yakin ingin keluar?");
        alert.setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Intent.ACTION_MAIN);
                        i.addCategory(Intent.CATEGORY_HOME);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
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
