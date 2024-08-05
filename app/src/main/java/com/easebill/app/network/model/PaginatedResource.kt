package com.easebill.app.network.model

class PaginatedResource<T> {
    val nextPageNumber = 0
    val previousPageNumber = 0
    val pageNumber = 0
    val data: List<T>? = null
    val message: String? = null
    val error: Boolean = false
}