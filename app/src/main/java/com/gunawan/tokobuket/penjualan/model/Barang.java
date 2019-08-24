package com.gunawan.tokobuket.penjualan.model;

import com.google.gson.annotations.SerializedName;

public class Barang {
    @SerializedName("id_barang")
    private int idBarang;

    @SerializedName("nama")
    private String nama;

    @SerializedName("harga")
    private int harga;

    @SerializedName("foto")
    private String foto;

    private boolean isCheckedBarang;

    public int getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(int idBarang) {
        this.idBarang = idBarang;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public boolean isCheckedBarang() {
        return isCheckedBarang;
    }

    public void setCheckedBarang(boolean checkedBarang) {
        isCheckedBarang = checkedBarang;
    }
}
