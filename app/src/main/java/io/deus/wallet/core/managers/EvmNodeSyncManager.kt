package io.deus.wallet.core.managers

import android.os.Handler
import android.os.Looper
import io.deus.wallet.core.IEvmNodeSyncManager
import io.deus.wallet.core.IEvmNodeSyncStorage
import io.deus.wallet.core.utils.EvmNodeSync

class EvmNodeSyncManager(
    private val storage: IEvmNodeSyncStorage
) : IEvmNodeSyncManager {
    override fun onAppLaunch() {
        prepareSync()
    }

    private fun prepareSync() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                EvmNodeSync.getRpcNodes()
                handler.postDelayed(this, 5000)
            }
        }, 5000)
    }


    // get all nodes for chainId
    override fun getNodes(chainId: Int): List<String> {
        // take top 3
        return storage.getSyncRecords(chainId).map { it.url }.take(3)
    }
}