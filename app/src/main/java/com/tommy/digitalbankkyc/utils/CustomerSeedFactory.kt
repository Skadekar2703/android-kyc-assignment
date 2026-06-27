package com.tommy.digitalbankkyc.utils

private val ifscCodes = listOf(
    "HDFC0CAGSBK",
    "SBIN0000001",
    "ICIC0000001",
    "PUNB0244200",
    "UTIB0000001"
)

fun deterministicIfsc(userId: Int): String = ifscCodes[userId % ifscCodes.size]

fun deterministicBalance(userId: Int): Double {
    val min = 5_000L
    val range = 495_000L
    val seededValue = ((userId.toLong() * 92_821L) + 17_711L) % range
    return (min + seededValue).toDouble()
}
