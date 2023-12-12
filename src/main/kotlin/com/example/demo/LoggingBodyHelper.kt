package com.example.demo

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.util.MimeType
import java.io.ByteArrayOutputStream
import java.nio.channels.Channels
import java.nio.charset.Charset

class LoggingBodyHelper(
    allowedContentTypeSet: Set<MimeType> = emptySet()
) {

    private val finalAllowedContentTypes = when {
        allowedContentTypeSet.isEmpty() -> setOf(APPLICATION_JSON)
        !allowedContentTypeSet.contains(APPLICATION_JSON) -> allowedContentTypeSet + APPLICATION_JSON
        else -> allowedContentTypeSet
    }

    fun getBodyContent(
        dataBuffer: DataBuffer,
        headers: HttpHeaders
    ): String? = if (!doShowBody(headers)) {
        null
    } else {
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            Channels.newChannel(byteArrayOutputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer())
            String(byteArrayOutputStream.toByteArray(), Charset.defaultCharset())
                .takeIf { it.isNotBlank() && it != "null" }
        }
    }

    private fun doShowBody(headers: HttpHeaders): Boolean = headers.contentType
        ?.isPresentIn(finalAllowedContentTypes)
        ?: false

}
