package com.example.hw2.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.hw2.domain.SplitCalculator
import com.example.hw2.domain.SplitResult

class SplitViewModel : ViewModel() {

    var ui by mutableStateOf(InputUiState())
        private set

    private val _history = mutableStateListOf<SplitResult>()
    val history: List<SplitResult> = _history

    fun onTotalChange(v: String) {
        ui = ui.copy(total = v, totalError = null)
    }

    fun onPeopleChange(v: String) {
        ui = ui.copy(people = v, peopleError = null)
    }

    fun isValid(): Boolean =
        ui.total.toDoubleOrNull()?.let { it > 0 } == true &&
                ui.people.toIntOrNull()?.let { it > 0 } == true

    fun calculate(): String? {
        val t = ui.total.toDoubleOrNull()
        val p = ui.people.toIntOrNull()

        val totalErr = if (t == null || t <= 0) "Enter total > 0" else null
        val peopleErr = if (p == null || p <= 0) "Enter people > 0" else null

        if (totalErr != null || peopleErr != null) {
            ui = ui.copy(totalError = totalErr, peopleError = peopleErr)
            return null
        }

        val r = SplitCalculator.calculate(total = t!!, people = p!!)
        _history.add(0, r)
        if (_history.size > 10) _history.removeAt(_history.lastIndex)
        return r.id
    }

    fun getByIdOrNull(id: String): SplitResult? =
        _history.firstOrNull { it.id == id }

    fun reset() {
        ui = InputUiState()
    }
}
