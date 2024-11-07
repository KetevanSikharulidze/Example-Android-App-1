package com.example.examplemobileapp1.models

data class Transaction(
    val imageUrl: String ?= null,
    val recipient: String,
    val description: String,
    val amount: String,
    val currency: String
)
