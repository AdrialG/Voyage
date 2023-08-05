package com.voyageteam.voyage.ui.register

import androidx.lifecycle.viewModelScope
import com.crocodic.core.api.ApiObserver
import com.crocodic.core.api.ApiResponse
import com.voyageteam.voyage.api.ApiService
import com.voyageteam.voyage.base.BaseViewModel
import com.voyageteam.voyage.data.response.RegisterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiService: ApiService
): BaseViewModel() {

    private val _registerResponse = MutableSharedFlow<RegisterResponse>()

    fun register(
        name: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ) = viewModelScope.launch {

        ApiObserver.run(
            block = {apiService.register(name, email, password, passwordConfirmation)},
            toast = false,
            listener = object : ApiObserver.ModelResponseListener<RegisterResponse> {

                override suspend fun onLoading(response: RegisterResponse) {
                    _registerResponse.emit(response)
                    _apiResponse.emit(ApiResponse().responseLoading("Hacking You Into The Mainframe…"))
                }

                override suspend fun onSuccess(response: RegisterResponse) {
                    _registerResponse.emit(response)
                    _apiResponse.emit(ApiResponse().responseSuccess("You're Good To Go!"))
                }
            }
        )
    }

}