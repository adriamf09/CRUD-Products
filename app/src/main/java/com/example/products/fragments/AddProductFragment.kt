package com.example.products.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.products.Product
import com.example.products.ProductsService
import com.example.products.R
import com.example.products.RetrofitHelper
import kotlinx.android.synthetic.main.fragment_add_product.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddProductFragment : Fragment() {

    private var productsService: ProductsService? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        productsService = RetrofitHelper.getAuthenticatedUser(getString(R.string.base_url), getAccessToken()!!)

        view.findViewById<Button>(R.id.btn_save_product).setOnClickListener {
            saveProduct()
        }
        return view
    }

    private  fun saveProduct(){
        if(validateFields()){
            val name = txt_product_name.text.toString()
            val type = txt_product_type.text.toString()
            val desc = txt_product_desc.text.toString()
            val price = txt_product_price.text.toString().toDouble()
            val shipping = txt_product_shipping.text.toString().toDouble()

            val product = Product(0, name, type, price, shipping, desc)

            productsService!!.createProduct(product).enqueue(object : Callback<Product> {
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    if (response.isSuccessful) {
                        Toast.makeText(activity, "Product added successfully!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.listProductsFragment)
                    }
                }

                override fun onFailure(call: Call<Product>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun validateFields() : Boolean{
        return when {
            TextUtils.isEmpty(txt_product_name.text) -> {
                txt_product_name.error = getString(R.string.fieldError)
                false
            }
            TextUtils.isEmpty(txt_product_type.text) -> {
                txt_product_type.error = getString(R.string.fieldError)
                false
            }
            TextUtils.isEmpty(txt_product_desc.text) -> {
                txt_product_desc.error = getString(R.string.fieldError)
                false
            }
            TextUtils.isEmpty(txt_product_price.text) -> {
                txt_product_price.error = getString(R.string.fieldError)
                false
            }
            TextUtils.isEmpty(txt_product_shipping.text) -> {
                txt_product_shipping.error = getString(R.string.fieldError)
                false
            }
            else -> true
        }
    }

    private fun getAccessToken(): String? {
        return activity?.getSharedPreferences(LoginFragment.DEFAULT_PREFERENCES_KEY, Context.MODE_PRIVATE)
            ?.getString(getString(R.string.access_token), "")
    }
}