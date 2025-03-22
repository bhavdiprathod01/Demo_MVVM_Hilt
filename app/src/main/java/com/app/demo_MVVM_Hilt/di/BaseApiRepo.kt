package com.app.demo_MVVM_Hilt.di

import android.content.Context
import com.app.demo_MVVM_Hilt.util.AppState

import com.google.gson.JsonElement
import dagger.hilt.android.qualifiers.ApplicationContext

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BaseApiRepo @Inject constructor(
    @ApplicationContext val context: Context,
    private val baseApiService: ApiService
) :
    BaseApiResponse() {

    fun loginApi(mobile: String): Flow<AppState<JsonElement>> =
        flow {
            emit(safeApiCall(context) { baseApiService.doLogin(mobile) })
        }.flowOn(Dispatchers.IO)
    fun oottppapi(mobile: String,oottpp:String): Flow<AppState<JsonElement>> =
        flow {
            emit(safeApiCall(context) { baseApiService.oottpp(mobile,oottpp) })
        }.flowOn(Dispatchers.IO)
}
