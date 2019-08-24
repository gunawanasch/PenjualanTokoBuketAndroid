package com.gunawan.tokobuket.penjualan.model;

import com.google.gson.annotations.SerializedName;

public class TotalPenjualan {
    @SerializedName("id_pemesanan")
    private int idPemesanan;

    @SerializedName("tgl_pemesanan")
    private String tglPemesanan;

    @SerializedName("id_pelanggan")
    private int idPelanggan;

    @SerializedName("jml_barang")
    private int jmlBarang;

    public int getIdPemesanan() {
        return idPemesanan;
    }

    public void setIdPemesanan(int idPemesanan) {
        this.idPemesanan = idPemesanan;
    }

    public String getTglPemesanan() {
        return tglPemesanan;
    }

    public void setTglPemesanan(String tglPemesanan) {
        this.tglPemesanan = tglPemesanan;
    }

    public int getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(int idPelanggan) {
        this.idPelanggan = idPelanggan;
    }

    public int getJmlBarang() {
        return jmlBarang;
    }

    public void setJmlBarang(int jmlBarang) {
        this.jmlBarang = jmlBarang;
    }
}
