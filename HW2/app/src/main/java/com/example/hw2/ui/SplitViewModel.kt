package com.example.hw2.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import java.util.UUID

data class CalcResult(
    val id: String,
    val tip: Double,
    val totalWithTip: Double,
    val perPerson: Double
)

class SplitViewModel : ViewModel() {

    var total by mutableStateOf("")
    var people by mutableStateOf("")

    private val _history = mutableStateListOf<CalcResult>()
    val history: List<CalcResult> = _history

    fun isValid(): Boolean =
        total.toDoubleOrNull()?.let { it > 0 } == true &&
                people.toIntOrNull()?.let { it > 0 } == true

    fun calculate(): String {
        val t = total.toDouble()
        val p = people.toInt()

        val tip = t * 0.1
        val sum = t + tip
        val per = sum / p

        val id = UUID.randomUUID().toString()
        _history.add(0, CalcResult(id, tip, sum, per))
        if (_history.size > 10) _history.removeAt(_history.lastIndex)

        return id
    }

    fun getById(id: String) = history.first { it.id == id }

    fun reset() {
        total = ""
        people = ""
    }
}
