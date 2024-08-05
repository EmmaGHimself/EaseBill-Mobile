package com.easebill.app.network.model

data class ErrorMessage(
    var code: Int = 0,
    var message: String? = null,
    var error: String? = null,
)
