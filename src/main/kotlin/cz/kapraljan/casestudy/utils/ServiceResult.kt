package cz.kapraljan.casestudy.utils

sealed class ServiceResult<out T> {
    data class Success<out T>(val data: T) : ServiceResult<T>()
    data class Error(val error: Exception) : ServiceResult<Nothing>()
}