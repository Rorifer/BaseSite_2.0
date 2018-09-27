package com.engineeringforyou.basesite.utils

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import com.engineeringforyou.basesite.R

class MessageDialog : DialogFragment() {

    lateinit var positive: () -> Unit
    lateinit var negative: () -> Unit

    companion object {
        const val TAG = "MessageDialog"
        private const val ARG_MESSAGE = "arg-message"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_BUTTON_POSITIVE = "arg_positive"
        private const val ARG_BUTTON_NEGATIVE = "arg_negative"
        private const val ARG_BUTTON_NEGATIVE_ENABLE = "arg_button_negative_enable"

        fun getInstance(message: String,
                        positiveButtonText: String? = null,
                        negativeButtonText: String? = null,
                        textTitle: String? = null,
                        isEnableNegativeButton: Boolean = true
        ): MessageDialog {
            val messageDialog = MessageDialog()
            val args = Bundle()
            args.putString(ARG_MESSAGE, message)
            args.putString(ARG_BUTTON_POSITIVE, positiveButtonText)
            args.putString(ARG_BUTTON_NEGATIVE, negativeButtonText)
            args.putBoolean(ARG_BUTTON_NEGATIVE_ENABLE, isEnableNegativeButton)
            args.putString(ARG_TITLE, textTitle)
            messageDialog.arguments = args
            return messageDialog
        }
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (r: IllegalStateException) {
        }
    }

     fun show(manager: FragmentManager?) {
        try {
            super.show(manager, TAG)
        } catch (r: IllegalStateException) {
        }
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val message = arguments?.getString(ARG_MESSAGE)
        val positiveButtonText = arguments?.getString(ARG_BUTTON_POSITIVE)
                ?: getString(R.string.ok)
        val negativeButtonText = arguments?.getString(ARG_BUTTON_NEGATIVE)
                ?: getString(R.string.cancel)
        val titleText = arguments?.getString(ARG_TITLE) ?: getString(R.string.attention)
        val isEnableNegativeButton = arguments?.getBoolean(ARG_BUTTON_NEGATIVE_ENABLE, true) ?: true

        val builder = AlertDialog.Builder(context!!)
        builder.setMessage(message)
        builder.setTitle(titleText)
        builder.setPositiveButton(positiveButtonText) { dialogInterface, _ ->
            dialogInterface.dismiss()
            if (::positive.isInitialized) positive()
        }
        if (isEnableNegativeButton) builder.setNegativeButton(negativeButtonText) { dialogInterface, _ ->
            dialogInterface.dismiss()
            if (::negative.isInitialized) negative()
        }

        isCancelable = false
        return builder.create()
    }
}