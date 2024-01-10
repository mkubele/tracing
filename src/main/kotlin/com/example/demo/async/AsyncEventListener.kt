package com.example.demo.async

import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async

@Async
@EventListener
annotation class AsyncEventListener
