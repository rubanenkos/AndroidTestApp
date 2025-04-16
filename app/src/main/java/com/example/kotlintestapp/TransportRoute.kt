package com.example.kotlintestapp


data class TransportRoute(
    val id: Int,
    val hospitalName: String,
    val bankName: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val requestBloodId: Int
)