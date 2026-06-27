package com.tommy.digitalbankkyc.utils

private const val CACHE_TTL_MILLIS = 24 * 60 * 60 * 1000L

fun isCacheExpired(updatedAt: Long?, now: Long = System.currentTimeMillis()): Boolean {
    if (updatedAt == null || updatedAt == 0L) return true
    return now - updatedAt >= CACHE_TTL_MILLIS
}
