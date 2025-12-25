package com.example.hw2.ui

import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test

class SplitViewModelTest {

    @Test
    fun calculate_invalidInput_returnsNull_andSetsErrors() {
        val vm = SplitViewModel()

        vm.onTotalChange("abc")
        vm.onPeopleChange("0")

        val id = vm.calculate()

        assertNull(id)
        assertNotNull(vm.ui.totalError)
        assertNotNull(vm.ui.peopleError)
    }
}
