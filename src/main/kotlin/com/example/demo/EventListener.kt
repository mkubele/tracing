package com.example.demo

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class EventListener {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Async
    @EventListener
    fun handleEvent(event: Event) {
        logger.info("Event received: ${event.message}")
    }
}
