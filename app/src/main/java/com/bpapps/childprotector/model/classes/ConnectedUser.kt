package com.bpapps.childprotector.model.classes

import java.util.*

class ConnectedUser(
    val _id: UUID,
    val _parentId: UUID,
    val _childId: UUID,
    val _connectivityCode: String
) {

}