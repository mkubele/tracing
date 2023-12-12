package com.example.demo

import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.security.Principal

open class BaseRestLoggingFilter : WebFilter {

    private val log = LoggerFactory.getLogger(javaClass)

    private val bodyMsgHelper = LoggingBodyHelper()

    private val excludePathsRegex = "^(/|/actuator.*)".toRegex()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request

        if (excludePathsRegex.matches(request.uri.path)) return chain.filter(exchange)

        val commonData = CommonLoggingData(request.method.name(), request.uri)

        return exchange.getPrincipal<Principal>()
            .switchIfEmpty(Mono.just(Principal { "" }))
            .flatMap {
                // First log of the chain - logging the request - method, path, query, user
                log.info(LoggingMessageHelper.getRequestLogMsg(commonData))

                chain.filter(
                    exchange.mutate().request(CustomLoggingHttpRequestDecorator(exchange.request, commonData))
                        .response(CustomLoggingHttpResponseDecorator(exchange.response, commonData)).build()
                )
            }.doOnSuccess {
                // Successful log message for response - method, path, status, duration
                val httpStatus = exchange.response.statusCode ?: HttpStatus.OK
                log.info(LoggingMessageHelper.getResponseLogMsg(commonData, httpStatus))
            }.doOnError { e ->
                val status = if (e is ResponseStatusException) e.statusCode else exchange.response.statusCode
                    ?.takeIf { !it.is2xxSuccessful }
                    ?: HttpStatus.INTERNAL_SERVER_ERROR
                log.error(LoggingMessageHelper.getResponseLogMsg(commonData, status), e)
            }
    }

    inner class CustomLoggingHttpRequestDecorator(
        request: ServerHttpRequest,
        private val commonData: CommonLoggingData
    ) : ServerHttpRequestDecorator(request) {

        override fun getBody(): Flux<DataBuffer> = super.getBody()
            .doOnNext { dataBuffer: DataBuffer ->
                // Logging content of the request body - with method and path, empty JSON string if empty body
                log.debug(
                    LoggingMessageHelper.getBodyLogMsg(
                        commonData,
                        LoggingMessageHelper.ReqOrResponseEnum.REQUEST,
                        bodyMsgHelper.getBodyContent(dataBuffer, delegate.headers)
                    )
                )
            }
    }

    inner class CustomLoggingHttpResponseDecorator(
        response: ServerHttpResponse,
        private val commonData: CommonLoggingData
    ) : ServerHttpResponseDecorator(response) {

        override fun writeWith(
            body: Publisher<out DataBuffer>
        ): Mono<Void> = super.writeWith(
            Flux.from(body)
                .doOnNext { dataBuffer: DataBuffer ->
                    // Logging content of the response body - with method and path, empty JSON string if empty body
                    log.debug(
                        LoggingMessageHelper.getBodyLogMsg(
                            commonData,
                            LoggingMessageHelper.ReqOrResponseEnum.RESPONSE,
                            bodyMsgHelper.getBodyContent(dataBuffer, delegate.headers)
                        )
                    )
                }
        )
    }

}
