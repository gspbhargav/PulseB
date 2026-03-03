package com.pulseb.app.domain.logic

object IntervalEscalationEngine {

    fun nextInterval(
        baseInterval: Int,
        currentInterval: Int,
        userLoggedRealEntry: Boolean
    ): Int {

        return if (userLoggedRealEntry) {
            baseInterval
        } else {
            when {
                currentInterval >= 60 -> 60
                else -> (currentInterval + baseInterval).coerceAtMost(60)
            }
        }
    }
}