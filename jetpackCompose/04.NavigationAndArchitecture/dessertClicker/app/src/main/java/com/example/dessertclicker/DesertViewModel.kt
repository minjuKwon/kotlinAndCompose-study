package com.example.dessertclicker


import androidx.lifecycle.ViewModel
import com.example.dessertclicker.data.DesertUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DesertViewModel: ViewModel() {
    private val _uiState= MutableStateFlow(DesertUiState())
    val uiState: StateFlow<DesertUiState> = _uiState.asStateFlow()

    fun updateRevenue() {
        val updateRevenue=_uiState.value.revenue+_uiState.value.currentDessertPrice
        _uiState.update{currentState->
            currentState.copy(revenue = updateRevenue)
        }
    }

    fun updateDesertSold() {
        val updateSold=_uiState.value.dessertsSold+1;
        _uiState.update { currentState->
            currentState.copy(dessertsSold = updateSold)
        }
    }

    fun updateDesertId(image: Int, price: Int) {
        _uiState.update { currentState->
            currentState.copy(currentDesertImageId = image, currentDessertPrice = price)
        }
    }

}