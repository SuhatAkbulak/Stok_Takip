package com.maltepeuniversiyesi.stok


import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.maltepeuniversiyesi.stok.models.*
import com.maltepeuniversiyesi.stok.repositories.TransceiverRepository
import com.maltepeuniversiyesi.stok.retrofitcoroutines.remote.NetworkResponse
import com.maltepeuniversiyesi.stok.ui.ui.login.LoginFormState
import org.json.JSONObject
import retrofit2.HttpException

class TransceiverViewModel(private val repository: TransceiverRepository) : ViewModel() {
    private val _loginForm = MutableLiveData<LoginFormState>()
    private val _refreshData = MutableLiveData<String>()
    private val _previousData = MutableLiveData<String>()

    val loginFormState: LiveData<LoginFormState> = _loginForm
    val refreshState: LiveData<String> = _refreshData
    val previousState: LiveData<String> = _previousData

    private val TAG = "TransceiverViewModel"
    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    fun refresh() { _refreshData.value = "REFRESHED" }
    fun previousRecord() { _previousData.value = "PREVIOUS" }


    fun showRefreshButton() {
        _refreshData.value = "VISIBLE"
    }

    fun hideRefreshButton() {
        _refreshData.value = "GONE"
    }


    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 2
    }

    fun token(username: String, password: String) = liveData {
        emit(Resource.loading(data = null))
        try {
            val request = TokenRequestModel(
                username, password
            )
            emit(Resource.success(data = repository.token(request)))
        } catch (exc: HttpException) {
            emit(
                Resource.error(
                    data = null, message = JSONObject(exc.response()?.errorBody()?.string().toString()).getString("message")

                )
            )
        }catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }

    fun tasks() = liveData {
        emit(Resource.loading(data = null))
        try {
            when (val resp=repository.tasks()) {
                is NetworkResponse.Success -> emit(Resource.success(data = resp.body.data))
                is NetworkResponse.ApiError -> emit(resp.body.message.let { Resource.error(message = it,data = resp.body) })
                is NetworkResponse.NetworkError -> throw java.lang.Exception()
                is NetworkResponse.UnknownError -> throw java.lang.Exception()
            }

        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }


    fun changeStockCount(id:Int,request: ChangeStockRequest) = liveData {
        emit(Resource.loading(data = null))
        try {
            when (val resp= repository.changeStockCount(id,request)) {
                is NetworkResponse.Success -> emit(Resource.success(data = resp.body.message))
                is NetworkResponse.ApiError -> emit(resp.body.message.let { Resource.apiError(message = it,data = resp.body) })
                is NetworkResponse.NetworkError -> throw java.lang.Exception()
                is NetworkResponse.UnknownError -> throw java.lang.Exception()
            }
        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }
    fun addProduct(request: ProductRequestModel) = liveData {
        emit(Resource.loading(data = null))
        try {
            when (val resp= repository.addProduct(request)) {
                is NetworkResponse.Success -> emit(Resource.success(data = resp.body.message))
                is NetworkResponse.ApiError -> emit(resp.body.message.let { Resource.apiError(message = it,data = resp.body) })
                is NetworkResponse.NetworkError -> throw java.lang.Exception()
                is NetworkResponse.UnknownError -> throw java.lang.Exception()
            }
        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null, message = exception.message
                        ?: "Beklenmeyen bir hata oluştu"
                )
            )
        }
    }
}
