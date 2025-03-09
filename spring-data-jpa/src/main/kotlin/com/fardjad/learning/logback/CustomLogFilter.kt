package com.fardjad.learning.logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply

class CustomLogFilter : Filter<ILoggingEvent>() {
    override fun decide(event: ILoggingEvent?): FilterReply {
        if (event?.loggerName?.startsWith("tc.") == true && !event.level.isGreaterOrEqual(Level.WARN)) {
            return FilterReply.DENY
        }

        return FilterReply.NEUTRAL
    }
}