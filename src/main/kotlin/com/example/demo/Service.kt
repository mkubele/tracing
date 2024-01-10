package com.example.demo

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class Service(
    private val eventPublisher: ApplicationEventPublisher
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun doSomething() {
        logger.info("Doing something")
        eventPublisher.publishEvent(Event())
    }
}

data class Event(val message: String = "Hello, World!")
