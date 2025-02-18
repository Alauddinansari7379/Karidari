package com.example.tlismimoti.retrofit


import com.amtech.mehfeel.category.model.ModelCatWithId.ModelCatWithId
import com.amtech.mehfeel.home.model.modelTop.ModelNewArraivals
import com.example.tlismimoti.cart.model.ModelActiveGateway.ModelActiveGateWays
import com.example.tlismimoti.cart.model.ModelOrderCreate
import com.example.tlismimoti.cart.model.ModelPayment.ModelPayment
import com.example.tlismimoti.cart.model.ModelState.ModelState
import com.example.tlismimoti.cart.model.modelRemove.ModelRemoveCart
import com.example.tlismimoti.cart.model.modelgetCart.ModelGetCart
import com.example.tlismimoti.categories.model.ModelCatWithId.modelCat.ModelGetCar
import com.example.tlismimoti.categories.modelCurrency.ModelCurrency
import com.example.tlismimoti.home.model.ModelProduct
import com.example.tlismimoti.home.model.modelSlider.ModelSlider
import com.example.tlismimoti.listing.model.ModelProductDetial
import com.example.tlismimoti.listing.model.cartModel.ModelCart
import com.example.tlismimoti.login.model.ModelLogin
import com.example.tlismimoti.login.model.ModelUserSignUp
import com.example.tlismimoti.mainActivity.ModelDestoryCart
import com.example.tlismimoti.order.model.ModelOrder
import com.example.tlismimoti.order.model.ModelPendingOrder.ModelOrderPending
import com.example.tlismimoti.order.model.modelOrderDet.ModelOrderDetail
import com.example.tlismimoti.setting.model.ModelSetting
import com.example.tlismimoti.wishlist.model.ModelWishlist
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiInterface {
    @POST("login")
    fun login(
        @Query("email") email: String,
        @Query("password") password: String,
    ): Call<ModelLogin>

    @POST("autologin")
    fun autologin(
        @Query("domain") domain: String,
     ): Call<ModelLogin>
    @Headers("content-type: application/json")
    @GET("home_page_products")
    fun newArrivals(
        @Header("Authorization") authHeader: String?,
        @Query("latest_product")latest_product:String?,
        @Query("get_offerable_products")get_offerable_products:String?,
        @Query("trending_products")trending_products:String?,
    ): Call<ModelNewArraivals>
    @POST("login-user")
    fun loginUser(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("device_id") device_id: String,
        @Query("device_type") device_type: String,
    ): Call<ModelLogin>

    @GET("get_product_bycategory")
    fun getProductByCategory(
        @Header("Authorization") Authorization: String,
        @Query("category_id") category_id:String,
    ): Call<ModelCatWithId>

    @POST("register-user")
    fun registerUser(
        @Header("Authorization") authHeader: String?,
        @Query("name") name: String,
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("mobile") mobile: String,
    ): Call<ModelUserSignUp>

    @GET("product")
    fun product(
        @Header("Authorization") Authorization: String,
     ): Call<ModelProduct>


    @Headers("content-type: application/json")
    @POST("get_slider")
    fun getSlider(
        @Header("Authorization") authHeader: String?,
    ): Call<ModelSlider>

    @Headers("content-type: application/json")
    @POST("delet_acct")
    fun deleteAccount(
        @Header("Authorization") authHeader: String?,
        @Query("email") email:String?
    ): Call<ModelDestoryCart>


    @Headers("content-type: application/json")
    @POST("get_pro_detail")
    fun getProductDetail(
        @Header("Authorization") authHeader: String?,
        @Query("id") id: String?,
    ): Call<ModelProductDetial>

    @Headers("content-type: application/json")
    @POST("destroy_cart")
    fun destroyCart(
        @Header("Authorization") authHeader: String?,
        @Query("device_id")device_id:String?
     ): Call<ModelDestoryCart>

    @Headers("content-type: application/json")
    @POST("add_to_cart")
    fun addToCart(
        @Header("Authorization") authHeader: String?,
        @Query("term_id") term_id: String?,
        @Query("qty") qty: String?,
        @Query("device_id")device_id:String?,
        @Query("unsession")unsession:String?,
        @Query("type")type:String?,
        @Query("option[]")option:String?,
        @Query("variation[]")variation:String?,
    ): Call<ModelCart>

    @Headers("content-type: application/json")
    @POST("cart_remove")
    fun removeToCart(
        @Header("Authorization") authHeader: String?,
        @Query("id") id: String?,
        @Query("device_id")device_id:String?,
        @Query("unsession")unsession:String?,
        @Query("type")type:String?,
        ): Call<ModelRemoveCart>

    @Headers("content-type: application/json")
    @GET("get_cart")
    fun getCart(
        @Header("Authorization") authHeader: String?,
        @Query("device_id")device_id:String?,
        @Query("unsession")unsession:String?,
        @Query("type")type:String?,
    ): Call<ModelGetCart>

    @Headers("content-type: application/json")
    @GET("get_category")
    fun getCategory(
        @Header("Authorization") authHeader: String?,
        @Query("type")type:String?,
    ): Call<ModelGetCar>

    @Headers("content-type: application/json")
    @GET("orderview")
    fun orderDetail(
        @Header("Authorization") authHeader: String?,
         @Query("email")email:String?,
        @Query("order_id")order_id:String?,
    ): Call<ModelOrderDetail>

    @Headers("content-type: application/json")
    @POST("uorders")
    fun getOrder(
        @Header("Authorization") authHeader: String?,
        @Query("email")email:String?
    ): Call<ModelOrder>

    @Headers("content-type: application/json")
    @POST("getcurrency")
    fun getCurrency(
        @Header("Authorization") authHeader: String?,
     ): Call<ModelCurrency>

    @Headers("content-type: application/json")
    @POST("showlist")
    fun orders(
        @Header("Authorization") authHeader: String?,
        @Query("email")email:String?,
        @Query("slug")slug:String?
    ): Call<ModelOrder>

    @Headers("content-type: application/json")
    @POST("showlist")
    fun ordersCompleted(
        @Header("Authorization") authHeader: String?,
        @Query("email")email:String?,
        @Query("slug")slug:String?
    ): Call<ModelOrderPending>

    @Headers("content-type: application/json")
    @POST("usettings")
    fun userSettings(
        @Header("Authorization") authHeader: String?,
        @Query("email")email:String?
    ): Call<ModelSetting>

    @Headers("content-type: application/json")
    @POST("add_to_wishlist")
    fun addToWishlist(
        @Header("Authorization") authHeader: String?,
        @Query("email")email:String?,
        @Query("term_id")term_id:String?,
        @Query("device_id")device_id:String?,
        @Query("unsession")unsession:String?,
        @Query("type")type:String?,
    ): Call<ModelDestoryCart>

    @Headers("content-type: application/json")
    @POST("remove_wishlist")
    fun removeWishlist(
        @Header("Authorization") authHeader: String?,
        @Query("id")id:String?,
        @Query("unsession")unsession:String?,
        @Query("type")type:String?,
    ): Call<ModelDestoryCart>

    @Headers("content-type: application/json")
    @POST("get_wishlistsa")
    fun getWishlists(
        @Header("Authorization") authHeader: String?,
        @Query("device_id")device_id:String?,
        @Query("unsession")unsession:String?,
        @Query("type")type:String?
    ): Call<ModelWishlist>

    @Headers("content-type: application/json")
    @POST("usettings_update")
    fun userSettingsUpdate(
        @Header("Authorization") authHeader: String?,
        @Query("email")email:String?,
        @Query("name")name:String?,
        @Query("mobile")mobile:String?,
        @Query("password_current")password_current:String?,
        @Query("password")password:String?,
    ): Call<ModelDestoryCart>

    @Headers("content-type: application/json")
    @GET("product")
    fun searchProduct(
        @Header("Authorization") authHeader: String?,
        @Query("src")src:String?,
        @Query("type")type:String?,
    ): Call<ModelProduct>

    @Headers("content-type: application/json")
    @POST("payment_list")
    fun paymentList(
        @Header("Authorization") authHeader: String?,
    ): Call<ModelPayment>

    @Headers("content-type: application/json")
    @POST("active_getways")
    fun activeGateways(
        @Header("Authorization") authHeader: String?,
    ): Call<ModelActiveGateWays>

    @Headers("content-type: application/json")
    @GET("get_state")
    fun getState(
     ): Call<ModelState>

    @Headers("content-type: application/json")
    @GET("get_city")
    fun getCity(
        @Query("state_id") state_id:String
     ): Call<ModelState>

    @POST("make_order")
    fun makeOrder(
        @Header("Authorization") authHeader: String?,
        @Query("customer_type")customer_type:String?,
        @Query("delivery_type")delivery_type:String?,
        @Query("shipping_method")shipping_method:String?,
        @Query("payment_id")payment_id:String?,
        @Query("payment_method")payment_method:String?,
        @Query("payment_status")payment_status:String?,
        @Query("name")name:String?,
        @Query("email")email:String?,
        @Query("phone")phone:String?,
        @Query("comment")comment:String?,
        @Query("address")address:String?,
        @Query("zip_code")zip_code:String?,
        @Query("location")location:String?,
        @Query("device_id")device_id:String?,
        @Query("total")total:String?,
        @Query("discount")discount:String?,
        @Query("tax")tax:String?,
        @Query("trans_id")trans_id:String?,
    ): Call<ModelOrderCreate>


//
//
//    @Multipart
//    @POST("adddoctor")
//    fun adddoctor(
//        @Query("nurse_id") nurse_id: String,
//        @Query("idToken") idToken: String,
//        @Query("group") group: String,
//        @Query("name") name: String,
//        @Query("password") password: String,
//        @Query("email") email: String,
//        @Query("address") address: String,
//        @Query("phone") phone: String,
//        @Query("department") department: String,
//        @Query("reg_no") reg_no: String,
//        @Query("profile") profile: String,
//        @Part img_url: MultipartBody.Part,
//        ): Call<ModelNewAppoint>


}