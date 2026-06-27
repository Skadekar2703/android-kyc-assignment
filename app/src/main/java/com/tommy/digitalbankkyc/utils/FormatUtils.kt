package com.tommy.digitalbankkyc.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale

/**
 * Formats a numeric amount with the corresponding currency symbol and rules.
 * Handles Indian Rupees (INR) specifically by applying Locale("en", "IN").
 */
fun formatCurrency(amount: Double, currencyCode: String): String {
    val locale = if (currencyCode.uppercase() == "INR") Locale.forLanguageTag("en-IN") else Locale.getDefault()
    return try {
        val format = NumberFormat.getCurrencyInstance(locale)
        format.currency = Currency.getInstance(currencyCode.uppercase())
        format.format(amount)
    } catch (e: Exception) {
        val symbol = if (currencyCode.uppercase() == "INR") "₹" else currencyCode
        String.format("%s%,.2f", symbol, amount)
    }
}

/**
 * Masks an account number, displaying only the last 4 digits visible and preceding characters replaced
 * with a bullet symbol, grouped into clusters of 4 characters for premium visual design.
 */
fun maskAccountNumber(accountNumber: String): String {
    if (accountNumber.length <= 4) return accountNumber
    val visibleDigits = 4
    val maskedLength = accountNumber.length - visibleDigits
    val mask = "•".repeat(maskedLength)
    val fullString = mask + accountNumber.takeLast(visibleDigits)
    return fullString.chunked(4).joinToString(" ")
}

/**
 * Formats a Date string of format YYYY-MM-DD into a human-readable "dd MMM yyyy" format.
 */
fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
        val date = inputFormat.parse(dateString)
        if (date != null) outputFormat.format(date) else dateString
    } catch (e: Exception) {
        dateString
    }
}
