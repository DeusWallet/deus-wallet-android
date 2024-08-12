package io.deus.wallet.core.storage

import android.util.Log
import io.deus.wallet.core.IAccountsStorage
import io.deus.wallet.core.IEvmNodeSyncStorage
import io.deus.wallet.entities.Account
import io.deus.wallet.entities.AccountOrigin
import io.deus.wallet.entities.AccountType
import io.deus.wallet.entities.ActiveAccount
import io.deus.wallet.entities.CexType
import io.reactivex.Flowable

class EvmNodeSyncStorage(appDatabase: AppDatabase) : IEvmNodeSyncStorage {
    private val dao: EvmNodeSyncDao by lazy {
        appDatabase.evmNodeDao()
    }

    override fun getSyncRecords(chainId: Int): List<EvmNodeSyncRecord> {
        return dao.getAll(chainId)
    }

    override fun save(syncRecord: EvmNodeSyncRecord) {
        dao.insert(syncRecord)
    }

    override fun delete(chainId: Int, url: String) {
        dao.delete(chainId, url)
    }

    override fun clear() {
        dao.deleteAll()
    }
}
