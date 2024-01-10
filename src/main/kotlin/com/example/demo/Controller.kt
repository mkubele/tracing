package com.example.demo

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(
    private val service: Service,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/hello")
    suspend fun hello(): Response {
        logger.info("Hello, World!")
        service.doSomething()
        return Response("Hello, World!")
    }
}

data class Response(val message: String)
