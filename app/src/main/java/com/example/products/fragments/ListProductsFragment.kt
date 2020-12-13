package com.example.products.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.products.*
import kotlinx.android.synthetic.main.product.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListProductsFragment : Fragment() {

    private var productsService: ProductsService? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list_products, container, false)

        val navController = findNavController()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = RecyclerViewAdapter(navController)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val accessToken = getAccessToken()!!
        productsService = RetrofitHelper.getAuthenticatedUser(getString(R.string.base_url), accessToken)

        productsService!!.getProducts().enqueue(object: Callback<List<Product>>{
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                adapter.setData(response.body()!!)
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_product -> {
                findNavController().navigate(R.id.action_listProductsFragment_to_addProductFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getAccessToken(): String? {
        return activity?.getSharedPreferences(LoginFragment.DEFAULT_PREFERENCES_KEY, Context.MODE_PRIVATE)
            ?.getString(getString(R.string.access_token), "")
    }
}