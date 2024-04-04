package com.example.movielibrary

data class User(
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var gender: String? = null,
    var birthday: String? = null,
    var phoneNumber: String? = null,
    var country: String? = null,
    var favouriteGenre: String? = null,
    var favouriteDirector: String? = null,
    var bio: String? = null,
    val favourites: Map<String, Any>? = null
)