package com.example.products

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ProductsService{
    @POST("auth/local")
    fun getToken (@Body() user: User) : Call<TokenInfo>

    @GET("products?_sort=id:desc")
    fun getProducts(@Query("_limit") limit: Int = 15): Call<List<Product>>

    @GET("products/{id}")
    fun getProductById(@Path("id") id : Int): Call<Product>

    @POST("products")
    fun createProduct(@Body() product: Product): Call<Product>

    @PUT("products/{id}")
    fun updateProduct(@Path("id") id : Int, @Body() product: Product): Call<Product>

    @DELETE("products/{id}")
    fun deleteProduct(@Path("id") id : Int) : Call<Void>
}

class RetrofitHelper{
    companion object{
        fun getInstance(baseUrl: String): ProductsService{
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient())
                .build()

            return retrofit.create(ProductsService::class.java)
        }
        fun getAuthenticatedUser(baseUrl: String, token: String): ProductsService{

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor{ chain ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(newRequest)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            return retrofit.create(ProductsService::class.java)
        }
    }
}
data class User(val identifier: String, val password: String)

class TokenInfo{
    lateinit var jwt: String
}

@Parcelize
data class Product(val id: Int, val name: String, val type: String, val price: Double,
                   val shipping: Double, val description: String) : Parcelable