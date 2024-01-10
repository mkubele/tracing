package com.example.demo

import com.example.demo.async.AsyncEventListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EventListener {

    private val logger = LoggerFactory.getLogger(javaClass)

    @AsyncEventListener
    fun handleEvent(event: Event) {
        logger.info("Event received: ${event.message}")
    }
}
