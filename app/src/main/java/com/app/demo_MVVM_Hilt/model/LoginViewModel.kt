package com.app.demo_MVVM_Hilt.model
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.demo_MVVM_Hilt.util.AppState
import com.app.demo_MVVM_Hilt.di.BaseApiRepo
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val apiRepo: BaseApiRepo) : ViewModel() {

    private var _stateLogin = MutableLiveData<AppState<JsonElement>>()
    val stateLogin: MutableLiveData<AppState<JsonElement>> get() = _stateLogin

    val errorMessage = MutableSharedFlow<String>()

    fun validate(mobile: String, isNetworkConnection: Boolean) =
        viewModelScope.launch {
            if (mobile.isEmpty()) {
                errorMessage.emit("Please enter mobile number")
                return@launch
            }

            if (mobile.length < 4) {
                errorMessage.emit("Mobile number must be 10 digit long")
                return@launch
            }

            if (!isNetworkConnection) {
                errorMessage.emit("Internet Connection not Available")
                return@launch
            }

            errorMessage.emit("success")
        }

    fun performLogin(mobile: String) = viewModelScope.launch {
        _stateLogin.value = AppState.Loading()
        apiRepo.loginApi(mobile).collect {
            _stateLogin.value = it
        }
    }
    fun oottppapi(mobile: String,oottpp:String) = viewModelScope.launch {
        _stateLogin.value = AppState.Loading()
        apiRepo.oottppapi(mobile,oottpp).collect {
            _stateLogin.value = it
        }
    }



}