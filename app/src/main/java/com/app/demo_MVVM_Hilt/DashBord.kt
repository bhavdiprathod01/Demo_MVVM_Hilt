package com.app.demo_MVVM_Hilt

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.demo_MVVM_Hilt.Adapter.FestivalAdapter
import com.app.demo_MVVM_Hilt.databinding.ActivityDashBordBinding
import com.app.demo_MVVM_Hilt.databinding.LoaderLayoutBinding
import com.app.demo_MVVM_Hilt.model.Festival
import com.app.demo_MVVM_Hilt.model.LoginViewModel
import com.app.demo_MVVM_Hilt.util.AppState
import com.google.gson.Gson
import com.google.gson.JsonElement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class DashBord : AppCompatActivity() {
    private lateinit var binding: ActivityDashBordBinding
    val viewModel by viewModels<LoginViewModel>()
    private lateinit var languageList : List<Festival>
    val adapter = FestivalAdapter(emptyList())
    var festival: List<Festival> = listOf()
    private var loader: Dialog? = null
    private var loaderBinding: LoaderLayoutBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyView.layoutManager = LinearLayoutManager(this)
        binding.recyView.adapter = adapter
        callLoginApi()
        lifecycleScope.launch {
            observeViewModel()
        }


    }
    private  fun observeViewModel() {
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

//        viewModel.errorMessage.collect {
//            if (it.isNullOrEmpty()) return@collect
//            if (it != "success") Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
//            else callLoginApi()
//        }
    }

    private fun parseResponse(jsonElement: JsonElement) {
        val data = jsonElement.asJsonObject
        val gson = Gson()
        if (data.get("status").asString == "1") {
//            showToastMessage(data.get("msg").toString())
            val festivalsJsonArray = data.getAsJsonArray("festivals")
            val festivalsList = gson.fromJson(festivalsJsonArray, Array<Festival>::class.java).toList()

            // Now you can use the festivalsList to update your UI or perform other operations
            festival = festivalsList.toList()
            adapter.festivals = festival
            adapter.notifyDataSetChanged()
        } else {
            showToastMessage(data.get("message").asString)
        }
    }
    private fun callLoginApi() {
        if (true) viewModel.festival("755","c1d56822fef1d4a2b849e219093e4538","0")
        else showToastMessage("No Internet..")
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
