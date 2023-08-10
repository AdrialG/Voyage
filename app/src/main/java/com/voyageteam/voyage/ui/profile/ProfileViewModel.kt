package com.voyageteam.voyage.ui.profile

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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel  @Inject constructor(
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

    fun updateProfileName(name: String) =
        viewModelScope.launch {
            observer(
                block = {
                    apiService.updateProfileName(name)
                },
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        _apiResponse.emit(ApiResponse().responseSuccess("Profile Updated"))
                    }

                    override suspend fun onError(response: ApiResponse) {
                        super.onError(response)
                        _apiResponse.emit(ApiResponse().responseError())
                    }
                })
        }

    fun updateProfile(name: String, photo: File) =
        viewModelScope.launch {
            val fileBody = photo.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("photo", photo.name, fileBody)
            observer(
                block = {
                    apiService.updateProfile(name, filePart)
                },
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        _apiResponse.emit(ApiResponse().responseSuccess("Profile Updated"))
                    }

                    override suspend fun onError(response: ApiResponse) {
                        super.onError(response)
                        _apiResponse.emit(ApiResponse().responseError())
                    }
                })
        }

    fun updatePassword(oldPassword : String, newPassword : String, passwordConfirmation : String) =
        viewModelScope.launch {
            observer(
                block = {
                    apiService.updatePassword(oldPassword, newPassword, passwordConfirmation)
                },
                responseListener = object : ApiObserver.ResponseListener {
                    override suspend fun onSuccess(response: JSONObject) {
                        _apiResponse.emit(ApiResponse().responseSuccess("Profile Updated"))
                    }

                    override suspend fun onError(response: ApiResponse) {
                        super.onError(response)
                        _apiResponse.emit(ApiResponse().responseError())
                    }
                })
        }

    fun logout() = viewModelScope.launch {
        ApiObserver({ apiService.logout() },
            false, object : ApiObserver.ResponseListener {
                override suspend fun onSuccess(response: JSONObject) {
                    session.clearAll()
                }
            })
    }

}