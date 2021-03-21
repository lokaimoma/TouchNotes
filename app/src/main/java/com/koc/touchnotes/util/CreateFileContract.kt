package com.koc.touchnotes.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

/**
Created by kelvin_clark on 3/21/2021 9:50 AM
 */
class CreateFileContract(val filename: String, val fileExtension: String):
    ActivityResultContract<Int, Uri?>() {

    override fun createIntent(context: Context, input: Int?): Intent {
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)

        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        TODO("Not yet implemented")
    }
}