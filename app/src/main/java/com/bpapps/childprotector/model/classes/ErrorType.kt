package com.bpapps.childprotector.model.classes

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
annotation class ErrorType {
    companion object {
        const val DEBUG = -1
        const val NOUN = 0
        const val MISSING_CHILD_PHONE_NUMBER = 1
        const val MISSING_AT_LEAST_ONE_PARENT_CODE = 2
        const val MISSING_PARENT_NAME = 3
        const val MISSING_PARENT_PHONE_NUMBER = 4
        const val ENTER_AT_LEAST_ONE_CHILD = 5
        const val USER_NOT_IN_THE_DATA_BASE = 6
        const val ERROR_LOADING_CONNECTED_USER = 7
        const val EXTERNAL_ERROR = 8
        const val CONNECTIVITY_CODE_NOT_IN_THE_DATA_BASE = 9
        const val CONNECTIVITY_CODE_AND_CHILD_PHONE_NUMBER_DO_NOT_MATCH = 10
        const val PHONE_NOT_IN_THE_DATA_BASE = 11

    }
}