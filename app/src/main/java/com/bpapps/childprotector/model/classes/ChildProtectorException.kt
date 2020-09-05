package com.bpapps.childprotector.model.classes

class ChildProtectorException(
    val errorType: @ErrorType Int,
    message: String?,
    private val exception: Exception?
) :
    Exception(message ?: "") {
}