package com.example.hw2

import com.example.hw2.domain.SplitCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SplitCalculatorTest {

    @Test
    fun calculate_correctValues() {
        val r = SplitCalculator.calculate(
            total = 100.0,
            people = 4
        )

        assertEquals(10.0, r.tip, 1e-9)
        assertEquals(110.0, r.totalWithTip, 1e-9)
        assertEquals(27.5, r.perPerson, 1e-9)
        assertTrue(r.id.isNotBlank())
    }
}
