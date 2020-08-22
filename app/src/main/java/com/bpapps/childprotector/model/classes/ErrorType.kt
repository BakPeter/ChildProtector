package com.bpapps.childprotector.model.classes

import androidx.annotation.IntDef

class ErrorType(@Type val type: Int) {

    companion object {
        @Retention(AnnotationRetention.SOURCE)
        @IntDef
        annotation class Type


        const val NOUN = 0
        const val MISSING_USER_PHONE_NUMBER = 1
        const val MISSING_AT_LEAST_ONE_PARENT_CODE = 2
        const val USER_NOT_IN_THE_DATA_BASE = 3
    }
}