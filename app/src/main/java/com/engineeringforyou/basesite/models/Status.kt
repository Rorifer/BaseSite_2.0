package com.engineeringforyou.basesite.models

enum class Status(val description: String, val code: Int) {
    ACTIVE("действующая БС", 1),
    DISMANTLED("БС демонтирована",2),
    NOT_EXIST("БС не существует",3),
}