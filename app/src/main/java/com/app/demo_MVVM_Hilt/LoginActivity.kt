package com.app.demo_MVVM_Hilt

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.demo_MVVM_Hilt.databinding.ActivityLoginBinding
import com.app.demo_MVVM_Hilt.databinding.LoaderLayoutBinding

import com.app.demo_MVVM_Hilt.model.LoginViewModel
import com.app.demo_MVVM_Hilt.util.AppState
import com.google.gson.JsonElement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    val viewModel by viewModels<LoginViewModel>()
    private var loader: Dialog? = null
    private var loaderBinding: LoaderLayoutBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonLogin.setOnClickListener {

            viewModel.validate(
                binding.editTextPhone.text.toString().trim(),
                isNetworkAvailable()
            )
        }
        lifecycleScope.launch {
            observeViewModel()
        }
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private suspend fun observeViewModel() {
        viewModel.stateLogin.observe(this) {
            when (it) {
                is AppState.Loading -> {
                    isLoading(true)
                }

                is AppState.Success -> {
                    isLoading(false)
                    parseResponse(it.model)
                }

                is AppState.Error -> {
                    isLoading(false)
                    showToastMessage(it.error)
                }
            }
        }

        viewModel.errorMessage.collect {
            if (it.isNullOrEmpty()) return@collect
            if (it != "success") Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            else callLoginApi()
        }
    }
    private fun callLoginApi() {
        if (isNetworkAvailable()) viewModel.performLogin(binding.editTextPhone.text.toString().trim())
        else showToastMessage("No Internet..")
    }


    private fun parseResponse(jsonElement: JsonElement) {
        val data = jsonElement.asJsonObject
        if (data.get("status").asString == "1") {
            showToastMessage(data.get("msg").toString())
            lifecycleScope.launch {
                delay(500)
                startActivity(
                    Intent(
                        this@LoginActivity,
                        MainActivity::class.java
                    )
                )
            }
        } else {
            showToastMessage(data.get("message").asString)
        }
    }



    fun showToastMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    fun isLoading(shown: Boolean) {
        if (!shown) {
            if (loader != null && loader!!.isShowing) {
                loader!!.dismiss()
            }
        } else {
            if (loader == null) {
                loaderBinding = LoaderLayoutBinding.inflate(layoutInflater)
                loader = Dialog(this, R.style.Loader).also {
                    it.setCancelable(false)
                    it.setContentView(loaderBinding!!.root)
                }
                loader?.window?.let {
                    it.setLayout(MATCH_PARENT, MATCH_PARENT)
                    it.setBackgroundDrawable(ColorDrawable(0))
                }
                loader?.show()
            } else {
                if (!loader?.isShowing!!) {
                    loader!!.show()
                }
            }
        }
    }
}
