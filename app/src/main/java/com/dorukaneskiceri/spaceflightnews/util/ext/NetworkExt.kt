package com.dorukaneskiceri.spaceflightnews.util.ext

import com.dorukaneskiceri.spaceflightnews.util.BaseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

inline fun <T, R> BaseResult<T>.onTransform(
    crossinline transform: (T) -> R
): Flow<BaseResult<R>> = flow {
    when (this@onTransform) {
        is BaseResult.Success -> {
            if (this@onTransform.data != null) {
                emit(BaseResult.Success(data = transform(this@onTransform.data)))
            } else {
                emit(BaseResult.Error(exception = IllegalStateException("BaseResult.Success data is null.")))
            }
        }

        is BaseResult.Error -> emit(BaseResult.Error(exception = this@onTransform.exception))
    }
}.catch { throwable ->
    val exception = if (throwable is Exception) {
        throwable
    } else {
        Exception(throwable)
    }
    emit(BaseResult.Error(exception = exception))
}