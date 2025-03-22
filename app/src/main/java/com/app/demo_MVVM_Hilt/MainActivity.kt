package com.app.demo_MVVM_Hilt

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.app.demo_MVVM_Hilt.databinding.ActivityMainBinding
import com.app.demo_MVVM_Hilt.databinding.LoaderLayoutBinding
import com.app.demo_MVVM_Hilt.model.LoginViewModel
import com.app.demo_MVVM_Hilt.util.AppState
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

import kotlinx.coroutines.launch
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val viewModel by viewModels<LoginViewModel>()
    private var loader: Dialog? = null
    private var loaderBinding: LoaderLayoutBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonLogin.setOnClickListener {

            viewModel.validate(
                binding.editTextPhone.text.toString().trim(),
              true
            )
        }
        lifecycleScope.launch {
            observeViewModel()
        }

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
        if (true) viewModel.oottppapi("6357891042",binding.editTextPhone.text.toString().trim())
        else showToastMessage("No Internet..")
    }


    private fun parseResponse(jsonElement: JsonElement) {
        val data = jsonElement.asJsonObject
        if (data.get("status").asString == "1") {
            showToastMessage(data.get("msg").toString())
//            lifecycleScope.launch {
//                delay(500)
//                startActivity(
//                    Intent(
//                        this@MainActivity,
//                        MainActivity::class.java
//                    )
//                )
//            }
            saveUserData(data)
        } else {
            showToastMessage(data.get("message").asString)
        }
    }


    private fun saveUserData(data: JsonObject) {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_id", data.get("user_id").asString)
            putString("profile_type", data.get("profile_type").asString)
            putString("device_id", data.get("device_id").asString)
            putString("user_phone", data.get("user_phone").asString)
            putString("user_email", data.get("user_email").asString)
            putString("user_city", data.get("user_city").asString)
            putString("user_address", data.get("user_address").asString)
            putString("paid_status", data.get("paid_status").asString)
            apply() // Apply changes asynchronously
            showToastMessage(data.get("user_phone").toString())
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