package com.engineeringforyou.basesite.models

enum class Operator(val label: String, val code: Int) {
    MTS("МТС", 1),
    MEGAFON("МегаФон", 2),
    VIMPELCOM("Билайн", 3),
    TELE2("Теле2", 4),
    ALL("Все", 0)
}