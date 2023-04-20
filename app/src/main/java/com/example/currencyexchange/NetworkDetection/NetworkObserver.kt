package com.example.currencyexchange.NetworkDetection

import kotlinx.coroutines.flow.Flow

interface NetworkObserver {
    fun observe(): Flow<NetworkStatus>

    enum class NetworkStatus{
        Available, Unavailable, Losing, Lost
    }
}