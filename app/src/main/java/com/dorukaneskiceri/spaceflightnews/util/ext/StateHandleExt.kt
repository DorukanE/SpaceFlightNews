package com.dorukaneskiceri.spaceflightnews.util.ext

import com.dorukaneskiceri.spaceflightnews.util.BaseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

inline fun <T> Flow<BaseResult<T>>.onSuccess(
    crossinline action: suspend (T) -> Unit
) = onEach {
    if (it is BaseResult.Success) {
        if (it.data != null) {
            action(it.data)
        }
    }
}

inline fun <T> Flow<BaseResult<T>>.onFailure(
    crossinline action: suspend (Exception) -> Unit
) = onEach {
    if (it is BaseResult.Error) {
        action(it.exception)
    }
}