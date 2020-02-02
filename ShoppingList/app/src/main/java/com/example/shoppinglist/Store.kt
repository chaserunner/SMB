package com.example.shoppinglist

import java.util.*

data class Store constructor(var name: String = "",
                             var location: Location = Location(),
                             var radius: Int = 0,
                             var description: String = "",
                             var isFavorite: Boolean = false,
                             var id: String = UUID.randomUUID().toString())

data class Location constructor(var latitude: Double = 0.0,
                                var longitude: Double = 0.0)