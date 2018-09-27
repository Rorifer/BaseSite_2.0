package com.engineeringforyou.basesite.domain.message

import io.reactivex.Completable

interface MessageInteractor {

     fun sendMessage(email: String, message: String): Completable

}