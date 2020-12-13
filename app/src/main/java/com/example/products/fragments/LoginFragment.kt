package com.example.products.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.products.*
import kotlinx.android.synthetic.main.fragment_login.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : Fragment() {

    companion object{
        const val DEFAULT_PREFERENCES_KEY: String = "DEFAULT_PREFERENCES"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_login, container, false)
        view.findViewById<Button>(R.id.btn_login).setOnClickListener {
            login()
        }
        return view
    }

    private fun login(){
        val email = txt_email.text.toString()
        val password = txt_password.text.toString()

        if(validateCredentials(email, password)){
            val user = User(email, password)

            val apiClient = RetrofitHelper.getInstance(getString(R.string.base_url))
            val getToken = apiClient.getToken(user)
            getToken.enqueue(object : Callback<TokenInfo> {
                override fun onResponse(call: Call<TokenInfo>, response: Response<TokenInfo>) {
                    if (response.isSuccessful) {
                        activity?.runOnUiThread {
                            run{
                                storeAccessToken(response.body()!!.jwt)
                                Toast.makeText(activity, "Autenticado correctamente!", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_loginFragment_to_listProductsFragment)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<TokenInfo>, t: Throwable) {
                    activity?.runOnUiThread {
                        run{
                            showAlert()
                        }
                    }
                }
            })
        }
    }
    private fun validateCredentials(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                txt_email.error = getString(R.string.text_field_error)
                false
            }
            TextUtils.isEmpty(password) -> {
                txt_password.error = getString(R.string.text_field_error)
                false
            }
            else -> true
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.login_alert_title))
        builder.setMessage(getString(R.string.login_alert_message))
        builder.setPositiveButton(getString(R.string.login_alert_positiveButton), null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun storeAccessToken(token: String){
        val sharedPreferencesEdit = activity?.getSharedPreferences(DEFAULT_PREFERENCES_KEY, Context.MODE_PRIVATE)?.edit()
        sharedPreferencesEdit!!.putString(getString(R.string.access_token), token)
        sharedPreferencesEdit.apply()
    }
}