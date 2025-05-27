package com.dorukaneskiceri.spaceflightnews.util

sealed class BaseResult<out T> {
    data class Success<out T>(val data: T?): BaseResult<T>()
    data class Error(val exception: Exception): BaseResult<Nothing>()
}
