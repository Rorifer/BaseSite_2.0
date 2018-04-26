package com.engineeringforyou.basesite.models

enum class Status(val description: String) {
    ACTIVE("действующая"),
    DISMANTLED("демонтирована"),
    NOT_EXIST("не существует"),
}