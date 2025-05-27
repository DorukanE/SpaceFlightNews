package com.dorukaneskiceri.spaceflightnews.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

fun <T> apiCall(
    networkHandler: NetworkHandler,
    apiCall: suspend () -> Flow<BaseResult<T>>
): Flow<BaseResult<T>> = flow {
    if (!networkHandler.isNetworkAvailable()) {
        emit(BaseResult.Error(NetworkException.NetworkConnectionFailure))
        return@flow
    }

    apiCall.invoke().collect { result ->
        emit(result)
    }
}.catch { throwable ->
    when (throwable) {
        is IOException -> emit(BaseResult.Error(NetworkException.NetworkFailure(throwable)))
        is HttpException -> {
            val code = throwable.code()
            val errorResponse = throwable.response()?.errorBody()?.string()
            emit(BaseResult.Error(NetworkException.ApiFailure(code, errorResponse)))
        }
        else -> emit(BaseResult.Error(NetworkException.UnexpectedFailure(throwable)))
    }
}

sealed class NetworkException(message: String? = null) : Exception(message) {
    data object NetworkConnectionFailure : NetworkException("Network connection failure") {
        private fun readResolve(): Any = NetworkConnectionFailure
    }

    class ApiFailure(responseCode: Int, message: String?) :
        NetworkException("API error: $responseCode - $message")

    class UnexpectedFailure(throwable: Throwable) : NetworkException(throwable.message)
    class NetworkFailure(throwable: Throwable) : NetworkException(throwable.message)
}