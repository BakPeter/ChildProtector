package com.bpapps.childprotector.model.classes

import androidx.annotation.IntDef


class UserType(@Type val type: Int) {
    companion object {
        @Retention(AnnotationRetention.SOURCE)
        @IntDef(PARENT, CHILD)
        annotation class Type

        const val PARENT = 1
        const val CHILD = 2
    }

    override fun toString(): String {
        return when (type) {
            PARENT ->
                "PARENT"
            CHILD ->
                "CHILD"
            else -> "NOT_DEFINED"
        }
    }
}