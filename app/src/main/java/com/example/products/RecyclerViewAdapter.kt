package com.example.products

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.product.view.*

class RecyclerViewAdapter(private val navController: NavController): Adapter<RecyclerViewAdapter.AdapterViewHolder>(){
    private var listOfProducts = emptyList<Product>()

    class AdapterViewHolder(linearLayout: LinearLayout):RecyclerView.ViewHolder(linearLayout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val linearLayout = LayoutInflater.from(parent.context).inflate(R.layout.product, parent, false) as LinearLayout
        return AdapterViewHolder(linearLayout)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val product = listOfProducts[position]
        holder.itemView.product_name.text = product.name
        holder.itemView.product_type.text = product.type
        holder.itemView.product_description.text = product.description
        holder.itemView.product_price.text = product.price.toString()
        holder.itemView.product_shipping.text = product.shipping.toString()

        holder.itemView.btn_launch_update.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("product", product)
            navController.navigate(R.id.action_listProductsFragment_to_editProductFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return listOfProducts.count()
    }

    fun setData(listOfProducts: List<Product>){
        this.listOfProducts = listOfProducts
        notifyDataSetChanged()
    }
}