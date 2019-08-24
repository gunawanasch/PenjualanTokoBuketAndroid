package com.gunawan.tokobuket.penjualan.api;

import com.gunawan.tokobuket.penjualan.model.Barang;
import com.gunawan.tokobuket.penjualan.model.Pelanggan;
import com.gunawan.tokobuket.penjualan.model.TotalPenjualan;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("Karyawan_controller/getLogin")
    Call<ResponseBody> getLogin(@Field("email") String email,
                               @Field("password") String password);

    @FormUrlEncoded
    @POST("Barang_controller/getBarang")
    Call<ArrayList<Barang>> getBarang(@Field("id_karyawan") String idKaryawan);

    @FormUrlEncoded
    @POST("Pelanggan_controller/getPelanggan")
    Call<ArrayList<Pelanggan>> getPelanggan(@Field("id_karyawan") String idKaryawan);

    @FormUrlEncoded
    @POST("Pelanggan_controller/getPelangganByNama")
    Call<ArrayList<Pelanggan>> getPelangganByNama(@Field("nama") String nama);

    @FormUrlEncoded
    @POST("Pelanggan_controller/getDetailPelanggan")
    Call<ResponseBody> geDetailPelanggan(@Field("id_pelanggan") int idPelanggan);

    @FormUrlEncoded
    @POST("Pelanggan_controller/addPelanggan")
    Call<ResponseBody> addPelanggan(@Field("nama") String nama,
                                    @Field("no_telp") String noTelp,
                                    @Field("alamat") String alamat);

    @FormUrlEncoded
    @POST("Pelanggan_controller/editPelanggan")
    Call<ResponseBody> editPelanggan(@Field("id_pelanggan") int idPelanggan,
                                     @Field("nama") String nama,
                                     @Field("no_telp") String noTelp,
                                     @Field("alamat") String alamat);

    @FormUrlEncoded
    @POST("Pemesanan_controller/addPemesanan")
    Call<ResponseBody> addPemesanan(@Field("id_karyawan") String idKaryawan,
                                    @Field("id_pelanggan") int idPelanggan,
                                    @Field("id_barang[]") ArrayList<Integer> listIdBarang,
                                    @Field("jumlah_barang[]") ArrayList<Integer> listJmlBarang,
                                    @Field("total_harga[]") ArrayList<Integer> listTotalHarga);

    @FormUrlEncoded
    @POST("Pemesanan_controller/getTotalPenjualanByDate")
    Call<ArrayList<TotalPenjualan>> getTotalPenjualanByDate(@Field("id_karyawan") String idKaryawan,
                                                            @Field("from_date") String fromDate,
                                                            @Field("to_date") String toDate);
}
