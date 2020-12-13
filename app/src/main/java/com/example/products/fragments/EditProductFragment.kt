package com.example.products.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.products.Product
import com.example.products.ProductsService
import com.example.products.R
import com.example.products.RetrofitHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_add_product.*
import kotlinx.android.synthetic.main.fragment_edit_product.*
import kotlinx.android.synthetic.main.fragment_edit_product.txt_product_desc
import kotlinx.android.synthetic.main.fragment_edit_product.txt_product_name
import kotlinx.android.synthetic.main.fragment_edit_product.txt_product_price
import kotlinx.android.synthetic.main.fragment_edit_product.txt_product_shipping
import kotlinx.android.synthetic.main.fragment_edit_product.txt_product_type
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProductFragment : Fragment() {

    lateinit var product: Product

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
        val view = inflater.inflate(R.layout.fragment_edit_product, container, false)

        product = arguments?.getParcelable("product")!!

        view.findViewById<EditText>(R.id.txt_product_name).setText(product.name)
        view.findViewById<EditText>(R.id.txt_product_type).setText(product.type)
        view.findViewById<EditText>(R.id.txt_product_desc).setText(product.description)
        view.findViewById<EditText>(R.id.txt_product_price).setText(product.price.toString())
        view.findViewById<EditText>(R.id.txt_product_shipping).setText(product.shipping.toString())

        productsService = RetrofitHelper.getAuthenticatedUser(getString(R.string.base_url), getAccessToken()!!)

        view.findViewById<Button>(R.id.btn_edit_product).setOnClickListener {
            updateProduct()
        }

        setHasOptionsMenu(true)
        return view
    }

    private fun updateProduct(){
        if(validateFields()){
            val name = txt_product_name.text.toString()
            val type = txt_product_type.text.toString()
            val desc = txt_product_desc.text.toString()
            val price = txt_product_price.text.toString().toDouble()
            val shipping = txt_product_shipping.text.toString().toDouble()

            val updatedProduct = Product(product.id, name, type, price, shipping, desc)

            productsService!!.updateProduct(product.id, updatedProduct).enqueue(object : Callback<Product>{
                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    if(response.isSuccessful){
                        Toast.makeText(activity, "Product updated successfully!", Toast.LENGTH_SHORT).show()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_product, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete_product -> {
                run {
                    showAlert()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.delete_alert_title))
        builder.setMessage(getString(R.string.delete_alert_message))
        builder.setPositiveButton(getString(R.string.delete_alert_positiveButton)) { _, _ ->
            productsService!!.deleteProduct(product.id).enqueue(object : Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful){
                        Toast.makeText(activity, "Product deleted successfully!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.listProductsFragment)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }
        builder.setNegativeButton(getString(R.string.delete_alert_negativeButton), null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun getAccessToken(): String? {
        return activity?.getSharedPreferences(LoginFragment.DEFAULT_PREFERENCES_KEY, Context.MODE_PRIVATE)
            ?.getString(getString(R.string.access_token), "")
    }
}