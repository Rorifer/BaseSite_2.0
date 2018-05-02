package com.engineeringforyou.basesite.domain.map

import com.engineeringforyou.basesite.models.Operator

interface MapInteractor {

    fun saveOperator(operator: Operator)

    fun getOperator(): Operator

}