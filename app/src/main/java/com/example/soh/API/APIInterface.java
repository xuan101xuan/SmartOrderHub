package com.example.soh.API;

import com.example.soh.APIModels.GetCashierResponseAPIModel;
import com.example.soh.APIModels.GetProductResponseAPIModel;
import com.example.soh.APIModels.GetKitchenResponseAPIModel;
import com.example.soh.APIModels.GetReportResponseAPIModel;
import com.example.soh.APIModels.GetStaffResponseAPIModel;
import com.example.soh.APIModels.GetTableResponseAPIModel;
import com.example.soh.APIModels.GetAccountResponseAPIModel;
import com.example.soh.APIModels.ResponseAPIModel;
import com.example.soh.Models.Account;
import com.example.soh.Models.Cashier;
import com.example.soh.Models.Product;
import com.example.soh.Models.Kitchen;
import com.example.soh.Models.Report;
import com.example.soh.Models.Staff;
import com.example.soh.Models.TableNumber;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIInterface {

    //--------------------Product-----------------------
    @GET("api/Sales/get-product-list")
    Call<GetProductResponseAPIModel> getProductList(@Query("ProductType") int productType);
    @GET("api/Sales/get-allproduct-list")
    Call<GetProductResponseAPIModel> getAllProductList();
    @POST("/api/Sales/delete-product")
    Call<ResponseAPIModel> deleteProduct(@Query("IdProduct") String idProduct);
    @POST("api/Sales/insert-product")
    Call<ResponseAPIModel> insertProduct(@Body Product product);
    @POST("api/Sales/update-product")
    Call<ResponseAPIModel> updateProduct(@Query("IdProduct") String idProduct, @Body Product product);

    @Multipart
    @POST("api/Sales/insert-product-image")
    Call<ResponseAPIModel> insertProductImage(@Part MultipartBody.Part image,
                                         @Part("productName") RequestBody productName,
                                         @Part("productPrice") RequestBody productPrice,
                                         @Part("productType") RequestBody productType,
                                         @Part("productStatus") RequestBody productStatus,
                                         @Part("productQuantity") RequestBody productQuantity);


    //---------------------Staff-------------------------------
    @GET("api/Sales/get-allstaff-list")
    Call<GetStaffResponseAPIModel> getStaffList();
    @POST("/api/Sales/delete-staff")
    Call<ResponseAPIModel> deleteStaff(@Query("IdStaff") String idStaff);
    @POST("api/Sales/insert-staff")
    Call<ResponseAPIModel> insertStaff(@Body Staff staff);
    @POST("api/Sales/update-staff")
    Call<ResponseAPIModel> updateStaff(@Query("IdStaff") String idStaff, @Body Staff staff);

    //-------------------------Kitchen--------------------------
    @POST("api/Sales/insert-kitchen")
    Call<ResponseAPIModel> insertProductForKitchen(@Body Kitchen kitchen);
    @GET("api/Sales/get-allkitchen-list")
    Call<GetKitchenResponseAPIModel> getAllKitchenList();
    @POST("/api/Sales/delete-kitchen")
    Call<ResponseAPIModel> deleteKitchen(@Query("IdKitchen") String idKitchen);

    //---------------------Account-----------------------------
    @GET("api/Sales/get-allaccount-list")
    Call<GetAccountResponseAPIModel> getAccountList();
    @POST("/api/Sales/delete-account")
    Call<ResponseAPIModel> deleteAccount(@Query("IdAcc") String idAcc);
    @POST("api/Sales/insert-account")
    Call<ResponseAPIModel> insertAccount(@Body Account account);
    @POST("api/Sales/update-account")
    Call<ResponseAPIModel> updateAccount(@Query("IdAcc") String idAcc, @Body Account account);

    //---------------------Cashier-----------------------------
    @GET("api/Sales/get-cashier-list")
    Call<GetCashierResponseAPIModel> getCashierList(@Query("TableNum") int tableNum);
    @POST("api/Sales/insert-cashier")
    Call<ResponseAPIModel> insertProductForCashier(@Body Cashier cashier);
    @POST("api/Sales/delete-cashier")
    Call<ResponseAPIModel> deleteCashier(@Query("IdCashier") String idCashier);

    //---------------------TableNumber-----------------------------
    @GET("api/Sales/get-tables-list")
    Call<GetTableResponseAPIModel> getTableList(@Query("Floor") int floor);
    @GET("api/Sales/get-tablesnumber-list")
    Call<GetTableResponseAPIModel> getTableNumberList(@Query("TableNum") int tableNum);
    @GET("api/Sales/get-tablesstatus-list")
    Call<GetTableResponseAPIModel> getTableStatusList(@Query("TableStatus") int tableStatus);
    @POST("api/Sales/insert-tables")
    Call<ResponseAPIModel> insertTable(@Body TableNumber tableNumber);
    @POST("/api/Sales/delete-tables")
    Call<ResponseAPIModel> deleteTable(@Query("IdTable") String idTable);
    @POST("api/Sales/update-tables")
    Call<ResponseAPIModel> updateTable(@Query("IdTable") String idTable, @Body TableNumber tableNumber);

    //-------------------------Report-------------------------------
    @POST("api/Sales/insert-report")
    Call<ResponseAPIModel> insertProductForReport(@Body Report report);
    @GET("api/Sales/get-allreport-list")
    Call<GetReportResponseAPIModel> getReportList();


}
