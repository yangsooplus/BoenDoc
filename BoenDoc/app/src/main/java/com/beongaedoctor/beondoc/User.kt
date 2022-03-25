package com.beongaedoctor.beondoc

data class User (
    var email: String = "",
    var password: String = "",
    var name: String = "",
    var height: Double = 0.0,
    var weight: Double = 0.0,
    var sex: Int = 0,
    var age: Int = 0,
    var anamnesis: List<String> = emptyList<String>()
)