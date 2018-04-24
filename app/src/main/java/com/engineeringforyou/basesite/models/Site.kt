package com.engineeringforyou.basesite.models


data class Site(
        val operator: Operator,
        val number: String,
        val latitude: Double,
        val longitude: Double,
        val address: String,
        val obj: String,
        val description: String
)