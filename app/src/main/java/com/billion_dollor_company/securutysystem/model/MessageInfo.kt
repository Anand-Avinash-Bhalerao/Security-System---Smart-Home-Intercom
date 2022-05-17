package com.billion_dollor_company.securutysystem.model

data class MessageInfo(
    var name: String = "",
    var text: String = "",
    var uid: String = "",
    var sentFromDevice: Boolean = false,
    var isDevice: Boolean = false,
    var dp_bitmap: String = "",
    var picPresent: Boolean = false,
    var photo: String = "",
    var time:String = ""
)