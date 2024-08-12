package io.deus.wallet.core.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.deus.wallet.entities.EnabledWalletCache

@Dao
interface EnabledWalletsCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<EnabledWalletCache>)

    @Query("SELECT * FROM `EnabledWalletCache`")
    fun getAll() : List<EnabledWalletCache>

}
