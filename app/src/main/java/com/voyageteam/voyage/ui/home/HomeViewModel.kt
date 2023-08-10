package com.voyageteam.voyage.ui.home

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiCode
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.extension.toObject
import com.google.gson.Gson
import com.voyageteam.voyage.api.ApiService
import com.voyageteam.voyage.base.BaseObserver
import com.voyageteam.voyage.base.BaseViewModel
import com.voyageteam.voyage.data.Session
import com.voyageteam.voyage.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val session: Session,
    private val observer: BaseObserver
) : BaseViewModel() {

    fun getProfile(
    ) = viewModelScope.launch {
        observer(
            block = { apiService.getprofile() },
            responseListener = object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    val data = response.getJSONObject(ApiCode.DATA).toObject<User>(gson)
                    _apiResponse.emit(ApiResponse().responseSuccess())
                    session.saveUser(data)
                }

                override suspend fun onError(response: ApiResponse) {
                    super.onError(response)
                    _apiResponse.emit(ApiResponse().responseError())
                }
            })
    }

}