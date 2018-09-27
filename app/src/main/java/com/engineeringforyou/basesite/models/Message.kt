package com.engineeringforyou.basesite.models


data class Message(

        val email: String,

        val message: String,

        val userAndroidId: String,

        var timestamp: Long,

        val date: String

        )