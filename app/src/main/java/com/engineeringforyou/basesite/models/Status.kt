package com.engineeringforyou.basesite.models

enum class Status(val description: String, val code: Int) {
    ACTIVE("действующая БС", 0),
    DISMANTLED("БС демонтирована",1),
    NOT_EXIST("БС не существует",2),
}