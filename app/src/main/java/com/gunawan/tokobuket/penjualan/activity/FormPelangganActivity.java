package com.gunawan.tokobuket.penjualan.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class FormPelangganActivity extends AppCompatActivity {
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private EditText etNama, etNoTelp, etAlamat;
    private Button btnSimpan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pelanggan);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        etNama = (EditText) findViewById(R.id.etNama);
        etNoTelp = (EditText) findViewById(R.id.etNoTelp);
        etAlamat = (EditText) findViewById(R.id.etAlamat);
        btnSimpan = (Button) findViewById(R.id.btnSimpan);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPelanggan();
            }
        });

    }

    private void addPelanggan() {
        String nama = etNama.getText().toString();
        String noTelp = etNoTelp.getText().toString();
        String alamat = etAlamat.getText().toString();
        if(nama.isEmpty() || noTelp.isEmpty() || alamat.isEmpty()) {
            AlertDialog.Builder ab = new AlertDialog.Builder(FormPelangganActivity.this);
            ab.setMessage("Harap lengkapi semua data Anda.");
            ab.setNeutralButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dlg, int sumthin) {}}).show();
        }
        else {
            final ProgressDialog progress = ProgressDialog.show(FormPelangganActivity.this, "", "Loading...", false, false);
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            apiInterface.addPelanggan(nama, noTelp, alamat).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    progress.dismiss();
                    try {
                        String strResponse = response.body().string();
                        if(response.isSuccessful()) {
                            JSONObject obj = new JSONObject(strResponse);
                            int status = obj.getInt("status");
                            if (status == 1) {
                                Toast.makeText(FormPelangganActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
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

}
