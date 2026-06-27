package com.tommy.digitalbankkyc.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun createSelfieFile(context: Context, customerId: Int): File {
    val directory = File(context.filesDir, "selfies").apply { mkdirs() }
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    return File(directory, "customer_${customerId}_$timestamp.jpg")
}
