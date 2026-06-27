package com.tommy.digitalbankkyc.domain.model

data class BankInfo(
    val ifsc: String,
    val bankName: String,
    val branch: String,
    val city: String,
    val state: String,
    val micr: String
)
