package com.engineeringforyou.basesite.presentation.message.presenter

import com.engineeringforyou.basesite.presentation.message.views.MessageView

interface MessagePresenter {

    fun bind(view: MessageView)

    fun sendMessage(email: String, message: String)

    fun unbindView()
}
