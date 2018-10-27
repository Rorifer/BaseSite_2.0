package com.engineeringforyou.basesite.models

data class Job(
        var linkSiteUid: String? = null,
        var linkSiteOperator: Operator? = null,
        val operatorPosition: Int,
        val number: String,
        val address: String,
        val description: String,
        val price: String,
        val contact: String
)
