package com.gunawan.tokobuket.penjualan.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.gunawan.tokobuket.penjualan.R;
import com.gunawan.tokobuket.penjualan.adapter.BarangAdapter;
import com.gunawan.tokobuket.penjualan.api.ApiClient;
import com.gunawan.tokobuket.penjualan.api.ApiInterface;
import com.gunawan.tokobuket.penjualan.model.Barang;
import com.gunawan.tokobuket.penjualan.model.TotalPenjualan;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiwayatPenjualanActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private EditText etDari, etSampai;
    private Button btnLihat;
    private ProgressBar pbGrafik;
    private LinearLayout llGrafik;
    private View lineChart;
    private String fromDate, toDate, selectedDate;
    private int fromYear, fromMonth, fromDay, toYear, toMonth, toDay;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private ArrayList<TotalPenjualan> listPenjualan = new ArrayList<TotalPenjualan>();
    private ArrayList<String> xVal = new ArrayList<String>();
    private ArrayList<Integer> yVal = new ArrayList<Integer>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_penjualan);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        etDari = (EditText) findViewById(R.id.etDari);
        etSampai = (EditText) findViewById(R.id.etSampai);
        btnLihat = (Button) findViewById(R.id.btnLihat);
        pbGrafik = (ProgressBar) findViewById(R.id.pbGrafik);
        llGrafik = (LinearLayout) findViewById(R.id.llGrafik);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        pbGrafik.setVisibility(View.GONE);
        llGrafik.setVisibility(View.GONE);

        calendar = Calendar.getInstance();
        fromYear = calendar.get(Calendar.YEAR) ;
        fromMonth = calendar.get(Calendar.MONTH);
        fromDay = calendar.get(Calendar.DAY_OF_MONTH);
        toYear = calendar.get(Calendar.YEAR) ;
        toMonth = calendar.get(Calendar.MONTH);
        toDay = calendar.get(Calendar.DAY_OF_MONTH);
        fromDate = fromYear + "-" + (fromMonth+1) + "-" + fromDay;
        toDate = toYear + "-" + (toMonth+1) + "-" + toDay;
        etDari.setText(fromDate);
        etSampai.setText(toDate);
        getTotalPenjualan();

        etDari.setOnClickListener(this);
        etSampai.setOnClickListener(this);
        btnLihat.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.etDari:
                selectedDate = "from";
                datePickerDialog = DatePickerDialog.newInstance(RiwayatPenjualanActivity.this, fromYear, fromMonth, fromDay);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setAccentColor(Color.parseColor("#0189CC"));
                datePickerDialog.show(getSupportFragmentManager(), "DatePickerFrom");
                break;
            case R.id.etSampai:
                selectedDate = "to";
                datePickerDialog = DatePickerDialog.newInstance(RiwayatPenjualanActivity.this, toYear, toMonth, toDay);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setAccentColor(Color.parseColor("#0189CC"));
                datePickerDialog.show(getSupportFragmentManager(), "DatePickerTo");
                break;
            case R.id.btnLihat:
                if(fromDate.isEmpty() || toDate.isEmpty()) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(
                            RiwayatPenjualanActivity.this);
                    ab.setMessage("Harap lengkapi data tanggal yang diinginkan.");
                    ab.setNeutralButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dlg, int sumthin) {}}).show();
                }
                else {
                    getTotalPenjualan();
                }
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int day) {
        if(selectedDate.equals("from")) {
            fromYear = year;
            fromMonth = month;
            fromDay = day;
            fromDate = year + "-" + (month+1) + "-" + day;
            etDari.setText(fromDate);
        }
        else {
            toYear = year;
            toMonth = month;
            toDay = day;
            toDate = year + "-" + (month+1) + "-" + day;
            etSampai.setText(toDate);
        }
    }

    private void getTotalPenjualan() {
        listPenjualan.clear();
        xVal.clear();
        yVal.clear();
        pbGrafik.setVisibility(View.VISIBLE);
        llGrafik.setVisibility(View.GONE);
        ApiInterface api = new ApiClient().getClient().create(ApiInterface.class);
        SharedPreferences sp = this.getSharedPreferences("userLogin", Context.MODE_PRIVATE);
        Call<ArrayList<TotalPenjualan>> call = api.getTotalPenjualanByDate(sp.getString("idKaryawan", ""), fromDate, toDate);
        call.enqueue(new Callback<ArrayList<TotalPenjualan>>() {
            @Override
            public void onResponse(Call<ArrayList<TotalPenjualan>> call, Response<ArrayList<TotalPenjualan>> response) {
                if(response.isSuccessful()) {
                    listPenjualan = response.body();
                    pbGrafik.setVisibility(View.GONE);
                    llGrafik.setVisibility(View.VISIBLE);
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    for(int i=0; i<listPenjualan.size(); i++) {
                        try {
                            date = sdf.parse(listPenjualan.get(i).getTglPemesanan());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        xVal.add(sdf.format(date));
                        yVal.add(listPenjualan.get(i).getJmlBarang());
                    }
                    displayLineChart();
                }
                else {
                    pbGrafik.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Gagal dalam mendapatkan respon", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TotalPenjualan>> call, Throwable t) {
                pbGrafik.setVisibility(View.GONE);
                t.printStackTrace();
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Gagal dalam mengakses data", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private void displayLineChart() {
        XYSeries xySeries = new XYSeries("Total Penjualan");
        for(int i=0;i<xVal.size();i++){
            xySeries.add(i,yVal.get(i));
        }
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(xySeries);

        XYSeriesRenderer xyRenderer = new XYSeriesRenderer();
        xyRenderer.setColor(Color.parseColor("#0189CC"));
        xyRenderer.setFillPoints(true);
        xyRenderer.setLineWidth(2f);
        xyRenderer.setDisplayChartValues(false);
        xyRenderer.setChartValuesTextSize(25);
        xyRenderer.setDisplayChartValuesDistance(150);
        xyRenderer.setPointStyle(PointStyle.CIRCLE);
        xyRenderer.setStroke(BasicStroke.SOLID);

        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setAxisTitleTextSize(20);
        multiRenderer.setLabelsTextSize(25);
        multiRenderer.setLegendTextSize(20);
        multiRenderer.setXLabelsColor(Color.BLACK);
        multiRenderer.setYLabelsColor(0, Color.BLACK);
        multiRenderer.setYLabelsPadding(4);
        multiRenderer.setAxesColor(Color.BLACK);
        multiRenderer.setLabelsColor(Color.BLACK);
        multiRenderer.setZoomButtonsVisible(false);
        multiRenderer.setPanEnabled(false, false);
        multiRenderer.setClickEnabled(false);
        multiRenderer.setZoomEnabled(true, true);
        multiRenderer.setShowGridY(true);
        multiRenderer.setShowGridX(true);
        multiRenderer.setFitLegend(true);
        multiRenderer.setShowGrid(true);
        multiRenderer.setZoomEnabled(true);
        multiRenderer.setExternalZoomEnabled(true);
        multiRenderer.setAntialiasing(true);
        multiRenderer.setInScroll(false);
        multiRenderer.setLegendHeight(30);
        multiRenderer.setXLabelsAlign(Paint.Align.CENTER);
        multiRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
        multiRenderer.setYAxisMin(0);
        multiRenderer.setXAxisMin(-0.5);
        multiRenderer.setXAxisMax(5);
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);
        multiRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setScale(2f);
        multiRenderer.setPointSize(7f);
        multiRenderer.setMargins(new int[]{30, 55, 30, 30});
        for(int i=0;i<xVal.size();i++){
            multiRenderer.addXTextLabel(i, xVal.get(i));
        }
        multiRenderer.addSeriesRenderer(xyRenderer);
        llGrafik.removeAllViews();
        lineChart = ChartFactory.getLineChartView(this, dataset, multiRenderer);
        llGrafik.addView(lineChart);
    }

}
