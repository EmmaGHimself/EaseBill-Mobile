package com.easebill.app.util

/**
 * @Author GBAYESOLA EMMANUEL
 * @Project EASEBILL APP
 * @Date 01/07/2024
 * @Email EGBYAESOLA@GMAIL.COM
 */
data class Resources<T>(
    val status: Status,
    val message: String? = null,
    val data: T? = null
){
    companion object {
        fun <T> loading() : Resources<T> = Resources(Status.Loading, null, null)
        fun <T> error(message: String) : Resources<T> = Resources(Status.Error, message, null)
        fun <T> success(message: String, data: T?) : Resources<T> = Resources(Status.Success, message, data)
    }
}
