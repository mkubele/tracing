package com.example.demo

import org.springframework.http.HttpStatusCode
import java.util.concurrent.TimeUnit

object LoggingMessageHelper {

    enum class ReqOrResponseEnum(val logString: String, val arrowString: String) {
        REQUEST("Request", "<--"),
        RESPONSE("Response", "-->");
    }

    fun getRequestLogMsg(
        commonData: CommonLoggingData
    ): String = ReqOrResponseEnum.REQUEST.arrowString +
            getMainLogMsg(commonData) +
            getQueryLogMsg(commonData.query)

    fun getResponseLogMsg(
        commonData: CommonLoggingData,
        httpStatus: HttpStatusCode
    ): String = ReqOrResponseEnum.RESPONSE.arrowString +
            getStatusLogMsg(httpStatus) +
            getMainLogMsg(commonData) +
            getDurationLogMsg(commonData.getDurationFromStart())

    fun getBodyLogMsg(
        commonData: CommonLoggingData,
        type: ReqOrResponseEnum,
        bodyContent: String? = null
    ): String = getMainLogMsg(commonData) + " | ${type.logString} body:\n$bodyContent"

    private fun getMainLogMsg(commonData: CommonLoggingData): String = " ${commonData.methodValue} ${commonData.path}"

    private fun getQueryLogMsg(query: String?): String = query?.let { "?$it" } ?: ""

    private fun getStatusLogMsg(httpStatus: HttpStatusCode): String = " $httpStatus"

    private fun getDurationLogMsg(durationMillis: Long): String = " | Duration: " + "%02dh %02dm %02ds %03dms".format(
        TimeUnit.MILLISECONDS.toHours(durationMillis),
        TimeUnit.MILLISECONDS.toMinutes(durationMillis) % TimeUnit.HOURS.toMinutes(1),
        TimeUnit.MILLISECONDS.toSeconds(durationMillis) % TimeUnit.MINUTES.toSeconds(1),
        durationMillis % TimeUnit.SECONDS.toMillis(1)
    )

}
