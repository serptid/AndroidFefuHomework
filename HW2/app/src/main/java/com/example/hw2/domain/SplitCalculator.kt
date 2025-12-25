package com.example.hw2.domain

import java.util.UUID

object SplitCalculator {
    fun calculate(total: Double, people: Int): SplitResult {
        val tip = total * 0.1
        val sum = total + tip
        val per = sum / people
        return SplitResult(
            id = UUID.randomUUID().toString(),
            tip = tip,
            totalWithTip = sum,
            perPerson = per
        )
    }
}
