package com.example.demo

import java.net.URI

data class CommonLoggingData(
    val methodValue: String,
    val uri: URI
) {
    val startTimeMilis: Long = System.currentTimeMillis()
    val path: String = uri.path
    val query: String? = uri.query

    fun getDurationFromStart(): Long = System.currentTimeMillis() - startTimeMilis
}
