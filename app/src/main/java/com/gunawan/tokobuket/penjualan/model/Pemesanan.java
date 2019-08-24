package com.gunawan.tokobuket.penjualan.model;

public class Pemesanan {
    private int idBarang;
    private int harga;
    private int jumlah;
    private boolean checkedBarang;

    public Pemesanan(int idBarang, int harga, int jumlah, boolean checkedBarang) {
        this.idBarang = idBarang;
        this.harga = harga;
        this.jumlah = jumlah;
        this.checkedBarang = checkedBarang;
    }

    public int getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(int idBarang) {
        this.idBarang = idBarang;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public boolean isCheckedBarang() {
        return checkedBarang;
    }

    public void setCheckedBarang(boolean checkedBarang) {
        this.checkedBarang = checkedBarang;
    }
}
