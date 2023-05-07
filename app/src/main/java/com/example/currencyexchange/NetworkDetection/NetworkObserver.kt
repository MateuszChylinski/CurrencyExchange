package com.example.currencyexchange.NetworkDetection

import kotlinx.coroutines.flow.Flow

interface NetworkObserver {
    fun observeStatus(): Flow<NetworkStatus>
    enum class NetworkStatus{
        Available, Unavailable, Losing, Lost
    }
}