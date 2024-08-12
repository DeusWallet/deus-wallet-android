package io.deus.wallet.core.managers

import io.deus.wallet.core.IAccountCleaner
import io.deus.wallet.core.adapters.BinanceAdapter
import io.deus.wallet.core.adapters.BitcoinAdapter
import io.deus.wallet.core.adapters.BitcoinCashAdapter
import io.deus.wallet.core.adapters.DashAdapter
import io.deus.wallet.core.adapters.ECashAdapter
import io.deus.wallet.core.adapters.Eip20Adapter
import io.deus.wallet.core.adapters.EvmAdapter
import io.deus.wallet.core.adapters.SolanaAdapter
import io.deus.wallet.core.adapters.TronAdapter
import io.deus.wallet.core.adapters.zcash.ZcashAdapter

class AccountCleaner : IAccountCleaner {

    override fun clearAccounts(accountIds: List<String>) {
        accountIds.forEach { clearAccount(it) }
    }

    private fun clearAccount(accountId: String) {
        BinanceAdapter.clear(accountId)
        BitcoinAdapter.clear(accountId)
        BitcoinCashAdapter.clear(accountId)
        ECashAdapter.clear(accountId)
        DashAdapter.clear(accountId)
        EvmAdapter.clear(accountId)
        Eip20Adapter.clear(accountId)
        ZcashAdapter.clear(accountId)
        SolanaAdapter.clear(accountId)
        TronAdapter.clear(accountId)
    }

}
