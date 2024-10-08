package io.deus.wallet.core.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.deus.wallet.entities.TokenAutoEnabledBlockchain
import io.horizontalsystems.marketkit.models.BlockchainType

@Dao
interface TokenAutoEnabledBlockchainDao {

    @Query("SELECT * FROM TokenAutoEnabledBlockchain WHERE accountId = :accountId AND blockchainType = :blockchainType")
    fun get(accountId: String, blockchainType: BlockchainType): TokenAutoEnabledBlockchain?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tokenAutoEnabledBlockchain: TokenAutoEnabledBlockchain)

}
