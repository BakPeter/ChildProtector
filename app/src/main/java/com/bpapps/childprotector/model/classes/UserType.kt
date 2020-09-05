package com.bpapps.childprotector.model.classes

import androidx.annotation.IntDef

@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
@IntDef(UserType.PARENT, UserType.CHILD)
annotation class UserType() {
    companion object {
        const val PARENT = 1
        const val CHILD = 2
    }
}