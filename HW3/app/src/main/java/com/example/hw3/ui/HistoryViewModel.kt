package com.example.hw3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3.data.HistoryRepository
import com.example.hw3.data.local.GameHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias HistoryState = UiState<List<GameHistoryEntity>>

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val historyState: StateFlow<HistoryState> = historyRepository.observeHistory()
        .map { items ->
            if (items.isEmpty()) UiState.Empty else UiState.Success(items)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun clearHistory() {
        viewModelScope.launch { historyRepository.clearHistory() }
    }

    fun removeEntry(gameId: Int) {
        viewModelScope.launch { historyRepository.removeFromHistory(gameId) }
    }
}
