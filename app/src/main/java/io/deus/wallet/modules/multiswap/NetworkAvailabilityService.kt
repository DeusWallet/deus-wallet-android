package io.deus.wallet.modules.multiswap

import io.deus.wallet.core.ServiceState
import io.deus.wallet.core.managers.ConnectivityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class NetworkAvailabilityService(
    private val connectivityManager: ConnectivityManager
) : ServiceState<NetworkAvailabilityService.State>() {

    private var networkAvailable = connectivityManager.isConnected
    private var error: UnknownHostException? = null

    override fun createState() = State(
        networkAvailable = networkAvailable,
        error = error,
    )

    fun start(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            connectivityManager.networkAvailabilityFlow.collect { networkAvailable ->
                handleUpdatedNetworkState(networkAvailable)
            }
        }
    }

    private fun handleUpdatedNetworkState(networkAvailable: Boolean) {
        this.networkAvailable = networkAvailable
        error = if (!networkAvailable) {
            UnknownHostException()
        } else {
            null
        }

        emitState()
    }

    data class State(val networkAvailable: Boolean, val error: UnknownHostException?)
}
