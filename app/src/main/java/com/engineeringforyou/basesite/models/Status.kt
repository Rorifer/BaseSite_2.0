package com.engineeringforyou.basesite.models

enum class Status(val description: String) {
    ACTIVE("действующая БС"),
    DISMANTLED("БС демонтирована"),
    NOT_EXIST("БС не существует"),
}