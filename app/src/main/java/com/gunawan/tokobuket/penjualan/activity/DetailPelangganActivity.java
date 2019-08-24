package com.gunawan.tokobuket.penjualan.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.gunawan.tokobuket.penjualan.R;
import com.gunawan.tokobuket.penjualan.api.ApiClient;
import com.gunawan.tokobuket.penjualan.api.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPelangganActivity extends AppCompatActivity {
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private ProgressBar pbDetailPelanggan;
    private ScrollView svDetailPelanggan;
    private EditText etNama, etNoTelp, etAlamat;
    private Button btnUbah;
    private Boolean isEditPosition = false;
    private int idPelanggan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pelanggan);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        pbDetailPelanggan = (ProgressBar) findViewById(R.id.pbDetailPelanggan);
        svDetailPelanggan = (ScrollView) findViewById(R.id.svDetailPelanggan);
        etNama = (EditText) findViewById(R.id.etNama);
        etNoTelp = (EditText) findViewById(R.id.etNoTelp);
        etAlamat = (EditText) findViewById(R.id.etAlamat);
        btnUbah = (Button) findViewById(R.id.btnUbah);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        isEditPosition = false;
        etNama.setEnabled(false);
        etNoTelp.setEnabled(false);
        etAlamat.setEnabled(false);

        btnUbah.setText("Ubah Data Pelanggan");
        btnUbah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditPosition = true;
                setEditPosition();
            }
        });

        Intent i = getIntent();
        idPelanggan = i.getIntExtra("idPelanggan", 0);
        getDetailPelanggan();
    }

    private void getDetailPelanggan() {
        pbDetailPelanggan.setVisibility(View.VISIBLE);
        svDetailPelanggan.setVisibility(View.GONE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        apiInterface.geDetailPelanggan(idPelanggan).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pbDetailPelanggan.setVisibility(View.GONE);
                try {
                    String strResponse = response.body().string();
                    if(response.isSuccessful()) {
                        JSONObject obj = new JSONObject(strResponse);
                        int status = obj.getInt("status");
                        if (status == 1) {
                            svDetailPelanggan.setVisibility(View.VISIBLE);
                            etNama.setText(obj.getString("nama"));
                            etNoTelp.setText(obj.getString("no_telp"));
                            etAlamat.setText(obj.getString("alamat"));
                        }
                        else {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Data tidak ada", Snackbar.LENGTH_LONG);
                            snackbar.show();
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
                pbDetailPelanggan.setVisibility(View.GONE);
                t.printStackTrace();
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Gagal dalam mengakses data", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private void setEditPosition() {
        etNama.setEnabled(true);
        etNoTelp.setEnabled(true);
        etAlamat.setEnabled(true);
        btnUbah.setText("Simpan");
        btnUbah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama     = etNama.getText().toString();
                String noTelp   = etNoTelp.getText().toString();
                String alamat   = etAlamat.getText().toString();
                if(nama.isEmpty() || noTelp.isEmpty() || alamat.isEmpty()) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(DetailPelangganActivity.this);
                    ab.setMessage("Harap lengkapi semua data Anda.");
                    ab.setNeutralButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dlg, int sumthin) {}}).show();
                }
                else {
                    final ProgressDialog progress = ProgressDialog.show(DetailPelangganActivity.this, "", "Loading...", false, false);
                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    apiInterface.editPelanggan(idPelanggan, nama, noTelp, alamat).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            progress.dismiss();
                            try {
                                String strResponse = response.body().string();
                                if(response.isSuccessful()) {
                                    JSONObject obj = new JSONObject(strResponse);
                                    int status = obj.getInt("status");
                                    if (status == 1) {
                                        Toast.makeText(DetailPelangganActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    else {
                                        Snackbar snackbar = Snackbar.make(coordinatorLayout, obj.getString("message"), Snackbar.LENGTH_LONG);
                                        snackbar.show();
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
            }
        });
    }

}
